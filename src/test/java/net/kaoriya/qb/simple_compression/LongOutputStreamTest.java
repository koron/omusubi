package net.kaoriya.qb.simple_compression;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongOutputStreamTest {

    @Test
    public void writeLong() {
        final long[] buf = new long[4];

        LongOutputStream s = new LongOutputStream() {
            private int pos = 0;
            public void write(long n) {
                buf[this.pos++] = n;
            }
        };

        s.write(new long[] { 0, 1, 2, 3, 4, 5, 6, 7 }, 2, 4);

        assertArrayEquals(new long[] { 2, 3, 4, 5 }, buf);
    }
}
