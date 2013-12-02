package net.kaoriya.qb.simple_compression;

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

    public final byte[] compress(long[] src) {
        ByteArrayLongOutputStream dst =
            new ByteArrayLongOutputStream(src.length * 4);
        compress(LongBuffer.wrap(src), dst);
        return dst.toByteArray();
    }

    public final long[] decompress(byte[] src) {
        LongBuffer srcBuf = ByteBuffer.wrap(src).asLongBuffer();
        // FIXME: create more efficient output buffer class.
        ByteArrayLongOutputStream dst = new ByteArrayLongOutputStream();
        decompress(srcBuf, dst);
        // Copy to return array.
        LongBuffer dstBuf = ByteBuffer.wrap(dst.toByteArray()).asLongBuffer();
        LongBuffer retval = LongBuffer.allocate(dstBuf.remaining());
        retval.put(dstBuf);
        return retval.array();
    }
}
