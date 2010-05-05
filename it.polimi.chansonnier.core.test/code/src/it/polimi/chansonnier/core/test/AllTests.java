package it.polimi.chansonnier.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for it.polimi.chansonnier.core.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(LyricsProcessingServiceTest.class);
		suite.addTestSuite(SynesketchEmotionRecognitionServiceTest.class);
		suite.addTestSuite(FfmpegTranscodingServiceTest.class);
		suite.addTestSuite(TextcatLanguageRecognitionServiceTest.class);
		//$JUnit-END$
		return suite;
	}

}
