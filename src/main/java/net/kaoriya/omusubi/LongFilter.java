package net.kaoriya.omusubi;

/**
 * Long filter.
 */
public interface LongFilter {

    long filterLong(long value);

    void saveContext();

    void restoreContext();

    void resetContext();

}
