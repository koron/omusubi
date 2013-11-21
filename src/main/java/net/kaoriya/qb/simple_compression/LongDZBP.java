package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

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
        // TODO: Output length of original array.
        // Output first int, and set it as delta's initial context.
        long first = src.get();
        dst.put(first);
        DZEncodeFilter filter = new DZEncodeFilter(first);
        // Compress intermediate blocks.
        this.bitPack.compress(src, dst, filter);
        // TODO: Compress last block.
    }

    public void decompress(LongBuffer src, LongBuffer dst) {
        // TODO: Fetch length of original array.
        // Fetch and output first int, and set it as delta's initial context.
        long first = src.get();
        dst.put(first);
        DZDecodeFilter filter = new DZDecodeFilter(first);
        // Decompress intermediate blocks.
        this.bitPack.decompress(src, dst, filter);
        // TODO: Decompress last block.
    }
}
