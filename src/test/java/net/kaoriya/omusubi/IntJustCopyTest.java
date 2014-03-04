package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntJustCopyTest
{
    public static int[] randomInts(Random r, int len) {
        int[] array = new int[len];
        for (int i = 0; i < len; ++i) {
            array[i] = r.nextInt();
        }
        return array;
    }

    @Test
    public void checkBytes() {
        int[] input = randomInts(new Random(), 10000);
        byte[] tmp = IntJustCopy.toBytes(input);
        int[] output = IntJustCopy.fromBytes(tmp);
        assertArrayEquals(input, output);
    }

    @Test
    public void checkBuffer() {
        int[] input = randomInts(new Random(), 10000);
        IntJustCopy codec = new IntJustCopy();

        IntArrayOutputStream tmp1 = new IntArrayOutputStream(input.length);
        codec.compress(IntBuffer.wrap(input), tmp1);
        assertArrayEquals(input, tmp1.toIntArray());

        IntArrayOutputStream tmp2 = new IntArrayOutputStream(input.length);
        codec.decompress(IntBuffer.wrap(tmp1.toIntArray()), tmp2);
        assertArrayEquals(input, tmp2.toIntArray());
    }
}
