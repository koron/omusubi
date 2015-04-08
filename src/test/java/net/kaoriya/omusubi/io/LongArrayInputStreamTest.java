package net.kaoriya.omusubi.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongArrayInputStreamTest
{
    @Test
    public void basic() {
        LongArrayInputStream s = new LongArrayInputStream(
                new long[]{ 0, 1, 2, 3 });
        assertEquals(Long.valueOf(0), s.read());
        assertEquals(Long.valueOf(1), s.read());
        assertEquals(Long.valueOf(2), s.read());
        assertEquals(Long.valueOf(3), s.read());
        assertNull(s.read());
    }
}
