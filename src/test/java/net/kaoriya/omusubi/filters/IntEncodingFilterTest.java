package net.kaoriya.omusubi.filters;

import org.junit.Test;
import static org.junit.Assert.*;

import net.kaoriya.omusubi.encodings.IntEncoder;

public class IntEncodingFilterTest {
    public static class VolatileEncoder extends IntEncoder {
        public VolatileEncoder(int contextValue) {
            super(contextValue);
        }
        public int encodeInt(int value) {
            return this.contextValue = value;
        }
    }

    @Test
    public void resetContext() {
        VolatileEncoder e = new VolatileEncoder(0);
        IntEncodingFilter.Factory ff = new IntEncodingFilter.Factory(e);
        IntEncodingFilter f = (IntEncodingFilter)ff.newFilter(10);

        f.filterInt(99);
        assertEquals(99, e.getContextValue());

        f.saveContext();
        f.filterInt(1);
        assertEquals(1, e.getContextValue());

        f.restoreContext();
        assertEquals(99, e.getContextValue());

        f.resetContext();
        assertEquals(99, e.getContextValue());
        f.restoreContext();
        assertEquals(0, e.getContextValue());
    }
}
