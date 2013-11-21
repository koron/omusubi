package net.kaoriya.qb.simple_compression;

public class TestUtils {
    public static long[] padding(long[] src, int chunkLen, int extend) {
        int over = src.length % chunkLen;
        if (src.length > 0 && over == 0) {
            return src;
        }

        long[] dst = new long[src.length - over + chunkLen + extend];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }
}
