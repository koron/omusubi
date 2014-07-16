package net.kaoriya.omusubi;

import java.nio.IntBuffer;
import java.util.Arrays;


public class IntDecompressStream extends IntBlockedInputStream {

    private final IntBuffer source;

    private final IntFilter filter;

    private final IntBitPacking packer;

    private final int chunkSize;

    public IntDecompressStream(
            IntBuffer source,
            IntFilterFactory factory,
            IntBitPacking packer)
    {
        super(packer.getBlockSize());
        this.source = source;
        int outLen = this.source.remaining();
        if (outLen == 0) {
            this.filter = factory.newFilter(0);
        } else {
            int first = this.source.get();
            updateBlock(new int[]{ first });
            this.filter = factory.newFilter(first);
        }
        this.packer = packer;
        this.chunkSize = this.packer.getBlockSize();
    }

    public void fetchBlock(IntOutputStream dst) {
        int remain = this.source.remaining();
        if (remain >= this.chunkSize) {
            this.packer.decompress(this.source, dst, this.filter, 1);
        } else if (remain > 0) {
            int[] last = new int[this.chunkSize];
            IntBuffer buf = IntBuffer.wrap(last);
            packer.decompress(this.source, new IntBufferOutputStream(buf),
                    filter, 1);
            dst.write(last, 0, remain);
        }
    }
}
