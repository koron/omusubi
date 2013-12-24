package net.kaoriya.omusubi;

import java.io.DataOutput;
import java.io.IOException;

public final class IntDataOutputStream extends IntOutputStream
{
    private final DataOutput dataOutput;

    public IntDataOutputStream(DataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    public void write(int n) {
        try {
            this.dataOutput.writeInt(n);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Underlying DataOutput throws an exception", e);
        }
    }

    @Override
    public void write(int[] array, int offset, int length) {
        try {
            for (int i = offset, end = offset + length; i < end; ++i) {
                this.dataOutput.writeInt(array[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Underlying DataOutput throws an exception", e);
        }
    }
}
