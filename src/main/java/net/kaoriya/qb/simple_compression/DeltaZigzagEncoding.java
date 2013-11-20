package net.kaoriya.qb.simple_compression;

public final class DeltaZigzagEncoding {

    public static class IntContext {
        protected int contextValue;

        protected IntContext(int contextValue) {
            this.contextValue = contextValue;
        }

        public void setContextValue(int contextValue) {
            this.contextValue = contextValue;
        }

        public int getContextValue() {
            return this.contextValue;
        }
    }

    public static class IntEncoder extends IntContext {
        public IntEncoder(int contextValue) {
            super(contextValue);
        }

        public IntEncoder() {
            super(0);
        }

        public int encodeInt(int value) {
            int n = value - this.contextValue;
            this.contextValue = value;
            return (n << 1) ^ (n >> 31);
        }

        public int[] encodeArray(int[] src, int srcoff, int length,
                int[] dst, int dstoff)
        {
            for (int i = 0; i < length; ++i) {
                dst[dstoff + i] = encodeInt(src[srcoff + i]);
            }
            return dst;
        }

        /*
        // Ununsed for now
        public int[] encodeArray(int[] src, int srcoff, int length,
                int[] dst)
        {
            return encodeArray(src, srcoff, length, dst, 0);
        }

        // Ununsed for now
        public int[] encodeArray(int[] src, int offset, int length) {
            return encodeArray(src, offset, length, new int[length], 0);
        }
        */

        public int[] encodeArray(int[] src) {
            return encodeArray(src, 0, src.length, new int[src.length], 0);
        }
    }

    public static class IntDecoder extends IntContext {
        public IntDecoder(int contextValue) {
            super(contextValue);
        }

        public IntDecoder() {
            super(0);
        }

        public int decodeInt(int value) {
            int n = (value >> 1) ^ ((value & 1) * -1);
            n += this.contextValue;
            this.contextValue = n;
            return n;
        }

        public int[] decodeArray(int[] src, int srcoff, int length,
                int[] dst, int dstoff)
        {
            for (int i = 0; i < length; ++i) {
                dst[dstoff + i] = decodeInt(src[srcoff + i]);
            }
            return dst;
        }

        /*
        // Unused for now.
        public int[] decodeArray(int[] src, int srcoff, int length,
                int[] dst)
        {
            return decodeArray(src, srcoff, length, dst, 0);
        }

        // Unused for now.
        public int[] decodeArray(int[] src, int offset, int length) {
            return decodeArray(src, offset, length, new int[length], 0);
        }
        */

        public int[] decodeArray(int[] src) {
            return decodeArray(src, 0, src.length, new int[src.length], 0);
        }
    }
}
