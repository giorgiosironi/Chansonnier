package it.polimi.chansonnier.test;

import junit.framework.TestCase;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class AddSongTest extends TestCase {
	public void testNothing() throws Exception {
		WebConversation wc = new WebConversation();
		WebRequest     req = new GetMethodWebRequest( "http://localhost:8080/chansonnier/add" );
		WebResponse   resp = wc.getResponse( req );
		System.out.println(resp.getText().contains("Hello from AddServlet"));
		assertTrue(true);
	}
}
