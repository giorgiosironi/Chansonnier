/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.lyricwiki.test;

import it.polimi.chansonnier.driver.lyricwiki.LyricWikiBackend;
import it.polimi.chansonnier.driver.lyricwiki.LyricWikiLyricsService;
import it.polimi.chansonnier.driver.lyricwiki.LyricWikiParser;
import it.polimi.chansonnier.spi.LyricsService;
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
