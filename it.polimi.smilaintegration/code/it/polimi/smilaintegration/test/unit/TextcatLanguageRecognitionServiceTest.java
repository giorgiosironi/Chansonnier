package it.polimi.smilaintegration.test.unit;

import it.polimi.smilaintegration.LanguageRecognitionService;
import it.polimi.smilaintegration.TextCatLanguageRecognitionService;
import junit.framework.TestCase;

public class TextcatLanguageRecognitionServiceTest extends TestCase {
	private LanguageRecognitionService textcat = new TextCatLanguageRecognitionService();
	
	public void testRecognizesAnEnglishSentence() {
		String language = textcat.getLanguage("This is an example of an English sentence that should be recognized by textcat.");
		assertEquals("English", language);
	}
	
	public void testRecognizesAnItalianSentence() {
		String language = textcat.getLanguage("Questa è una frase italiana che dovrebbe essere riconosciuta come tale.");
		assertEquals("Italian", language);
	}
	
	public void testRecognizesAFrenchSentence() {
		String language = textcat.getLanguage("Viens, mon beau chat, sur mon coeur amoureux; "
				                            + "etiens les griffes de ta patte,"
				                            + "Et laisse-moi plonger dans tes beaux yeux");
		assertEquals("French", language);
	}
	
	public void testReturnsEmptyStringForAnAmbiguosSentence() {
		String language = textcat.getLanguage("Ciao ciao, hello my friend, allons enfants de la Patrie");
		assertEquals("", language);
	}

	public void testReturnsEmptyStringForATooShortSentence() {
		String language = textcat.getLanguage("Ciao");
		assertEquals("", language);
	}
}
