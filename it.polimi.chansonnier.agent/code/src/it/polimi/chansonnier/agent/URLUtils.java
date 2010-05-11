package it.polimi.chansonnier.agent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class URLUtils {

	public static String retrieve(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("GET");
	    InputStream is = conn.getInputStream();
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    for (int bytesRead = 0; (bytesRead = is.read(buffer)) != -1; ) {
	      output.write(buffer, 0, bytesRead);
	    }
	    return output.toString();
	}

}
