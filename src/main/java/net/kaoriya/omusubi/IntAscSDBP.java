package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;

import net.kaoriya.omusubi.encodings.DeltaEncoding;
import net.kaoriya.omusubi.filters.IntEncodingFilter;

/**
 * Int Ascending Sorted Delta Bit Packing.
 */
public class IntAscSDBP extends IntCodec
{
    private final IntBitPacking bitPack;

    private final IntFilterFactory encodeFilterFactory;

    private final IntFilterFactory decodeFilterFactory;

    public IntAscSDBP(IntBitPacking bitPack) {
        this.bitPack = bitPack;
        this.encodeFilterFactory = new IntEncodingFilter.Factory(
                new DeltaEncoding.IntAscendEncoder());
        this.decodeFilterFactory = new IntEncodingFilter.Factory(
                new DeltaEncoding.IntAscendDecoder());
    }

    public IntAscSDBP() {
        this(new IntBitPacking());
    }

    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        CodecUtils.encodeBlockPack(src, this.encodeFilterFactory,
                this.bitPack, dst);
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        CodecUtils.decodeBlockPack(src, this.decodeFilterFactory,
                this.bitPack, dst);
    }

    public static byte[] toBytes(int[] src) {
        return (new IntAscSDBP()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntAscSDBP()).decompress(src);
    }

    @Override
    public IntInputStream newCompressStream(IntBuffer src) {
        return new IntCompressStream(src, null, null);
    }

    @Override
    public IntInputStream newDecompressStream(IntBuffer src) {
        return new IntDecompressStream(src, null, null);
    }

    static class Reader {
        IntInputStream stream;
        Integer last = null;
        Reader(IntInputStream stream) {
            this.stream = stream;
        }
        Integer read() {
            this.last = this.stream.read();
            return this.last;
        }
        Integer last() {
            return this.last;
        }
    }

    static Reader newBytesDecompressReader(byte[] b) {
        IntDecompressStream ds = new IntDecompressStream(
                ByteBuffer.wrap(b).asIntBuffer(),
                new IntEncodingFilter.Factory(
                    new DeltaEncoding.IntAscendEncoder()),
                new IntBitPacking());
        Reader r = new Reader(ds);
        r.read();
        return r;
    }

    static void skipEqualOrLessValues(List<Reader> readers, int n) {
        for (Reader r : readers) {
            Integer v = r.last();
            while (v != null && v <= n) {
                v = r.read();
            }
        }
    }

    static Integer fetchMinimumInt(List<Reader> readers) {
        Reader minR = null;
        for (Reader r : readers) {
            Integer v = r.last();
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
        Integer minV = minR.last();
        skipEqualOrLessValues(readers, minV.intValue());
        return minV;
    }

    static boolean allReadersHaveInt(List<Reader> readers, int n) {
        boolean retval = true;
        for (Reader r : readers) {
            Integer v = r.last();
            if (v == null || v.intValue() != n) {
                retval = false;
                break;
            }
        }
        // skip same or less values.
        skipEqualOrLessValues(readers, n);
        return retval;
    }

    static boolean anyReadersHaveInt(List<Reader> readers, int n) {
        boolean retval = false;
        for (Reader r : readers) {
            Integer v = r.last();
            if (v != null && v.intValue() == n) {
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
        IntArrayOutputStream os = new IntArrayOutputStream();
        while (true) {
            Integer n = fetchMinimumInt(readers);
            if (n == null) {
                break;
            }
            os.write(n.intValue());
        }
        return toBytes(os.toIntArray());
    }

    public static byte[] intersect(byte[] a, byte[] b, byte[] ...others) {
        Reader pivot = newBytesDecompressReader(a);
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(b));
        for (byte[] c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        IntArrayOutputStream os = new IntArrayOutputStream();
        Integer n = pivot.last;
        while (n != null) {
            if (allReadersHaveInt(readers, n)) {
                os.write(n.intValue());
            }
            n = pivot.read();
        }
        return toBytes(os.toIntArray());
    }

    public static byte[] difference(byte[] a, byte[] b, byte[] ...others) {
        Reader pivot = newBytesDecompressReader(a);
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(b));
        for (byte[] c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        IntArrayOutputStream os = new IntArrayOutputStream();
        Integer v = pivot.last;
        while (v != null) {
            if (!anyReadersHaveInt(readers, v)) {
                os.write(v.intValue());
            }
            Integer w = pivot.read();
            while (w != null && w <= v) {
                w = pivot.read();
            }
            v = w;
        }
        return toBytes(os.toIntArray());
    }
}
