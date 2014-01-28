package net.kaoriya.omusubi;

import java.io.ByteArrayOutputStream;

public final class ByteArrayIntOutputStream extends IntOutputStream
{
    private final ByteArrayOutputStream byteStream;

    public ByteArrayIntOutputStream(ByteArrayOutputStream s) {
        this.byteStream = s;
    }

    public ByteArrayIntOutputStream(int size) {
        this(new ByteArrayOutputStream(size));
    }

    public ByteArrayIntOutputStream() {
        this(new ByteArrayOutputStream());
    }

    public void write(int n) {
        this.byteStream.write((n >>> 24) & 0xff);
        this.byteStream.write((n >>> 16) & 0xff);
        this.byteStream.write((n >>>  8) & 0xff);
        this.byteStream.write((n >>>  0) & 0xff);
    }

    @Override
    public void write(int[] array) {
        write(array, 0, array.length);
    }

    @Override
    public void write(int[] array, int offset, int length) {
        for (int i = offset, end = offset + length; i < end; ++i) {
            int n = array[i];
            this.byteStream.write((n >>> 24) & 0xff);
            this.byteStream.write((n >>> 16) & 0xff);
            this.byteStream.write((n >>>  8) & 0xff);
            this.byteStream.write((n >>>  0) & 0xff);
        }
    }

    public byte[] toByteArray() {
        return this.byteStream.toByteArray();
    }
}
