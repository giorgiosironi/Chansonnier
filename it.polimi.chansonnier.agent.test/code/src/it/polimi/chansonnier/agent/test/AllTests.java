package it.polimi.chansonnier.agent.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for it.polimi.chansonnier.agent.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(YoutubeGrabberTest.class);

		//$JUnit-END$
		return suite;
	}

}
