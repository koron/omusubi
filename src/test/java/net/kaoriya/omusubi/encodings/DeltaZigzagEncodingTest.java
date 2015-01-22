package net.kaoriya.omusubi.encodings;

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

    public static void checkEncodeDecode(
            int[] original,
            int[] encoded)
    {
        DeltaZigzagEncoding.IntEncoder e =
            new DeltaZigzagEncoding.IntEncoder();
        int[] actual = e.encodeArray(original);
        assertArrayEquals(encoded, actual);
        assertEquals(original[original.length - 1], e.getContextValue());

        DeltaZigzagEncoding.IntDecoder d =
            new DeltaZigzagEncoding.IntDecoder();
        int[] decoded = d.decodeArray(actual);
        assertArrayEquals(original, decoded);
        assertEquals(decoded[decoded.length - 1], d.getContextValue());
    }

    public static void checkEncodeDecode(
            long[] original,
            long[] encoded)
    {
        DeltaZigzagEncoding.LongEncoder e =
            new DeltaZigzagEncoding.LongEncoder();
        long[] actual = e.encodeArray(original);
        assertArrayEquals(encoded, actual);
        assertEquals(original[original.length - 1], e.getContextValue());

        DeltaZigzagEncoding.LongDecoder d =
            new DeltaZigzagEncoding.LongDecoder();
        long[] decoded = d.decodeArray(actual);
        assertArrayEquals(original, decoded);
        assertEquals(decoded[decoded.length - 1], d.getContextValue());
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
    public void encodeMaxValue() {
        DeltaZigzagEncoding.IntEncoder e =
            new DeltaZigzagEncoding.IntEncoder();
        checkEncode(e,
                new int[]{ 0, 1, 2, Integer.MAX_VALUE },
                new int[]{ 0, 2, 2, 0xFFFFFFFA});
    }

    @Test
    public void decodeMaxValue() {
        DeltaZigzagEncoding.IntDecoder d =
            new DeltaZigzagEncoding.IntDecoder();
        checkDecode(d,
                new int[]{ 0, 2, 2, 0xFFFFFFFA},
                new int[]{ 0, 1, 2, Integer.MAX_VALUE });
    }

    @Test
    public void intMinMax() {
        checkEncodeDecode(
                new int[]{ 1, Integer.MAX_VALUE },
                new int[]{ 2, 0xFFFFFFFC });
        checkEncodeDecode(
                new int[]{ 0, Integer.MAX_VALUE },
                new int[]{ 0, 0xFFFFFFFE });
        checkEncodeDecode(
                new int[]{ -1, Integer.MAX_VALUE },
                new int[]{  1, 0xFFFFFFFF });
        checkEncodeDecode(
                new int[]{ -2, Integer.MAX_VALUE },
                new int[]{  3, 0xFFFFFFFD });

        checkEncodeDecode(
                new int[]{ 1, Integer.MIN_VALUE },
                new int[]{ 2, 0xFFFFFFFE });
        checkEncodeDecode(
                new int[]{ 0, Integer.MIN_VALUE },
                new int[]{ 0, 0xFFFFFFFF });
        checkEncodeDecode(
                new int[]{ -1, Integer.MIN_VALUE },
                new int[]{  1, 0xFFFFFFFD });
        checkEncodeDecode(
                new int[]{ -2, Integer.MIN_VALUE },
                new int[]{  3, 0xFFFFFFFB });

        checkEncodeDecode(
                new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE },
                new int[]{ 0xFFFFFFFF, 1 });
        checkEncodeDecode(
                new int[]{ Integer.MAX_VALUE, Integer.MIN_VALUE },
                new int[]{ 0xFFFFFFFE, 2 });
    }

    @Test
    public void longMinMax() {
        checkEncodeDecode(
                new long[]{ 1l, Long.MAX_VALUE },
                new long[]{ 2l, 0xFFFFFFFFFFFFFFFCl });
        checkEncodeDecode(
                new long[]{ 0l, Long.MAX_VALUE },
                new long[]{ 0l, 0xFFFFFFFFFFFFFFFEl });
        checkEncodeDecode(
                new long[]{ -1l, Long.MAX_VALUE },
                new long[]{  1l, 0xFFFFFFFFFFFFFFFFl });
        checkEncodeDecode(
                new long[]{ -2l, Long.MAX_VALUE },
                new long[]{  3l, 0xFFFFFFFFFFFFFFFDl });

        checkEncodeDecode(
                new long[]{ 1l, Long.MIN_VALUE },
                new long[]{ 2l, 0xFFFFFFFFFFFFFFFEl });
        checkEncodeDecode(
                new long[]{ 0l, Long.MIN_VALUE },
                new long[]{ 0l, 0xFFFFFFFFFFFFFFFFl });
        checkEncodeDecode(
                new long[]{ -1l, Long.MIN_VALUE },
                new long[]{  1l, 0xFFFFFFFFFFFFFFFDl });
        checkEncodeDecode(
                new long[]{ -2l, Long.MIN_VALUE },
                new long[]{  3l, 0xFFFFFFFFFFFFFFFBl });

        checkEncodeDecode(
                new long[]{ Long.MIN_VALUE, Long.MAX_VALUE },
                new long[]{ 0xFFFFFFFFFFFFFFFFl, 1 });
        checkEncodeDecode(
                new long[]{ Long.MAX_VALUE, Long.MIN_VALUE },
                new long[]{ 0xFFFFFFFFFFFFFFFEl, 2 });
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
        LongContext c = new LongContext(123);
        assertEquals(123, c.getContextValue());

        c.setContextValue(456);
        assertEquals(456, c.getContextValue());

        c.setContextValue(789);
        assertEquals(789, c.getContextValue());
    }

    @Test
    public void checkLongEncoder() {
        DeltaZigzagEncoding.LongEncoder e1 =
            new DeltaZigzagEncoding.LongEncoder();
        long[] r1 = e1.encodeArray(new long[] { 0, 1, 0, 2, 0 });
        assertArrayEquals(new long[]{ 0, 2, 1, 4, 3 }, r1);
        assertEquals(0, e1.getContextValue());

        DeltaZigzagEncoding.LongEncoder e2 =
            new DeltaZigzagEncoding.LongEncoder(10);
        long[] r2 = e2.encodeArray(new long[] { 10, 11, 10, 12, 10 });
        assertArrayEquals(new long[]{ 0, 2, 1, 4, 3 }, r2);
        assertEquals(10, e2.getContextValue());
    }

    @Test
    public void checkLongDecoder() {
        DeltaZigzagEncoding.LongDecoder d1 =
            new DeltaZigzagEncoding.LongDecoder();
        long[] r1 = d1.decodeArray(new long[] { 0, 2, 1, 4, 3 });
        assertArrayEquals(new long[]{ 0, 1, 0, 2, 0 }, r1);
        assertEquals(0, d1.getContextValue());

        DeltaZigzagEncoding.LongDecoder d2 =
            new DeltaZigzagEncoding.LongDecoder(10);
        long[] r2 = d2.decodeArray(new long[] { 0, 2, 1, 4, 3 });
        assertArrayEquals(new long[]{ 10, 11, 10, 12, 10 }, r2);
        assertEquals(10, d2.getContextValue());
    }
}
