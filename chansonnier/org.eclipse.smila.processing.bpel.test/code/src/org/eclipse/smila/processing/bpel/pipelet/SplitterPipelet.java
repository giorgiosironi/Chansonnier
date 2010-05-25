/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel.pipelet;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * simple pipelet that splits the incoming IDs in 3 fragment Ids.
 * 
 * @author jschumacher
 * 
 */
public class SplitterPipelet implements SimplePipelet, ProcessingService {

  /**
   * split each Id in how many fragment Ids?
   */
  public static final int SPLIT_FACTOR = 3;

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    _log.info("SplitterPipelet.configure()");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.RecordProcessor#process(org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    final Id[] result = new Id[SPLIT_FACTOR * recordIds.length];
    for (int i = 0; i < recordIds.length; i++) {
      try {
        if (blackboard.hasAnnotations(recordIds[i], null)) {
          final Iterator<String> names = blackboard.getAnnotationNames(recordIds[i], null);
          while (names.hasNext()) {
            final String name = names.next();
            final List<Annotation> annotations = blackboard.getAnnotations(recordIds[i], null, name);
            for (Annotation annotation : annotations) {
              _log.info("    annotation " + name + ":");
              if (annotation.hasAnonValues()) {
                _log.info("        anon values: " + annotation.getAnonValues());
              }
              if (annotation.hasNamedValues()) {
                final Iterator<String> valueNames = annotation.getValueNames();
                while (valueNames.hasNext()) {
                  final String valueName = valueNames.next();
                  _log.info("        named value " + valueName + " = " + annotation.getNamedValue(valueName));
                }
              }
            }
          }
        }
      } catch (BlackboardAccessException ex) {
        _log.error("error accessing " + recordIds[i] + " on blackboard.", ex);
      }
      for (int j = 0; j < SPLIT_FACTOR; j++) {
        try {
          result[SPLIT_FACTOR * i + j] = blackboard.split(recordIds[i], "fragment" + j);
        } catch (BlackboardAccessException ex) {
          _log.error("error spliting a record", ex);
          result[i] = recordIds[i];
        }
      }
    }
    return result;
  }
}
