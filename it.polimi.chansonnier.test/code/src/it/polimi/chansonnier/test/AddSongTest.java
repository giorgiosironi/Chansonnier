package it.polimi.chansonnier.test;

import junit.framework.TestCase;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class AddSongTest extends TestCase {
	public void testNothing() throws Exception {
		WebConversation wc = new WebConversation();
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		WebResponse   resp = wc.getResponse( req );
		assertTrue(resp.getText().contains("Hello from AddServlet"));
	}
	
	public void testGivenAYouTubeLinkAddsTheRelatedSongToTheIndex() throws Exception {
		WebConversation wc = new WebConversation();
		PostMethodWebRequest	req = new PostMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		req.setParameter("link", "http://YoutubeDummyLink.com");
		WebResponse resp = wc.getResponse(req);
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));
		Thread.sleep(30000);
		WebConversation wc2 = new WebConversation();
		WebRequest     req2 = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		WebResponse   resp2 = wc2.getResponse( req2 );
		assertTrue(resp2.getText().contains("With or without you"));
	}
}
