package net.kaoriya.omusubi;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import net.kaoriya.omusubi.io.ByteArrayLongOutputStream;
import net.kaoriya.omusubi.io.LongArrayOutputStream;
import net.kaoriya.omusubi.io.LongInputStream;
import net.kaoriya.omusubi.io.LongOutputStream;

/**
 * LongCodec interface.
 *
 * Support compress and decopress methods.
 */
public abstract class LongCodec {

    public abstract void compress(LongBuffer src, LongOutputStream dst);

    public abstract void decompress(LongBuffer src, LongOutputStream dst);

    public LongInputStream newCompressStream(LongBuffer src) {
        throw new UnsupportedOperationException();
    }

    public LongInputStream newDecompressStream(LongBuffer src) {
        throw new UnsupportedOperationException();
    }

    protected int decompressLength(LongBuffer src) {
        return -1;
    }

    public byte[] compress(long[] src) {
        ByteArrayLongOutputStream dst = new ByteArrayLongOutputStream();
        compress(LongBuffer.wrap(src), dst);
        return dst.toByteArray();
    }

    public long[] decompress(byte[] src) {
        LongBuffer srcBuf = ByteBuffer.wrap(src).asLongBuffer();
        int len = decompressLength(srcBuf);
        LongArrayOutputStream dst = (len < 0)
            ? new LongArrayOutputStream() : new LongArrayOutputStream(len);
        decompress(srcBuf, dst);
        return dst.toLongArray();
    }
}
