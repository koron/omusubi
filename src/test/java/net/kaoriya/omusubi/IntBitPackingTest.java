package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class IntBitPackingTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        IntBitPacking packing = new IntBitPacking();
        IntBuffer buf = IntBuffer.allocate(expected.length);
        packing.unpack(IntBuffer.wrap(src),
                new IntBufferOutputStream(buf), validBits, expected.length);
        assertArrayEquals(expected, buf.array());
    }

    @Test
    public void unpack0() {
        checkUnpack(new int[0], 0, new int[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        });
    }

    @Test
    public void unpack() {
        checkUnpack(new int[] { 0x01234567 }, 1, new int[] {
            0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1,
            0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1,
        });
        /*
        checkUnpack(new int[] {
            0xffffffff, 0xffffffff, 0xffffffff,
        }, 24, new int[] {
            0xffffff, 0xffffff, 0xffffff,
        });
        */
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

    private int[] randomInts(Random r, int n) {
        int[] d = new int[n];
        for (int i = 0; i < d.length; ++i) {
            d[i] = r.nextInt();
        }
        return d;
    }

    /**
     * pack with packN(), compare with result of packAny(), then unpackN().
     */
    private int[] packAndUnpack(IntBitPacking p, int[] src, int validBits) {
        // pack.
        IntArrayOutputStream packed = new IntArrayOutputStream(validBits);
        p.pack(IntBuffer.wrap(src), packed, validBits, src.length);

        // compare with packAny.
        IntArrayOutputStream packedEx = new IntArrayOutputStream(validBits);
        p.packAny(IntBuffer.wrap(src), packedEx, validBits, src.length);
        assertArrayEquals("pack() returns different from packAny()",
                packedEx.toIntArray(), packed.toIntArray());

        // unpack.
        IntArrayOutputStream unpacked = new IntArrayOutputStream(src.length);
        p.unpack(IntBuffer.wrap(packed.toIntArray()), unpacked, validBits,
                src.length);

        // compare with unpackAny.
        IntArrayOutputStream unpackedEx = new IntArrayOutputStream(src.length);
        p.unpackAny(IntBuffer.wrap(packed.toIntArray()), unpackedEx, validBits,
                src.length);
        assertArrayEquals("unpack() returns different from unpackAny()",
                unpackedEx.toIntArray(), unpacked.toIntArray());

        return unpacked.toIntArray();
    }

    /**
     * allBits test all validBits for pack()/unpack().
     */
    @Test
    public void allBits() {
        IntBitPacking p = new IntBitPacking();
        Random r = new Random();
        int[] masks = IntBitPacking.newMasks();
        for (int i = 0; i <= 32; ++i) {
            for (int j = 0; j < 100; ++j) {
                // generate random data with bits mask.
                int[] indata = randomInts(r, IntBitPacking.BLOCK_LEN);
                for (int k = 0; k < indata.length; ++k) {
                    indata[k] &= masks[i];
                }
                // pack and unpack with IntBitPacking
                int[] outdata = packAndUnpack(p, indata, i);
                // check equality.
                // FIXME: better outputs.
                assertArrayEquals(indata, outdata);
            }
        }
    }

    @Test
    public void checkBytes() {
        int[] input = randomInts(new Random(),
                IntBitPacking.BLOCK_LEN * IntBitPacking.BLOCK_NUM * 100);
        byte[] tmp = IntBitPacking.toBytes(input);
        int[] output = IntBitPacking.fromBytes(tmp);
        assertArrayEquals(input, output);
    }

    @Test
    public void packInvalidBits() {
        IntBitPacking codec = new IntBitPacking();
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Invalid bits: -1");
        codec.pack(null, null, -1, 0);
    }

    @Test
    public void unpackInvalidBits() {
        IntBitPacking codec = new IntBitPacking();
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Invalid bits: -1");
        codec.unpack(null, null, -1, 0);
    }
}
