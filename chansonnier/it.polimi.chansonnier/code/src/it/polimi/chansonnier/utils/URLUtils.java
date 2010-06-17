/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLUtils {
	static final int LENGTH = 256;

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
	
	public static String readStart(InputStream is) throws Exception {
		byte[] bytes = new byte[LENGTH];
		int len = is.read(bytes, 0, LENGTH);
		if (len != LENGTH) {
			throw new IOException("Length read was not sufficient.");
		}
		return new String(bytes);    
	}

	public static String readStart(File fp) throws Exception {
		return readStart(new FileInputStream(fp));
	}

	public static String readStart(String filename) throws Exception {
		File fp = new File(filename);
		return readStart(fp);
	}

	public static String escape(String textSample) {
		return textSample.replaceAll(" ", "%20");
	}
}
