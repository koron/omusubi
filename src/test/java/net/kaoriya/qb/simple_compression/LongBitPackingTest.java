package net.kaoriya.qb.simple_compression;

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
}
