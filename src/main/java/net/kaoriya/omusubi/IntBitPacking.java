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

    private final int[] packBuf;

    private final int[] unpackBuf;

    public IntBitPacking(int blockLen, int blockNum) {
        this.blockLen = blockLen;
        this.blockNum = blockNum;
        this.packBuf = new int[blockLen];
        this.unpackBuf = new int[blockLen];
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

    public void pack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        pack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void pack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        switch (validBits) {
            case 0: pack0(src, dst, len); break;
            case 1: pack1(src, dst, len, filter); break;
            case 2: pack2(src, dst, len, filter); break;
            case 3: pack3(src, dst, len, filter); break;
            case 4: pack4(src, dst, len, filter); break;
            case 10: pack10(src, dst, len, filter); break;
            case 11: pack11(src, dst, len, filter); break;
            case 12: pack12(src, dst, len, filter); break;
            default:
                packAny(src, dst, validBits, len, filter);
                break;
        }
    }

    public void pack0(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        src.position(src.position() + len);
    }

    public void pack1(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack1(src, dst, len, THROUGH_FILTER);
    }

    public void pack1(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[1];

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 31 |
            (filter.filterInt(src.get()) & m) << 30 |
            (filter.filterInt(src.get()) & m) << 29 |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 27 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf[0]);
    }

    public void pack2(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack2(src, dst, len, THROUGH_FILTER);
    }

    public void pack2(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[2];

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 30 |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[1] =
            (filter.filterInt(src.get()) & m) << 30 |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 2);
    }

    public void pack3(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack3(src, dst, len, THROUGH_FILTER);
    }

    public void pack3(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[3];
        int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 29 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >> 1;

        this.packBuf[1] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >> 2;

        this.packBuf[2] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 27 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 3);
    }

    public void pack4(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack4(src, dst, len, THROUGH_FILTER);
    }

    public void pack4(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[4];

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[1] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[2] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[3] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 4);
    }

    public void pack10(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack10(src, dst, len, THROUGH_FILTER);
    }

    public void pack10(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[10];
        int n;

        this.packBuf[0] = 
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[1] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 6;
        this.packBuf[2] = 
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[3] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 2;
        this.packBuf[4] = 
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[5] = 
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[6] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 6;
        this.packBuf[7] = 
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[8] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 2;
        this.packBuf[9] = 
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 10);
    }

    public void pack11(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack11(src, dst, len, THROUGH_FILTER);
    }

    public void pack11(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[11];
        int n;

        this.packBuf[0] = 
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >> 1;
        this.packBuf[1] = 
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >> 2;
        this.packBuf[2] = 
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 3;
        this.packBuf[3] = 
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[4] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >> 5;
        this.packBuf[5] = 
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >> 6;
        this.packBuf[6] = 
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 7;
        this.packBuf[7] = 
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[8] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >> 9;
        this.packBuf[9] = 
            (n << 23) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >> 10;
        this.packBuf[10] = 
            (n << 22) |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 11);
    }

    public void pack12(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        pack12(src, dst, len, THROUGH_FILTER);
    }

    public void pack12(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[12];
        int n;

        this.packBuf[0] = 
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[1] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[2] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[3] = 
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[4] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[5] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[6] = 
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[7] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[8] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[9] = 
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[10] = 
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[11] = 
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 12);
    }


    public void packAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        packAny(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void packAny(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        int current = 0;
        int capacity = Integer.SIZE;
        int mask = MASKS[validBits];
        int packIndex = 0;
        for (int i = len; i > 0; --i) {
            int n = filter.filterInt(src.get());
            if (capacity >= validBits) {
                current |= (n & mask) << (capacity - validBits);
                capacity -= validBits;
                if (capacity == 0) {
                    this.packBuf[packIndex++] = current;
                    current = 0;
                    capacity = Integer.SIZE;
                }
            } else {
                int remain = validBits - capacity;
                current |= (n >> remain) & MASKS[capacity];
                this.packBuf[packIndex++] = current;
                capacity = Integer.SIZE - remain;
                current = (n & MASKS[remain]) << capacity;
            }
        }
        if (capacity < Integer.SIZE) {
            this.packBuf[packIndex++] = current;
        }
        if (packIndex > 0) {
            dst.write(this.packBuf, 0, packIndex);
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

    public void unpack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len)
    {
        unpack(src, dst, validBits, len, THROUGH_FILTER);
    }

    public void unpack(
            IntBuffer src,
            IntOutputStream dst,
            int validBits,
            int len,
            IntFilter filter)
    {
        int fetchedData = 0;
        int fetchedBits = 0;
        int mask = MASKS[validBits];
        for (int i = 0; i < len; ++i) {
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
            this.unpackBuf[i] = filter.filterInt(n);
        }
        dst.write(this.unpackBuf, 0, len);
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
