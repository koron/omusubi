package net.kaoriya.omusubi;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.LinkedList;

import net.kaoriya.omusubi.io.LongArrayInputStream;

public class LongAscSDBPTest
{
    @Test
    public void reader() {
        LongAscSDBP.Reader r = new LongAscSDBP.Reader(new LongArrayInputStream(
                    new long[]{ 0, 10, 20, 100 }));
        assertNull(r.last());
        assertEquals(Long.valueOf(0), r.read());
        assertEquals(Long.valueOf(0), r.last());
        assertEquals(Long.valueOf(10), r.read());
        assertEquals(Long.valueOf(10), r.last());
        assertEquals(Long.valueOf(20), r.read());
        assertEquals(Long.valueOf(20), r.last());
        assertEquals(Long.valueOf(100), r.read());
        assertEquals(Long.valueOf(100), r.last());
        assertNull(r.read());
        assertNull(r.last());
        assertNull(r.read());
        assertNull(r.last());
    }

    static List<LongAscSDBP.Reader> newReaders(long[][] dataSet) {
        List<LongAscSDBP.Reader> readers = new LinkedList<LongAscSDBP.Reader>();
        for (long[] data : dataSet) {
            LongAscSDBP.Reader r = new LongAscSDBP.Reader(
                    new LongArrayInputStream(data));
            r.read();
            readers.add(r);
        }
        return readers;
    }

