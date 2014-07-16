package net.kaoriya.omusubi;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.LinkedList;

public class IntAscSDBPTest
{
    @Test
    public void reader() {
        IntAscSDBP.Reader r = new IntAscSDBP.Reader(new IntArrayInputStream(
                    new int[]{ 0, 10, 20, 100 }));
        assertNull(r.last());
        assertEquals(Integer.valueOf(0), r.read());
        assertEquals(Integer.valueOf(0), r.last());
        assertEquals(Integer.valueOf(10), r.read());
        assertEquals(Integer.valueOf(10), r.last());
        assertEquals(Integer.valueOf(20), r.read());
        assertEquals(Integer.valueOf(20), r.last());
        assertEquals(Integer.valueOf(100), r.read());
        assertEquals(Integer.valueOf(100), r.last());
        assertNull(r.read());
        assertNull(r.last());
        assertNull(r.read());
        assertNull(r.last());
    }

    static List<IntAscSDBP.Reader> newReaders(int[][] dataSet) {
        List<IntAscSDBP.Reader> readers = new LinkedList<>();
        for (int[] data : dataSet) {
            IntAscSDBP.Reader r = new IntAscSDBP.Reader(
                    new IntArrayInputStream(data));
            r.read();
            readers.add(r);
        }
        return readers;
    }

    @Test
    public void fetchMinInt_single() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][] {
            new int[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 },
        });
        assertEquals(1, r.size());
        assertEquals(Integer.valueOf(0), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(1), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(2), IntAscSDBP.fetchMinimumInt(r));
        assertNull(IntAscSDBP.fetchMinimumInt(r));
        assertNull(IntAscSDBP.fetchMinimumInt(r));
    }

    @Test
    public void fetchMinInt_empty_single() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][] {
            new int[]{},
        });
        assertNull(IntAscSDBP.fetchMinimumInt(r));
        assertNull(IntAscSDBP.fetchMinimumInt(r));
    }

    @Test
    public void fetchMinInt_empty_multi() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][] {
            new int[]{}, new int[]{}, new int[]{}, new int[]{}, new int[]{},
        });
        assertNull(IntAscSDBP.fetchMinimumInt(r));
        assertNull(IntAscSDBP.fetchMinimumInt(r));
    }

    @Test
    public void fetchMinInt_dual() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][] {
            new int[]{ 0, 2, 4, 6, 8 },
            new int[]{ 0, 3, 6, 9 },
        });
        assertEquals(2, r.size());
        assertEquals(Integer.valueOf(0), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(2), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(3), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(4), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(6), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(8), IntAscSDBP.fetchMinimumInt(r));
        assertEquals(Integer.valueOf(9), IntAscSDBP.fetchMinimumInt(r));
        assertNull(IntAscSDBP.fetchMinimumInt(r));
        assertNull(IntAscSDBP.fetchMinimumInt(r));
    }
}
