package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

public class LongBitPacking implements LongCompressor, LongDecompressor
{
    public static final int BLOCK_LEN = 16;
    public static final int BLOCK_NUM = 4;

    public static int countMaxBits(LongBuffer buf, int len) {
        long n = 0;
        for (int i = len; i > 0; --i) {
            n |= buf.get();
        }
        return 64 - Long.numberOfLeadingZeros(n);
    }

    public static void pack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len)
    {
        long current = 0;
        long capacity = 64;
        for (int i = len; i > 0; --i) {
            long n = src.get();
            // TODO:
            dst.put(n);
        }
    }

    public static void unpack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len)
    {
        // TODO:
    }

    public void compress(LongBuffer src, LongBuffer dst) {
        int srclen = src.limit() - src.position();
        int[] maxBits = new int[BLOCK_NUM];
        while (src.remaining() >= BLOCK_LEN * BLOCK_NUM) {
            src.mark();
            long head = 0;
            for (int i = 0; i < BLOCK_NUM; ++i) {
                long n = maxBits[i] = countMaxBits(src, BLOCK_LEN);
                head = (head << 8) | n;
            }
            src.reset();

            dst.put(head);
            for (int i = 0; i < BLOCK_NUM; ++i) {
                pack(src, dst, maxBits[i], BLOCK_LEN);
            }
        }
        return;
    }

    public void decompress(LongBuffer src, LongBuffer dst) {
        int[] maxBits = new int[BLOCK_NUM];
        while (src.remaining() > 0) {
            long head = src.get();
            for (int i = BLOCK_NUM - 1; i >= 0; ++i) {
                int validBits = (int)(head & 0xff);
                head >>= 8;
                unpack(src, dst, validBits, BLOCK_LEN);
            }
        }
        return;
    }
}
