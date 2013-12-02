package net.kaoriya.qb.simple_compression;

import java.nio.LongBuffer;

/**
 * LongCodec interface.
 *
 * Support compress and decopress methods.
 */
public interface LongCodec {

    void compress(LongBuffer src, LongOutputStream dst);

    void decompress(LongBuffer src, LongOutputStream dst);

}
