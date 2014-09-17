package net.kaoriya.omusubi.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.LongBuffer;

public class CodecUtilsTest {
    @Test
    public void ctor() {
        new CodecUtils();
    }

    @Test
    public void encodeEmptyPack() {
        CodecUtils.encodeBlockPack(LongBuffer.wrap(new long[0]),
                null, null, null);
    }

    @Test
    public void decodeEmptyPack() {
        CodecUtils.decodeBlockPack(LongBuffer.wrap(new long[0]),
                null, null, null);
    }
}
