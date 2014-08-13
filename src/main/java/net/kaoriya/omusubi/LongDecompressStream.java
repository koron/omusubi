package net.kaoriya.omusubi;

import java.nio.LongBuffer;
import java.util.Arrays;


public class LongDecompressStream extends LongBlockedInputStream {

    private final LongBuffer source;

    private final LongFilter filter;

    private final LongBitPacking packer;

    private final int chunkSize;

    public LongDecompressStream(
            LongBuffer source,
            LongFilterFactory factory,
            LongBitPacking packer)
    {
        super(packer.getBlockSize());
        this.source = source;
        int outLen = this.source.remaining();
        if (outLen == 0) {
            this.filter = factory.newFilter(0);
        } else {
            long first = this.source.get();
            updateBlock(new long[]{ first });
            this.filter = factory.newFilter(first);
        }
        this.packer = packer;
        this.chunkSize = this.packer.getBlockSize();
    }

    public void fetchBlock(LongOutputStream dst) {
        int remain = this.source.remaining();
        if (remain >= this.chunkSize) {
            this.packer.decompress(this.source, dst, this.filter, 1);
        } else if (remain > 0) {
            long[] last = new long[this.chunkSize];
            LongBuffer buf = LongBuffer.wrap(last);
            packer.decompress(this.source, new LongBufferOutputStream(buf),
                    filter, 1);
            dst.write(last, 0, remain);
        }
    }
}
