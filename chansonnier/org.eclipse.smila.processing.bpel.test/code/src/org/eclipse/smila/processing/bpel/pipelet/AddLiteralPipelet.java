package org.eclipse.smila.processing.bpel.pipelet;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * Test pipelet/service that appends a literal value to a certain attribute.
 * 
 * @author jschumacher
 * 
 */
public class AddLiteralPipelet implements SimplePipelet, ProcessingService {

  /**
   * the attribute I change.
   */
  public static final Path ATTRIBUTE = new Path(AddLiteralPipelet.class.getName());

  /**
   * I add values $VALUE_PREFIX + index.
   */
  public static final String VALUE_PREFIX = "SMILA #";

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    for (int i = 0; i < recordIds.length; i++) {
      try {
        final Literal lit = blackboard.createLiteral(recordIds[i]);
        lit.setStringValue(VALUE_PREFIX + i);
        blackboard.addLiteral(recordIds[i], ATTRIBUTE, lit);
      } catch (BlackboardAccessException ex) {
        throw new ProcessingException(ex);
      }
    }
    return recordIds;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.IPipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    // nothing to do
  }

}
