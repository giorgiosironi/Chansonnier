package it.polimi.chansonnier.core.test;

import it.polimi.chansonnier.processing.EmotionProcessingService;
import it.polimi.chansonnier.spi.EmotionRecognitionService;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;


public class EmotionProcessingServiceTest extends ProcessingServiceTest implements EmotionRecognitionService {
	EmotionProcessingService _service;
	public static final String LYRICS = "I wanna feel sunlight on my face...";
	
	protected void init() throws Exception {
		_service = new EmotionProcessingService();
		_service.setEmotionRecognitionService(this);
	}
	
	public void testAnnotatesTextWithTheEmotionFound() throws Exception {
		final Id id = createBlackboardRecord("source", "item");
		Path p = new Path("Lyrics");
	    setAttribute(id, p, LYRICS);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    Literal lyrics = getBlackboard().getLiteral(id, new Path("Lyrics"));
	    final String text = lyrics.getAnnotation("Emotion").getAnonValues().toArray()[0].toString();
	    assertEquals("Happiness", text);
	}

	@Override
	public String getEmotion(String textSample) {
		assertEquals(LYRICS, textSample);
		return "Happiness";
	}

}
