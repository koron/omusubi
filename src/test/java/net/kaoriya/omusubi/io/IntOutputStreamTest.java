package net.kaoriya.omusubi.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntOutputStreamTest {

    @Test
    public void writeInt() {
        final int[] buf = new int[4];

        IntOutputStream s = new IntOutputStream() {
            private int pos = 0;
            public void write(int n) {
                buf[this.pos++] = n;
            }
        };

        s.write(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, 2, 4);

        assertArrayEquals(new int[] { 2, 3, 4, 5 }, buf);
    }
}
