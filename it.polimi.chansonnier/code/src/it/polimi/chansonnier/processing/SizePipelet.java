package it.polimi.chansonnier.processing;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

public class SizePipelet implements SimplePipelet {
	private static final String SIZE_TYPE = "SIZE_TYPE";
	
	private String _sizeType;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		for (Id id : recordIds) {
			try {
				Annotation a = blackboard.createAnnotation(id);
				a.addAnonValue(_sizeType);
				final Path path = new Path("SizeType");
				blackboard.addAnnotation(id, path, "SizeType", a);
			} catch (final Exception e) {
				throw new ProcessingException("During execution of SizePipelet: " + e.toString());
			}
		}
		return recordIds;
	}

	@Override
	public void configure(PipeletConfiguration configuration)
			throws ProcessingException {
		_sizeType = ((String) configuration.getPropertyFirstValueNotNull(SIZE_TYPE)).trim();
		if (_sizeType.length() == 0) {
			throw new ProcessingException("Property " + SIZE_TYPE + " is empty.");
		}
	}

}
