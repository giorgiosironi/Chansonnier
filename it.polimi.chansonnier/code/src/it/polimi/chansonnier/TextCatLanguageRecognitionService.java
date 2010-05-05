package it.polimi.chansonnier;

import it.polimi.chansonnier.spi.LanguageRecognitionService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextCatLanguageRecognitionService implements
		LanguageRecognitionService {
	private final static String TEXTCAT_BIN = "/home/giorgio/libtextcat-2.2/src/testtextcat";
	private final static String TEXTCAT_LANGCLASS = "/home/giorgio/libtextcat-2.2/langclass/";

	@Override
	public String getLanguage(String textSample) {
		Runtime r = Runtime.getRuntime();
		String command = TEXTCAT_BIN + " conf.txt";
		File dir = new File(TEXTCAT_LANGCLASS);
		try {
			Process textcat = r.exec(command, new String[0], dir);
			OutputStream os = textcat.getOutputStream();
			_feedText(os, textSample);
			List<String> lines = _convertStreamToLines(textcat.getInputStream());
			return _resultInterpretation(lines); 
		} catch (IOException e) {
			throw new RuntimeException("Unable to run textcat: " + e.getMessage());
		}
	}
	
	private void _feedText(OutputStream os, String text) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(os);
		writer.write(text);
		writer.flush();
		writer.close();
	}
	
    private List<String> _convertStreamToLines(InputStream is) throws IOException {
    	List<String> lines = new ArrayList<String>();
    	String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
    
    private String _resultInterpretation(List<String> lines) {
    	Pattern shortResult = Pattern.compile("^Result == SHORT");
		Pattern multipleResult = Pattern.compile("^Result == (\\[([a-z]+)\\]){2,}");
		Pattern singleResult = Pattern.compile("^Result == \\[([a-z]+)\\]");
		String language = null;
		for (String line : lines) {
			Matcher m = shortResult.matcher(line);
			if (m.find()) {
				return "";
			}
			m = multipleResult.matcher(line);
			if (m.find()) {
				return "";
			}
			m = singleResult.matcher(line);
			if (m.find()) {
				language = _ucfirst(m.group(1));
			}
		}
		return language;
    }
    
    private String _ucfirst(String string) {
    	return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
    }
}
