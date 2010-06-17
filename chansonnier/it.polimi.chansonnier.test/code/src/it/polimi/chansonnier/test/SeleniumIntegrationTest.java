package it.polimi.chansonnier.test;

import junit.framework.TestCase;

import com.thoughtworks.selenium.Selenium;

public class SeleniumIntegrationTest extends TestCase {
	WrappableSeleneseTestCase wrapped;
	Selenium selenium;
	
	public void setUp() throws Exception {
		wrapped = new WrappableSeleneseTestCase();
		wrapped.setUp("http://www.google.com/", "*chrome");
		selenium = wrapped.getSelenium();
	}
	
	public void tearDown() throws Exception {
		wrapped.tearDown();
	}
	
	public void testSeleniumRCIsStartedAndWorks() throws Exception {
		selenium.open("/");
		wrapped.verifyTrue(selenium.isTextPresent("Google"));
	}	
}
