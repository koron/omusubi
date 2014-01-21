package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Int Delta Zigzag Encoded Bit Packing.
 */
public class IntDZBP extends IntCodec
{
    public static class DZEncodeFilter
        extends DeltaZigzagEncoding.IntEncoder
        implements IntFilter
    {
        private int savedContext = 0;

        public DZEncodeFilter(int contextValue) {
            super(contextValue);
        }

        public DZEncodeFilter() {
            this(0);
        }

        public int filterInt(int value) {
            return encodeInt(value);
        }

        public void saveContext() {
            this.savedContext = this.contextValue;
        }

        public void restoreContext() {
            this.contextValue = this.savedContext;
        }

        public void resetContext() {
            this.contextValue = 0;
            this.savedContext = 0;
        }
    }

    public static class DZDecodeFilter
        extends DeltaZigzagEncoding.IntDecoder
        implements IntFilter
    {
        private int savedContext = 0;

        public DZDecodeFilter(int contextValue) {
            super(contextValue);
        }

        public DZDecodeFilter() {
            this(0);
        }

        public int filterInt(int value) {
            return decodeInt(value);
        }

        public void saveContext() {
            this.savedContext = this.contextValue;
        }

        public void restoreContext() {
            this.contextValue = this.savedContext;
        }

        public void resetContext() {
            this.contextValue = 0;
            this.savedContext = 0;
        }
    }

    private final IntBitPacking bitPack;

    public IntDZBP setDebug(boolean value) {
        this.bitPack.setDebug(value);
        return this;
    }

    public boolean getDebug() {
        return this.bitPack.getDebug();
    }

    public IntDZBP(IntBitPacking bitPack) {
        this.bitPack = bitPack;
    }

    public IntDZBP() {
        this(new IntBitPacking());
    }

    public IntBitPacking getBitPacking() {
        return this.bitPack;
    }

    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        // Output length of original array.  When input array is empty, make
        // empty output for memory efficiency.
        final int srcLen = src.remaining();
        if (srcLen == 0) {
            return;
        }
        dst.write(srcLen);

        // Output first int, and set it as delta's initial context.
        final int first = src.get();
        dst.write(first);
        DZEncodeFilter filter = new DZEncodeFilter(first);

        // Compress intermediate blocks.
        final int chunkSize = this.bitPack.getBlockSize();
        final int chunkRemain = src.remaining() % chunkSize;
        IntBuffer window = src.slice();
        window.limit(window.limit() - chunkRemain);
        this.bitPack.compress(window, dst, filter);
        src.position(src.position() + window.position());

        // Compress last block.
        if (chunkRemain > 0) {
            int[] last = new int[chunkSize];
            src.get(last, 0, chunkRemain);
            // Padding extended area by last value.  It make delta zigzag
            // efficient.
            Arrays.fill(last, chunkRemain, last.length,
                    last[chunkRemain - 1]);
            this.bitPack.compress(IntBuffer.wrap(last), dst, filter);
        }
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        // Fetch length of original array.
        if (!src.hasRemaining()) {
            return;
        }
        final int outLen = (int)src.get() - 1;

        // Fetch and output first int, and set it as delta's initial context.
        final int first = src.get();
        dst.write(first);
        DZDecodeFilter filter = new DZDecodeFilter(first);

        // Decompress intermediate blocks.
        final int chunkSize = this.bitPack.getBlockSize();
        final int chunkNum = outLen / chunkSize;
        if (chunkNum > 0) {
            this.bitPack.decompress(src, dst, filter, chunkNum);
        }

        // Decompress last block.
        final int chunkRemain = outLen % chunkSize;
        if (chunkRemain > 0) {
            int[] last = new int[chunkSize];
            IntBuffer buf = IntBuffer.wrap(last);
            this.bitPack.decompress(src, new IntBufferOutputStream(buf),
                    filter, 1);
            dst.write(last, 0, chunkRemain);
        }
    }

    @Override
    protected int decompressLength(IntBuffer src) {
        src.mark();
        final int outLen = (int)src.get();
        src.reset();
        return outLen;
    }

    public static byte[] toBytes(int[] src) {
        return (new IntDZBP()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntDZBP()).decompress(src);
    }
}
