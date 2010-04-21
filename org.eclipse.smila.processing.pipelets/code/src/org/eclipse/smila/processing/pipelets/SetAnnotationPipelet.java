/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfiguration.Property;

/**
 * Pipelet that sets an root metadata object or attribute annotation on the records in process. The annotation to set is
 * specified in the pipelet configuration. The possible properties are:
 * <ul>
 * <li>Path: the path to the attribute to attach the annotation to. If missing, the annotation is added to the root</li>
 * metadata objects of the records.
 * <li>Name: the name of the annotations</li>
 * <li>AnonValue: an anonymous value of the annotation. Use multiple Properties of this name to specify a list of
 * anonymous values.</li>
 * <li>NamedValue:&lt;name&gt;: a named value of the annotation. The value name is the part of the property name after
 * the ":".
 * <li>
 * </ul>
 * 
 * @author jschumacher
 * 
 */
public class SetAnnotationPipelet implements SimplePipelet {
  /**
   * Name of the Path property: "Path".
   */
  public static final String PROP_PATH = "Path";

  /**
   * Name of the Name property: "Name".
   */
  public static final String PROP_NAME = "Name";

  /**
   * Name of the AnonValue property: "AnonValue".
   */
  public static final String PROP_ANONVALUE = "AnonValue";

  /**
   * Name of the NamedValue property: "NamedValue".
   */
  public static final String PROP_NAMEDVALUE = "NamedValue:";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(SetAnnotationPipelet.class);

  /**
   * path to attribute to attach annotation to.
   */
  private Path _attributePath;

  /**
   * name of annotation to attach.
   */
  private String _annotationName;

  /**
   * named values of annotation.
   */
  private final Map<String, String> _namedValues = new HashMap<String, String>();

  /**
   * anonymous values of annotation.
   */
  private final List<String> _anonValues = new ArrayList<String>();

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    final List<Property> properties = configuration.getProperties();
    for (Property property : properties) {
      final String name = property.getName();
      if (PROP_PATH.equals(name)) {
        final String value = property.getValue().toString();
        setAttributePath(value);
      } else if (PROP_NAME.equals(name)) {
        final String value = property.getValue().toString();
        setAnnotationName(value);
      } else if (PROP_ANONVALUE.equals(name)) {
        final List<Object> list = property.getValues();
        for (Object value : list) {
          final String stringValue = (String) value;
          addAnonValue(stringValue);
        }
      } else if (name.startsWith(PROP_NAMEDVALUE)) {
        final List<Object> list = property.getValues();
        for (Object value : list) {
          final String stringValue = (String) value;
          putNamedValue(name, stringValue);
        }
      } else {
        _log.warn("Unknown property name " + name);
      }
    }
    if (_annotationName == null) {
      _log.error("No annotation name specified: Please set property " + PROP_NAME);
      throw new ProcessingException("SetAnnotationsPipelet: No annotation name specified: Please set property "
        + PROP_NAME);
    }
  }

  /**
   * @param name
   *          name of an named value
   * @param value
   *          value
   */
  protected void putNamedValue(final String name, final String value) {
    final String valueName = name.substring(PROP_NAMEDVALUE.length());
    _namedValues.put(valueName, value);
    if (_log.isDebugEnabled()) {
      _log.debug("annotation namedValues = " + _namedValues);
    }
  }

  /**
   * add an anonymous value.
   * 
   * @param value
   *          another anonymous value
   */
  protected void addAnonValue(final String value) {
    _anonValues.add(value);
    if (_log.isDebugEnabled()) {
      _log.debug("annotation anonValues = " + _anonValues);
    }
  }

  /**
   * @param value
   *          name of annotation
   */
  protected void setAnnotationName(final String value) {
    _annotationName = value;
    if (_log.isDebugEnabled()) {
      _log.debug("annotation name = " + _annotationName);
    }
  }

  /**
   * create path for annotation from string. sets index of final step to {@link PathStep#ATTRIBUTE_ANNOTATION}
   * regardless of the actual final index given in the string.
   * 
   * @param value
   *          a path string
   */
  protected void setAttributePath(final String value) {
    _attributePath = new Path(value);
    if (_attributePath != null && _attributePath.length() > 0) {
      final PathStep step = _attributePath.get(_attributePath.length() - 1);
      _attributePath.up().add(new PathStep(step.getName(), PathStep.ATTRIBUTE_ANNOTATION));
    }
    if (_log.isDebugEnabled()) {
      _log.debug("annotation path = " + _attributePath);
    }
  }

  /**
   * set configured annotation on each record on blackboard as specified by the recordIds. {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (Id id : recordIds) {
        setAnnotations(blackboard, id);
      }
    }
    return recordIds;
  }

  /**
   * set the configured annotation for the given record.
   * 
   * @param blackboard
   *          blackboard to work on.
   * @param id
   *          id of record to manipulate.
   */
  private void setAnnotations(Blackboard blackboard, Id id) {
    try {
      final Annotation annotation = blackboard.createAnnotation(id);
      if (!_anonValues.isEmpty()) {
        annotation.setAnonValues(_anonValues);
      }
      if (!_namedValues.isEmpty()) {
        for (Map.Entry<String, String> entry : _namedValues.entrySet()) {
          annotation.setNamedValue(entry.getKey(), entry.getValue());
        }
      }
      blackboard.setAnnotation(id, _attributePath, _annotationName, annotation);
    } catch (BlackboardAccessException ex) {
      _log.error("Error setting annotation on record " + id, ex);
    }
  }
}
