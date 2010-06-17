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
	public void testSolrInstanceCanBeUsedForStorageAndSearch() throws Exception {
		String url = "http://localhost:8983/solr";
		CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
		server.setParser(new XMLResponseParser());
		
		server.deleteByQuery( "*:*" );
		
	    SolrInputDocument doc = new SolrInputDocument();
	    doc.addField("id", "myId", 1.0f);
	    doc.addField("title", "myDummyTitle", 1.0f);
	    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	    docs.add(doc);
	    server.add(docs);
	    server.commit();
	    
	    SolrQuery query = new SolrQuery();
	    query.setQuery("*:*");
	    QueryResponse rsp = server.query(query);
	    SolrDocumentList docList = rsp.getResults();
	    assertEquals("myDummyTitle", docList.get(0).get("title").toString());
	}
}
