/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.synesketch.test;

import it.polimi.chansonnier.driver.synesketch.SynesketchEmotionRecognitionService;
import it.polimi.chansonnier.spi.EmotionRecognitionService;
import it.polimi.chansonnier.spi.FuzzyResult;
import junit.framework.TestCase;

public class SynesketchEmotionRecognitionServiceTest extends TestCase {
	private EmotionRecognitionService synesketch = new SynesketchEmotionRecognitionService();
	
	public void testRecognizesHappiness() {
		FuzzyResult emotion = synesketch.getEmotion("I'm so glad it's a sunny day!");
		assertEquals("happiness", emotion.getValue());
		assertGreaterThan(emotion.getConfidence(), 0.4);
	}
	
	public void testRecognizesSadness() {
		FuzzyResult emotion = synesketch.getEmotion("I am full of sorrow and regrets...");
		assertEquals("sadness", emotion.getValue());
		assertGreaterThan(emotion.getConfidence(), 0.15);
	}
	
	public void testRecognizesFear() {
		FuzzyResult emotion = synesketch.getEmotion("I am shaking... Horror movies scare me");
		assertEquals("fear", emotion.getValue());
		assertGreaterThan(emotion.getConfidence(), 0.3);
	}
	
	public void testRecognizesAnger() {
		FuzzyResult emotion = synesketch.getEmotion("I am mad at you!");
		assertEquals("anger", emotion.getValue());
		assertGreaterThan(emotion.getConfidence(), 0.2);
	}
	
	public void testRecognizesDisgust() {
		FuzzyResult emotion = synesketch.getEmotion("This is so repulsive...");
		assertEquals("disgust", emotion.getValue());
		assertGreaterThan(emotion.getConfidence(), 0.18);
	}
	
	public void testRecognizesSurprise() {
		FuzzyResult emotion = synesketch.getEmotion("This is really unexpected for me!");
		assertEquals("surprise", emotion.getValue());
		assertGreaterThan(emotion.getConfidence(), 0.75);
	}
	
	public void testDoesNotJudgeIfThereIsNoStrongestEmotion()
	{
		FuzzyResult emotion = synesketch.getEmotion("Hello.");
		assertLessThan(emotion.getConfidence(), 0.1);
	}
	
	private void assertGreaterThan(Double confidence, Double reference) {
		assertTrue("Confidence is " + confidence, confidence > reference);
	}
	
	private void assertLessThan(Double confidence, Double reference) {
		assertTrue("Confidence is " + confidence, confidence < reference);
	}
}
