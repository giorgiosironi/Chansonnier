package it.polimi.chansonnier.test;

import junit.framework.TestCase;

import com.thoughtworks.selenium.*;

public class SeleniumIntegrationTest extends TestCase {
	private SeleniumWrapper seleniumWrapper;
	WrappableSeleneseTestCase wrapped;
	Selenium selenium;
	
	
	public void setUp() throws Exception {
		seleniumWrapper = new SeleniumWrapper();
		seleniumWrapper.start();
		wrapped = new WrappableSeleneseTestCase();
		wrapped.setUp("http://www.google.com/", "*chrome");
		selenium = wrapped.getSelenium();
	}
	
	public void tearDown() throws Exception {
		wrapped.tearDown();
		seleniumWrapper.stop();
	}
	
	public void testSeleniumRCIsStartedAndWorks() throws Exception {
		selenium.open("/");
		wrapped.verifyTrue(selenium.isTextPresent("Google"));
	}	
}
