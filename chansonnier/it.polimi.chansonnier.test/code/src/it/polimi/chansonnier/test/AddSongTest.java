package it.polimi.chansonnier.test;



import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class AddSongTest extends AcceptanceTest {
	public void testTheAddPageIsLoaded() throws Exception {
		WebConversation wc = new WebConversation();
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		WebResponse   resp = wc.getResponse( req );
		assertTrue(resp.getText().contains("Hello from AddServlet"));
	}
	
	public void testGivenAYouTubeLinkAddsTheRelatedSongToTheIndex() throws Exception {
		WebResponse resp = addVideoLink("http://www.youtube.com/watch?v=e8w7f0ShtIM");
		// TODO insert redirect
		assertTrue(resp.getText().contains("Success"));
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/last" );
		assertWebPageContains(req, "Beautiful Day", 300000);
	}
	

}
