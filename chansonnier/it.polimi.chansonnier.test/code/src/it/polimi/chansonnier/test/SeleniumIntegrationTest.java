package it.polimi.chansonnier.test;

import com.thoughtworks.selenium.*;

public class SeleniumIntegrationTest extends SeleneseTestCase {
	private SeleniumWrapper seleniumWrapper;
	
	public void setUp() throws Exception {
		seleniumWrapper = new SeleniumWrapper();
		seleniumWrapper.start();
		setUp("http://www.google.com/", "*chrome");
	}
	
	public void tearDown() {
		seleniumWrapper.stop();
	}
	
	public void testSeleniumRCIsStartedAndWorks() throws Exception {
		selenium.open("/");
		verifyTrue(selenium.isTextPresent("Google"));
	}	
}
