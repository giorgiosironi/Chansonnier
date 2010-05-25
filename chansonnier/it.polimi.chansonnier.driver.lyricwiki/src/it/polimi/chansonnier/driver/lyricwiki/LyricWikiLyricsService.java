/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.lyricwiki;

import it.polimi.chansonnier.spi.LyricsService;

/**
 * Mediator between LyricWikiBackend and LyricWikiParser
 * @author giorgio
 *
 */
public class LyricWikiLyricsService implements LyricsService {

	private LyricWikiBackend _backend;
	private LyricWikiParser _parser;

	public LyricWikiLyricsService(LyricWikiBackend backend, LyricWikiParser parser) {
		_backend = backend;
		_parser = parser;
	}
	
	/**
	 * default constructor called by the OSGi framework
	 */
	public LyricWikiLyricsService() {
		_backend = new HttpLyricWikiBackend();
		_parser = new XMLLyricWikiParser();
	}

	@Override
	public String getLyrics(String title, String artist) {
		String rawResponse = _backend.getSong(title, artist);
		return _parser.getLyrics(rawResponse);
	}

}
