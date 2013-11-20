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
}
