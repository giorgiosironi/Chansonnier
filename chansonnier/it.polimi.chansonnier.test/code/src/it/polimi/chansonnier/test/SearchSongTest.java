/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class SearchSongTest extends AcceptanceTest {
	public void testGivenAnAddedYouTubeLinkTheSongIsSearchable() throws Exception {
		String link = "http://www.youtube.com/watch?v=GMDd4on20Yg";
		addVideoLink(link);
		// TODO: avoid all errors "index does not exist in data dictionary [test_index]"
		WebRequest req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/search" );
		req.setParameter("lyrics", "I walk alone");
		WebResponse response = assertWebPageContains(req, link, 300000);
        // TODO: make it case insensitive
		assertSearchPageContainsSongTitle(response, "Boulevard of Broken Dreams");
		assertSearchPageContainsSongArtist(response, "Green Day");
		assertSearchPageContainsSongLyrics(response, "I walk a lonely road");
		assertSearchPageContainsSongImage(response, "<img src=\"attachment?name=Image1&id=" + link + "\" />");

        String hero = "http://www.youtube.com/watch?v=owTmJrtD7g8";
		addVideoLink(hero);
		req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/search" );
		req.setParameter("lyrics", "Would you dance");
		response = assertWebPageContains(req, hero, 300000);
		assertSearchPageContainsSongTitle(response, "Hero");
		assertSearchPageContainsSongArtist(response, "Enrique Iglesias");
		assertSearchPageContainsSongLyrics(response, "if I asked you to dance?");
		assertSearchPageContainsSongImage(response, "<img src=\"attachment?name=Image1&id=" + hero + "\" />");
        assertWebPageNotContains(response, "Boulevard of Broken Dreams");
    }

	private void assertSearchPageContainsSongTitle(WebResponse res, String text) 
		throws Exception {
		assertWebPageContains(res, text);
	}
	
	
	private void assertSearchPageContainsSongLyrics(WebResponse res,
			String text) throws Exception {
		assertWebPageContains(res, text);
		
	}

	private void assertSearchPageContainsSongArtist(WebResponse res,
			String text) throws Exception {
		assertWebPageContains(res, text);
	}

	private void assertSearchPageContainsSongImage(WebResponse res,
			String text) throws Exception {
		assertWebPageContains(res, text);	
    }
}
