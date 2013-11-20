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
}
