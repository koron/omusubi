package net.kaoriya.omusubi.encodings;

public abstract class LongDecoder extends LongContext {

    protected LongDecoder(long contextValue) {
        super(contextValue);
    }

    protected LongDecoder() {
        this(0);
    }

    public abstract long dencodeLong(long value);

    public long[] dencodeArray(long[] src, int srcoff, int length,
            long[] dst, int dstoff)
    {
        for (int i = 0; i < length; ++i) {
            dst[dstoff + i] = dencodeLong(src[srcoff + i]);
        }
        return dst;
    }

    public long[] dencodeArray(long[] src) {
        return dencodeArray(src, 0, src.length, new long[src.length], 0);
    }
}
