package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongBitPackingTest
{
    @Test
    public void newMasks() {
        long[] masks = LongBitPacking.newMasks();
        for (int i = 0; i < masks.length; ++i) {
            long m = masks[i];
            assertEquals(64 - i, Long.numberOfLeadingZeros(m));
            assertEquals(i, Long.bitCount(m));
        }
    }

    private void checkCountMaxBits(long[] src, int expected) {
        LongBuffer buf = LongBuffer.wrap(src);
        assertEquals(expected, LongBitPacking.countMaxBits(buf, src.length));
    }

    @Test
    public void countMaxBits() {
        checkCountMaxBits(new long[] {
            0L,
        }, 0);
        checkCountMaxBits(new long[] {
            0L, 1L,
        }, 1);
        checkCountMaxBits(new long[] {
            0L, 1L, 2L,
        }, 2);
        checkCountMaxBits(new long[] {
            0L, 1L, 2L, 4L,
        }, 3);
        checkCountMaxBits(new long[] {
            0L, 1L, 2L, 4L, 8L,
        }, 4);

        checkCountMaxBits(new long[] {
            0L, 1L, 2L, 4L, 8L, 0xffffffffffffffffL,
        }, 64);

        checkCountMaxBits(new long[] {
            0x80000000L, 0x80000000L, 0x80000000L, 0x80000000L,
        }, 32);
    }

    private void checkPack(long[] src, int validBits, long[] expected) {
        LongBuffer buf = LongBuffer.allocate(expected.length);
        LongBitPacking.pack(LongBuffer.wrap(src), buf, validBits, src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void pack() {
        long[] indata = new long[64];
        for (int i = 0; i < indata.length; ++i) {
            indata[i] = 0x5555555555555555L;
        }

        checkPack(indata, 0, new long[0]);

        checkPack(indata, 1, new long[] {
            0xffffffffffffffffL,
        });
        checkPack(indata, 2, new long[] {
            0x5555555555555555L,
            0x5555555555555555L,
        });
        checkPack(indata, 3, new long[] {
            0xb6db6db6db6db6dbL,
            0x6db6db6db6db6db6L,
            0xdb6db6db6db6db6dL,
        });

        checkPack(indata, 4, new long[] {
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
        });
        checkPack(indata, 6, new long[] {
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
        });
        checkPack(indata, 8, new long[] {
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
            0x5555555555555555L,
        });
    }

    private void checkPackAny(long[] src, int validBits, long[] expected) {
        LongBuffer buf = LongBuffer.allocate(expected.length);
        LongBitPacking.packAny(LongBuffer.wrap(src), buf, validBits,
                src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void packAny() {
        checkPackAny(new long[] { 1L }, 1, new long[] { 0x8000000000000000L });
    }

    private void checkUnpack(long[] src, int validBits, long[] expected)
    {
        LongBuffer buf = LongBuffer.allocate(expected.length);
        LongBitPacking.unpack(LongBuffer.wrap(src), buf, validBits,
                expected.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void unpack() {
        checkUnpack(new long[0], 0, new long[] {
            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
        });
        checkUnpack(new long[] { 0x0123456789abcdefL }, 1, new long[] {
            0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1,
            0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1,
            1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1,
            1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1,
        });
        checkUnpack(new long[] {
            0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL,
        }, 48, new long[] {
            0x0000ffffffffffffL, 0x0000ffffffffffffL, 0x0000ffffffffffffL,
            0x0000ffffffffffffL,
        });
    }

    private long[] padding(long[] src) {
        final int chunkLen =
            LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM;
        int over = src.length % chunkLen;
        if (src.length > 0 && over == 0) {
            return src;
        }

        long[] dst = new long[src.length - over + chunkLen];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    private void checkCompress(long[] orig, long[] compressed) {
        LongBitPacking p = new LongBitPacking();

        LongBuffer origBuf = LongBuffer.wrap(orig);
        LongBuffer buf1 = LongBuffer.allocate(compressed.length);
        p.compress(origBuf, buf1);
        assertArrayEquals(compressed, buf1.array());
        buf1.rewind();

        LongBuffer buf2 = LongBuffer.allocate(orig.length);
        p.decompress(buf1, buf2);
        assertArrayEquals(orig, buf2.array());
    }

    private void checkCompressPadded(long[] orig, long[] compressed) {
        checkCompress(padding(orig), compressed);
    }

    @Test
    public void compress() {
        checkCompressPadded(new long[0], new long[] { 0L });
    }
}
