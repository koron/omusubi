package net.kaoriya.omusubi;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.LinkedList;

import net.kaoriya.omusubi.io.IntArrayInputStream;

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
        List<IntAscSDBP.Reader> readers = new LinkedList<IntAscSDBP.Reader>();
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

    @Test
    public void allReadersHaveInt_empty() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][]{
            new int[]{},
        });
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 0));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 1));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 999));
    }

    @Test
    public void allReadersHaveInt_single() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][]{
            new int[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 },
        });
        assertTrue(IntAscSDBP.allReadersHaveInt(r, 0));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 0));
        assertTrue(IntAscSDBP.allReadersHaveInt(r, 1));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 1));
        assertTrue(IntAscSDBP.allReadersHaveInt(r, 2));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 2));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 999));
    }

    @Test
    public void allReadersHaveInt_dual() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][] {
            new int[]{ 0, 2, 4, 6, 8, 10, 12 },
            new int[]{ 0, 3, 6, 9, 12 },
        });
        assertTrue(IntAscSDBP.allReadersHaveInt(r, 0));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 1));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 2));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 3));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 4));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 5));
        assertTrue(IntAscSDBP.allReadersHaveInt(r, 6));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 7));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 8));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 9));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 10));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 11));
        assertTrue(IntAscSDBP.allReadersHaveInt(r, 12));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 13));
        assertFalse(IntAscSDBP.allReadersHaveInt(r, 999));
    }

    @Test
    public void anyReadersHaveInt_empty() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][]{
            new int[]{},
        });
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 0));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 1));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 999));
    }

    @Test
    public void anyReadersHaveInt_single() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][]{
            new int[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 },
        });
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 0));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 0));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 1));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 1));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 2));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 2));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 999));
    }

    @Test
    public void anyReadersHaveInt_dual() {
        List<IntAscSDBP.Reader> r = newReaders(new int[][] {
            new int[]{ 0, 2, 4, 6, 8, 10, 12 },
            new int[]{ 0, 3, 6, 9, 12 },
        });
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 0));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 1));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 2));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 3));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 4));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 5));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 6));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 7));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 8));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 9));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 10));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 11));
        assertTrue(IntAscSDBP.anyReadersHaveInt(r, 12));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 13));
        assertFalse(IntAscSDBP.anyReadersHaveInt(r, 999));
    }

    private void check_toBytes(int[] src, byte[] dst) {
        byte[] compressed = IntAscSDBP.toBytes(src);
        assertArrayEquals(dst, compressed);
        int[] decompressed = IntAscSDBP.fromBytes(dst);
        assertArrayEquals(src, decompressed);
    }

    @Test
    public void toBytes() {
        check_toBytes(
                new int[] { 10, 11, 12, 13, 14, 15, 16, 17, 18, },
                new byte[] {
                    0, 0, 0, 9,
                    0, 0, 0, 10,
                    1, 0, 0, 0,
                    (byte)0xff, 0, 0, 0,
                });
    }

    @Test
    public void toBytes2() {
        check_toBytes(
                new int[] {1, 3, 5, 7, 9},
                new byte[] {
                    0, 0, 0, 5,
                    0, 0, 0, 1,
                    2, 0, 0, 0,
                    (byte)0xaa, 0, 0, 0,
                    0, 0, 0, 0,
                });
    }

    @Test
    public void toBytes3() {
        check_toBytes(
                new int[] {2, 4, 6, 8},
                new byte[] {
                    0, 0, 0, 4,
                    0, 0, 0, 2,
                    2, 0, 0, 0,
                    (byte)0xa8, 0, 0, 0,
                    0, 0, 0, 0,
                });
    }

    @Test
    public void toBytes4() {
        check_toBytes(
                new int[] {3, 6, 9},
                new byte[] {
                    0, 0, 0, 3,
                    0, 0, 0, 3,
                    2, 0, 0, 0,
                    (byte)0xf0, 0, 0, 0,
                    0, 0, 0, 0,
                });
    }

    @Test
    public void union() {
        byte[] set1 = IntAscSDBP.toBytes(new int[] {1, 3, 5, 7, 9});
        byte[] set2 = IntAscSDBP.toBytes(new int[] {2, 4, 6, 8});
        byte[] set3 = IntAscSDBP.toBytes(new int[] {3, 6, 9});

        byte[] compressed = IntAscSDBP.union(set1, set2, set3);
        int[] decompressed = IntAscSDBP.fromBytes(compressed);

        assertArrayEquals(
                new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                decompressed);
    }

    @Test
    public void intersect() {
        byte[] set1 = IntAscSDBP.toBytes(new int[] {1, 2, 3, 4});
        byte[] set2 = IntAscSDBP.toBytes(new int[] {3, 4});
        byte[] set3 = IntAscSDBP.toBytes(new int[] {1, 3, 5});

        byte[] compressed = IntAscSDBP.intersect(set1, set2, set3);
        int[] decompressed = IntAscSDBP.fromBytes(compressed);

        assertArrayEquals(
                new int[] {3},
                decompressed);
    }

    @Test
    public void intersect2() {
        int[] set1 = new int[201];
        int[] set2 = new int[134];
        int[] set3 = new int[67];
        for (int i = 0; i < set1.length; ++i) {
            set1[i] = i * 2;
        }
        for (int i = 0; i < set2.length; ++i) {
            set2[i] = i * 3;
        }
        for (int i = 0; i < set3.length; ++i) {
            set3[i] = i * 6;
        }
        byte compressed[] = IntAscSDBP.intersect(
                IntAscSDBP.toBytes(set1),
                IntAscSDBP.toBytes(set2));
        int[] decompressed = IntAscSDBP.fromBytes(compressed);
        assertArrayEquals(set3, decompressed);
    }

    @Test
    public void difference() {
        byte[] set1 = IntAscSDBP.toBytes(new int[] {1, 2, 3, 4});
        byte[] set2 = IntAscSDBP.toBytes(new int[] {3});
        byte[] set3 = IntAscSDBP.toBytes(new int[] {1, 3, 5});

        byte[] compressed = IntAscSDBP.difference(set1, set2, set3);
        int[] decompressed = IntAscSDBP.fromBytes(compressed);

        assertArrayEquals(
                new int[] {2, 4},
                decompressed);
    }
}
