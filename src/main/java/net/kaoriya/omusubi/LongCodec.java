package net.kaoriya.omusubi;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

/**
 * LongCodec interface.
 *
 * Support compress and decopress methods.
 */
public abstract class LongCodec {

    public abstract void compress(LongBuffer src, LongOutputStream dst);

    public abstract void decompress(LongBuffer src, LongOutputStream dst);

    protected int decompressLength(LongBuffer src) {
        return -1;
    }

    public final byte[] compress(long[] src) {
        ByteArrayLongOutputStream dst = new ByteArrayLongOutputStream();
        compress(LongBuffer.wrap(src), dst);
        return dst.toByteArray();
    }

    public final long[] decompress(byte[] src) {
        LongBuffer srcBuf = ByteBuffer.wrap(src).asLongBuffer();
        int len = decompressLength(srcBuf);
        LongArrayOutputStream dst = (len < 0)
            ? new LongArrayOutputStream() : new LongArrayOutputStream(len);
        decompress(srcBuf, dst);
        return dst.toLongArray();
    }
}
