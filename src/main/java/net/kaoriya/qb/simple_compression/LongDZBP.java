package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;
import java.util.Arrays;

/**
 * Long Delta Zigzag Encoded Bit Packing.
 */
public class LongDZBP implements LongCompressor, LongDecompressor
{
    public static class DZEncodeFilter
        extends DeltaZigzagEncoding.LongEncoder
        implements LongFilter
    {
        private long savedContext = 0;

        public DZEncodeFilter(long contextValue) {
            super(contextValue);
        }

        public DZEncodeFilter() {
            this(0L);
        }

        public long filterLong(long value) {
            return encodeLong(value);
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
        extends DeltaZigzagEncoding.LongDecoder
        implements LongFilter
    {
        private long savedContext = 0;

        public DZDecodeFilter(long contextValue) {
            super(contextValue);
        }

        public DZDecodeFilter() {
            this(0L);
        }

        public long filterLong(long value) {
            return decodeLong(value);
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

    private final LongBitPacking bitPack;

    public LongDZBP setDebug(boolean value) {
        this.bitPack.setDebug(value);
        return this;
    }

    public boolean getDebug() {
        return this.bitPack.getDebug();
    }

    public LongDZBP(LongBitPacking bitPack) {
        this.bitPack = bitPack;
    }

    public LongDZBP() {
        this(new LongBitPacking());
    }

    public LongBitPacking getBitPacking() {
        return this.bitPack;
    }

    public void compress(LongBuffer src, LongBuffer dst) {
        // Output length of original array.  When input array is empty, make
        // empty output for memory efficiency.
        final int srcLen = src.remaining();
        if (srcLen == 0) {
            return;
        }
        dst.put(srcLen);

        // Output first int, and set it as delta's initial context.
        final long first = src.get();
        dst.put(first);
        DZEncodeFilter filter = new DZEncodeFilter(first);

        // Compress intermediate blocks.
        final int chunkSize = this.bitPack.getBlockSize();
        final int chunkRemain = src.remaining() % chunkSize;
        LongBuffer window = src.slice();
        window.limit(window.limit() - chunkRemain);
        this.bitPack.compress(window, dst, filter);
        src.position(src.position() + window.position());

        // Compress last block.
        if (chunkRemain > 0) {
            long[] last = new long[chunkSize];
            src.get(last, 0, chunkRemain);
            // Padding extended area by last value.  It make delta zigzag
            // efficient.
            Arrays.fill(last, chunkRemain, last.length,
                    last[chunkRemain - 1]);
            this.bitPack.compress(LongBuffer.wrap(last), dst, filter);
        }
    }

    public void decompress(LongBuffer src, LongBuffer dst) {
        // Fetch length of original array.
        if (!src.hasRemaining()) {
            return;
        }
        final int outLen = (int)src.get() - 1;

        // Fetch and output first int, and set it as delta's initial context.
        final long first = src.get();
        dst.put(first);
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
            long[] last = new long[chunkSize];
            LongBuffer buf = LongBuffer.wrap(last);
            this.bitPack.decompress(src, buf, filter, 1);
            dst.put(last, 0, chunkRemain);
        }
    }
}
