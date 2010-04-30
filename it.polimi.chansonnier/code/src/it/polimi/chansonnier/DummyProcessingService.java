package it.polimi.chansonnier;

import java.io.InputStream;

import javax.xml.bind.Unmarshaller;


import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.osgi.service.component.ComponentContext;

public class DummyProcessingService implements ProcessingService {
	private String _attributeName = "Filename";
	
	protected void activate(ComponentContext context) {

	}

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
		for (Id id : recordIds) {
			try {
				final Path path = new Path(_attributeName);
				if (blackboard.hasAttribute(id, path)) {
					String currentValue = blackboard.getLiteral(id, path).getStringValue();
					String newValue = currentValue + " ...edited by DummyProcessingService";
					final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
					literal.setStringValue(newValue);
					blackboard.setLiteral(id, path, literal);
				}
			} catch (final Exception e) {
                throw new ProcessingException("During execution of DummyProcessingService: " + e.toString());
            }
        }
        return recordIds;
    }
}
