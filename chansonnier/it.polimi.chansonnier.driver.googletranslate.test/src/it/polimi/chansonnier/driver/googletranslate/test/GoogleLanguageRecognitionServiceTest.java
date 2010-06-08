package it.polimi.chansonnier.driver.googletranslate.test;

import it.polimi.chansonnier.driver.googletranslate.GoogleLanguageRecognitionService;
import junit.framework.TestCase;

public class GoogleLanguageRecognitionServiceTest extends TestCase {
	GoogleLanguageRecognitionService _service;

	protected void setUp() throws Exception {
		_service = new GoogleLanguageRecognitionService();
	}
	
	public void testRecognizesAnEnglishSentence() {
		String language = _service.getLanguage("This is an example of an English sentence that should be recognized by _service.");
		assertEquals("en", language);
	}
	
	public void testRecognizesAnItalianSentence() {
		String language = _service.getLanguage("Questa è una frase italiana che dovrebbe essere riconosciuta come tale.");
		assertEquals("it", language);
	}
	
	public void testRecognizesAFrenchSentence() {
		String language = _service.getLanguage("Viens, mon beau chat, sur mon coeur amoureux; "
				                            + "etiens les griffes de ta patte,"
				                            + "Et laisse-moi plonger dans tes beaux yeux");
		assertEquals("fr", language);
	}
	
	public void testReturnsEmptyStringForAnAmbiguosSentence() {
		String language = _service.getLanguage("Ciao ciao, hello my friend, allons enfants de la Patrie");
		assertEquals("", language);
	}

	public void testReturnsEmptyStringForATooShortSentence() {
		String language = _service.getLanguage("Ciao");
		assertEquals("", language);
	}

}
