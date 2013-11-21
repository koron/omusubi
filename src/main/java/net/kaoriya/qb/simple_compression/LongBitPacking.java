package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

public class LongBitPacking implements LongCompressor, LongDecompressor
{
    public static final int BLOCK_LEN = 16;
    public static final int BLOCK_NUM = 4;

    private static final long[] MASKS = newMasks();

    private static final LongFilter THROUGH_FILTER = new ThroughLongFilter();

    private boolean debug = false;

    private final int blockLen;

    private final int blockNum;

    public LongBitPacking(int blockLen, int blockNum) {
        this.blockLen = blockLen;
        this.blockNum = blockNum;
    }

    public LongBitPacking() {
        this(BLOCK_LEN, BLOCK_NUM);
    }

    public LongBitPacking setDebug(boolean value) {
        this.debug = value;
        return this;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public static long[] newMasks() {
        long[] masks = new long[65];
        long m = 0xffffffffffffffffL;
        for (int i = 64; i >= 0; --i) {
            masks[i] = m;
            m >>>= 1;
        }
        return masks;
    }

    public static int countMaxBits(
            LongBuffer buf,
            int len,
            LongFilter filter)
    {
        long n = 0;
        for (int i = len; i > 0; --i) {
            n |= filter.filterLong(buf.get());
        }
        return 64 - Long.numberOfLeadingZeros(n);
    }

    public static int countMaxBits(LongBuffer buf, int len) {
        return countMaxBits(buf, len, THROUGH_FILTER);
    }

    public static void pack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len)
    {
        pack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void pack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len,
            LongFilter filter)
    {
        switch (validBits) {
            case 0:
                pack0(src, dst, len);
                break;
            default:
                packAny(src, dst, validBits, len, filter);
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
        packAny(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void packAny(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len,
            LongFilter filter)
    {
        long current = 0;
        int capacity = 64;
        long mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            long n = filter.filterLong(src.get());
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

    public void compress(
            LongBuffer src,
            LongBuffer dst,
            LongFilter filter)
    {
        int srclen = src.limit() - src.position();
        int[] maxBits = new int[this.blockNum];
        while (src.remaining() >= this.blockLen * this.blockNum) {
            src.mark();
            filter.saveContext();
            long head = 0;
            for (int i = 0; i < this.blockNum; ++i) {
                long n = maxBits[i] = countMaxBits(src, this.blockLen, filter);
                head = (head << 8) | n;
            }
            filter.restoreContext();
            src.reset();

            dst.put(head);
            for (int i = 0; i < this.blockNum; ++i) {
                pack(src, dst, maxBits[i], this.blockLen, filter);
            }
        }
        return;
    }

    public void compress(LongBuffer src, LongBuffer dst) {
        compress(src, dst, THROUGH_FILTER);
    }

    public static void unpack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len)
    {
        unpack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void unpack(
            LongBuffer src,
            LongBuffer dst,
            int validBits,
            int len,
            LongFilter filter)
    {
        long fetchedData = 0;
        int fetchedBits = 0;
        long mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            long n;
            if (fetchedBits < validBits) {
                long n0 = fetchedBits > 0 ?
                    fetchedData << (validBits - fetchedBits) : 0;
                fetchedData = src.get();
                fetchedBits += 64 - validBits;
                n = (n0 | (fetchedData >>> fetchedBits)) & mask;
            } else {
                fetchedBits -= validBits;
                n = (fetchedData >>> fetchedBits) & mask;
            }
            dst.put(filter.filterLong(n));
        }
    }

    public void decompress(
            LongBuffer src,
            LongBuffer dst,
            LongFilter filter)
    {
        int[] maxBits = new int[this.blockNum];
        while (src.remaining() > 0) {
            long head = src.get();
            for (int i = (this.blockNum - 1) * 8; i >= 0; i -= 8) {
                int validBits = (int)((head >> i) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    public void decompress(LongBuffer src, LongBuffer dst) {
        decompress(src, dst, THROUGH_FILTER);
    }
}
