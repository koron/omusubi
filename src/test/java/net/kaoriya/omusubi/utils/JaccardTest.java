package net.kaoriya.omusubi.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class JaccardTest {

    @Test
    public void ctor() {
        Jaccard j = new Jaccard();
        assertNotNull(j);
    }

    static List<Integer> asList(int[] a) {
        ArrayList<Integer> l = new ArrayList<Integer>(a.length);
        for (int n : a) {
            l.add(n);
        }
        return l;
    }

    static double jaccard(int[] a, int[] b) {
        return Jaccard.jaccard(asList(a), asList(b));
    }

    static void check(int[] a, int[] b, double value) {
        assertEquals(value, jaccard(a, b), 0.01);
    }

    @Test
    public void emptyBoth() {
        check(new int[]{}, new int[]{}, 0.0);
    }

    @Test
    public void emptyFirst() {
        check(new int[]{}, new int[]{1, 2, 3, 4}, 0.0);
    }

    @Test
    public void emptySecond() {
        check(new int[]{1, 2, 3, 4}, new int[]{}, 0.0);
    }

    @Test
    public void matchHalf() {
        check(new int[]{1, 2}, new int[]{1, 2, 3, 4}, 0.5);
        check(new int[]{1, 3}, new int[]{1, 2, 3, 4}, 0.5);
        check(new int[]{1, 4}, new int[]{1, 2, 3, 4}, 0.5);
        check(new int[]{2, 3}, new int[]{1, 2, 3, 4}, 0.5);
        check(new int[]{2, 4}, new int[]{1, 2, 3, 4}, 0.5);
        check(new int[]{3, 4}, new int[]{1, 2, 3, 4}, 0.5);
    }

    @Test
    public void matchQuarter() {
        check(new int[]{1}, new int[]{1, 2, 3, 4}, 0.25);
        check(new int[]{2}, new int[]{1, 2, 3, 4}, 0.25);
        check(new int[]{3}, new int[]{1, 2, 3, 4}, 0.25);
        check(new int[]{4}, new int[]{1, 2, 3, 4}, 0.25);
    }

    @Test
    public void matchThreeQuarter() {
        check(new int[]{1, 2, 3}, new int[]{1, 2, 3, 4}, 0.75);
        check(new int[]{1, 2, 4}, new int[]{1, 2, 3, 4}, 0.75);
        check(new int[]{1, 3, 4}, new int[]{1, 2, 3, 4}, 0.75);
        check(new int[]{2, 3, 4}, new int[]{1, 2, 3, 4}, 0.75);
    }

    @Test
    public void remain() {
        int[] a = new int[]{1, 2, 3};
        assertNull(Jaccard.<Integer>remain(
                    new IteratorReader<Integer>(asList(a).iterator()),
                    new IteratorReader<Integer>(asList(a).iterator())));
    }
}
