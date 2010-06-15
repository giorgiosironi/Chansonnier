package it.polimi.chansonnier.test;


import it.polimi.chansonnier.utils.FixtureManager;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

public abstract class FunctionalTest extends AWorkflowProcessorTest {
	protected CommonsHttpSolrServer solrServer;
	protected FixtureManager fixtureManager;

	public FunctionalTest() {
		super();
	}

	public void setUp() throws Exception {
		super.setUp();
		try {
			String url = "http://localhost:8983/solr";
			solrServer = new CommonsHttpSolrServer( url );
			solrServer.setParser(new XMLResponseParser());
			solrServer.deleteByQuery( "*:*" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		fixtureManager = new FixtureManager(getProcessor(), getBlackboard(), getPipelineName());
	}
}