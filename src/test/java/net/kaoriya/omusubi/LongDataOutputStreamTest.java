package net.kaoriya.omusubi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class LongDataOutputStreamTest
{
    public static class LimitedStream extends FilterOutputStream {

        private final int limit;

        private final ByteArrayOutputStream bytesStream;

        public LimitedStream(int limit) {
            super(new ByteArrayOutputStream(limit));
            this.limit = limit;
            this.bytesStream = (ByteArrayOutputStream)this.out;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            super.write(b, off, len);
            checkLimit();
        }

        @Override
        public void write(byte[] b) throws IOException
        {
            super.write(b);
            checkLimit();
        }

        @Override
        public void write(int b) throws IOException {
            super.write(b);
            checkLimit();
        }

        private void checkLimit() throws IOException {
            if (this.bytesStream.size() > this.limit) {
                throw new IOException("buffer overflow");
            }
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void writeFailure() throws Exception {
        LongDataOutputStream s = new LongDataOutputStream(
                new DataOutputStream(new LimitedStream(10)));
        s.write(0L);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Underlying DataOutput throws an exception");
        s.write(1L);
    }

    @Test
    public void writeArrayFailure() {
        LongDataOutputStream s = new LongDataOutputStream(
                new DataOutputStream(new LimitedStream(10)));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Underlying DataOutput throws an exception");
        s.write(new long[] { 0, 1 }, 0, 2);
    }
}
