package net.kaoriya.omusubi;

import java.nio.IntBuffer;

import static net.kaoriya.omusubi.IntBitPackingPacks.*;
//import static net.kaoriya.omusubi.IntBitPackingUnpacks.*;

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
            case 1: pack1(this.packBuf, src, dst, filter); break;
            case 2: pack2(this.packBuf, src, dst, filter); break;
            case 3: pack3(this.packBuf, src, dst, filter); break;
            case 4: pack4(this.packBuf, src, dst, filter); break;
            case 5: pack5(this.packBuf, src, dst, filter); break;
            case 6: pack6(this.packBuf, src, dst, filter); break;
            case 7: pack7(this.packBuf, src, dst, filter); break;
            case 8: pack8(this.packBuf, src, dst, filter); break;
            case 9: pack9(this.packBuf, src, dst, filter); break;
            case 10: pack10(this.packBuf, src, dst, filter); break;
            case 11: pack11(this.packBuf, src, dst, filter); break;
            case 12: pack12(this.packBuf, src, dst, filter); break;
            case 13: pack13(this.packBuf, src, dst, filter); break;
            case 14: pack14(this.packBuf, src, dst, filter); break;
            case 15: pack15(this.packBuf, src, dst, filter); break;
            case 16: pack16(this.packBuf, src, dst, filter); break;
            case 17: pack17(this.packBuf, src, dst, filter); break;
            case 18: pack18(this.packBuf, src, dst, filter); break;
            case 19: pack19(this.packBuf, src, dst, filter); break;
            case 20: pack20(this.packBuf, src, dst, filter); break;
            case 21: pack21(this.packBuf, src, dst, filter); break;
            case 22: pack22(this.packBuf, src, dst, filter); break;
            case 23: pack23(this.packBuf, src, dst, filter); break;
            case 24: pack24(this.packBuf, src, dst, filter); break;
            case 25: pack25(this.packBuf, src, dst, filter); break;
            case 26: pack26(this.packBuf, src, dst, filter); break;
            case 27: pack27(this.packBuf, src, dst, filter); break;
            case 28: pack28(this.packBuf, src, dst, filter); break;
            case 29: pack29(this.packBuf, src, dst, filter); break;
            case 30: pack30(this.packBuf, src, dst, filter); break;
            case 31: pack31(this.packBuf, src, dst, filter); break;
            case 32: pack32(this.packBuf, src, dst, filter); break;
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
        switch (validBits) {
            case 10: unpack10(src, dst, len, filter); break;
            case 11: unpack11(src, dst, len, filter); break;
            default:
                unpackAny(src, dst, validBits, len, filter);
                break;
        }
    }

    public void unpack10(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[10];
        int n, c;

        n = src.get();
        this.unpackBuf[ 0] = filter.filterInt(n >>> 22 & m);
        this.unpackBuf[ 1] = filter.filterInt(n >>> 12 & m);
        this.unpackBuf[ 2] = filter.filterInt(n >>>  2 & m);
        c = n <<  8 & m;

        n = src.get();
        this.unpackBuf[ 3] = filter.filterInt(c | n >>> 24);
        this.unpackBuf[ 4] = filter.filterInt(n >>> 14 & m);
        this.unpackBuf[ 5] = filter.filterInt(n >>>  4 & m);
        c = n <<  6 & m;

        n = src.get();
        this.unpackBuf[ 6] = filter.filterInt(c | n >>> 26);
        this.unpackBuf[ 7] = filter.filterInt(n >>> 16 & m);
        this.unpackBuf[ 8] = filter.filterInt(n >>>  6 & m);
        c = n <<  4 & m;

        n = src.get();
        this.unpackBuf[ 9] = filter.filterInt(c | n >>> 28);
        this.unpackBuf[10] = filter.filterInt(n >>> 18 & m);
        this.unpackBuf[11] = filter.filterInt(n >>>  8 & m);
        c = n <<  2 & m;

        n = src.get();
        this.unpackBuf[12] = filter.filterInt(c | n >>> 30);
        this.unpackBuf[13] = filter.filterInt(n >>> 20 & m);
        this.unpackBuf[14] = filter.filterInt(n >>> 10 & m);
        this.unpackBuf[15] = filter.filterInt(n <<  0 & m);

        n = src.get();
        this.unpackBuf[16] = filter.filterInt(n >>> 22 & m);
        this.unpackBuf[17] = filter.filterInt(n >>> 12 & m);
        this.unpackBuf[18] = filter.filterInt(n >>>  2 & m);
        c = n <<  8 & m;

        n = src.get();
        this.unpackBuf[19] = filter.filterInt(c | n >>> 24);
        this.unpackBuf[20] = filter.filterInt(n >>> 14 & m);
        this.unpackBuf[21] = filter.filterInt(n >>>  4 & m);
        c = n <<  6 & m;

        n = src.get();
        this.unpackBuf[22] = filter.filterInt(c | n >>> 26);
        this.unpackBuf[23] = filter.filterInt(n >>> 16 & m);
        this.unpackBuf[24] = filter.filterInt(n >>>  6 & m);
        c = n <<  4 & m;

        n = src.get();
        this.unpackBuf[25] = filter.filterInt(c | n >>> 28);
        this.unpackBuf[26] = filter.filterInt(n >>> 18 & m);
        this.unpackBuf[27] = filter.filterInt(n >>>  8 & m);
        c = n <<  2 & m;

        n = src.get();
        this.unpackBuf[28] = filter.filterInt(c | n >>> 30);
        this.unpackBuf[29] = filter.filterInt(n >>> 20 & m);
        this.unpackBuf[30] = filter.filterInt(n >>> 10 & m);
        this.unpackBuf[31] = filter.filterInt(n <<  0 & m);

        dst.write(this.unpackBuf);
    }

    public void unpack11(
            IntBuffer src,
            IntOutputStream dst,
            int len)
    {
        unpack11(src, dst, len, THROUGH_FILTER);
    }

    public void unpack11(
            IntBuffer src,
            IntOutputStream dst,
            int len,
            IntFilter filter)
    {
        final int m = MASKS[11];
        int n, c;

        n = src.get();
        this.unpackBuf[ 0] = filter.filterInt(n >>> 21 & m);
        this.unpackBuf[ 1] = filter.filterInt(n >>> 10 & m);
        c = n <<  1 & m;

        n = src.get();
        this.unpackBuf[ 2] = filter.filterInt(c | n >>> 31);
        this.unpackBuf[ 3] = filter.filterInt(n >>> 20 & m);
        this.unpackBuf[ 4] = filter.filterInt(n >>>  9 & m);
        c = n <<  2 & m;

        n = src.get();
        this.unpackBuf[ 5] = filter.filterInt(c | n >>> 30);
        this.unpackBuf[ 6] = filter.filterInt(n >>> 19 & m);
        this.unpackBuf[ 7] = filter.filterInt(n >>>  8 & m);
        c = n <<  3 & m;

        n = src.get();
        this.unpackBuf[ 8] = filter.filterInt(c | n >>> 29);
        this.unpackBuf[ 9] = filter.filterInt(n >>> 18 & m);
        this.unpackBuf[10] = filter.filterInt(n >>>  7 & m);
        c = n <<  4 & m;

        n = src.get();
        this.unpackBuf[11] = filter.filterInt(c | n >>> 28);
        this.unpackBuf[12] = filter.filterInt(n >>> 17 & m);
        this.unpackBuf[13] = filter.filterInt(n >>>  6 & m);
        c = n <<  5 & m;

        n = src.get();
        this.unpackBuf[14] = filter.filterInt(c | n >>> 27);
        this.unpackBuf[15] = filter.filterInt(n >>> 16 & m);
        this.unpackBuf[16] = filter.filterInt(n >>>  5 & m);
        c = n <<  6 & m;

        n = src.get();
        this.unpackBuf[17] = filter.filterInt(c | n >>> 26);
        this.unpackBuf[18] = filter.filterInt(n >>> 15 & m);
        this.unpackBuf[19] = filter.filterInt(n >>>  4 & m);
        c = n <<  7 & m;

        n = src.get();
        this.unpackBuf[20] = filter.filterInt(c | n >>> 25);
        this.unpackBuf[21] = filter.filterInt(n >>> 14 & m);
        this.unpackBuf[22] = filter.filterInt(n >>>  3 & m);
        c = n <<  8 & m;

        n = src.get();
        this.unpackBuf[23] = filter.filterInt(c | n >>> 24);
        this.unpackBuf[24] = filter.filterInt(n >>> 13 & m);
        this.unpackBuf[25] = filter.filterInt(n >>>  2 & m);
        c = n <<  9 & m;

        n = src.get();
        this.unpackBuf[26] = filter.filterInt(c | n >>> 23);
        this.unpackBuf[27] = filter.filterInt(n >>> 12 & m);
        this.unpackBuf[28] = filter.filterInt(n >>>  1 & m);
        c = n << 10 & m;

        n = src.get();
        this.unpackBuf[29] = filter.filterInt(c | n >>> 22);
        this.unpackBuf[30] = filter.filterInt(n >>> 11 & m);
        this.unpackBuf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(this.unpackBuf);
    }

    public void unpackAny(
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

    public static byte[] toBytes(int[] src) {
        return (new IntBitPacking()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntBitPacking()).decompress(src);
    }
}
