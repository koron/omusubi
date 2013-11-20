package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

/**
 * Long Delta Zigzag Encoded Bit Packing.
 */
public class LongDZBP implements LongCompressor, LongDecompressor
{
    private final LongBitPacking bitPack;

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
    }

    public void decompress(LongBuffer src, LongBuffer dst) {
        // TODO:
    }
}
