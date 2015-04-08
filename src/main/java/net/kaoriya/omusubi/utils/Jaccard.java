package net.kaoriya.omusubi.utils;

import java.util.Iterator;

public final class Jaccard
{

    static <E> IteratorReader<E> remain(
            IteratorReader<E> a,
            IteratorReader<E> b)
    {
        if (a.hasCurrent() && !b.hasCurrent()) {
            return a;
        } else if (!a.hasCurrent() && b.hasCurrent()) {
            return b;
        } else {
            return null;
        }
    }

    public static <E extends Comparable<E>> double jaccard(
            Iterable<E> a,
            Iterable<E> b)
    {
        return Jaccard.<E>jaccard(a.iterator(), b.iterator());
    }

    public static <E extends Comparable<E>> double jaccard(
            Iterator<E> a,
            Iterator<E> b)
    {
        int match = 0;
        int uniq = 0;

        IteratorReader<E> ra = new IteratorReader<E>(a);
        IteratorReader<E> rb = new IteratorReader<E>(b);

        while (ra.hasCurrent() && rb.hasCurrent()) {
            int d = ra.current().compareTo(rb.current());
            if (d == 0) {
                ++match;
                ++uniq;
                ra.next();
                rb.next();
            } else {
                IteratorReader<E> smaller = d < 0 ? ra : rb;
                ++uniq;
                smaller.next();
            }
        }

        IteratorReader<E> remain = Jaccard.<E>remain(ra, rb);
        if (remain != null) {
            ++uniq;
            while (remain.next() != null) {
                ++uniq;
            }
        }

        if (match == 0) {
            return 0.0;
        } else if (uniq == 0) {
            // FIXME: nerver reached
            return Double.POSITIVE_INFINITY;
        } else {
            return (double)match / uniq;
        }
    }

}
