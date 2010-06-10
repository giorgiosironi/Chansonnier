package it.polimi.chansonnier.test;


import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

public abstract class FunctionalTest extends AWorkflowProcessorTest {

	private SolrWrapper solrWrapper;
	protected CommonsHttpSolrServer solrServer;
	protected FixtureManager fixtureManager;

	public FunctionalTest() {
		super();
	}

	public void setUp() throws Exception {
		super.setUp();
		solrWrapper = new SolrWrapper();
		try {
			solrWrapper.start();
			String url = "http://localhost:8983/solr";
			solrServer = new CommonsHttpSolrServer( url );
			solrServer.setParser(new XMLResponseParser());
			solrServer.deleteByQuery( "*:*" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		fixtureManager = new FixtureManager(getProcessor(), getBlackboard(), getPipelineName());
	}

	public void tearDown() {
		solrWrapper.stop();
		try {
			super.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}