package it.polimi.chansonnier.fixtures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Fixtures {
	public static InputStream getAsStream(String name) {
		return Fixtures.class.getResourceAsStream(name);
	}
	
	public static File getAsFile(String name) throws IOException {
		InputStream is = getAsStream(name);
		String tmpdir = System.getProperty("java.io.tmpdir");
		File f = new File(tmpdir + "/" + name);
	    OutputStream out = new FileOutputStream(f);
	    byte buf[] = new byte[1024];
	    int len;
	    while ((len = is.read(buf)) > 0) {
	    	out.write(buf, 0, len);
	    }
	    out.close();
	    is.close();
	    return f;
	}
}
