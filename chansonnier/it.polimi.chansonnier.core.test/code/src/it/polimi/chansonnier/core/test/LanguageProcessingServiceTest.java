package it.polimi.chansonnier.core.test;

import it.polimi.chansonnier.processing.LanguageProcessingService;
import it.polimi.chansonnier.spi.FuzzyResult;
import it.polimi.chansonnier.spi.LanguageRecognitionService;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;

public class LanguageProcessingServiceTest extends ProcessingServiceTest implements LanguageRecognitionService {

	LanguageProcessingService _service;
	public static final String LYRICS = "Vamos a la playa...";
	
	protected void init() throws Exception {
		_service = new LanguageProcessingService();
		_service.setLanguageRecognitionService(this);
	}
	
	public void testAddsALanguageAttributeUsingTheLyricsOne() throws Exception {
		final Id id = createBlackboardRecord("source", "item");
		Path p = new Path("lyrics");
	    setAttribute(id, p, LYRICS);
	    
	    Id[] result = _service.process(getBlackboard(), new Id[] { id });
	    assertEquals(1, result.length);
	    
	    Literal language = getBlackboard().getLiteral(id, new Path("language"));
	    assertEquals("es", language.getStringValue());
	    Literal confidence = getBlackboard().getLiteral(id, new Path("languageConfidence"));
	    assertEquals(0.9, confidence.getFpValue());
	}

	@Override
	public FuzzyResult getLanguage(String textSample) {
		assertEquals(LYRICS, textSample);
		return new FuzzyResult("es", 0.9);
	}


}
