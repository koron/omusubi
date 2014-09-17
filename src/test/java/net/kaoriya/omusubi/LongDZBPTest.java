package net.kaoriya.omusubi;

import java.nio.LongBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

import net.kaoriya.omusubi.io.LongBufferOutputStream;
import net.kaoriya.omusubi.packers.LongBitPacking;

public class LongDZBPTest
{
    @Test
    public void ctor() {
        LongDZBP p = new LongDZBP();
        assertNotNull(p.getBitPacking());

        LongBitPacking bitPack = new LongBitPacking();
        LongDZBP p2 = new LongDZBP(bitPack);
        assertEquals(bitPack, p2.getBitPacking());
    }

    @Test
    public void encodeFilter() {
        LongDZBP.DZEncodeFilter ef = new LongDZBP.DZEncodeFilter();
        assertEquals(2, ef.filterLong(1));
        assertEquals(1, ef.getContextValue());
        ef.saveContext();
        assertEquals(18, ef.filterLong(10));
        assertEquals(10, ef.getContextValue());

        ef.restoreContext();
        assertEquals(1, ef.getContextValue());

        ef.resetContext();
        assertEquals(0, ef.getContextValue());
    }

    @Test
    public void decodeFilter() {
        LongDZBP.DZDecodeFilter df = new LongDZBP.DZDecodeFilter();
        assertEquals(1, df.filterLong(2));
        assertEquals(1, df.getContextValue());
        df.saveContext();
        assertEquals(10, df.filterLong(18));
        assertEquals(10, df.getContextValue());

        df.restoreContext();
        assertEquals(1, df.getContextValue());

        df.resetContext();
        assertEquals(0, df.getContextValue());
    }

    @Test
    public void debug() {
        LongDZBP codec = new LongDZBP();
        assertFalse(codec.getDebug());
        codec.setDebug(true);
        assertTrue(codec.getDebug());
        codec.setDebug(false);
        assertFalse(codec.getDebug());
    }

    private static long[] padding(long[] src) {
        return TestUtils.padding(src, 
            LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM, 1);
    }

    private static void checkCompress(
            LongDZBP codec,
            long[] src,
            long[] dst)
    {
        LongBuffer srcBuf = LongBuffer.wrap(src);
        LongBuffer dstBuf = LongBuffer.allocate(dst.length);
        codec.compress(srcBuf, new LongBufferOutputStream(dstBuf));
        assertArrayEquals(dst, dstBuf.array());
    }

    @Test
    public void compress() {
        checkCompress(new LongDZBP(), new long[0], new long[0]);

        checkCompress(
                new LongDZBP(),
                new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 },
                new long[] { 9, 10, 0x2000000, 0xaaaa000000000000L });

        checkCompress(
                new LongDZBP(),
                padding(new long[] { 0 }),
                new long[] {
                    LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM + 1,
                    0, 0
                });
    }

    private static void checkDecompress(
            LongDZBP codec,
            long[] src,
            long[] dst)
    {
        LongBuffer srcBuf = LongBuffer.wrap(src);
        LongBuffer dstBuf = LongBuffer.allocate(dst.length);
        codec.decompress(srcBuf, new LongBufferOutputStream(dstBuf));
        assertArrayEquals(dst, dstBuf.array());
    }

    @Test
    public void decompress() {
        checkDecompress(new LongDZBP(), new long[0], new long[0]);

        checkDecompress(
                new LongDZBP(),
                new long[] { 9, 10, 0x2000000, 0xaaaa000000000000L },
                new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 });

        checkDecompress(
                new LongDZBP(),
                new long[] {
                    LongBitPacking.BLOCK_LEN * LongBitPacking.BLOCK_NUM + 1,
                    0, 0
                },
                padding(new long[] { 0 }));
    }

    @Test
    public void utilities() {
        long[] src = new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        byte[] dst = new byte[] {
            0, 0, 0, 0, 0, 0, 0, 9,
            0, 0, 0, 0, 0, 0, 0, 10,
            0, 0, 0, 0, 2, 0, 0, 0,
            (byte)0xaa, (byte)0xaa, 0, 0, 0, 0, 0, 0,
        };

        // test class method.
        LongDZBP codec = new LongDZBP();
        byte[] compressed = codec.compress(src);
        assertArrayEquals(dst, compressed);
        long[] decompressed = codec.decompress(dst);
        assertArrayEquals(src, decompressed);

        // test static methods.
        byte[] compressed2 = LongDZBP.toBytes(src);
        assertArrayEquals(dst, compressed2);
        long[] decompressed2 = LongDZBP.fromBytes(dst);
        assertArrayEquals(src, decompressed2);
    }

    @Test
    public void bigFew() {
        LongDZBP codec = new LongDZBP();

        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 1,
            0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
        }, codec.compress(new long[] {
            Long.MAX_VALUE,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            0, 0, 0, 0, 2, 0, 0, 0,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            Long.MAX_VALUE, Long.MAX_VALUE + 1,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            0, 0, 0, 0, 1, 0, 0, 0,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            Long.MAX_VALUE, Long.MAX_VALUE - 1,
        }));

        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 1,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            Long.MIN_VALUE,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 2, 0, 0, 0,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            Long.MIN_VALUE, Long.MIN_VALUE + 1,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            Long.MIN_VALUE, Long.MIN_VALUE - 1,
        }));

        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 1,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
        }, codec.compress(new long[] {
            -1,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            0, 0, 0, 0, 2, 0, 0, 0,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            -1, 0,
        }));
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            0, 0, 0, 0, 1, 0, 0, 0,
            (byte)0x80, 0, 0, 0, 0, 0, 0, 0,
        }, codec.compress(new long[] {
            -1, -2,
        }));
    }

    @Test
    public void decodeLength() {
        long[] src = new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        byte[] dst = LongDZBP.toBytes(src);

        assertEquals(9, LongDZBP.decodeLength(dst));
    }

    @Test
    public void decodeFirstValue() {
        long[] src = new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        byte[] dst = LongDZBP.toBytes(src);

        assertEquals(10, LongDZBP.decodeFirstValue(dst));
    }
}