    @Test
    public void fetchMinLong_single() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][] {
            new long[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 },
        });
        assertEquals(1, r.size());
        assertEquals(Long.valueOf(0), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(1), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(2), LongAscSDBP.fetchMinimumLong(r));
        assertNull(LongAscSDBP.fetchMinimumLong(r));
        assertNull(LongAscSDBP.fetchMinimumLong(r));
    }

    @Test
    public void fetchMinLong_empty_single() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][] {
            new long[]{},
        });
        assertNull(LongAscSDBP.fetchMinimumLong(r));
        assertNull(LongAscSDBP.fetchMinimumLong(r));
    }

    @Test
    public void fetchMinLong_empty_multi() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][] {
            new long[]{}, new long[]{}, new long[]{}, new long[]{}, new long[]{},
        });
        assertNull(LongAscSDBP.fetchMinimumLong(r));
        assertNull(LongAscSDBP.fetchMinimumLong(r));
    }

    @Test
    public void fetchMinLong_dual() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][] {
            new long[]{ 0, 2, 4, 6, 8 },
            new long[]{ 0, 3, 6, 9 },
        });
        assertEquals(2, r.size());
        assertEquals(Long.valueOf(0), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(2), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(3), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(4), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(6), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(8), LongAscSDBP.fetchMinimumLong(r));
        assertEquals(Long.valueOf(9), LongAscSDBP.fetchMinimumLong(r));
        assertNull(LongAscSDBP.fetchMinimumLong(r));
        assertNull(LongAscSDBP.fetchMinimumLong(r));
    }

    @Test
    public void allReadersHaveLong_empty() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][]{
            new long[]{},
        });
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 0));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 1));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 999));
    }

    @Test
    public void allReadersHaveLong_single() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][]{
            new long[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 },
        });
        assertTrue(LongAscSDBP.allReadersHaveLong(r, 0));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 0));
        assertTrue(LongAscSDBP.allReadersHaveLong(r, 1));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 1));
        assertTrue(LongAscSDBP.allReadersHaveLong(r, 2));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 2));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 999));
    }

    @Test
    public void allReadersHaveLong_dual() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][] {
            new long[]{ 0, 2, 4, 6, 8, 10, 12 },
            new long[]{ 0, 3, 6, 9, 12 },
        });
        assertTrue(LongAscSDBP.allReadersHaveLong(r, 0));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 1));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 2));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 3));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 4));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 5));
        assertTrue(LongAscSDBP.allReadersHaveLong(r, 6));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 7));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 8));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 9));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 10));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 11));
        assertTrue(LongAscSDBP.allReadersHaveLong(r, 12));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 13));
        assertFalse(LongAscSDBP.allReadersHaveLong(r, 999));
    }

    @Test
    public void anyReadersHaveLong_empty() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][]{
            new long[]{},
        });
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 0));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 1));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 999));
    }

    @Test
    public void anyReadersHaveLong_single() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][]{
            new long[]{ 0, 0, 0, 1, 1, 1, 2, 2, 2 },
        });
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 0));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 0));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 1));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 1));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 2));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 2));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 999));
    }

    @Test
    public void anyReadersHaveLong_dual() {
        List<LongAscSDBP.Reader> r = newReaders(new long[][] {
            new long[]{ 0, 2, 4, 6, 8, 10, 12 },
            new long[]{ 0, 3, 6, 9, 12 },
        });
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 0));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 1));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 2));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 3));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 4));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 5));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 6));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 7));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 8));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 9));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 10));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 11));
        assertTrue(LongAscSDBP.anyReadersHaveLong(r, 12));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 13));
        assertFalse(LongAscSDBP.anyReadersHaveLong(r, 999));
    }

    private void check_toBytes(long[] src, byte[] dst) {
        byte[] compressed = LongAscSDBP.toBytes(src);
        assertArrayEquals(dst, compressed);
        long[] decompressed = LongAscSDBP.fromBytes(dst);
        assertArrayEquals(src, decompressed);
    }

    @Test
    public void toBytes() {
        check_toBytes(
                new long[] { 10, 11, 12, 13, 14, 15, 16, 17, 18, },
                new byte[] {
                    0, 0, 0, 0, 0, 0, 0, 9,
                    0, 0, 0, 0, 0, 0, 0, 10,
                    0, 0, 0, 0, 1, 0, 0, 0,
                    (byte)0xff, 0, 0, 0, 0, 0, 0, 0,
                });
    }

    @Test
    public void union() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 3, 5, 7, 9});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {2, 4, 6, 8});
        byte[] set3 = LongAscSDBP.toBytes(new long[] {3, 6, 9});

        byte[] compressed = LongAscSDBP.union(set1, set2, set3);
        long[] decompressed = LongAscSDBP.fromBytes(compressed);

        assertArrayEquals(
                new long[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
                decompressed);
    }

    @Test
    public void intersect() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {3, 4});
        byte[] set3 = LongAscSDBP.toBytes(new long[] {1, 3, 5});

        byte[] compressed = LongAscSDBP.intersect(set1, set2, set3);
        long[] decompressed = LongAscSDBP.fromBytes(compressed);

        assertArrayEquals(
                new long[] {3},
                decompressed);
    }

    @Test
    public void intersect2() {
        long[] set1 = new long[201];
        long[] set2 = new long[134];
        long[] set3 = new long[67];
        for (int i = 0; i < set1.length; ++i) {
            set1[i] = i * 2;
        }
        for (int i = 0; i < set2.length; ++i) {
            set2[i] = i * 3;
        }
        for (int i = 0; i < set3.length; ++i) {
            set3[i] = i * 6;
        }
        byte compressed[] = LongAscSDBP.intersect(
                LongAscSDBP.toBytes(set1),
                LongAscSDBP.toBytes(set2));
        long[] decompressed = LongAscSDBP.fromBytes(compressed);
        assertArrayEquals(set3, decompressed);
    }

    @Test
    public void difference() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {3});
        byte[] set3 = LongAscSDBP.toBytes(new long[] {1, 3, 5});

        byte[] compressed = LongAscSDBP.difference(set1, set2, set3);
        long[] decompressed = LongAscSDBP.fromBytes(compressed);

        assertArrayEquals(
                new long[] {2, 4},
                decompressed);
    }

    static Iterator<Long> toIter(long[] array) {
        byte[] b = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        return LongAscSDBP.toIterable(b).iterator();
    }

    @Test
    public void longIteration() {
        Iterator<Long> iter = toIter(new long[] {1, 2, 3, 4});
        assertEquals(Long.valueOf(1), iter.next());
        assertEquals(Long.valueOf(2), iter.next());
        assertEquals(Long.valueOf(3), iter.next());
        assertEquals(Long.valueOf(4), iter.next());
        assertFalse("iteration not finished",
                iter.hasNext());
    }

    static void jaccardCheck(byte[] a, byte[] b, double value) {
        assertEquals(value, LongAscSDBP.jaccard(a, b), 0.01);
    }

    @Test
    public void jaccard0() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {});
        jaccardCheck(set1, set2, 0);
        jaccardCheck(set2, set1, 0);
    }

    @Test
    public void jaccard25() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {1});
        jaccardCheck(set1, set2, 0.25);
        jaccardCheck(set2, set1, 0.25);
    }

    @Test
    public void jaccard50() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {2, 4});
        jaccardCheck(set1, set2, 0.5);
        jaccardCheck(set2, set1, 0.5);
    }

    @Test
    public void jaccard75() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {1, 2, 4});
        jaccardCheck(set1, set2, 0.75);
        jaccardCheck(set2, set1, 0.75);
    }

    @Test
    public void jaccard100() {
        byte[] set1 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        byte[] set2 = LongAscSDBP.toBytes(new long[] {1, 2, 3, 4});
        jaccardCheck(set1, set2, 1.0);
        jaccardCheck(set2, set1, 1.0);
    }
}
