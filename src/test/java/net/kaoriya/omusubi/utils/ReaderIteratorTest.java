package net.kaoriya.omusubi.utils;

import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import net.kaoriya.omusubi.io.IntArrayInputStream;
import net.kaoriya.omusubi.io.IntInputStream;

public class ReaderIteratorTest {

    private static ReaderIterator<Integer, IntInputStream> newIter(
            int[] array)
    {
        return new ReaderIterator<Integer, IntInputStream>(
            new IntArrayInputStream(array));
    }

    @Test
    public void cowardlyIteration() {
        ReaderIterator<Integer, IntInputStream> iter = newIter(
                new int[]{ 1, 2, 3 }
        );
        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(1), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(2), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(3), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void bravelyIteration() {
        ReaderIterator<Integer, IntInputStream> iter = newIter(
                new int[]{ 1, 2, 3 }
        );
        assertEquals(Integer.valueOf(1), iter.next());
        assertEquals(Integer.valueOf(2), iter.next());
        assertEquals(Integer.valueOf(3), iter.next());
        assertFalse(iter.hasNext());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void empty() {
        ReaderIterator<Integer, IntInputStream> iter = newIter(new int[]{});
        thrown.expect(NoSuchElementException.class);
        iter.next();
    }

    @Test
    public void unsupportedRemove() {
        ReaderIterator<Integer, IntInputStream> iter = newIter(
                new int[]{ 1, 2, 3 }
        );
        thrown.expect(UnsupportedOperationException.class);
        iter.remove();
    }

    @Test
    public void unsupportedRemove2() {
        ReaderIterator<Integer, IntInputStream> iter = newIter(
                new int[]{ 1, 2, 3 }
        );
        assertEquals(Integer.valueOf(1), iter.next());
        thrown.expect(UnsupportedOperationException.class);
        iter.remove();
    }
}
