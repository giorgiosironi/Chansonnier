/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;



import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebForm;

public class AddSongTest extends AcceptanceTest {
	public void testTheAddPageIsLoaded() throws Exception {
		WebConversation wc = new WebConversation();
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		WebResponse   resp = wc.getResponse( req );
        WebForm add = resp.getForms()[0];
        assertEquals("add", add.getAction());
        assertEquals("post", add.getMethod());
        String[] parameters = add.getParameterNames();
        assertEquals(1, parameters.length);
        assertEquals("link", parameters[0]);
        assertEquals(1, add.getSubmitButtons().length);
	}
	
	public void testGivenAYouTubeLinkAddsTheRelatedSongToTheLastIndexedList() throws Exception {

		String link = "http://www.youtube.com/watch?v=GMDd4on20Yg";
		WebResponse resp = addVideoLink(link);
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));

		WebRequest req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
        resp = assertWebPageContains(req, link, 300000);
	}
	
	public void testGivenAnAddedYouTubeLinkTheSongIsSearchableThrougSolr() throws Exception {
		String hero = "http://www.youtube.com/watch?v=owTmJrtD7g8";
		WebResponse resp = addVideoLink(hero);
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		// TODO: avoid all errors "index does not exist in data dictionary [test_index]"
		assertWebPageContains(req, hero, 250000);
        Thread.sleep(10000);
		
		String url = "http://localhost:8983/solr";
		CommonsHttpSolrServer server = new CommonsHttpSolrServer( url );
		server.setParser(new XMLResponseParser());
	    SolrQuery query = new SolrQuery();
	    query.setQuery( "*:*" );
	    QueryResponse rsp = server.query( query );
	    SolrDocumentList docList = rsp.getResults();
	    assertEquals(1, docList.size());
	    SolrDocument song = docList.get(0);
	    assertEquals("Enrique Iglesias", song.get("Artist"));
	    assertEquals("Hero", song.get("Title"));
	    assertTrue(((String) song.get("Lyrics")).contains("if I asked you to dance"));
	    assertEquals("anger", song.get("Emotion"));
	}
}
