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
		WebResponse resp = addVideoLink("http://www.youtube.com/watch?v=e8w7f0ShtIM");
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));

		String link = "http://www.youtube.com/watch?v=GMDd4on20Yg";
		addVideoLink(link);
		WebRequest req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		assertWebPageContains(req, link, 300000);
//		assertSongsListContainsSongTitle(response, "Boulevard of Broken Dreams");
//		assertSongsListContainsSongArtist(response, "Green Day");
//		assertSongsListContainsSongLyrics(response, "I walk a lonely road");
//		assertSongsListContainsSongImage(response, "<img src=\"attachment?name=Image1&id=" + link + "\" />");
	}
	
	public void testGivenAnAddedYouTubeLinkTheSongIsSearchableThrougSolr() throws Exception {
		String hero = "http://www.youtube.com/watch?v=owTmJrtD7g8";
		WebResponse resp = addVideoLink(hero);
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		// TODO: avoid all errors "index does not exist in data dictionary [test_index]"
		assertWebPageContains(req, hero, 250000);
		
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
