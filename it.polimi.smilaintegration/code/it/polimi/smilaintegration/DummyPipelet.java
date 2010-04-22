package it.polimi.smilaintegration;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

public class DummyPipelet implements SimplePipelet {
	private static final String ATTRIBUTE_NAME = "ATTRIBUTE_NAME";
	
	private String _attributeName;
	
	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		for (Id id : recordIds) {
			try {
				final Path path = new Path(_attributeName);
				if (blackboard.hasAttribute(id, path)) {
					String currentValue = blackboard.getLiteral(id, path).getStringValue();
					String newValue = currentValue + " ...edited by DummyPipelet";
					final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
					literal.setStringValue(newValue);
					blackboard.setLiteral(id, path, literal);
				}
			} catch (final Exception e) {
				throw new ProcessingException("During execution of DummyPipelet: " + e.toString());
			}
		}
		return recordIds;
	}

	@Override
	public void configure(PipeletConfiguration configuration)
			throws ProcessingException {
		_attributeName = ((String) configuration.getPropertyFirstValueNotNull(ATTRIBUTE_NAME)).trim();
		if (_attributeName.length() == 0) {
			throw new ProcessingException("Property " + ATTRIBUTE_NAME + " is empty.");
		}
	}

}
