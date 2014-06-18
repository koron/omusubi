package net.kaoriya.omusubi.encodings;

public final class DeltaEncoding {

    public static class IntAscendEncoder extends IntEncoder {
        public IntAscendEncoder(int contextValue) {
            super(contextValue);
        }

        public IntAscendEncoder() {
            this(0);
        }

        public int encodeInt(int value) {
            int n = value - this.contextValue;
            if (n < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be greater than or equals %2$d",
                            value, n));
            }
            this.contextValue = value;
            return n;
        }
    }

    public static class IntDescendEncoder extends IntDecoder {
        public IntDescendEncoder(int contextValue) {
            super(contextValue);
        }

        public IntDescendEncoder() {
            this(0);
        }

        public int dencodeInt(int value) {
            int n = this.contextValue - value;
            if (n < 0) {
                throw new IllegalArgumentException(
                        String.format(
                            "input:%1$d must be smaller than or equals %2$d",
                            value, n));
            }
            this.contextValue = value;
            return n;
        }
    }
}
