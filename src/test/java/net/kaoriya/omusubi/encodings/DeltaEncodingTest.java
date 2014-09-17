package net.kaoriya.omusubi.encodings;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class DeltaEncodingTest {
    @Test
    public void ctor() {
        // not be used.
        DeltaEncoding e = new DeltaEncoding();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void intAscendEncodeError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("input:0 must be greater than or equals 999");
        IntEncoder e = new DeltaEncoding.IntAscendEncoder(999);
        e.encodeInt(0);
    }

    @Test
    public void intAscendDecodeError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("input:-1 must be greater than or equal zero");
        IntEncoder e = new DeltaEncoding.IntAscendDecoder(0);
        e.encodeInt(-1);
    }

    @Test
    public void longAscendEncodeError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("input:0 must be greater than or equals 999");
        LongEncoder e = new DeltaEncoding.LongAscendEncoder(999);
        e.encodeLong(0);
    }

    @Test
    public void longAscendDecodeError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("input:-1 must be greater than or equal zero");
        LongEncoder e = new DeltaEncoding.LongAscendDecoder(0);
        e.encodeLong(-1);
    }
}
