package net.kaoriya.omusubi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class ExampleByteBufferTest
{

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public static void write(File f, int[] d) throws IOException {
        FileOutputStream o = new FileOutputStream(f, false);
        try {
            o.write(IntAscSDBP.toBytes(d));
        } finally {
            o.close();
        }
    }

    private static MappedByteBuffer mapFile(File file) throws IOException {
        FileInputStream s = new FileInputStream(file);
        try {
            FileChannel c = s.getChannel();
            return c.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    public static byte[] unionFiles(File a, File b) throws IOException {
        return IntAscSDBP.union(mapFile(a), mapFile(b));
    }

    public void unionViaFile(int[] a, int[] b, int[] expected, String message)
        throws IOException
    {
        File fa = tempFolder.newFile("union-a.bin");
        write(fa, a);
        File fb = tempFolder.newFile("union-b.bin");
        write(fb, b);
        byte[] c = unionFiles(fa, fb);
        int[] d = IntAscSDBP.fromBytes(c);
        assertArrayEquals(message, expected, d);
    }

    @Test
    public void union() throws Exception {
        unionViaFile(new int[]{0, 3, 6, 9}, new int[]{0, 2, 4, 6, 8},
                new int[]{0, 2, 3, 4, 6, 8, 9}, "basic");
    }
}
