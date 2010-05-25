package it.polimi.chansonnier.agent.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import it.polimi.chansonnier.agent.URLUtils;
import junit.framework.TestCase;

public class URLUtilsTest extends TestCase {
	public void testMakesAnHttpConnectionToRetrieveUrlContent() throws Exception {
		String content = URLUtils.retrieve(new URL("http://www.google.com"));
		System.out.println(content);
		assertTrue(content.contains("<html>"));
	}
}
