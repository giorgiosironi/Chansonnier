package it.polimi.chansonnier.driver.synesketch.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for it.polimi.chansonnier.driver.synesketch.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(SynesketchEmotionRecognitionServiceTest.class);
		//$JUnit-END$
		return suite;
	}

}
