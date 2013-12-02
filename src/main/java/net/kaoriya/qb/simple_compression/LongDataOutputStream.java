package net.kaoriya.qb.simple_compression;

import java.io.DataOutput;
import java.io.IOException;

public final class LongDataOutputStream extends LongOutputStream
{
    private final DataOutput dataOutput;

    public LongDataOutputStream(DataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    public void write(long n) {
        try {
            this.dataOutput.writeLong(n);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Underlying DataOutput throws an exception", e);
        }
    }

    @Override
    public void write(long[] array, int offset, int length) {
        try {
            for (int i = offset, end = offset + length; i < end; ++i) {
                this.dataOutput.writeLong(array[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Underlying DataOutput throws an exception", e);
        }
    }
}
