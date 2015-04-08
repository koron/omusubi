package net.kaoriya.omusubi.utils;

import java.util.Iterator;

public class IteratorReader<T> implements Iterator<T> {

    private Iterator<T> iter;
    private T value;

    public IteratorReader(Iterator<T> iter) {
        this.iter = iter;
        next();
    }

    public T current() {
        return this.value;
    }

    public T next() {
        return this.value = this.iter.hasNext() ? this.iter.next() : null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.iter.hasNext();
    }

    public boolean hasCurrent() {
        return this.value != null;
    }
}
