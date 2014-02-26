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
            case 1: pack1(src, dst, filter); break;
            case 2: pack2(src, dst, filter); break;
            case 3: pack3(src, dst, filter); break;
            case 4: pack4(src, dst, filter); break;
            case 5: pack5(src, dst, filter); break;
            case 6: pack6(src, dst, filter); break;
            case 7: pack7(src, dst, filter); break;
            case 8: pack8(src, dst, filter); break;
            case 9: pack9(src, dst, filter); break;
            case 10: pack10(src, dst, filter); break;
            case 11: pack11(src, dst, filter); break;
            case 12: pack12(src, dst, filter); break;
            case 13: pack13(src, dst, filter); break;
            case 14: pack14(src, dst, filter); break;
            //case 15: pack15(src, dst, filter); break;
            case 16: pack16(src, dst, filter); break;
            //case 17: pack17(src, dst, filter); break;
            //case 18: pack18(src, dst, filter); break;
            //case 19: pack19(src, dst, filter); break;
            //case 20: pack20(src, dst, filter); break;
            //case 21: pack21(src, dst, filter); break;
            //case 22: pack22(src, dst, filter); break;
            //case 23: pack23(src, dst, filter); break;
            //case 24: pack24(src, dst, filter); break;
            //case 25: pack25(src, dst, filter); break;
            //case 26: pack26(src, dst, filter); break;
            //case 27: pack27(src, dst, filter); break;
            //case 28: pack28(src, dst, filter); break;
            //case 29: pack29(src, dst, filter); break;
            //case 30: pack30(src, dst, filter); break;
            //case 31: pack31(src, dst, filter); break;
            case 32: pack32(src, dst, filter); break;
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

    public void pack5(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[5];
        int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 27 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 3;

        this.packBuf[1] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 1;

        this.packBuf[2] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 4;

        this.packBuf[3] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 2;

        this.packBuf[4] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 5);
    }

    public void pack6(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[6];
        int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 4;
        this.packBuf[1] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 2;
        this.packBuf[2] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[3] =
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 4;
        this.packBuf[4] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 2;
        this.packBuf[5] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 6);
    }

    public void pack7(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[7];
        int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 3;
        this.packBuf[1] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >> 6;
        this.packBuf[2] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >> 2;
        this.packBuf[3] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >> 5;
        this.packBuf[4] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >> 1;
        this.packBuf[5] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[6] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 7);
    }

    public void pack8(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[8];

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[1] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[2] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[3] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[4] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[5] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[6] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[7] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 8);
    }

    public void pack9(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[9];
        int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 4;

        this.packBuf[1] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 8;

        this.packBuf[2] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 3;

        this.packBuf[3] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 7;

        this.packBuf[4] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>> 2;

        this.packBuf[5] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 6;

        this.packBuf[6] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 1;

        this.packBuf[7] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 5;

        this.packBuf[8] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 9);
    }

    public void pack10(
            IntBuffer src,
            IntOutputStream dst,
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

    public void pack13(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[13];
	int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >> 7;
        this.packBuf[1] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >> 1;
        this.packBuf[2] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >> 8;
        this.packBuf[3] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 11 |
            (n = filter.filterInt(src.get()) & m) >> 2;
        this.packBuf[4] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >> 9;
        this.packBuf[5] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >> 3;
        this.packBuf[6] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >> 10;
        this.packBuf[7] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >> 4;
        this.packBuf[8] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >> 11;
        this.packBuf[9] =
            (n << 21) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >> 5;
        this.packBuf[10] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >> 12;
        this.packBuf[11] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >> 6;
        this.packBuf[12] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  13 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 13);
    }


    public void pack14(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[14];
	int n;

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        this.packBuf[1] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 6;
        this.packBuf[2] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>> 2;
        this.packBuf[3] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        this.packBuf[4] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 8;
        this.packBuf[5] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>> 4;
        this.packBuf[6] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m);

        this.packBuf[7] =
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        this.packBuf[8] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 6;
        this.packBuf[9] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>> 2;
        this.packBuf[10] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        this.packBuf[11] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 8;
        this.packBuf[12] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>> 4;
        this.packBuf[13] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 14);
    }

    // TODO: pack15

    public void pack16(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = MASKS[16];

        this.packBuf[0] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[1] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[2] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[3] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[4] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[5] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[6] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[7] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[8] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[9] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[10] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[11] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[12] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[13] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[14] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        this.packBuf[15] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);

        dst.write(this.packBuf, 0, 16);
    }

    // TODO: pack17
    // TODO: pack18
    // TODO: pack19
    // TODO: pack20
    // TODO: pack21
    // TODO: pack22
    // TODO: pack23
    // TODO: pack24
    // TODO: pack25
    // TODO: pack26
    // TODO: pack27
    // TODO: pack28
    // TODO: pack29
    // TODO: pack30
    // TODO: pack31

    public void pack32(
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        this.packBuf[ 0] = filter.filterInt(src.get());
        this.packBuf[ 1] = filter.filterInt(src.get());
        this.packBuf[ 2] = filter.filterInt(src.get());
        this.packBuf[ 3] = filter.filterInt(src.get());
        this.packBuf[ 4] = filter.filterInt(src.get());
        this.packBuf[ 5] = filter.filterInt(src.get());
        this.packBuf[ 6] = filter.filterInt(src.get());
        this.packBuf[ 7] = filter.filterInt(src.get());
        this.packBuf[ 8] = filter.filterInt(src.get());
        this.packBuf[ 9] = filter.filterInt(src.get());
        this.packBuf[10] = filter.filterInt(src.get());
        this.packBuf[11] = filter.filterInt(src.get());
        this.packBuf[12] = filter.filterInt(src.get());
        this.packBuf[13] = filter.filterInt(src.get());
        this.packBuf[14] = filter.filterInt(src.get());
        this.packBuf[15] = filter.filterInt(src.get());
        this.packBuf[16] = filter.filterInt(src.get());
        this.packBuf[17] = filter.filterInt(src.get());
        this.packBuf[18] = filter.filterInt(src.get());
        this.packBuf[19] = filter.filterInt(src.get());
        this.packBuf[20] = filter.filterInt(src.get());
        this.packBuf[21] = filter.filterInt(src.get());
        this.packBuf[22] = filter.filterInt(src.get());
        this.packBuf[23] = filter.filterInt(src.get());
        this.packBuf[24] = filter.filterInt(src.get());
        this.packBuf[25] = filter.filterInt(src.get());
        this.packBuf[26] = filter.filterInt(src.get());
        this.packBuf[27] = filter.filterInt(src.get());
        this.packBuf[28] = filter.filterInt(src.get());
        this.packBuf[29] = filter.filterInt(src.get());
        this.packBuf[30] = filter.filterInt(src.get());
        this.packBuf[31] = filter.filterInt(src.get());

        dst.write(this.packBuf, 0, 32);
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
