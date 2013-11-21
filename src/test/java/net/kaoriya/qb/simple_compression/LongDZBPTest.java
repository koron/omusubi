package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

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

    private static void checkCompress(long[] src, long[] dst) {
        LongDZBP codec = new LongDZBP();
        LongBuffer srcBuf = LongBuffer.wrap(src);
        LongBuffer dstBuf = LongBuffer.allocate(dst.length);
        codec.compress(srcBuf, dstBuf);
        assertArrayEquals(dst, dstBuf.array());
    }

    @Test
    public void compress() {
        checkCompress(
                padding(new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 }),
                padding(new long[] { 10, 0x6000000, 0x0820820820828C00L }));
    }

    private static void checkDecompress(long[] src, long[] dst) {
        LongDZBP codec = new LongDZBP();
        codec.setDebug(true);
        LongBuffer srcBuf = LongBuffer.wrap(src);
        LongBuffer dstBuf = LongBuffer.allocate(dst.length);
        codec.decompress(srcBuf, dstBuf);
        assertArrayEquals(dst, dstBuf.array());
    }

    @Test
    public void decompress() {
        checkDecompress(
                new long[] { 10, 0x6000000, 0x0820820820828C00L, 0 },
                padding(new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18 }));
    }
}
