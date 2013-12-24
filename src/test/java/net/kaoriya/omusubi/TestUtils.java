package net.kaoriya.omusubi;

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

    public static int[] paddingInt(int[] src, int chunkLen, int extend) {
        int over = src.length % chunkLen;
        if (src.length > 0 && over == 0) {
            return src;
        }

        int[] dst = new int[src.length - over + chunkLen + extend];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public static void dumpInt(String name, int[] src) {
        System.out.println(String.format("%s: [", name));
        int col = 0;
        for (int i = 0; i < src.length; ++i) {
            if (col == 0) {
                System.out.format(" ");
            }
            System.out.format(" %08x", src[i]);
            col = (col + 1) % 8;
            if (col == 0) {
                System.out.println();
            }
        }
        if (col != 0) {
            System.out.println();
        }
        System.out.println("]");
    }
}
