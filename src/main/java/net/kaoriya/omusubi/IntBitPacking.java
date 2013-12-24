package net.kaoriya.omusubi;

import java.nio.IntBuffer;

public class IntBitPacking extends IntCodec
{
    public static final int BLOCK_LEN = 32;
    public static final int BLOCK_NUM = 4;

    private static final int[] MASKS = newMasks();

    private static final IntFilter THROUGH_FILTER = new ThroughIntFilter();

    private boolean debug = false;

    private final int blockLen;

    private final int blockNum;

    public IntBitPacking(int blockLen, int blockNum) {
        this.blockLen = blockLen;
        this.blockNum = blockNum;
    }

    public IntBitPacking() {
        this(BLOCK_LEN, BLOCK_NUM);
    }

    public IntBitPacking setDebug(boolean value) {
        this.debug = value;
        return this;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public int getBlockLen() {
        return this.blockLen;
    }

    public int getBlockNum() {
        return this.blockNum;
    }

    public int getBlockSize() {
        return this.blockLen * this.blockNum;
    }

    public static int[] newMasks() {
        int[] masks = new int[Integer.SIZE + 1];
        int m = 0xffffffff;
        for (int i = Integer.SIZE; i >= 0; --i) {
            masks[i] = m;
            m >>>= 1;
        }
        return masks;
    }

    public static int countMaxBits(
            IntBuffer buf,
            int len,
            IntFilter filter)
    {
        int n = 0;
        for (int i = len; i > 0; --i) {
            n |= filter.filterInt(buf.get());
        }
        return Integer.SIZE - Integer.numberOfLeadingZeros(n);
    }

    public static int countMaxBits(IntBuffer buf, int len) {
        return countMaxBits(buf, len, THROUGH_FILTER);
    }

    public static void pack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        pack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void pack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
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
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        src.position(src.position() + len);
    }

    public static void packAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        packAny(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void packAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        int current = 0;
        int capacity = Integer.SIZE;
        int mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            int n = filter.filterInt(src.get());
            if (capacity >= validBits) {
                current |= (n & mask) << (capacity - validBits);
                capacity -= validBits;
                if (capacity == 0) {
                    dst.write(current);
                    current = 0;
                    capacity = Integer.SIZE;
                }
            } else {
                int remain = validBits - capacity;
                current |= (n >> remain) & MASKS[capacity];
                dst.write(current);
                capacity = Integer.SIZE - remain;
                current = (n & MASKS[remain]) << capacity;
            }
        }
        if (capacity < Integer.SIZE) {
            dst.write(current);
        }
    }

    protected void compress(
            IntBuffer src,
            IntOutputStream dst, 
            IntFilter filter)
    {
        int srclen = src.limit() - src.position();
        int[] maxBits = new int[this.blockNum];
        while (src.remaining() >= this.blockLen * this.blockNum) {
            src.mark();
            filter.saveContext();
            int head = 0;
            for (int i = 0; i < this.blockNum; ++i) {
                int n = maxBits[i] = countMaxBits(src, this.blockLen, filter);
                head = (head << 8) | n;
            }
            filter.restoreContext();
            src.reset();

            dst.write(head);
            for (int i = 0; i < this.blockNum; ++i) {
                pack(src, dst, maxBits[i], this.blockLen, filter);
            }
        }
        return;
    }

    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        compress(src, dst, THROUGH_FILTER);
    }

    public static void unpack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        unpack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public static void unpack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        int fetchedData = 0;
        int fetchedBits = 0;
        int mask = MASKS[validBits];
        for (int i = len; i > 0; --i) {
            int n;
            if (fetchedBits < validBits) {
                int n0 = fetchedBits > 0 ?
                    fetchedData << (validBits - fetchedBits) : 0;
                fetchedData = src.get();
                fetchedBits += Integer.SIZE - validBits;
                n = (n0 | (fetchedData >>> fetchedBits)) & mask;
            } else {
                fetchedBits -= validBits;
                n = (fetchedData >>> fetchedBits) & mask;
            }
            dst.write(filter.filterInt(n));
        }
    }

    protected void decompress(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter,
            int numOfChunks)
    {
        int[] maxBits = new int[this.blockNum];
        for (int i = numOfChunks; i > 0; --i) {
            int head = src.get();
            for (int j = (this.blockNum - 1) * 8; j >= 0; j -= 8) {
                int validBits = (int)((head >> j) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    protected void decompress(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        int[] maxBits = new int[this.blockNum];
        while (src.hasRemaining()) {
            int head = src.get();
            for (int i = (this.blockNum - 1) * 8; i >= 0; i -= 8) {
                int validBits = (int)((head >> i) & 0xff);
                unpack(src, dst, validBits, this.blockLen, filter);
            }
        }
        return;
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        decompress(src, dst, THROUGH_FILTER);
    }

}
