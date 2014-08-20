package net.kaoriya.omusubi;

import java.nio.LongBuffer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;

import net.kaoriya.omusubi.encodings.DeltaEncoding;
import net.kaoriya.omusubi.filters.LongEncodingFilter;

import net.kaoriya.omusubi.io.LongArrayOutputStream;
import net.kaoriya.omusubi.io.LongCompressStream;
import net.kaoriya.omusubi.io.LongDecompressStream;
import net.kaoriya.omusubi.io.LongInputStream;
import net.kaoriya.omusubi.io.LongOutputStream;

/**
 * Long Ascending Sorted Delta Bit Packing.
 */
public class LongAscSDBP extends LongCodec
{
    private final LongBitPacking bitPack;

    private final LongFilterFactory encodeFilterFactory;

    private final LongFilterFactory decodeFilterFactory;

    public LongAscSDBP(LongBitPacking bitPack) {
        this.bitPack = bitPack;
        this.encodeFilterFactory = new LongEncodingFilter.Factory(
                new DeltaEncoding.LongAscendEncoder());
        this.decodeFilterFactory = new LongEncodingFilter.Factory(
                new DeltaEncoding.LongAscendDecoder());
    }

    public LongAscSDBP() {
        this(new LongBitPacking());
    }

    // @Implemnets: LongCodec
    public void compress(LongBuffer src, LongOutputStream dst) {
        CodecUtils.encodeBlockPack(src, this.encodeFilterFactory,
                this.bitPack, dst);
    }

    // @Implemnets: LongCodec
    public void decompress(LongBuffer src, LongOutputStream dst) {
        CodecUtils.decodeBlockPack(src, this.decodeFilterFactory,
                this.bitPack, dst);
    }

    public static byte[] toBytes(long[] src) {
        return (new LongAscSDBP()).compress(src);
    }

    public static long[] fromBytes(byte[] src) {
        return (new LongAscSDBP()).decompress(src);
    }

    @Override
    public LongInputStream newCompressStream(LongBuffer src) {
        return new LongCompressStream(src, null, null);
    }

    @Override
    public LongInputStream newDecompressStream(LongBuffer src) {
        return new LongDecompressStream(src, null, null);
    }

    public static class Reader {
        LongInputStream stream;
        Long last = null;
        public Reader(LongInputStream stream) {
            this.stream = stream;
        }
        public Long read() {
            this.last = this.stream.read();
            return this.last;
        }
        public Long last() {
            return this.last;
        }
    }

    static Reader newBytesDecompressReader(byte[] b) {
        LongDecompressStream ds = new LongDecompressStream(
                ByteBuffer.wrap(b).asLongBuffer(),
                new LongEncodingFilter.Factory(
                    new DeltaEncoding.LongAscendDecoder()),
                new LongBitPacking());
        Reader r = new Reader(ds);
        r.read();
        return r;
    }

    static void skipEqualOrLessValues(List<Reader> readers, long n) {
        for (Reader r : readers) {
            Long v = r.last();
            while (v != null && v <= n) {
                v = r.read();
            }
        }
    }

    static Long fetchMinimumLong(List<Reader> readers) {
        Reader minR = null;
        for (Reader r : readers) {
            Long v = r.last();
            if (v == null) {
                continue;
            }
            if (minR == null || v < minR.last()) {
                minR = r;
            }
        }
        if (minR == null) {
            return null;
        }
        // skip same or less values.
        Long minV = minR.last();
        skipEqualOrLessValues(readers, minV.longValue());
        return minV;
    }

    static boolean allReadersHaveLong(List<Reader> readers, long n) {
        boolean retval = true;
        for (Reader r : readers) {
            Long v = r.last();
            if (v == null || v.longValue() != n) {
                retval = false;
                break;
            }
        }
        // skip same or less values.
        skipEqualOrLessValues(readers, n);
        return retval;
    }

    static boolean anyReadersHaveLong(List<Reader> readers, long n) {
        boolean retval = false;
        for (Reader r : readers) {
            Long v = r.last();
            if (v != null && v.longValue() == n) {
                retval = true;
                break;
            }
        }
        // skip same or less values.
        skipEqualOrLessValues(readers, n);
        return retval;
    }

    public static byte[] union(byte[] a, byte[] b, byte[] ...others) {
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(a));
        readers.add(newBytesDecompressReader(b));
        for (byte[] c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        return toBytes(union(readers).toLongArray());
    }

    public static LongArrayOutputStream union(List<Reader> readers) {
        LongArrayOutputStream os = new LongArrayOutputStream();
        while (true) {
            Long n = fetchMinimumLong(readers);
            if (n == null) {
                break;
            }
            os.write(n.longValue());
        }
        return os;
    }

    public static byte[] intersect(byte[] a, byte[] b, byte[] ...others) {
        Reader pivot = newBytesDecompressReader(a);
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(b));
        for (byte[] c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        return toBytes(intersect(pivot, readers).toLongArray());
    }

    public static LongArrayOutputStream intersect(
            Reader pivot,
            List<Reader> readers)
    {
        LongArrayOutputStream os = new LongArrayOutputStream();
        Long n = pivot.last;
        while (n != null) {
            if (allReadersHaveLong(readers, n)) {
                os.write(n.longValue());
            }
            n = pivot.read();
        }
        return os;
    }

    public static byte[] difference(byte[] a, byte[] b, byte[] ...others) {
        Reader pivot = newBytesDecompressReader(a);
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(b));
        for (byte[] c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        return toBytes(difference(pivot, readers).toLongArray());
    }

    public static LongArrayOutputStream difference(
            Reader pivot,
            List<Reader> readers)
    {
        LongArrayOutputStream os = new LongArrayOutputStream();
        Long v = pivot.last;
        while (v != null) {
            if (!anyReadersHaveLong(readers, v)) {
                os.write(v.longValue());
            }
            Long w = pivot.read();
            while (w != null && w <= v) {
                w = pivot.read();
            }
            v = w;
        }
        return os;
    }
}
