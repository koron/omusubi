package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

public interface LongCompressor {

    void compress(LongBuffer src, LongBuffer dst);

}
