package net.kaoriya.omusubi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public final class ByteArrayIntOutputStream extends IntOutputStream
{
    private final ByteArrayOutputStream byteStream;

    private final IntDataOutputStream intStream;

    public ByteArrayIntOutputStream(ByteArrayOutputStream s) {
        this.byteStream = s;
        this.intStream = new IntDataOutputStream(
                new DataOutputStream(this.byteStream));
    }

    public ByteArrayIntOutputStream(int size) {
        this(new ByteArrayOutputStream(size));
    }

    public ByteArrayIntOutputStream() {
        this(new ByteArrayOutputStream());
    }

    public void write(int n) {
        this.intStream.write(n);
    }

    @Override
    public void write(int[] array) {
        this.intStream.write(array);
    }

    @Override
    public void write(int[] array, int offset, int length) {
        this.intStream.write(array, offset, length);
    }

    public byte[] toByteArray() {
        return this.byteStream.toByteArray();
    }
}
