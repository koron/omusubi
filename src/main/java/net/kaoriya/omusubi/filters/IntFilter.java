package net.kaoriya.omusubi.filters;

/**
 * Int filter.
 */
public interface IntFilter {

    int filterInt(int value);

    void saveContext();

    void restoreContext();

    void resetContext();

}
