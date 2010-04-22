package it.polimi.smilaintegration.test.unit;

import it.polimi.smilaintegration.LyricWikiBackend;
import it.polimi.smilaintegration.LyricWikiLyricsService;
import it.polimi.smilaintegration.LyricWikiParser;
import it.polimi.smilaintegration.LyricsService;
import junit.framework.TestCase;

public class LyricWikiLyricsServiceTest extends TestCase implements LyricWikiBackend, LyricWikiParser {
	private LyricsService service;
	
	public void setUp() {
		service = new LyricWikiLyricsService(this, this);
	}
	
	public void testChainsBackEndAndParser() {
		String lyrics = service.getLyrics("A Title", "An Artist");
		assertEquals("Lyrics Stub...", lyrics);
	}

	@Override
	public String getSong(String title, String artist) {
		// TODO Auto-generated method stub
		assertEquals("A Title", title);
		assertEquals("An Artist", artist);
		return "<XMLSTUB>";
	}

	@Override
	public String getLyrics(String xmlContent) {
		assertEquals("<XMLSTUB>", xmlContent);
		return "Lyrics Stub...";
	}
}
