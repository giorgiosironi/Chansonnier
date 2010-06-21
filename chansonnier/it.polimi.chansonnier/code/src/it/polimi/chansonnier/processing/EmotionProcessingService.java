/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.processing;

import it.polimi.chansonnier.spi.EmotionRecognitionService;
import it.polimi.chansonnier.spi.FuzzyResult;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class EmotionProcessingService extends AbstractProcessingService implements ProcessingService {

	private EmotionRecognitionService _emotionRecognitionService;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		try {
			for (Id id : recordIds) {
				Path p = new Path(getInputPath(blackboard, id));
				Literal lyrics = blackboard.getLiteral(id, p);
				FuzzyResult emotion = _emotionRecognitionService.getEmotion(lyrics.toString());
				Literal value = blackboard.createLiteral(id);
				value.setStringValue(emotion.getValue());
				blackboard.addLiteral(id, new Path(getOutputPath(blackboard, id)), value);
				Literal confidence = blackboard.createLiteral(id);
				confidence.setFpValue(emotion.getConfidence());
				blackboard.addLiteral(id, new Path(getOutputPath(blackboard, id) + "Confidence"), confidence);
			}
		} catch (BlackboardAccessException e) {
			throw new ProcessingException(e);
		}
		return recordIds;
	}

	public void setEmotionRecognitionService(
			EmotionRecognitionService emotionRecognitionService) {
		_emotionRecognitionService = emotionRecognitionService;
		
	}

}
