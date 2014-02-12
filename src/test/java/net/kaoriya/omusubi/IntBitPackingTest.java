package net.kaoriya.omusubi;

import java.nio.IntBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntBitPackingTest
{
    @Test
    public void newMasks() {
        int[] masks = IntBitPacking.newMasks();
        for (int i = 0; i < masks.length; ++i) {
            int m = masks[i];
            assertEquals(32 - i, Integer.numberOfLeadingZeros(m));
            assertEquals(i, Integer.bitCount(m));
        }
    }

    private void checkCountMaxBits(int[] src, int expected) {
        IntBuffer buf = IntBuffer.wrap(src);
        assertEquals(expected, IntBitPacking.countMaxBits(buf, src.length));
    }

    @Test
    public void countMaxBits() {
        checkCountMaxBits(new int[] {
            0,
        }, 0);
        checkCountMaxBits(new int[] {
            0, 1,
        }, 1);
        checkCountMaxBits(new int[] {
            0, 1, 2,
        }, 2);
        checkCountMaxBits(new int[] {
            0, 1, 2, 4,
        }, 3);
        checkCountMaxBits(new int[] {
            0, 1, 2, 4, 8,
        }, 4);

        checkCountMaxBits(new int[] {
            0, 1, 2, 4, 8, 0xffffffff,
        }, 32);

        checkCountMaxBits(new int[] {
            0x80000000, 0x80000000, 0x80000000, 0x80000000,
        }, 32);
    }

    private void checkPack(int[] src, int validBits, int[] expected) {
        IntBitPacking packing = new IntBitPacking();
        IntBuffer buf = IntBuffer.allocate(expected.length);
        packing.pack(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), validBits, src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void pack() {
        int[] indata = new int[32];
        for (int i = 0; i < indata.length; ++i) {
            indata[i] = 0x55555555;
        }

        checkPack(indata, 0, new int[0]);

        checkPack(indata, 1, new int[] {
            0xffffffff,
        });
        checkPack(indata, 2, new int[] {
            0x55555555,
            0x55555555,
        });
        checkPack(indata, 3, new int[] {
            0xb6db6db6,
            0xdb6db6db,
            0x6db6db6d,
        });

        checkPack(indata, 4, new int[] {
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
        });
        checkPack(indata, 6, new int[] {
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
        });
        checkPack(indata, 8, new int[] {
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
            0x55555555,
        });
    }

    private void checkPackAny(int[] src, int validBits, int[] expected) {
        IntBitPacking packing = new IntBitPacking();
        IntBuffer buf = IntBuffer.allocate(expected.length);
        packing.packAny(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), validBits, src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void packAny() {
        checkPackAny(new int[] { 1 }, 1, new int[] { 0x80000000 });
    }

    @Test
    public void packAnyEmpty() {
        checkPackAny(new int[0], 1, new int[0]);
    }

    private void checkUnpack(int[] src, int validBits, int[] expected)
    {
        IntBuffer buf = IntBuffer.allocate(expected.length);
        IntBitPacking.unpack(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), validBits, expected.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void unpack() {
        checkUnpack(new int[0], 0, new int[] {
            0, 0, 0, 0, 0, 0, 0, 0,
        });
        checkUnpack(new int[] { 0x01234567 }, 1, new int[] {
            0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1,
            0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1,
        });
        checkUnpack(new int[] {
            0xffffffff, 0xffffffff, 0xffffffff,
        }, 24, new int[] {
            0xffffff, 0xffffff, 0xffffff,
        });
    }

    private static int[] padding(int[] src) {
        return TestUtils.paddingInt(src, 
            IntBitPacking.BLOCK_LEN * IntBitPacking.BLOCK_NUM, 0);
    }

    private void checkCompress(int[] orig, int[] compressed) {
        IntBitPacking p = new IntBitPacking();

        IntBuffer origBuf = IntBuffer.wrap(orig);
        IntBuffer buf1 = IntBuffer.allocate(compressed.length);
        p.compress(origBuf, new IntBufferOutputStream(buf1));
        assertArrayEquals(compressed, buf1.array());
        buf1.rewind();

        IntBuffer buf2 = IntBuffer.allocate(orig.length);
        p.decompress(buf1, new IntBufferOutputStream(buf2));
        assertArrayEquals(orig, buf2.array());
    }

    private void checkCompressPadded(int[] orig, int[] compressed) {
        checkCompress(padding(orig), compressed);
    }

    @Test
    public void compress() {
        checkCompressPadded(new int[0], new int[] { 0 });
        // TODO: check againt many series of input.
    }

    @Test
    public void getBlockProps() {
        IntBitPacking p1 = new IntBitPacking();
        assertEquals(IntBitPacking.BLOCK_LEN, p1.getBlockLen());
        assertEquals(IntBitPacking.BLOCK_NUM, p1.getBlockNum());
        assertEquals(IntBitPacking.BLOCK_LEN * IntBitPacking.BLOCK_NUM,
                p1.getBlockSize());

        IntBitPacking p2 = new IntBitPacking(123, 456);
        assertEquals(123, p2.getBlockLen());
        assertEquals(456, p2.getBlockNum());
        assertEquals(56088, p2.getBlockSize());
    }

    @Test
    public void debug() {
        IntBitPacking codec = new IntBitPacking();
        assertFalse(codec.getDebug());
        codec.setDebug(true);
        assertTrue(codec.getDebug());
        codec.setDebug(false);
        assertFalse(codec.getDebug());
    }

    private void checkPack10(int[] src) {
        IntBitPacking packing = new IntBitPacking();
        IntArrayOutputStream dst = new IntArrayOutputStream(10);
        packing.packAny(IntBuffer.wrap(src), dst, 10, src.length);
        int[] expected = dst.toIntArray();
        IntBuffer buf = IntBuffer.allocate(10);
        packing.pack10(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void pack10() {
        checkPack10(new int[] {
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256,
        });
        checkPack10(new int[] {
            0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000,
            11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 19000,
            20000, 21000, 22000, 23000, 24000, 25000, 26000, 27000, 28000,
            29000, 30000, 31000,
        });
    }

    private void checkPack11(int[] src) {
        IntBitPacking packing = new IntBitPacking();
        IntArrayOutputStream dst = new IntArrayOutputStream(11);
        packing.packAny(IntBuffer.wrap(src), dst, 11, src.length);
        int[] expected = dst.toIntArray();
        IntBuffer buf = IntBuffer.allocate(11);
        packing.pack11(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void pack11() {
        checkPack11(new int[] {
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256,
        });
        checkPack11(new int[] {
            0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000,
            11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 19000,
            20000, 21000, 22000, 23000, 24000, 25000, 26000, 27000, 28000,
            29000, 30000, 31000,
        });
    }

    private void checkPack12(int[] src) {
        IntBitPacking packing = new IntBitPacking();
        IntArrayOutputStream dst = new IntArrayOutputStream(12);
        packing.packAny(IntBuffer.wrap(src), dst, 12, src.length);
        int[] expected = dst.toIntArray();
        IntBuffer buf = IntBuffer.allocate(12);
        packing.pack12(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), src.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void pack12() {
        checkPack12(new int[] {
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512,
            0, 1, 2, 4, 8, 16, 32, 64, 128, 256,
        });
        checkPack12(new int[] {
            0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000,
            11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 19000,
            20000, 21000, 22000, 23000, 24000, 25000, 26000, 27000, 28000,
            29000, 30000, 31000,
        });
    }
}
