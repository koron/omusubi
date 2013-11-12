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
}
