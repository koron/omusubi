package net.kaoriya.omusubi.filters;

import org.junit.Test;
import static org.junit.Assert.*;

public class ThroughIntFilterTest {

    @Test
    public void check() {
        ThroughIntFilter f = new ThroughIntFilter();
        assertEquals(0, f.filterInt(0));
        assertEquals(10, f.filterInt(10));

        f.saveContext();
        assertEquals(0, f.filterInt(0));
        assertEquals(10, f.filterInt(10));
        f.restoreContext();
        assertEquals(0, f.filterInt(0));
        assertEquals(10, f.filterInt(10));
        f.resetContext();
        assertEquals(0, f.filterInt(0));
        assertEquals(10, f.filterInt(10));
    }

}
