package net.kaoriya.omusubi.io;

import java.nio.IntBuffer;
import java.util.Arrays;

import net.kaoriya.omusubi.filters.IntFilter;
import net.kaoriya.omusubi.filters.IntFilterFactory;
import net.kaoriya.omusubi.packers.IntBitPacking;

public class IntCompressStream extends IntBlockedInputStream {

    private final IntBuffer source;

    private final IntFilter filter;

    private final IntBitPacking packer;

    private final int chunkSize;

    public IntCompressStream(
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
