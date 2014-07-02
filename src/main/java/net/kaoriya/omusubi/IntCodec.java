package net.kaoriya.omusubi;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * IntCodec interface.
 *
 * Support compress and decopress methods.
 */
public abstract class IntCodec {

    public abstract void compress(IntBuffer src, IntOutputStream dst);

    public abstract void decompress(IntBuffer src, IntOutputStream dst);

    public IntInputStream newCompressStream(IntBuffer src) {
        throw new UnsupportedOperationException();
    }

    public IntInputStream newDecompressStream(IntBuffer src) {
        throw new UnsupportedOperationException();
    }

    protected int decompressLength(IntBuffer src) {
        return -1;
    }

    public byte[] compress(int[] src) {
        ByteArrayIntOutputStream dst = new ByteArrayIntOutputStream();
        compress(IntBuffer.wrap(src), dst);
        return dst.toByteArray();
    }

    public int[] decompress(byte[] src) {
        IntBuffer srcBuf = ByteBuffer.wrap(src).asIntBuffer();
        int len = decompressLength(srcBuf);
        IntArrayOutputStream dst = (len < 0)
            ? new IntArrayOutputStream() : new IntArrayOutputStream(len);
        decompress(srcBuf, dst);
        return dst.toIntArray();
    }
}
