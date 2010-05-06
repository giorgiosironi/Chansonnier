package it.polimi.chansonnier.processing;

import it.polimi.chansonnier.spi.EmotionRecognitionService;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.impl.AnnotationImpl;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class EmotionProcessingService implements ProcessingService {

	private EmotionRecognitionService _emotionRecognitionService;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		Path p = new Path();
		p.add("Lyrics");
		try {
			for (Id id : recordIds) {
				String lyrics = blackboard.getLiteral(id, p).toString();
				String emotion = _emotionRecognitionService.getEmotion(lyrics);
				Annotation ann = new AnnotationImpl();
				ann.addAnonValue(emotion);
				blackboard.addAnnotation(id, p, "Emotion", ann);
			}
		} catch (BlackboardAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recordIds;
	}

	public void setEmotionRecognitionService(
			EmotionRecognitionService emotionRecognitionService) {
		_emotionRecognitionService = emotionRecognitionService;
		
	}

}
