package net.kaoriya.omusubi.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntArrayInputStreamTest
{
    @Test
    public void basic() {
        IntArrayInputStream s = new IntArrayInputStream(
                new int[]{ 0, 10, 20, 100 });

        assertEquals(Integer.valueOf(0), s.read());
        assertEquals(Integer.valueOf(10), s.read());
        assertEquals(Integer.valueOf(20), s.read());
        assertEquals(Integer.valueOf(100), s.read());
    }
}
