package net.kaoriya.qb.simple_compression;

import org.junit.Test;
import static org.junit.Assert.*;

public class DeltaZigzagEncodingTest {

    @Test
    public void ctor() {
        // not be used.
        DeltaZigzagEncoding enc = new DeltaZigzagEncoding();
    }

    public static int zigzagEncode(DeltaZigzagEncoding.IntEncoder e, int value)
    {
        e.setContextValue(0);
        return e.encodeInt(value);
    }

    public static int zigzagDecode(DeltaZigzagEncoding.IntDecoder d, int value) {
        d.setContextValue(0);
        return d.decodeInt(value);
    }

    public static void checkEncode(
            DeltaZigzagEncoding.IntEncoder e,
            int[] data,
            int[] expected)
    {
        assertArrayEquals(expected, e.encodeArray(data));
        assertEquals(data[data.length - 1], e.getContextValue());
    }

    public static void checkDecode(
            DeltaZigzagEncoding.IntDecoder d,
            int[] data,
            int[] expected)
    {
        int[] r = d.decodeArray(data);
        assertArrayEquals(expected, r);
        assertEquals(r[r.length - 1], d.getContextValue());
    }

    @Test
    public void checkZigzagEncode() {
        DeltaZigzagEncoding.IntEncoder e =
            new DeltaZigzagEncoding.IntEncoder();
        assertEquals(0, zigzagEncode(e, 0));
        assertEquals(2, zigzagEncode(e, 1));
        assertEquals(4, zigzagEncode(e, 2));
        assertEquals(6, zigzagEncode(e, 3));
        assertEquals(1, zigzagEncode(e, -1));
        assertEquals(3, zigzagEncode(e, -2));
        assertEquals(5, zigzagEncode(e, -3));
    }

    @Test
    public void checkZigzagDecoder() {
        DeltaZigzagEncoding.IntDecoder d =
            new DeltaZigzagEncoding.IntDecoder();
        assertEquals( 0, zigzagDecode(d, 0));
        assertEquals(-1, zigzagDecode(d, 1));
        assertEquals( 1, zigzagDecode(d, 2));
        assertEquals(-2, zigzagDecode(d, 3));
        assertEquals( 2, zigzagDecode(d, 4));
        assertEquals(-3, zigzagDecode(d, 5));
    }

    @Test
    public void checkEncodeSimple() {
        DeltaZigzagEncoding.IntEncoder e =
            new DeltaZigzagEncoding.IntEncoder();
        checkEncode(e,
            new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
            new int[]{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
    }

    @Test
    public void checkDecodeSimple() {
        DeltaZigzagEncoding.IntDecoder d =
            new DeltaZigzagEncoding.IntDecoder();
        checkDecode(d,
            new int[]{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
            new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    }

    @Test
    public void checkIntialContext() {
        DeltaZigzagEncoding.IntEncoder e
            = new DeltaZigzagEncoding.IntEncoder(10);
        int[] r1 = e.encodeArray(new int[] { 10, 11, 10, 12, 10 });
        assertArrayEquals(new int[]{ 0, 2, 1, 4, 3 }, r1);

        DeltaZigzagEncoding.IntDecoder d =
            new DeltaZigzagEncoding.IntDecoder(10);
        int[] r2 = d.decodeArray(r1);
        assertArrayEquals(new int[]{ 10, 11, 10, 12, 10 }, r2);
    }

    @Test
    public void checkLongContext() {
        DeltaZigzagEncoding.LongContext c =
            new DeltaZigzagEncoding.LongContext(123);
        assertEquals(123, c.getContextValue());

        c.setContextValue(456);
        assertEquals(456, c.getContextValue());

        c.setContextValue(789);
        assertEquals(789, c.getContextValue());
    }
}
