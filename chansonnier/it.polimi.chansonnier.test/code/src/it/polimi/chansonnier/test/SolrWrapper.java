package it.polimi.chansonnier.test;

import java.io.File;

public class SolrWrapper {
	private Process solr;
	private static final String FOLDER_PROPERTY = "it.polimi.chansonnier.solr.root";
	
	public void start() throws Exception {
		Runtime r = Runtime.getRuntime();
		solr =  r.exec("/usr/bin/java -jar start.jar", null, getSolrRoot());
		Thread.sleep(5000);
	}
	
	public void stop() {
		if (solr != null) {
			solr.destroy();
		}
	}
	
	private File getSolrRoot() throws Exception {
		String root = System.getProperty(FOLDER_PROPERTY);
		if (root == null) {
			throw new Exception("Solr path is not specified, please add the property " + FOLDER_PROPERTY);
		}
		return new File(root);
	}
}
