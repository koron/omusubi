package net.kaoriya.omusubi.io;

public abstract class LongInputStream
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
}
