package net.kaoriya.omusubi.encodings;

import org.junit.Test;
import static org.junit.Assert.*;

public class LongEncoderTest {

    public static class MultiplyEncoder extends LongEncoder {
        public MultiplyEncoder(long contextValue) {
            super(contextValue);
        }
        public long encodeLong(long value) {
            return value * this.contextValue;
        }
    }

    @Test
    public void encodeArray() {
        long[] src = new long[]{0, 1, 2, 3, 4, 5};
        long[] dst = new MultiplyEncoder(2).encodeArray(src);
        assertArrayEquals(new long[]{0, 2, 4, 6, 8, 10}, dst);
    }
}
