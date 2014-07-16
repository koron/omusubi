package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.LinkedList;

/**
 * Int Ascending Sorted Delta Bit Packing.
 */
public class IntAscSDBP extends IntCodec
{
    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        // TODO:
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        // TODO:
    }

    public static byte[] toBytes(int[] src) {
        return (new IntDZBP()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntDZBP()).decompress(src);
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
                null,
                new IntBitPacking());
        Reader r = new Reader(ds);
        r.read();
        return r;
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
        for (Reader r : readers) {
            Integer v = r.last();
            while (v != null && v <= minV) {
                v = r.read();
            }
        }
        return minV;
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
        // TODO:
        return null;
    }

    public static byte[] difference(byte[] a, byte[] b) {
        // TODO:
        return null;
    }
}
