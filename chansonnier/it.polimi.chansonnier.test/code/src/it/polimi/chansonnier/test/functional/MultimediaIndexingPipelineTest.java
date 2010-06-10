/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test.functional;

import java.io.File;

import it.polimi.chansonnier.test.SolrWrapper;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

public class MultimediaIndexingPipelineTest extends AWorkflowProcessorTest {
	public static final String PIPELINE_NAME = "AddPipeline";
	private SolrWrapper solrWrapper;
	protected CommonsHttpSolrServer solrServer;
	
	@Override
	protected String getPipelineName() {
	    return PIPELINE_NAME;
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
	}

	public void testSongIsIndexedInSolr() throws Exception {		
		Id hero = createRecord();
		final Id[] result = getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { hero });
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
		  //assertTrue(getBlackboard().hasAttribute(result[0], filenamePath));
		  //assertEquals(1, getBlackboard().getLiteralsSize(result[0], filenamePath));
		  //assertEquals("readme.txt ...edited by DummyProcessingService", getBlackboard().getLiteral(result[0], filenamePath).toString());

	}
	
	public Id createRecord() throws Exception {
		final Id song = createBlackboardRecord("source", "key");
		final Literal pageTitle = getBlackboard().createLiteral(song);
		pageTitle.setStringValue("Enrique Iglesias- Hero (with lyrics)");
		getBlackboard().setLiteral(song, new Path("PageTitle"), pageTitle);
		File original = new File("fixtures/hero.flv");
		getBlackboard().setAttachmentFromFile(song, "Original", original);
		return song;
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
