/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;


import it.polimi.chansonnier.fixtures.Fixtures;

import java.io.InputStream;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.smila.datamodel.id.Id;

public class AddPipelineTest extends FunctionalTest {
	public static final String PIPELINE_NAME = "AddPipeline";
	
	@Override
	protected String getPipelineName() {
	    return PIPELINE_NAME;
	}
	
	public void testSongsAreIndexedInSolr() throws Exception {
		InputStream heroFlv = Fixtures.class.getResourceAsStream("hero.flv");
		Id[] result = fixtureManager.addSong("http://www.youtube.com/watch?v=owTmJrtD7g8", heroFlv, "Enrique Iglesias- Hero (with lyrics)");
		assertEquals(1, result.length);
		InputStream haloFlv = Fixtures.class.getResourceAsStream("halo.flv");
		result = fixtureManager.addSong("http://www.youtube.com/watch?v=fSdgBse1o7Q", haloFlv, "Beyonce-Halo Lyrics");
		assertEquals(1, result.length);
		Thread.sleep(15000);
		
		SolrQuery query = new SolrQuery();
	    query.setQuery( "title:Hero" );
	    QueryResponse rsp = solrServer.query( query );
	    SolrDocumentList docList = rsp.getResults();
	    assertEquals(1, docList.size());
	    SolrDocument song = docList.get(0);
	    assertEquals("http://www.youtube.com/watch?v=owTmJrtD7g8", song.get("link"));
	    assertEquals("Enrique Iglesias", song.get("artist"));
	    assertEquals("Hero", song.get("title"));
	    assertTrue(((String) song.get("lyrics")).contains("if I asked you to dance"));
	    assertEquals("anger", song.get("emotion"));
	    assertEquals("en", song.get("language"));
	    Collection attachmentNames = song.getFieldValues("image");
	    assertEquals(3, attachmentNames.size());
	    
		query = new SolrQuery();
	    query.setQuery( "title:Halo" );
	    rsp = solrServer.query( query );
	    docList = rsp.getResults();
	    assertEquals(1, docList.size());
	    song = docList.get(0);
	    assertEquals("http://www.youtube.com/watch?v=fSdgBse1o7Q", song.get("link"));
	    assertEquals("Beyonce", song.get("artist"));
	    assertEquals("Halo", song.get("title"));
	    assertEquals("surprise", song.get("emotion"));
	    assertEquals("en", song.get("language"));
	    assertTrue(((String) song.get("lyrics")).contains("Remember those walls I built?"));
	    attachmentNames = song.getFieldValues("image");
	    assertEquals(3, attachmentNames.size());
	}
}
