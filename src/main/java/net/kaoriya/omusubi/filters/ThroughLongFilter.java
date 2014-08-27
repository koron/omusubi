package net.kaoriya.omusubi.filters;

/**
 * Through origin values.
 */
public final class ThroughLongFilter implements LongFilter {

    public long filterLong(long value) {
        return value;
    }

    public void saveContext() {
        // nothing to do.
    }

    public void restoreContext() {
        // nothing to do.
    }

    public void resetContext() {
        // nothing to do.
    }
}
