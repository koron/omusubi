package net.kaoriya.omusubi.io;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntInputStreamTest {

    public static class ArrayInputStream extends IntInputStream {
        private int[] array;
        private int index;
        public ArrayInputStream(int[] array) {
            this.array = array;
            this.index = 0;
        }
        public Integer read() {
            if (this.index >= this.array.length) {
                return null;
            }
            return Integer.valueOf(this.array[this.index++]);
        }
    }

    @Test
    public void readArray() {
        ArrayInputStream s = new ArrayInputStream(
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        int[] array = new int[10];
        int len = s.read(array);
        assertEquals(10, len);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
    }

    @Test
    public void readArray2() {
        ArrayInputStream s = new ArrayInputStream(
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        int[] array = new int[12];
        int len = s.read(array);
        assertEquals(10, len);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0},
                array);
    }
}
