package net.kaoriya.omusubi;

import java.nio.LongBuffer;
import java.util.Arrays;

public class LongCompressStream extends LongBlockedInputStream {

    private final LongBuffer source;

    private final LongFilter filter;

    private final LongBitPacking packer;

    private final int chunkSize;

    public LongCompressStream(
            LongBuffer source,
            LongFilterFactory factory,
            LongBitPacking packer)
    {
        super(packer.getBlockSize() + 1);
        this.source = source;
        int srcLen = this.source.remaining();
        if (srcLen == 0) {
            this.filter = factory.newFilter(0);
        } else {
            long first = this.source.get();
            updateBlock(new long[]{ srcLen, first });
            this.filter = factory.newFilter(first);
        }
        this.packer = packer;
        this.chunkSize = this.packer.getBlockSize();
    }

    public void fetchBlock(LongOutputStream dst) {
        int remain = this.source.remaining();
        if (remain >= this.chunkSize) {
            this.packer.compressChunk(this.source, dst, this.filter);
        } else if (remain > 0) {
            long[] last = new long[this.chunkSize];
            this.source.get(last, 0, remain);
            Arrays.fill(last, remain, last.length, last[remain - 1]);
            this.packer.compress(LongBuffer.wrap(last), dst, this.filter);
        }
    }
}
