package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

public class LongBitPacking implements LongCompressor, LongDecompressor
{
    public static final int BLOCK_LEN = 16;
    public static final int BLOCK_NUM = 4;

    private static final long[] MASKS = newMasks();

    public static long[] newMasks() {
        long[] masks = new long[65];
        long m = 0xffffffffffffffffL;
        for (int i = 64; i >= 0; --i) {
            masks[i] = m;
            m >>>= 1;
        }
        return masks;
    }

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
        switch (validBits) {
            case 0:
                pack0(src, dst, len);
                break;
            default:
                packAny(src, dst, validBits, len);
                break;
        }
    }

    public static void pack0(
            LongBuffer src,
            LongBuffer dst,
            int len)
    {
        src.position(src.position() + len);
    }

    public static void packAny(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len)
    {
        long current = 0;
        int capacity = 64;
        long mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            long n = src.get();
            if (capacity >= validBits) {
                current |= (n & mask) << (capacity - validBits);
                capacity -= validBits;
                if (capacity == 0) {
                    dst.put(current);
                    current = 0;
                    capacity = 64;
                }
            } else {
                int remain = validBits - capacity;
                current |= (n >> remain) & MASKS[capacity];
                dst.put(current);
                capacity = 64 - remain;
                current = (n & MASKS[remain]) << capacity;
            }
        }
        if (capacity < 64) {
            dst.put(current);
        }
    }

    public static void unpack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len)
    {
        long fetchedData = 0;
        int fetchedBits = 0;
        long mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            if (fetchedBits < validBits) {
                long n0 = fetchedBits > 0 ?
                    fetchedData << (validBits - fetchedBits) : 0;
                fetchedData = src.get();
                fetchedBits += 64 - validBits;
                dst.put((n0 | (fetchedData >>> fetchedBits)) & mask);
            } else {
                fetchedBits -= validBits;
                dst.put((fetchedData >>> fetchedBits) & mask);
            }
        }
    }

    // TODO: test
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

    // TODO: test
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
