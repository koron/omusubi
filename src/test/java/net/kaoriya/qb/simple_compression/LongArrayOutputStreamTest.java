package net.kaoriya.qb.simple_compression;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongArrayOutputStreamTest
{
    @Test
    public void ctor() {
        LongArrayOutputStream s = new LongArrayOutputStream();
        assertEquals(0, s.count());
        assertArrayEquals(new long[0], s.toLongArray());

        s.write(123);
        s.write(456);
        assertEquals(2, s.count());
        assertArrayEquals(new long[] { 123, 456 }, s.toLongArray());
    }

    @Test
    public void ctor2() {
        LongArrayOutputStream s = new LongArrayOutputStream(5);
        s.write(1);
        s.write(2);
        s.write(3);
        s.write(4);
        s.write(5);
        assertEquals(5, s.count());
        assertArrayEquals(new long[] { 1, 2, 3, 4, 5 }, s.toLongArray());
    }

    @Test
    public void writeArray() {
        LongArrayOutputStream s = new LongArrayOutputStream(3);
        s.write(new long[] { 1, 2, 3, 4, 5 });
        assertEquals(5, s.count());
        assertArrayEquals(new long[] { 1, 2, 3, 4, 5 }, s.toLongArray());
    }
}
