package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;

import net.kaoriya.omusubi.encodings.DeltaEncoding;
import net.kaoriya.omusubi.filters.IntEncodingFilter;
import net.kaoriya.omusubi.filters.IntFilterFactory;

import net.kaoriya.omusubi.io.IntArrayOutputStream;
import net.kaoriya.omusubi.io.IntCompressStream;
import net.kaoriya.omusubi.io.IntDecompressStream;
import net.kaoriya.omusubi.io.IntInputStream;
import net.kaoriya.omusubi.io.IntOutputStream;

import net.kaoriya.omusubi.packers.IntBitPacking;
import net.kaoriya.omusubi.utils.CodecUtils;

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

    public static class Reader {
        IntInputStream stream;
        Integer last = null;
        public Reader(IntInputStream stream) {
            this.stream = stream;
        }
        public Integer read() {
            this.last = this.stream.read();
            return this.last;
        }
        public Integer last() {
            return this.last;
        }
    }

    static Reader newBytesDecompressReader(ByteBuffer b) {
        IntDecompressStream ds = new IntDecompressStream(
                b.asIntBuffer(),
                new IntEncodingFilter.Factory(
                    new DeltaEncoding.IntAscendDecoder()),
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

    private static ByteBuffer[] toByteBufferArray(byte[][] bufArray) {
        ByteBuffer[] results = new ByteBuffer[bufArray.length];
        for (int i = 0; i < bufArray.length; ++i) {
            results[i] = ByteBuffer.wrap(bufArray[i]);
        }
        return results;
    }

    public static byte[] union(byte[] a, byte[] b, byte[] ...others) {
        return union(ByteBuffer.wrap(a), ByteBuffer.wrap(b),
                toByteBufferArray(others));
    }

    public static byte[] union(
            ByteBuffer a,
            ByteBuffer b,
            ByteBuffer ...others)
    {
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(a));
        readers.add(newBytesDecompressReader(b));
        for (ByteBuffer c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        return toBytes(union(readers).toIntArray());
    }

    public static IntArrayOutputStream union(List<Reader> readers) {
        IntArrayOutputStream os = new IntArrayOutputStream();
        while (true) {
            Integer n = fetchMinimumInt(readers);
            if (n == null) {
                break;
            }
            os.write(n.intValue());
        }
        return os;
    }

    public static byte[] intersect(byte[] a, byte[] b, byte[] ...others) {
        return intersect(ByteBuffer.wrap(a), ByteBuffer.wrap(b),
                toByteBufferArray(others));
    }

    public static byte[] intersect(
            ByteBuffer a,
            ByteBuffer b,
            ByteBuffer ...others)
    {
        Reader pivot = newBytesDecompressReader(a);
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(b));
        for (ByteBuffer c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        return toBytes(intersect(pivot, readers).toIntArray());
    }

    public static IntArrayOutputStream intersect(
            Reader pivot,
            List<Reader> readers)
    {
        IntArrayOutputStream os = new IntArrayOutputStream();
        Integer n = pivot.last;
        while (n != null) {
            if (allReadersHaveInt(readers, n)) {
                os.write(n.intValue());
            }
            n = pivot.read();
        }
        return os;
    }

    public static byte[] difference(byte[] a, byte[] b, byte[] ...others) {
        return difference(ByteBuffer.wrap(a), ByteBuffer.wrap(b),
                toByteBufferArray(others));
    }

    public static byte[] difference(
            ByteBuffer a,
            ByteBuffer b,
            ByteBuffer ...others)
    {
        Reader pivot = newBytesDecompressReader(a);
        List<Reader> readers = new LinkedList<Reader>();
        readers.add(newBytesDecompressReader(b));
        for (ByteBuffer c : others) {
            readers.add(newBytesDecompressReader(c));
        }
        return toBytes(difference(pivot, readers).toIntArray());
    }

    public static IntArrayOutputStream difference(
            Reader pivot,
            List<Reader> readers)
    {
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
        return os;
    }
}
