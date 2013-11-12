package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

public interface LongDecompressor {

    void decompress(LongBuffer src, LongBuffer dst);

}
