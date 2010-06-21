package it.polimi.chansonnier.core.test;

import it.polimi.chansonnier.processing.LanguageProcessingService;
import it.polimi.chansonnier.spi.FuzzyResult;
import it.polimi.chansonnier.spi.LanguageRecognitionService;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;

public class LanguageProcessingServiceTest extends ProcessingServiceTest implements LanguageRecognitionService {
	LanguageProcessingService _languageProcessingService;
	public static final String LYRICS = "Vamos a la playa...";
	
	protected void init() throws Exception {
		_languageProcessingService = new LanguageProcessingService();
		_languageProcessingService.setLanguageRecognitionService(this);
		_service = _languageProcessingService;
		inputAnnotationValue = "myLyrics";
		outputAnnotationValue = "myLanguage";
	}
	
	public void testAddsALanguageAttributeUsingTheLyricsOne() throws Exception {
		final Id id = createBlackboardRecord("source", "item");
		Path p = new Path(inputAnnotationValue);
	    setAttribute(id, p, LYRICS);
	    
	    process(id);
	    
	    Literal language = getBlackboard().getLiteral(id, new Path(outputAnnotationValue));
	    assertEquals("es", language.getStringValue());
	    Literal confidence = getBlackboard().getLiteral(id, new Path(outputAnnotationValue + "Confidence"));
	    assertEquals(0.9, confidence.getFpValue());
	}

	@Override
	public FuzzyResult getLanguage(String textSample) {
		assertEquals(LYRICS, textSample);
		return new FuzzyResult("es", 0.9);
	}


}
