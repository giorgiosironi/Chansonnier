/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets;

import java.util.List;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.parameters.ParameterAccessor;

/**
 * @author jschumacher
 *
 */
public class AddLiteralsPipelet implements SimplePipelet {
  /** config property name for attribute name to add literals to. */
  private static final String PARAM_ATTRIBUTE = "AddLiterals.attribute";

  /** config property name for the literal values to add. */
  private static final String PARAM_VALUES = "AddLiterals.values";

  /**
   * my configuration.
   */
  private PipeletConfiguration _configuration;

  /**
   * add literal string values to an attribute as described in pipelet config or parameters.
   *
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    try {
      final ParameterAccessor parameters =
        new ParameterAccessor(blackboard).setPipeletConfiguration(_configuration);
      for (final Id id : recordIds) {
        parameters.setCurrentRecord(id);
        final String attributeName = parameters.getRequiredParameter(PARAM_ATTRIBUTE);
        final Path path = new Path(attributeName);
        final List<String> values = parameters.getParameters(PARAM_VALUES);
        if (values != null) {
          for (final String value : values) {
            final Literal literal = blackboard.createLiteral(id);
            literal.setStringValue(value);
            blackboard.addLiteral(id, path, literal);
          }
        }
      }
    } catch (final Exception ex) {
      throw new ProcessingException(ex);
    }
    return recordIds;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.IPipelet#
   *      configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(final PipeletConfiguration configuration) throws ProcessingException {
    _configuration = configuration;

  }

}
