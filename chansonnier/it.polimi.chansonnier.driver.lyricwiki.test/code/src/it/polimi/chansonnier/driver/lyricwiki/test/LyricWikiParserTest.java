/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.lyricwiki.test;

import it.polimi.chansonnier.driver.lyricwiki.LyricWikiParser;
import it.polimi.chansonnier.driver.lyricwiki.XMLLyricWikiParser;

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
