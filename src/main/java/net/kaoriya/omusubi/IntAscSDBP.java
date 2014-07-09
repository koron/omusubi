package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Int Ascending Sorted Delta Bit Packing.
 */
public class IntAscSDBP extends IntCodec
{
    static class CompressStream extends IntBlockedInputStream {

        private final IntBuffer source;

        private final IntFilter filter;

        private final IntBitPacking packer;

        private final int chunkSize;

        CompressStream(
                IntBuffer source,
                IntFilterFactory factory,
                IntBitPacking packer)
        {
            super(packer.getBlockSize() + 1);
            this.source = source;
            int srcLen = this.source.remaining();
            if (srcLen == 0) {
                this.filter = factory.newFilter(0);
            } else {
                int first = this.source.get();
                updateBlock(new int[]{ srcLen, first });
                this.filter = factory.newFilter(first);
            }
            this.packer = packer;
            this.chunkSize = this.packer.getBlockSize();
        }

        public void fetchBlock(IntOutputStream dst) {
            int remain = this.source.remaining();
            if (remain >= this.chunkSize) {
                this.packer.compressChunk(this.source, dst, this.filter);
            } else if (remain > 0) {
                int[] last = new int[this.chunkSize];
                this.source.get(last, 0, remain);
                Arrays.fill(last, remain, last.length, last[remain - 1]);
                this.packer.compress(IntBuffer.wrap(last), dst, this.filter);
            }
        }
    }

    static class DecompressStream extends IntBlockedInputStream {

        IntBuffer source;

        DecompressStream(IntBuffer source) {
            super(129);
            this.source = source;
            // TODO: setup initial block.
        }

        public void fetchBlock(IntOutputStream dst) {
            // TODO: setup next block.
        }
    }

    // @Implemnets: IntCodec
    public void compress(IntBuffer src, IntOutputStream dst) {
        // TODO:
    }

    // @Implemnets: IntCodec
    public void decompress(IntBuffer src, IntOutputStream dst) {
        // TODO:
    }

    public static byte[] toBytes(int[] src) {
        return (new IntDZBP()).compress(src);
    }

    public static int[] fromBytes(byte[] src) {
        return (new IntDZBP()).decompress(src);
    }

    @Override
    public IntInputStream newCompressStream(IntBuffer src) {
        // Prepare other objects.
        return new CompressStream(src, null, null);
    }

    public static byte[] union(byte[] a, byte[] b, byte[] ...others) {
        // TODO:
        return null;
    }

    public static byte[] intersect(byte[] a, byte[] b, byte[] ...others) {
        // TODO:
        return null;
    }

    public static byte[] difference(byte[] a, byte[] b) {
        // TODO:
        return null;
    }
}
