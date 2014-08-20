package net.kaoriya.omusubi.filters;

/**
 * Long filter.
 */
public interface LongFilter {

    long filterLong(long value);

    void saveContext();

    void restoreContext();

    void resetContext();

}
