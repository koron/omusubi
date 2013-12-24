package net.kaoriya.omusubi;

/**
 * Through origin values.
 */
public final class ThroughIntFilter implements IntFilter {

    public int filterInt(int value) {
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
