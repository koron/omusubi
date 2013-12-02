package net.kaoriya.qb.simple_compression;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public final class ByteArrayLongOutputStream extends LongOutputStream
{
    private final ByteArrayOutputStream byteStream;

    private final LongDataOutputStream longStream;

    public ByteArrayLongOutputStream() {
        this.byteStream = new ByteArrayOutputStream();
        this.longStream = new LongDataOutputStream(
                new DataOutputStream(this.byteStream));
    }

    public void write(long n) {
        this.longStream.write(n);
    }

    @Override
    public void write(long[] array) {
        this.longStream.write(array);
    }

    @Override
    public void write(long[] array, int offset, int length) {
        this.longStream.write(array, offset, length);
    }

    public byte[] toByteArray() {
        return this.byteStream.toByteArray();
    }
}
