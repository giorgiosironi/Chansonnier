package it.polimi.chansonnier.test.unit;

import it.polimi.chansonnier.FfmpegTranscodingService;
import it.polimi.chansonnier.spi.TranscodingService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

public class FfmpegTranscodingServiceTest extends TestCase {
	TranscodingService transcoder;
	
	public void setUp() {
		transcoder = new FfmpegTranscodingService();
	}
	
	public void testConvertsFlvToWav() throws Exception {
		File output = transcoder.convert(getFlv(), "wav");
		assertContentEquals(getWav(), output);
	}
	
	public File getFlv() {
		return new File("test/flv/desmond.flv");
	}
	
	public File getWav() {
		return new File("test/desmond.wav");
	}
	
	private void assertContentEquals( File expected, File actual )
    throws IOException
{
    InputStream expectedIs = new java.io.FileInputStream( expected );
    try {
        InputStream actualIs = new java.io.FileInputStream( actual );
        try {
            byte[] buf0 = new byte[ 1024 ];
            byte[] buf1 = new byte[ 1024 ];
            int n0 = 0;
            int n1 = 0;

            while(-1 != n0) {
                n0 = expectedIs.read(buf0);
                n1 = actualIs.read(buf1);
                assertTrue("The files " + expected + " and " + actual +
                           " have differing number of bytes available (" + n0 +
                           " vs " + n1 + ")",
                           n0 == n1);

                assertTrue("The files " + expected + " and " + actual +
                           " have different content",
                           Arrays.equals(buf0, buf1));
            }
        } finally {
            actualIs.close();
        }
    } finally {
        expectedIs.close();
    }
}

}
