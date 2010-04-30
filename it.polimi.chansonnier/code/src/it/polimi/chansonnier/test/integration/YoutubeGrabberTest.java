package it.polimi.chansonnier.test.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import it.polimi.chansonnier.YoutubeGrabber;
import junit.framework.TestCase;

public class YoutubeGrabberTest extends TestCase {
	private static final int LENGTH = 256;
	
	private YoutubeGrabber _grabber;
	
	protected void setUp() throws Exception {
		_grabber = new YoutubeGrabber();
	}
	
	public void testLedZeppelinSStairwayToHeavenIsDownloaded() throws Exception {
		assertFlvStartIsTheSame("http://www.youtube.com/watch?v=BcL---4xQYA", "test/flv/stairway_to_heaven_start.dat");
	}
	
	public void testGreenDaySBasketCaseIsDownloaded() throws Exception {
		assertFlvStartIsTheSame("http://www.youtube.com/watch?v=GTwJo0HeNmU", "test/flv/basketcase_start.dat");
	}
	
	public void testGreenDaySTimeOfYourLifeIsDownloaded() throws Exception {
		assertFlvStartIsTheSame("http://www.youtube.com/watch?v=IR6uz_VTCUo", "test/flv/time_of_your_life_start.dat");
	}
	
	public void assertFlvStartIsTheSame(String pageUrl, String datFile) throws Exception {
		InputStream is = _grabber.getVideo(pageUrl);
		assertEquals(readStart(datFile), readStart(is));
	}
	
	private String readStart(InputStream is) throws Exception {
		byte[] bytes = new byte[LENGTH];
		int read = is.read(bytes, 0, LENGTH);
		assertEquals(LENGTH, read);
		return new String(bytes);    
	}

	private String readStart(String filename) throws Exception {
		File fp = new File(filename);
		return readStart(new FileInputStream(fp));
	}
}
