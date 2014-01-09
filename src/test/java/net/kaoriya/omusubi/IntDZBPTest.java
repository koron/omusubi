package net.kaoriya.omusubi;

import java.nio.IntBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntDZBPTest
{
    @Test
    public void ctor() {
        IntDZBP p = new IntDZBP();
        assertNotNull(p.getBitPacking());

        IntBitPacking bitPack = new IntBitPacking();
        IntDZBP p2 = new IntDZBP(bitPack);
        assertEquals(bitPack, p2.getBitPacking());
    }

    @Test
    public void encodeFilter() {
        IntDZBP.DZEncodeFilter ef = new IntDZBP.DZEncodeFilter();
        assertEquals(2, ef.filterInt(1));
        assertEquals(1, ef.getContextValue());
        ef.saveContext();
        assertEquals(18, ef.filterInt(10));
        assertEquals(10, ef.getContextValue());

        ef.restoreContext();
        assertEquals(1, ef.getContextValue());

        ef.resetContext();
        assertEquals(0, ef.getContextValue());
    }

    @Test
    public void decodeFilter() {
        IntDZBP.DZDecodeFilter df = new IntDZBP.DZDecodeFilter();
        assertEquals(1, df.filterInt(2));
        assertEquals(1, df.getContextValue());
        df.saveContext();
        assertEquals(10, df.filterInt(18));
        assertEquals(10, df.getContextValue());

        df.restoreContext();
        assertEquals(1, df.getContextValue());

        df.resetContext();
        assertEquals(0, df.getContextValue());
    }

    @Test
    public void debug() {
        IntDZBP codec = new IntDZBP();
        assertFalse(codec.getDebug());
        codec.setDebug(true);
        assertTrue(codec.getDebug());
        codec.setDebug(false);
        assertFalse(codec.getDebug());
    }

    private static int[] padding(int[] src) {
        return TestUtils.paddingInt(src, 
            IntBitPacking.BLOCK_LEN * IntBitPacking.BLOCK_NUM, 1);
    }

    private static void checkCompress(
            IntDZBP codec,
            int[] src,
            int[] dst)
    {
        IntBuffer srcBuf = IntBuffer.wrap(src);
        IntBuffer dstBuf = IntBuffer.allocate(dst.length);
        codec.compress(srcBuf, new IntBufferOutputStream(dstBuf));
        assertArrayEquals(dst, dstBuf.array());
    }

    @Test
    public void compress() {
        checkCompress(new IntDZBP(), new int[0], new int[0]);

        checkCompress(
                new IntDZBP(),
                new int[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 },
                new int[] { 9, 10, 0x2000000, 0xaaaa0000, 0 });

        checkCompress(
                new IntDZBP(),
                padding(new int[] { 0 }),
                new int[] {
                    IntBitPacking.BLOCK_LEN * IntBitPacking.BLOCK_NUM + 1,
                    0, 0
                });
    }

    private static void checkDecompress(
            IntDZBP codec,
            int[] src,
            int[] dst)
    {
        IntBuffer srcBuf = IntBuffer.wrap(src);
        IntBuffer dstBuf = IntBuffer.allocate(dst.length);
        codec.decompress(srcBuf, new IntBufferOutputStream(dstBuf));
        TestUtils.dumpInt("expected", dst);
        TestUtils.dumpInt("actually", dst);
        assertArrayEquals(dst, dstBuf.array());
    }

    @Test
    public void decompress() {
        checkDecompress(new IntDZBP(), new int[0], new int[0]);

        checkDecompress(
                new IntDZBP(),
                new int[] { 9, 10, 0x2000000, 0xaaaa0000, 0 },
                new int[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 });

        checkDecompress(
                new IntDZBP(),
                new int[] {
                    IntBitPacking.BLOCK_LEN * IntBitPacking.BLOCK_NUM + 1,
                    0, 0
                },
                padding(new int[] { 0 }));
    }

    @Test
    public void utilities() {
        int[] src = new int[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        byte[] dst = new byte[] {
            0, 0, 0, 9,
            0, 0, 0, 10,
            2, 0, 0, 0,
            (byte)0xaa, (byte)0xaa, 0, 0,
            0, 0, 0, 0,
        };

        // test class method.
        IntDZBP codec = new IntDZBP();
        byte[] compressed = codec.compress(src);
        assertArrayEquals(dst, compressed);
        int[] decompressed = codec.decompress(dst);
        assertArrayEquals(src, decompressed);

        // test static methods.
        byte[] compressed2 = IntDZBP.toBytes(src);
        assertArrayEquals(dst, compressed2);
        int[] decompressed2 = IntDZBP.fromBytes(dst);
        assertArrayEquals(src, decompressed2);
    }

    @Test
    public void bigFew() {
        IntDZBP codec = new IntDZBP();

        assertArrayEquals(new byte[] {
            0, 0, 0, 1, 0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
        }, codec.compress(new int[] {
            Integer.MAX_VALUE,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 2, 0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
            2, 0, 0, 0, (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new int[] {
            Integer.MAX_VALUE, Integer.MAX_VALUE + 1,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 2, 0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
            1, 0, 0, 0, (byte)0x80, 0, 0, 0,
        }, codec.compress(new int[] {
            Integer.MAX_VALUE, Integer.MAX_VALUE - 1,
        }));

        assertArrayEquals(new byte[] {
            0, 0, 0, 1, (byte)0x80, 0, 0, 0,
        }, codec.compress(new int[] {
            Integer.MIN_VALUE,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 2, (byte)0x80, 0, 0, 0,
            2, 0, 0, 0, (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE + 1,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 2, (byte)0x80, 0, 0, 0,
            1, 0, 0, 0, (byte)0x80, 0, 0, 0,
        }, codec.compress(new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE - 1,
        }));

        assertArrayEquals(new byte[] {
            0, 0, 0, 1, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
        }, codec.compress(new int[] {
            -1,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 2, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            2, 0, 0, 0, (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new int[] {
            -1, 0,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 2, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            1, 0, 0, 0, (byte)0x80, 0, 0, 0,
        }, codec.compress(new int[] {
            -1, -2,
        }));
    }
}
