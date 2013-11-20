package net.kaoriya.qb.simple_compression;

/**
 * Long filter.
 */
public interface LongFilter {

    long filterLong(long value);

    void saveContext();

    void restoreContext();

    void resetContext();

}
