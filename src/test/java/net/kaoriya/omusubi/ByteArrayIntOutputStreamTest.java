package net.kaoriya.omusubi;

import org.junit.Test;
import static org.junit.Assert.*;

public class ByteArrayIntOutputStreamTest
{
    @Test
    public void writeInt() {
        ByteArrayIntOutputStream s = new ByteArrayIntOutputStream();
        s.write(0);
        s.write(1);
        assertArrayEquals(new byte[] {
            0, 0, 0, 0,
            0, 0, 0, 1,
        }, s.toByteArray());
    }

    @Test
    public void writeIntArray() {
        ByteArrayIntOutputStream s = new ByteArrayIntOutputStream();
        s.write(new int[] { 0, 1, 2, 3 });
        assertArrayEquals(new byte[] {
            0, 0, 0, 0,
            0, 0, 0, 1,
            0, 0, 0, 2,
            0, 0, 0, 3,
        }, s.toByteArray());
    }

    @Test
    public void writeIntArraySub() {
        ByteArrayIntOutputStream s = new ByteArrayIntOutputStream();
        s.write(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, 2, 4);
        assertArrayEquals(new byte[] {
            0, 0, 0, 2,
            0, 0, 0, 3,
            0, 0, 0, 4,
            0, 0, 0, 5,
        }, s.toByteArray());
    }
}
