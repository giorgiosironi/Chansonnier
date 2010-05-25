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
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * SimplePipelet implementation for test. Just logs the given configuration and record IDs.
 * 
 * @author jschumacher
 * 
 */
public class SimpleTestPipelet implements SimplePipelet {
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
    _log.info("SimpleTestPipelet.configure():");
    for (PipeletConfiguration.Property prop : configuration.getProperties()) {
      _log.info("    property " + prop.getName() + " = " + prop.getValue());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    _log.info("SimpleTestPipelet.process():");
    for (Id id : recordIds) {
      _log.info("  id = " + id);
      try {
        if (blackboard.hasAnnotations(id, null)) {
          final Iterator<String> names = blackboard.getAnnotationNames(id, null);
          while (names.hasNext()) {
            final String name = names.next();
            final List<Annotation> annotations = blackboard.getAnnotations(id, null, name);
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
        _log.error("error accessing " + id + " on blackboard.", ex);
      }
    }
    return recordIds;
  }
}
