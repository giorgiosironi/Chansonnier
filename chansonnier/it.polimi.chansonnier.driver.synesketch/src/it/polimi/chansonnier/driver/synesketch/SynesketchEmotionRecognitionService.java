/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.synesketch;

import it.polimi.chansonnier.spi.EmotionRecognitionService;
import it.polimi.chansonnier.spi.FuzzyResult;

import java.io.IOException;

import synesketch.emotion.Emotion;
import synesketch.emotion.EmotionalState;
import synesketch.emotion.Empathyscope;

public class SynesketchEmotionRecognitionService implements
		EmotionRecognitionService {

	@Override
	public FuzzyResult getEmotion(String textSample) {
         EmotionalState state;
		try {
			state = Empathyscope.getInstance().feel(textSample);
			Emotion strongest = state.getFirstStrongestEmotions(1).get(0);
			return _toFuzzy(strongest, state);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       return null;
	}
	
	private FuzzyResult _toFuzzy(Emotion e, EmotionalState state) {
		if (e.getType() == Emotion.HAPPINESS) {
			return new FuzzyResult("happiness", state.getHappinessWeight());
		}
		if (e.getType() == Emotion.SADNESS) {
			return new FuzzyResult("sadness", state.getSadnessWeight());
		}
		if (e.getType() == Emotion.FEAR) {
			return new FuzzyResult("fear", state.getFearWeight());
		}
		if (e.getType() == Emotion.ANGER) {
			return new FuzzyResult("anger", state.getAngerWeight());
		}
		if (e.getType() == Emotion.DISGUST) {
			return new FuzzyResult("disgust", state.getDisgustWeight());
		}
		if (e.getType() == Emotion.SURPRISE) {
			return new FuzzyResult("surprise", state.getSurpriseWeight());
		}
		return new FuzzyResult("", 0.0);
	}
}
