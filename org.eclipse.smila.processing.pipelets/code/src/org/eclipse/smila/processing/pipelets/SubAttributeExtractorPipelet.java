/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * Extracts Literal values from an attribute that has a nested MObject. The attributes in the nested MObject can have
 * nested MOBjects themselves. To address a attribute in the nested structure a path needs to be specified. The pipelet
 * supports different execution modes: <li>FIRST: selects only the first Literal of the specified attribute</li> <li>
 * LAST: selects only the last Literal of the specified attribute</li> <li>ALL_AS_LIST: selects all Literal values of
 * the specified attribute and returns a list</li> <li>ALL_AS_ONE: selects all Literal values of the specified attribute
 * and concatenates them to a single string, using a seperator (default is blank)</li>
 */
// TODO: the pipelet currently does not support extraction of lists of MObjects !!!
public class SubAttributeExtractorPipelet implements SimplePipelet {

  /**
   * Name of the property: "inputPath".
   */
  public static final String PROPPERTY_INPUT_PATH = "inputPath";

  /**
   * Name of the property: "outputPath".
   */
  public static final String PROPPERTY_OUTPUT_PATH = "outputPath";

  /**
   * Name of the property: "mode".
   */
  public static final String PROPPERTY_MODE = "mode";

  /**
   * Name of the property: "separator".
   */
  public static final String PROPPERTY_SEPARATOR = "separator";

  /**
   * The default separator.
   */
  public static final String DEFAULT_SEPARATOR = " ";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(SubAttributeExtractorPipelet.class);

  /**
   * The path of the input attribute.
   */
  private Path _inputPath;

  /**
   * The path of the output attribute.
   */
  private Path _outputPath;

  /**
   * The mode.
   */
  private Mode _mode;

  /**
   * The separator used for mode ALL_AS_ONE.
   */
  private String _separator;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(final PipeletConfiguration configuration) throws ProcessingException {
    _inputPath = new Path((String) configuration.getPropertyFirstValueNotNull(PROPPERTY_INPUT_PATH));
    _outputPath = new Path((String) configuration.getPropertyFirstValueNotNull(PROPPERTY_OUTPUT_PATH));
    _mode = Mode.valueOf((String) configuration.getPropertyFirstValueNotNull(PROPPERTY_MODE));
    _separator = (String) configuration.getPropertyFirstValue(PROPPERTY_SEPARATOR);
    if (_separator == null) {
      _separator = DEFAULT_SEPARATOR;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (final Id id : recordIds) {
        try {
          if (blackboard.hasAttribute(id, _inputPath)) {
            setAttributeValues(blackboard, id, blackboard.getLiterals(id, _inputPath));
          } else {
            if (_log.isDebugEnabled()) {
              _log.debug("Could not find attribute with path " + _inputPath + " in record " + id);
            }
          }
        } catch (Exception e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error while processing record " + id, e);
          }
        }
      }
    }
    return recordIds;
  }

  /**
   * Returns the input path.
   * 
   * @return the input path
   */
  public Path getInputPath() {
    return _inputPath;
  }

  /**
   * Returns the output path.
   * 
   * @return the output path
   */
  public Path getOutputPath() {
    return _outputPath;
  }

  /**
   * Returns the name of the mode.
   * 
   * @return the name of the mode
   */
  public String getMode() {
    return _mode.name();
  }

  /**
   * Gets the separator.
   * 
   * @return the separator.
   */
  public String getSeparator() {
    return _separator;
  }

  /**
   * Sets the given Literal values in the output attribute. What literal values are set depoends on the execution mode.
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the id of the record
   * @param values
   *          the Literal values
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private void setAttributeValues(final Blackboard blackboard, final Id id, List<Literal> values)
    throws BlackboardAccessException {
    if (values != null && !values.isEmpty()) {
      switch (_mode) {
        case FIRST:
          blackboard.setLiteral(id, _outputPath, values.get(0));
          break;
        case LAST:
          blackboard.setLiteral(id, _outputPath, values.get(values.size() - 1));
          break;
        case ALL_AS_LIST:
          blackboard.setLiterals(id, _outputPath, values);
          break;
        case ALL_AS_ONE:
          blackboard.setLiteral(id, _outputPath, concat(blackboard.getRecord(id).getFactory(), values));
          break;
        default:
          if (_log.isErrorEnabled()) {
            _log.error("Error while processing record " + id + ". Invalid mode " + _mode);
          }
      }
    }
  }

  /**
   * Concatenates the provided values as a new String and creates a new Literal.
   * 
   * @param factory
   *          the RecordFactory used to create the Literal
   * @param values
   *          a List of Literals
   * @return a Literal
   */
  private Literal concat(final RecordFactory factory, List<Literal> values) {
    final StringBuffer buffer = new StringBuffer();
    for (final Literal value : values) {
      final String stringValue = value.getStringValue();
      if (buffer.length() > 0) {
        buffer.append(_separator);
      }
      buffer.append(stringValue);
    }
    final Literal literal = factory.createLiteral();
    literal.setStringValue(buffer.toString());
    return literal;
  }

  /**
   * Enumeration of modes of how to handle multiple values.
   */
  private enum Mode {
    /**
     * Return only the first value.
     */
    FIRST,
    /**
     * Return only the last value.
     */
    LAST,
    /**
     * Return all values as a List.
     */
    ALL_AS_LIST,
    /**
     * Return all values concatenated as one value.
     */
    ALL_AS_ONE;
  }

}
