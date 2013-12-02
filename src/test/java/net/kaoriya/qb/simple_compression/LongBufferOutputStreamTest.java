package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongBufferOutputStreamTest {

    @Test
    public void write() {
        LongBuffer buf = LongBuffer.allocate(10);
        LongBufferOutputStream s = new LongBufferOutputStream(buf);
        s.write(0L);
        s.write(1L);
        s.write(new long[] { 2, 3, 4, 5 });
        s.write(new long[] { 6, 7, 8, 9, 10, 11, 12, 13 }, 2, 4);
        assertArrayEquals(new long[] {
            0, 1, 2, 3, 4, 5, 8, 9, 10, 11
        }, buf.array());
    }

}
