package net.kaoriya.omusubi;

import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.IntBuffer;

import net.kaoriya.omusubi.io.IntOutputStream;

public class IntCodecTest
{
    public static class ThroughCodec extends IntCodec {
        public int compressedLen = 0;
        public int decompressedLen = 0;

        public void compress(IntBuffer src, IntOutputStream dst) {
            int[] l = src.array();
            dst.write(l);
            this.compressedLen += l.length;
        }

        public void decompress(IntBuffer src, IntOutputStream dst) {
            int[] l = new int[src.remaining()];
            src.get(l);
            dst.write(l);
            this.decompressedLen += l.length;
        }
    }

    @Test
    public void compress() {
        ThroughCodec c = new ThroughCodec();
        byte[] b = c.compress(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
        assertArrayEquals(new byte[] {
            0, 0, 0, 0,
            0, 0, 0, 1,
            0, 0, 0, 2,
            0, 0, 0, 3,
            0, 0, 0, 4,
            0, 0, 0, 5,
            0, 0, 0, 6,
            0, 0, 0, 7,
        }, b);
        assertEquals(8, c.compressedLen);
    }

    @Test
    public void decompress() {
        ThroughCodec c = new ThroughCodec();
        int[] l = c.decompress(new byte[] {
            0, 0, 0, 0,
            0, 0, 0, 1,
            0, 0, 0, 2,
            0, 0, 0, 3,
            0, 0, 0, 4,
            0, 0, 0, 5,
            0, 0, 0, 6,
            0, 0, 0, 7,
        });
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, l);
        assertEquals(8, c.decompressedLen);
    }
}
