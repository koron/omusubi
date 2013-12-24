package net.kaoriya.omusubi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class IntArrayOutputStreamTest
{
    @Test
    public void ctor() {
        IntArrayOutputStream s = new IntArrayOutputStream();
        assertEquals(0, s.count());
        assertArrayEquals(new int[0], s.toIntArray());

        s.write(123);
        s.write(456);
        assertEquals(2, s.count());
        assertArrayEquals(new int[] { 123, 456 }, s.toIntArray());
    }

    @Test
    public void ctor2() {
        IntArrayOutputStream s = new IntArrayOutputStream(5);
        s.write(1);
        s.write(2);
        s.write(3);
        s.write(4);
        s.write(5);
        assertEquals(5, s.count());
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, s.toIntArray());
    }

    @Test
    public void writeArray() {
        IntArrayOutputStream s = new IntArrayOutputStream(3);
        s.write(new int[] { 1, 2, 3, 4, 5 });
        assertEquals(5, s.count());
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, s.toIntArray());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void calcNewSize_minus() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Required length was minus");
        IntArrayOutputStream.calcNewSize(0, -1, 10);
    }

    @Test
    public void calcNewSize_overflow() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Buffer overflow");
        IntArrayOutputStream.calcNewSize(0x40000000, 0x3fffffff, 0x40000000);
    }
}
