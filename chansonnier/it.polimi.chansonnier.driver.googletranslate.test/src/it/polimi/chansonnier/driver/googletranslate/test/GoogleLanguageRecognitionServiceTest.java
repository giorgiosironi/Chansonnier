package it.polimi.chansonnier.driver.googletranslate.test;

import it.polimi.chansonnier.driver.googletranslate.GoogleLanguageRecognitionService;
import it.polimi.chansonnier.spi.FuzzyResult;
import junit.framework.TestCase;

public class GoogleLanguageRecognitionServiceTest extends TestCase {
	GoogleLanguageRecognitionService _service;

	protected void setUp() throws Exception {
		_service = new GoogleLanguageRecognitionService();
	}
	
	public void testRecognizesAnEnglishSentence() {
		FuzzyResult language = _service.getLanguage("This is an example of an English sentence that should be recognized by _service.");
		assertEquals("en", language.getValue());
		assertGreatherThan(language.getConfidence(), 0.9);
	}
	
	public void testSanitizesASentenceWithLineBreaks() {
		FuzzyResult language = _service.getLanguage("This is an example of an English sentence\n"
				 								  + "that should be recognized by _service.");
		assertEquals("en", language.getValue());
	}
	
	public void testRecognizesAnItalianSentence() {
		FuzzyResult language = _service.getLanguage("Questa è una frase italiana che dovrebbe essere riconosciuta come tale.");
		assertEquals("it", language.getValue());
		assertGreatherThan(language.getConfidence(), 0.9);
	}
	
	public void testRecognizesAFrenchSentence() {
		FuzzyResult language = _service.getLanguage("Viens, mon beau chat, sur mon coeur amoureux; "
				                            + "etiens les griffes de ta patte,"
				                            + "Et laisse-moi plonger dans tes beaux yeux");
		assertEquals("fr", language.getValue());
		assertGreatherThan(language.getConfidence(), 0.4);
	}
	
	public void testReturnsEmptyStringForAnAmbiguosSentence() {
		FuzzyResult language = _service.getLanguage("Ciao ciao, hello my friend, allons enfants de la Patrie");
		assertLessThan(language.getConfidence(), 0.5);

	}

	public void testReturnsEmptyStringForATooShortSentence() {
		FuzzyResult language = _service.getLanguage("Ciao");
		assertLessThan(language.getConfidence(), 0.1);

	}
	
	private void assertGreatherThan(Double confidence, Double reference) {
		assertTrue("Confidence is " + confidence, confidence > reference);
	}
	
	private void assertLessThan(Double confidence, Double reference) {
		assertTrue("Confidence is " + confidence, confidence < reference);
	}

}
