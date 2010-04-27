package it.polimi.smilaintegration.test.integration;

import it.polimi.smilaintegration.HttpLyricWikiBackend;
import it.polimi.smilaintegration.LyricWikiBackend;
import junit.framework.TestCase;

public class HttpLyricWikiBackendTest extends TestCase {
	LyricWikiBackend backend;
	
	protected void setUp() throws Exception {
		backend = new HttpLyricWikiBackend(); 
	}
	
	public void testObtainsTheResponseFromTheWebAsAnXmlString() {
		String xmlResult = backend.getSong("Pride", "U2");
		assertTrue(xmlResult.indexOf("<LyricsResult>") > -1);
	}
	
	public void testRetrievesLyricOfTheRequestedSong() {
		String xmlResult = backend.getSong("Pride", "U2");
		assertTrue(xmlResult.indexOf("One man come in the name of love") > -1);
	}
	
	public void testEscapesSpacesInTheSongName() {
		String xmlResult = backend.getSong("Radio gaga", "Queen");
		assertTrue(xmlResult.indexOf("sit alone and watch") > -1);
	}
	public void testEscapesSpacesInTheArtistName() {
		String xmlResult = backend.getSong("Thriller", "Michael Jackson");
		assertTrue(xmlResult.indexOf("Thriller night") > -1);
	}
}
