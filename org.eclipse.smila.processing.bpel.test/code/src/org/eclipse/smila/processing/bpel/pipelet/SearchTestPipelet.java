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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchPipelet;
import org.eclipse.smila.processing.SearchProcessingService;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * SimplePipelet implementation for test. Just logs the given configuration and record IDs.
 * 
 * @author jschumacher
 * 
 */
public class SearchTestPipelet implements SearchPipelet, SearchProcessingService {
  /**
   * produce how many query results.
   */
  public static final int RESULT_SIZE = 3;

  /**
   * attribute to set in results to $PREFIX + index (0 <= index < RESULT_SIZE).
   */
  public static final Path ATTRIBUTE = new Path("title");

  /**
   * prefix for fragment name and attribute value.
   */
  public static final String PREFIX = "Result #";

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
    _log.info("SearchTestPipelet.configure():");
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
  public SearchMessage process(Blackboard blackboard, SearchMessage message) throws ProcessingException {
    _log.info("SearchTestPipelet.process():");
    if (message.hasQuery()) {
      final List<Id> results = new ArrayList<Id>(RESULT_SIZE);
      for (int i = 0; i < RESULT_SIZE; i++) {
        final String value = PREFIX + i;
        try {
          final Id result = blackboard.split(message.getQuery(), value);
          final Literal literal = blackboard.createLiteral(result);
          literal.setStringValue(value);
          blackboard.addLiteral(result, ATTRIBUTE, literal);
          results.add(result);
        } catch (BlackboardAccessException ex) {
          _log.error(ex);
        }
      }
      message.setRecords(results);
    }
    return message;
  }
}
