package net.kaoriya.omusubi.filters;

import org.junit.Test;
import static org.junit.Assert.*;

public class ThroughLongFilterTest {

    @Test
    public void check() {
        ThroughLongFilter f = new ThroughLongFilter();
        assertEquals(0, f.filterLong(0));
        assertEquals(10, f.filterLong(10));

        f.saveContext();
        assertEquals(0, f.filterLong(0));
        assertEquals(10, f.filterLong(10));
        f.restoreContext();
        assertEquals(0, f.filterLong(0));
        assertEquals(10, f.filterLong(10));
        f.resetContext();
        assertEquals(0, f.filterLong(0));
        assertEquals(10, f.filterLong(10));
    }

}
