package net.kaoriya.omusubi.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongInputStreamTest {

    public static class ArrayInputStream extends LongInputStream {
        private long[] array;
        private int index;
        public ArrayInputStream(long[] array) {
            this.array = array;
            this.index = 0;
        }
        public Long read() {
            if (this.index >= this.array.length) {
                return null;
            }
            return Long.valueOf(this.array[this.index++]);
        }
    }

    @Test
    public void readArray() {
        ArrayInputStream s = new ArrayInputStream(
                new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        long[] array = new long[10];
        int len = s.read(array);
        assertEquals(10, len);
        assertArrayEquals(new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
    }

    @Test
    public void readArray2() {
        ArrayInputStream s = new ArrayInputStream(
                new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        long[] array = new long[12];
        int len = s.read(array);
        assertEquals(10, len);
        assertArrayEquals(new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0},
                array);
    }
}
