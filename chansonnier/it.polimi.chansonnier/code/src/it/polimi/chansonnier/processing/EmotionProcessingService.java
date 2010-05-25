package it.polimi.chansonnier.processing;

import it.polimi.chansonnier.spi.EmotionRecognitionService;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class EmotionProcessingService implements ProcessingService {

	private EmotionRecognitionService _emotionRecognitionService;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		Path p = new Path("Lyrics");
		try {
			for (Id id : recordIds) {
				Literal lyrics = blackboard.getLiteral(id, p);
				String emotion = _emotionRecognitionService.getEmotion(lyrics.toString());
				Literal value = blackboard.createLiteral(id);
				value.setStringValue(emotion);
				blackboard.addLiteral(id, new Path("Emotion"), value);
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
