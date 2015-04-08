package net.kaoriya.omusubi.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReaderIterator<E, R extends Reader<E>> implements Iterator<E> {
    final Reader<E> reader;
    E next = null;

    public ReaderIterator(Reader<E> r) {
        this.reader = r;
    }

    public E next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        return this.next;
    }

    public boolean hasNext() {
        return (this.next = this.reader.read()) != null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
