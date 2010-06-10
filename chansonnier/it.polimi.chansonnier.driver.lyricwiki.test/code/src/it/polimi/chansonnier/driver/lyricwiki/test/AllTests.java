package it.polimi.chansonnier.driver.lyricwiki.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for it.polimi.chansonnier.driver.lyricwiki.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(LyricWikiLyricsServiceTest.class);
		suite.addTestSuite(HttpLyricWikiBackendTest.class);
		suite.addTestSuite(LyricWikiParserTest.class);
		//$JUnit-END$
		return suite;
	}

}
