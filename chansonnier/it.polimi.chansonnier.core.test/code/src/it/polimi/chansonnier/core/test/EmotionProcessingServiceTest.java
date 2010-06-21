/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.core.test;

import it.polimi.chansonnier.processing.EmotionProcessingService;
import it.polimi.chansonnier.spi.EmotionRecognitionService;
import it.polimi.chansonnier.spi.FuzzyResult;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;


public class EmotionProcessingServiceTest extends ProcessingServiceTest implements EmotionRecognitionService {
	protected EmotionProcessingService _emotionProcessingService;
	protected static final String LYRICS = "I wanna feel sunlight on my face...";
	
	protected void init() throws Exception {
		_emotionProcessingService = new EmotionProcessingService();
		_emotionProcessingService.setEmotionRecognitionService(this);
		_service = _emotionProcessingService;
		inputAnnotationValue = "myLyrics";
		outputAnnotationValue = "myEmotion";
	}
	
	public void testAddsAnEmotionAttributeUsingTheLyricsOne() throws Exception {
		final Id id = createBlackboardRecord("source", "item");
		Path p = new Path(inputAnnotationValue);
	    setAttribute(id, p, LYRICS);
	    
	    process(id);
	    
	    Literal emotion = getBlackboard().getLiteral(id, new Path(outputAnnotationValue));
	    assertEquals("Happiness", emotion.getStringValue());
	    Literal confidence = getBlackboard().getLiteral(id, new Path(outputAnnotationValue + "Confidence"));
	    assertEquals(0.9, confidence.getFpValue());
	}

	@Override
	public FuzzyResult getEmotion(String textSample) {
		assertEquals(LYRICS, textSample);
		return new FuzzyResult("Happiness", 0.9);
	}

}
