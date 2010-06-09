package it.polimi.chansonnier.test;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class SolrWrapper {
	private Process solr;
	
	public void start() throws IOException, InterruptedException {
		Runtime r = Runtime.getRuntime();
		solr =  r.exec("/usr/bin/java -jar start.jar", null, new File("../../solr"));
		Thread.sleep(5000);
	}
	
	public void stop() {
		if (solr != null) {
			solr.destroy();
		}
	}
}
