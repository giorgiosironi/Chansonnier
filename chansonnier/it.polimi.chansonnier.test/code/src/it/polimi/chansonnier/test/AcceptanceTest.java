/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import junit.framework.TestCase;

public abstract class AcceptanceTest extends TestCase {
	private SolrWrapper solrWrapper;
	
	public void setUp() {
		solrWrapper = new SolrWrapper();
		try {
			solrWrapper.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void tearDown() {
		solrWrapper.stop();
	}
	
	protected WebResponse addVideoLink(String link) throws Exception {
		WebConversation wc = new WebConversation();
		PostMethodWebRequest	req = new PostMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		req.setParameter("link", link);
		WebResponse resp = wc.getResponse(req);
		return resp;
	}

	protected WebResponse assertWebPageContains(WebRequest req, String text, int timeout) throws Exception {
        int tries = timeout / 10000;
        for (int i = 0; i < tries; i++) {
            System.out.println("Try " + i + " for text ''" + text + "'");
            WebConversation wc = new WebConversation();
            WebResponse   resp = wc.getResponse( req );
            System.out.println(resp.getText());
            if (resp.getText().contains(text)) {
                assertTrue(true);
                return resp;
            }
            Thread.sleep(10000);
        }
        fail("After " + timeout + "milliseconds of waiting, the web page does not contain the prescribed text (" + text + ").");
        return null;
    }

	protected void assertWebPageContains(WebResponse response, String text) throws Exception {
        assertTrue("The page does not contain the text '" + text + "'.", response.getText().contains(text));
    }

	protected void assertWebPageNotContains(WebResponse response, String text) throws Exception {
        assertFalse("The page should not contain \"" + text + "\", but it does.", response.getText().contains(text));
    }

	protected void assertSongsListContainsSongTitle(WebResponse res, String text) 
		throws Exception {
		assertWebPageContains(res, text);
	}
	
	protected void assertSongsListContainsSongLyrics(WebResponse res,
			String text) throws Exception {
		assertWebPageContains(res, text);
		
	}

    protected void assertSongsListContainsSongEmotion(WebResponse res, String text) throws Exception {
		assertWebPageContains(res, text);
    }

	protected void assertSongsListContainsSongArtist(WebResponse res,
			String text) throws Exception {
		assertWebPageContains(res, text);
	}

	protected void assertSongsListContainsSongImage(WebResponse res,
			String text) throws Exception {
		assertWebPageContains(res, text);	
    }
}
