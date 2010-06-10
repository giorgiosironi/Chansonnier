/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;


import java.io.File;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Literal;

public class AddPipelineTest extends FunctionalTest {
	public static final String PIPELINE_NAME = "AddPipeline";
	
	@Override
	protected String getPipelineName() {
	    return PIPELINE_NAME;
	}
	
	public void testSongIsIndexedInSolr() throws Exception {		
		Id[] result = fixtureManager.addSong("http://www.youtube.com/watch?v=owTmJrtD7g8", new File("fixtures/hero.flv"), "Enrique Iglesias- Hero (with lyrics)");
		assertEquals(1, result.length);
		Thread.sleep(5000);
		
		SolrQuery query = new SolrQuery();
	    query.setQuery( "*:*" );
	    QueryResponse rsp = solrServer.query( query );
	    SolrDocumentList docList = rsp.getResults();
	    assertEquals(1, docList.size());
	    SolrDocument song = docList.get(0);
	    assertEquals("Enrique Iglesias", song.get("Artist"));
	    assertEquals("Hero", song.get("Title"));
	    assertTrue(((String) song.get("Lyrics")).contains("if I asked you to dance"));
	    assertEquals("anger", song.get("Emotion"));
	}
}
