package net.kaoriya.omusubi.io;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class IntInputStream implements Iterable<Integer>
{
    public abstract Integer read();

    public int read(int[] array) {
        return read(array, 0, array.length);
    }

    public int read(int[] array, int offset, int length) {
        int i;
        int end;
        for (i = offset, end = offset + length; i < end; ++i) {
            Integer n = read();
            if (n == null) {
                break;
            }
            array[i] = n;
        }
        return i - offset;
    }

    public Iterator<Integer> iterator() {
        return new StreamIterator();
    }

    class StreamIterator implements Iterator<Integer> {
        Integer next = null;

        public Integer next() {
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
