package net.kaoriya.omusubi;

import java.nio.LongBuffer;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongBitPackingTest
{
    public static long[] randomLongs(Random r, int len) {
        long[] array = new long[len];
        for (int i = 0; i < len; ++i) {
            array[i] = r.nextLong();
        }
        return array;
    }

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
        LongBitPacking packing = new LongBitPacking();
        LongBuffer buf = LongBuffer.allocate(expected.length);
        packing.pack(LongBuffer.wrap(src),
                new LongBufferOutputStream(buf), validBits, src.length);
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
        LongBitPacking packing = new LongBitPacking();
        LongBuffer buf = LongBuffer.allocate(expected.length);
        packing.packAny(LongBuffer.wrap(src),
                new LongBufferOutputStream(buf), validBits, src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void packAny() {
        checkPackAny(new long[] { 1L }, 1, new long[] { 0x8000000000000000L });
    }

    @Test
    public void packAnyEmpty() {
        checkPackAny(new long[0], 1, new long[0]);
    }

    private void checkUnpack(long[] src, int validBits, long[] expected)
    {
        LongBuffer buf = LongBuffer.allocate(expected.length);
        LongBitPacking.unpack(LongBuffer.wrap(src),
                new LongBufferOutputStream(buf), validBits, expected.length);
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

    private static long[] padding(long[] src) {
        return TestUtils.padding(src, 
            LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM, 0);
    }

    private void checkCompress(long[] orig, long[] compressed) {
        LongBitPacking p = new LongBitPacking();

        LongBuffer origBuf = LongBuffer.wrap(orig);
        LongBuffer buf1 = LongBuffer.allocate(compressed.length);
        p.compress(origBuf, new LongBufferOutputStream(buf1));
        assertArrayEquals(compressed, buf1.array());
        buf1.rewind();

        LongBuffer buf2 = LongBuffer.allocate(orig.length);
        p.decompress(buf1, new LongBufferOutputStream(buf2));
        assertArrayEquals(orig, buf2.array());
    }

    private void checkCompressPadded(long[] orig, long[] compressed) {
        checkCompress(padding(orig), compressed);
    }

    @Test
    public void compress() {
        checkCompressPadded(new long[0], new long[] { 0L });
        // TODO: check againt many series of input.
    }

    @Test
    public void getBlockProps() {
        LongBitPacking p1 = new LongBitPacking();
        assertEquals(LongBitPacking.BLOCK_LEN, p1.getBlockLen());
        assertEquals(LongBitPacking.BLOCK_NUM, p1.getBlockNum());
        assertEquals(LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM,
                p1.getBlockSize());

        LongBitPacking p2 = new LongBitPacking(123, 456);
        assertEquals(123, p2.getBlockLen());
        assertEquals(456, p2.getBlockNum());
        assertEquals(56088, p2.getBlockSize());
    }

    @Test
    public void checkBytes() {
        long[] input = randomLongs(new Random(),
                LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM * 100);
        byte[] tmp = LongBitPacking.toBytes(input);
        long[] output = LongBitPacking.fromBytes(tmp);
        assertArrayEquals(input, output);
    }
}
