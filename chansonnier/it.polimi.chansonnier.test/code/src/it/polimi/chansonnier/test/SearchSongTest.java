/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;

import it.polimi.chansonnier.fixtures.Fixtures;

import java.io.InputStream;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;

public class SearchSongTest extends AcceptanceTest {	
	public void testGivenAnAddedYouTubeLinkTheSongIsSearchable() throws Exception {
		InputStream beautifulDayFlv = Fixtures.class.getResourceAsStream("beautifulday.flv");
		fixtureManager.addSong("http://www.youtube.com/watch?v=e8w7f0ShtIM", beautifulDayFlv, "U2 - Beautiful Day (with Lyrics)");
		InputStream heroFlv = Fixtures.class.getResourceAsStream("hero.flv");
		fixtureManager.addSong("http://www.youtube.com/watch?v=owTmJrtD7g8", heroFlv, "Enrique Iglesias- Hero (with lyrics)");
		InputStream haloFlv = Fixtures.class.getResourceAsStream("halo.flv");
		fixtureManager.addSong("http://www.youtube.com/watch?v=fSdgBse1o7Q", haloFlv, "Beyonce-Halo Lyrics");
		
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		assertWebPageContains(req, "http://www.youtube.com/watch?v=e8w7f0ShtIM", 20000);
		Thread.sleep(10000);
		
		selenium.open("/chansonnier/index.html");
		selenium.click("link=happiness");
        waitForPageToLoad();
        wrapped.verifyTrue(selenium.isTextPresent("(x) emotion:happiness"));
        wrapped.verifyTrue(selenium.isTextPresent("Beautiful Day"));
		wrapped.verifyTrue(selenium.isTextPresent("The heart is a bloom"));
		selenium.click("link=*emotion:happiness");
		selenium.click("link=anger");
        waitForPageToLoad();
		wrapped.verifyTrue(selenium.isTextPresent("(x) emotion:anger"));
        wrapped.verifyTrue(selenium.isTextPresent("Hero"));
		wrapped.verifyTrue(selenium.isTextPresent("Would you dance"));
		selenium.click("link=*emotion:anger");
        waitForPageToLoad();
		selenium.click("link=en");
		wrapped.verifyTrue(selenium.isTextPresent("of 3 results"));
    }
}
