package it.polimi.chansonnier.test;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

public class WrappableSeleneseTestCase extends SeleneseTestCase {
	public Selenium getSelenium() {
		return selenium;
	}
}
