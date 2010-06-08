package it.polimi.chansonnier.driver.googletranslate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import it.polimi.chansonnier.spi.LanguageRecognitionService;
import it.polimi.chansonnier.spi.FuzzyResult;
import it.polimi.chansonnier.utils.URLUtils;

public class GoogleLanguageRecognitionService implements
		LanguageRecognitionService {

	@Override
	public FuzzyResult getLanguage(String textSample) {
		//-e http://www.my-ajax-site.com
		try {
			URL serviceEndpoint = new URL("http://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q="
					                   + URLUtils.escape(textSample));
			HttpURLConnection connection = (HttpURLConnection) serviceEndpoint.openConnection();
			connection.addRequestProperty("REFERER", "http://www.polimi.it");
			connection.connect();
			BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    connection.getInputStream()));

			StringBuilder builder = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
			builder.append("\n");
			}
			in.close();
			
			JSONObject response = new JSONObject(builder.toString());
			String language = response.getJSONObject("responseData").getString("language");
			Double confidence = response.getJSONObject("responseData").getDouble("confidence");
			return new FuzzyResult(language, confidence);//language;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//{"responseData": {
			//"language":"en",
			//"isReliable":false,
			//"confidence":0.114892714}
		//, "responseDetails": null, "responseStatus": 200}
		// TODO Auto-generated method stub
		return null;
	}
}
