package it.polimi.chansonnier.processing;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingService;

public abstract class AbstractProcessingService implements ProcessingService {

	public AbstractProcessingService() {
		super();
	}

	protected String getInputPath(Blackboard blackboard, Id id)
			throws BlackboardAccessException {
				return annotationToString(blackboard, id, "it.polimi.chansonnier.processing.Input");
			}

	protected String getOutputPath(Blackboard blackboard, Id id)
			throws BlackboardAccessException {
				return annotationToString(blackboard, id, "it.polimi.chansonnier.processing.Output");
			}

	private String annotationToString(Blackboard blackboard, Id id, String name)
			throws BlackboardAccessException {
				Annotation inputAttribute = blackboard.getAnnotation(id, null, name);
				if (inputAttribute == null) {
					throw new BlackboardAccessException("Annotation '" + name + "' does not exist on current record.");
				}
				return inputAttribute.getAnonValues().iterator().next();
			}

}