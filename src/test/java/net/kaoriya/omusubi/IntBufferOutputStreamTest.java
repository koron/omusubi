package net.kaoriya.omusubi;

import java.nio.IntBuffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntBufferOutputStreamTest {

    @Test
    public void write() {
        IntBuffer buf = IntBuffer.allocate(10);
        IntBufferOutputStream s = new IntBufferOutputStream(buf);
        s.write(0);
        s.write(1);
        s.write(new int[] { 2, 3, 4, 5 });
        s.write(new int[] { 6, 7, 8, 9, 10, 11, 12, 13 }, 2, 4);
        assertArrayEquals(new int[] {
            0, 1, 2, 3, 4, 5, 8, 9, 10, 11
        }, buf.array());
    }

}
