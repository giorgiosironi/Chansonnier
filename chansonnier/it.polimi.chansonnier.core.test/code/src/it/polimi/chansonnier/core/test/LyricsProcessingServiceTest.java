/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.core.test;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;

import it.polimi.chansonnier.processing.LyricsProcessingService;
import it.polimi.chansonnier.spi.LyricsService;

public class LyricsProcessingServiceTest extends ProcessingServiceTest implements LyricsService {
	LyricsProcessingService _service;
	private static final String LYRICS = "The warden threw a party in the county jail...";
	private static final String TITLE = "Jailhouse Rock";
	private static final String ARTIST = "Elvis Presley";
	
	protected void init() throws Exception {
		_service = new LyricsProcessingService();
		_service.setLyricsService(this);
	}
	
	public void testStoresLyricsAsAnAnnotationStartingFromThePageTitle() throws Exception {
		final Id id = createNewRecord(ARTIST + " - " + TITLE);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertEquals(LYRICS, extractAttribute(id, "lyrics"));
	}
	
	public void testCreatesArtistAndTitleAttributesBasingOnWhichCombinationFindsTheLyrics() throws Exception {
		final Id id = createNewRecord(ARTIST + " - " + TITLE);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testStoresLyricsAsAnAnnotationWhenTitleComesBeforeArtist() throws Exception {
		final Id id = createNewRecord(TITLE + " - " + ARTIST);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertEquals(LYRICS, extractAttribute(id, "lyrics"));
	}
	
	public void testRemovesParenthetizedNoiseFromPageTitle() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " (with lyrics!)");
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testRemovesParenthetizedNoiseFromPageTitle2() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " (song & lyrics)"); 
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testRemovesParenthetizedNoiseFromPageTitle3() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " [ with lyrics]"); 
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testRemovesTheWordLyricsFromPageTitle() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " lyrics"); 
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testRemovesTheWordLyricsFromPageTitleWithoutCaseSensitivity() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " Lyrics"); 
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testRemovesTheWordsWithLyricsFromPageTitle() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " with lyrics"); 
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void testRemovesTheWordsWithLyricsFromPageTitleWithoutCaseSensitivity() throws Exception {
	    final Id id = createNewRecord(ARTIST + " - " + TITLE + " With Lyrics"); 
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertArtistAndTitleAttributesAreExtractedCorrectly(id);
	}
	
	public void assertArtistAndTitleAttributesAreExtractedCorrectly(Id id) throws BlackboardAccessException {
	    assertEquals(ARTIST, extractAttribute(id, "artist"));
	    assertEquals(TITLE, extractAttribute(id, "title"));
	}
	
	private Id createNewRecord(String pageTitle) throws BlackboardAccessException {
	    final Id id = createBlackboardRecord("youtube", "http://www.youtube.com/dummy");
	    setAttribute(id, new Path("PageTitle"), pageTitle);
	    return id;
	}
	
	private String extractAttribute(Id id, String attributeName) throws BlackboardAccessException {
		return getAttribute(id, new Path(attributeName)).toString();
	}
	
	@Override
	public String getLyrics(String title, String artist) {
		if (title.equals("Jailhouse Rock")
	     && artist.equals("Elvis Presley")) {
			return "The warden threw a party in the county jail...";
	    }
		return null;
	}
}
