/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.test;



import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebForm;

public class AddSongTest extends AcceptanceTest {
	public void testTheAddPageIsLoaded() throws Exception {
		WebConversation wc = new WebConversation();
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		WebResponse   resp = wc.getResponse( req );
        WebForm add = resp.getForms()[0];
        assertEquals("add", add.getAction());
        assertEquals("post", add.getMethod());
        String[] parameters = add.getParameterNames();
        assertEquals(1, parameters.length);
        assertEquals("link", parameters[0]);
        assertEquals(1, add.getSubmitButtons().length);
	}
	
	public void testGivenAYouTubeLinkAddsTheRelatedSongToTheIndex() throws Exception {
		WebResponse resp = addVideoLink("http://www.youtube.com/watch?v=e8w7f0ShtIM");
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		WebResponse res = assertWebPageContains(req, "Beautiful Day", 300000);
		assertWebPageContains(res, "U2");
		assertWebPageContains(res, "The heart is a bloom");
		assertWebPageContains(res, "happiness");
		assertWebPageContains(res, "http://www.youtube.com/watch?v=e8w7f0ShtIM");
	}
}
