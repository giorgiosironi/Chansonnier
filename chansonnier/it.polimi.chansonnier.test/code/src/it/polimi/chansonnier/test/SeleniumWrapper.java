package it.polimi.chansonnier.test;

import java.io.File;

public class SeleniumWrapper {
	private Process solr;
	private static final String FOLDER_PROPERTY = "it.polimi.chansonnier.selenium.root";
	
	public void start() throws Exception {
		Runtime r = Runtime.getRuntime();
		solr =  r.exec("/usr/bin/java -jar selenium-server.jar", null, getSeleniumRoot());
		Thread.sleep(5000);
	}
	
	public void stop() {
		if (solr != null) {
			solr.destroy();
		}
	}
	
	private File getSeleniumRoot() throws Exception {
		String root = System.getProperty(FOLDER_PROPERTY);
		if (root == null) {
			throw new Exception("Selenium RC path is not specified, please add the property " + FOLDER_PROPERTY);
		}
		return new File(root);
	}
}
