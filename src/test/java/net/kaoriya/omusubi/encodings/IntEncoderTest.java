package net.kaoriya.omusubi.encodings;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntEncoderTest {

    public static class MultiplyEncoder extends IntEncoder {
        public MultiplyEncoder(int contextValue) {
            super(contextValue);
        }
        public int encodeInt(int value) {
            return value * this.contextValue;
        }
    }

    @Test
    public void encodeArray() {
        int[] src = new int[]{0, 1, 2, 3, 4, 5};
        int[] dst = new MultiplyEncoder(2).encodeArray(src);
        assertArrayEquals(new int[]{0, 2, 4, 6, 8, 10}, dst);
    }
}
