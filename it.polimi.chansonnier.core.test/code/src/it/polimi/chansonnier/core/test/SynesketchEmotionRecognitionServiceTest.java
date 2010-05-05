package it.polimi.chansonnier.core.test;

import it.polimi.chansonnier.SynesketchEmotionRecognitionService;
import it.polimi.chansonnier.spi.EmotionRecognitionService;
import junit.framework.TestCase;

public class SynesketchEmotionRecognitionServiceTest extends TestCase {
	private EmotionRecognitionService synesketch = new SynesketchEmotionRecognitionService();
	
	public void testRecognizesHappiness() {
		String emotion = synesketch.getEmotion("I'm so glad it's a sunny day!");
		assertEquals("happiness", emotion);
	}
	
	public void testRecognizesSadness() {
		String emotion = synesketch.getEmotion("I am full of sorrow and regrets...");
		assertEquals("sadness", emotion);
	}
	
	public void testRecognizesFear() {
		String emotion = synesketch.getEmotion("I am shaking... Horror movies scare me");
		assertEquals("fear", emotion);
	}
	
	public void testRecognizesAnger() {
		String emotion = synesketch.getEmotion("I am mad at you!");
		assertEquals("anger", emotion);
	}
	
	public void testRecognizesDisgust() {
		String emotion = synesketch.getEmotion("This is so repulsive...");
		assertEquals("disgust", emotion);
	}
	
	public void testRecognizesSurprise() {
		String emotion = synesketch.getEmotion("This is really unexpected for me!");
		assertEquals("surprise", emotion);
	}
	
	public void testDoesNotJudgeIfThereIsNoStrongestEmotion()
	{
		String emotion = synesketch.getEmotion("Hello.");
		assertEquals("", emotion);
	}
}
