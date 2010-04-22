package it.polimi.smilaintegration.test.unit;

import it.polimi.smilaintegration.LyricWikiParser;
import it.polimi.smilaintegration.XMLLyricWikiParser;

import java.io.File;
import java.io.FileInputStream;
import junit.framework.TestCase;


public class LyricWikiParserTest extends TestCase {
	private LyricWikiParser parser;

	public void setUp() throws Exception {
		parser = new XMLLyricWikiParser();
	}
	
	public void testExtractLyricsFromXml() throws Exception {
		String xmlContent = fileToString("./test/lyricwiki_song_fixture.xml");
		String lyrics = parser.getLyrics(xmlContent);
		assertTrue(lyrics.indexOf("all that glitters is gold") > -1);
	}

	private static String fileToString(String path) throws java.io.IOException {
	    byte[] buffer = new byte[(int) new File(path).length()];
	    FileInputStream f = new FileInputStream(path);
	    f.read(buffer);
	    return new String(buffer);
	}
}
