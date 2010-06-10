package it.polimi.chansonnier.test;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;


import junit.framework.TestCase;

public class SolrIntegrationTest extends TestCase {
	private SolrWrapper solrWrapper;
	
	public void testSolrInstanceCanBeStartedQueriedAndStopped() throws Exception {
		solrWrapper = new SolrWrapper();
		solrWrapper.start();

		String url = "http://localhost:8983/solr";
		CommonsHttpSolrServer server = new CommonsHttpSolrServer( url );
		server.setParser(new XMLResponseParser());
		server.deleteByQuery( "*:*" );// delete everything!
	    SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField( "id", "id1", 1.0f );
	    doc1.addField( "name", "doc1", 1.0f );
	    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	    docs.add( doc1 );
	    server.add( docs );
	    server.commit();
	    
	    SolrQuery query = new SolrQuery();
	    query.setQuery( "*:*" );
	    //query.addSortField( "name", SolrQuery.ORDER.asc );
	    QueryResponse rsp = server.query( query );
	    SolrDocumentList docList = rsp.getResults();
	    assertEquals("[id1, doc1]", docList.get(0).values().toString());
	}
	
	public void tearDown() {
		solrWrapper.stop();
	}
}
