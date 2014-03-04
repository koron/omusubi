package net.kaoriya.omusubi;

import java.nio.LongBuffer;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongJustCopyTest
{
    public static long[] randomLongs(Random r, int len) {
        long[] array = new long[len];
        for (int i = 0; i < len; ++i) {
            array[i] = r.nextLong();
        }
        return array;
    }

    @Test
    public void checkBytes() {
        long[] input = randomLongs(new Random(), 10000);
        byte[] tmp = LongJustCopy.toBytes(input);
        long[] output = LongJustCopy.fromBytes(tmp);
        assertArrayEquals(input, output);
    }

    @Test
    public void checkBuffer() {
        long[] input = randomLongs(new Random(), 10000);
        LongJustCopy codec = new LongJustCopy();

        LongArrayOutputStream tmp1 = new LongArrayOutputStream(input.length);
        codec.compress(LongBuffer.wrap(input), tmp1);
        assertArrayEquals(input, tmp1.toLongArray());

        LongArrayOutputStream tmp2 = new LongArrayOutputStream(input.length);
        codec.decompress(LongBuffer.wrap(tmp1.toLongArray()), tmp2);
        assertArrayEquals(input, tmp2.toLongArray());
    }
}
