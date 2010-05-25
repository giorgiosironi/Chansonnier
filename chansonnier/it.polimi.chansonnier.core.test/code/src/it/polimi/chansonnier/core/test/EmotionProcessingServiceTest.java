/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
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
	
	public void testAddsAnEmotionAttributeUsingTheLyricsOne() throws Exception {
		final Id id = createBlackboardRecord("source", "item");
		Path p = new Path("Lyrics");
	    setAttribute(id, p, LYRICS);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    Literal emotion = getBlackboard().getLiteral(id, new Path("Emotion"));
	    assertEquals("Happiness", emotion.getStringValue());
	}

	@Override
	public String getEmotion(String textSample) {
		assertEquals(LYRICS, textSample);
		return "Happiness";
	}

}
