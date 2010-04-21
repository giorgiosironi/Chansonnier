/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.search.api.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.parameters.SearchParameters;

/**
 * wrapper for access to the service runtime parameters annotation of an record.
 * 
 * @author jschumacher
 * 
 */
public class ParameterAnnotation {
  /**
   * record with parameters.
   */
  private Record _record;

  /**
   * the parameter annotation.
   */
  private Annotation _annotation;

  /**
   * create wrapper for record.
   * 
   * @param record
   *          usually a query record.
   */
  public ParameterAnnotation(Record record) {
    _record = record;
  }

  /**
   * ensure that the record has a parameters annotation and return it.
   * 
   * @return parameter annotation
   */
  public Annotation getAnnotation() {
    if (_annotation == null) {
      if (_record.getMetadata().hasAnnotation(SearchParameters.PARAMETERS)) {
        _annotation = _record.getMetadata().getAnnotation(SearchParameters.PARAMETERS);
      } else {
        _annotation = _record.getFactory().createAnnotation();
        _record.getMetadata().setAnnotation(SearchParameters.PARAMETERS, _annotation);
      }
    }
    return _annotation;
  }

  /**
   * set a single valued parameter.
   * 
   * @param name
   *          parameter name
   * @param value
   *          parameter value
   */
  public void setParameter(String name, String value) {
    getAnnotation().setNamedValue(name, value);
  }

  /**
   * set a single valued parameter to an int value.
   * 
   * @param name
   *          parameter name
   * @param value
   *          parameter value
   */
  public void setIntParameter(String name, int value) {
    setParameter(name, Integer.toString(value));
  }

  /**
   * set a single valued parameter to an floating point value.
   * 
   * @param name
   *          parameter name
   * @param value
   *          parameter value
   */
  public void setFloatParameter(String name, double value) {
    setParameter(name, Double.toString(value));
  }

  /**
   * set a single valued parameter to an boolean value.
   * 
   * @param name
   *          parameter name
   * @param value
   *          parameter value
   */
  public void setBooleanParameter(String name, boolean value) {
    setParameter(name, Boolean.toString(value));
  }

  /**
   * get a single valued parameter value.
   * 
   * @param name
   *          parameter name
   * @return parameter value
   */
  public String getParameter(String name) {
    return getAnnotation().getNamedValue(name);
  }

  /**
   * get a single valued parameter value as an integer value.
   * 
   * @param name
   *          parameter name
   * @return parameter value. Null, if the value does not exists or cannot be parsed as an Integer.
   */
  public Integer getIntParameter(String name) {
    final String value = getParameter(name);
    if (value != null) {
      try {
        return Integer.valueOf(value);
      } catch (Exception ex) {
        ex = null;
      }
    }
    return null;
  }

  /**
   * get a single valued parameter value as an floating point value.
   * 
   * @param name
   *          parameter name
   * @return parameter value. Null, if the value does not exists or cannot be parsed as a Double.
   */
  public Double getFloatParameter(String name) {
    final String value = getParameter(name);
    if (value != null) {
      try {
        return Double.valueOf(value);
      } catch (Exception ex) {
        ex = null;
      }
    }
    return null;
  }

  /**
   * get a single valued parameter value as a boolean value.
   * 
   * @param name
   *          parameter name
   * @return parameter value. Null, if the value does not exists.
   */
  public Boolean getBooleanParameter(String name) {
    final String value = getParameter(name);
    if (value != null) {
      return Boolean.valueOf(value);
    }
    return null;
  }

  /**
   * set a multi valued parameter to a collection of string. Existing values are overwritten.
   * 
   * @param name
   *          parameter name.
   * @param values
   *          parameter values.
   */
  public void setParameters(String name, Collection<String> values) {
    final Annotation listParameter = _record.getFactory().createAnnotation();
    listParameter.setAnonValues(values);
    getAnnotation().setAnnotation(name, listParameter);
  }

  /**
   * add a value to a multi valued parameter.
   * 
   * @param name
   *          parameter name
   * @param value
   *          additional parameter value.
   */
  public void addParameter(String name, String value) {
    final Annotation listParameter = getAnnotation().getAnnotation(name);
    if (listParameter == null) {
      setParameters(name, Collections.singleton(value));
    } else {
      listParameter.addAnonValue(value);
    }
  }

  /**
   * get a multi valued parameter's value list. The returned list is not modifiiable, use
   * {@link #addParameter(String, String)} and {@link #setParameters(String, Collection)} to change the value.
   * 
   * @param name
   *          parameter name
   * @return list of values. empty list, if parameter is not set.
   */
  @SuppressWarnings("unchecked")
  public List<String> getParameters(String name) {
    final Annotation listParameter = getAnnotation().getAnnotation(name);
    if (listParameter == null) {
      return Collections.EMPTY_LIST;
    } else {
      final Collection<String> values = listParameter.getAnonValues();
      return RecordAccessor.unmodifiableList(values);
    }
  }

  /**
   * get unmodifiable list of subannotations.
   * 
   * @param name
   *          annotation name
   * @return unmodifiable list of subannotations
   */
  public List<Annotation> getSubAnnotations(String name) {
    return RecordAccessor.unmodifiableList(getAnnotation().getAnnotations(name));
  }
}
