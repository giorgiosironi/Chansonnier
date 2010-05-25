/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;

public class SearchSongTest extends AcceptanceTest {
	public void testGivenAnAddedYouTubeLinkTheSongIsSearchable() throws Exception {
		String link = "http://www.youtube.com/watch?v=5tK7-OuYfJc";
		addVideoLink(link);
		// TODO: avoid all errors "index does not exist in data dictionary [test_index]"
		WebRequest req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/search" );
		req.setParameter("lyrics", "I walk alone");
		assertWebPageContains(req, link, 300000);
        // TODO: make it case insensitive
		assertSearchPageContainsSongTitle(req, "Boulevard of Broken Dreams");
		assertSearchPageContainsSongArtist(req, "Green Day");
		assertSearchPageContainsSongLyrics(req, "I walk a lonely road"); // other lyrics
	}

	private void assertSearchPageContainsSongTitle(WebRequest req, String text) 
		throws Exception {
		assertWebPageContains(req, text, 20000);
	}
	
	
	private void assertSearchPageContainsSongLyrics(WebRequest req,
			String text) throws Exception {
		assertWebPageContains(req, text, 20000);
		
	}

	private void assertSearchPageContainsSongArtist(WebRequest req,
			String text) throws Exception {
		assertWebPageContains(req, text, 20000);
		
	}
}
