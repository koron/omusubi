package net.kaoriya.omusubi.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class ByteArrayLongOutputStreamTest
{
    @Test
    public void writeLong() {
        ByteArrayLongOutputStream s = new ByteArrayLongOutputStream();
        s.write(0L);
        s.write(1L);
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1,
        }, s.toByteArray());
    }

    @Test
    public void writeLongArray() {
        ByteArrayLongOutputStream s = new ByteArrayLongOutputStream();
        s.write(new long[] { 0, 1, 2, 3 });
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 3,
        }, s.toByteArray());
    }

    @Test
    public void writeLongArraySub() {
        ByteArrayLongOutputStream s = new ByteArrayLongOutputStream();
        s.write(new long[] { 0, 1, 2, 3, 4, 5, 6, 7 }, 2, 4);
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 4,
            0, 0, 0, 0, 0, 0, 0, 5,
        }, s.toByteArray());
    }

    @Test
    public void flushWhenWriteAValue() {
        ByteArrayLongOutputStream s = new ByteArrayLongOutputStream(32, 4);
        s.write(2);
        s.write(3);
        s.write(4);
        s.write(5);
        s.write(6);
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 4,
            0, 0, 0, 0, 0, 0, 0, 5,
            0, 0, 0, 0, 0, 0, 0, 6,
        }, s.toByteArray());
    }

    @Test
    public void flushWhenWriteArray() {
        ByteArrayLongOutputStream s = new ByteArrayLongOutputStream(32, 4);
        s.write(new long[] { 0, 1, 2, 3, 4 });
        assertArrayEquals(new byte[] {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 0, 0, 2,
            0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 0, 4,
        }, s.toByteArray());
    }
}
