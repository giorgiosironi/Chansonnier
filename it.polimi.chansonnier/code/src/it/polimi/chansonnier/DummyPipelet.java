package it.polimi.chansonnier;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

public class DummyPipelet implements SimplePipelet {
    /**
     * Nome della chiave di configurazione.
     */
	private static final String ATTRIBUTE_NAME = "ATTRIBUTE_NAME";
	
    /**
     * Nome dell'attributo dei Record da modificare.
     */
	private String _attributeName;
	
	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		for (Id id : recordIds) {
			try {
                // cerchiamo semplicemente un attributo sul Record stesso
                // avremmo potuto cercarlo all'interno di un altro attributo o
                // cercare un'annotazione su questo attributo, etc.
				final Path path = new Path(_attributeName);
                // tutta l'interazione con i dati avviene tramite la Blackboard
				if (blackboard.hasAttribute(id, path)) {
                    // costruiamo un nuovo valore (oggetto Literal che fa da 
                    // wrapper a diversi valori base come stringhe, interi...)
					String currentValue = blackboard.getLiteral(id, path).getStringValue();
					String newValue = currentValue + " ...edited by DummyPipelet";
					final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
					literal.setStringValue(newValue);
                    // la blackboard riceve il nuovo valore per questo Record
					blackboard.setLiteral(id, path, literal);
				}
			} catch (final Exception e) {
				throw new ProcessingException("During execution of DummyPipelet: " + e.toString());
			}
		}
        // gli Id dei Record ritornati verranno passati alla Pipelet successiva
		return recordIds;
	}

    /**
     * Riceve la configurazione alla creazione della Pipeline.
     */
	@Override
	public void configure(PipeletConfiguration configuration)
			throws ProcessingException {
		_attributeName = ((String) configuration.getPropertyFirstValueNotNull(ATTRIBUTE_NAME)).trim();
		if (_attributeName.length() == 0) {
			throw new ProcessingException("Property " + ATTRIBUTE_NAME + " is empty.");
		}
	}

}
