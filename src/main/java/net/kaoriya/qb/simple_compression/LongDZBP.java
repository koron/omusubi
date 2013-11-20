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

    private final DZEncodeFilter encodeFilter = new DZEncodeFilter();

    private final DZDecodeFilter decodeFilter = new DZDecodeFilter();

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
        // TODO:
        this.bitPack.compress(src, dst, this.encodeFilter);
    }

    public void decompress(LongBuffer src, LongBuffer dst) {
        // TODO:
        this.bitPack.decompress(src, dst, this.decodeFilter);
    }
}
