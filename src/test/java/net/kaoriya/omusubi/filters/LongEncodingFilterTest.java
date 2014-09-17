package net.kaoriya.omusubi.filters;

import org.junit.Test;
import static org.junit.Assert.*;

import net.kaoriya.omusubi.encodings.LongEncoder;

public class LongEncodingFilterTest {
    public static class VolatileEncoder extends LongEncoder {
        public VolatileEncoder(long contextValue) {
            super(contextValue);
        }
        public long encodeLong(long value) {
            return this.contextValue = value;
        }
    }

    @Test
    public void resetContext() {
        VolatileEncoder e = new VolatileEncoder(0);
        LongEncodingFilter.Factory ff = new LongEncodingFilter.Factory(e);
        LongEncodingFilter f = (LongEncodingFilter)ff.newFilter(10);

        f.filterLong(99);
        assertEquals(99, e.getContextValue());

        f.saveContext();
        f.filterLong(1);
        assertEquals(1, e.getContextValue());

        f.restoreContext();
        assertEquals(99, e.getContextValue());

        f.resetContext();
        assertEquals(99, e.getContextValue());
        f.restoreContext();
        assertEquals(0, e.getContextValue());
    }
}
