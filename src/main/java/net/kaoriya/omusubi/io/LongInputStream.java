package net.kaoriya.omusubi.io;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class LongInputStream implements Iterable<Long>
{
    public abstract Long read();

    public int read(long[] array) {
        return read(array, 0, array.length);
    }

    public int read(long[] array, int offset, int length) {
        int i;
        int end;
        for (i = offset, end = offset + length; i < end; ++i) {
            Long n = read();
            if (n == null) {
                break;
            }
            array[i] = n;
        }
        return i - offset;
    }

    public Iterator<Long> iterator() {
        return null;
    }

    class StreamIterator implements Iterator<Long> {
        Long next = null;

        public Long next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            return this.next;
        }

        public boolean hasNext() {
            return (this.next = read()) != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
