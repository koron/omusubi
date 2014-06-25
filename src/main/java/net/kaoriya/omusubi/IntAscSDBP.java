package net.kaoriya.omusubi;

import java.nio.IntBuffer;

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

    public static byte[] union(byte[] a, byte[] b, byte[] ...others) {
        // TODO:
        return null;
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
