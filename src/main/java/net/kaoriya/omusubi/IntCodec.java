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

    public final byte[] compress(int[] src) {
        ByteArrayIntOutputStream dst =
            new ByteArrayIntOutputStream(src.length * 4);
        compress(IntBuffer.wrap(src), dst);
        return dst.toByteArray();
    }

    public final int[] decompress(byte[] src) {
        IntBuffer srcBuf = ByteBuffer.wrap(src).asIntBuffer();
        // FIXME: create more efficient output buffer class.
        ByteArrayIntOutputStream dst = new ByteArrayIntOutputStream();
        decompress(srcBuf, dst);
        // Copy to return array.
        IntBuffer dstBuf = ByteBuffer.wrap(dst.toByteArray()).asIntBuffer();
        IntBuffer retval = IntBuffer.allocate(dstBuf.remaining());
        retval.put(dstBuf);
        return retval.array();
    }
}
