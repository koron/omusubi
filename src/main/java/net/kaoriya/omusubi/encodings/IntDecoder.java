package net.kaoriya.omusubi.encodings;

public abstract class IntDecoder extends IntContext {

    protected IntDecoder(int contextValue) {
        super(contextValue);
    }

    protected IntDecoder() {
        this(0);
    }

    public abstract int dencodeInt(int value);

    public int[] dencodeArray(int[] src, int srcoff, int length,
            int[] dst, int dstoff)
    {
        for (int i = 0; i < length; ++i) {
            dst[dstoff + i] = dencodeInt(src[srcoff + i]);
        }
        return dst;
    }

    public int[] dencodeArray(int[] src) {
        return dencodeArray(src, 0, src.length, new int[src.length], 0);
    }
}
