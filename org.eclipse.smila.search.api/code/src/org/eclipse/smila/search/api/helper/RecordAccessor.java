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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * methods to make reading of literals and annotations a bit easier, similar to blackboard.
 * 
 * @author jschumacher
 * 
 */
public class RecordAccessor {
  /**
   * the record to read.
   */
  private Record _record;

  /**
   * create accessor for record.
   * 
   * @param record
   *          a record.
   */
  public RecordAccessor(Record record) {
    _record = record;
  }

  /**
   * check if an attribute exists.
   * 
   * @param attributeName
   *          an attribute name.
   * @return true if the record metadata contains the named attribute.
   */
  public boolean hasAttribute(String attributeName) {
    return _record.getMetadata().hasAttribute(attributeName);
  }

  /**
   * @param attributeName
   *          an attribute name.
   * @return true if the record metadata contains the named attribute and it has literal values.
   */
  public boolean hasLiterals(String attributeName) {
    if (hasAttribute(attributeName)) {
      return _record.getMetadata().getAttribute(attributeName).hasLiterals();
    }
    return false;
  }

  /**
   * get number of literals in an attribute.
   * 
   * @param attributeName
   *          an attribute name.
   * @return number of literal values in the named attribute. 0, if the attribute does not exist.
   */
  public int literalSize(String attributeName) {
    if (hasAttribute(attributeName)) {
      return _record.getMetadata().getAttribute(attributeName).literalSize();
    }
    return 0;
  }

  /**
   * get a literal attribute value.
   * 
   * @param attributeName
   *          an attribute name.
   * @return first literal value in this attribute, if any, else null.
   */
  public Literal getLiteral(String attributeName) {
    if (hasAttribute(attributeName)) {
      return _record.getMetadata().getAttribute(attributeName).getLiteral();
    }
    return null;
  }

  /**
   * get literal attribute values. The returned list cannot be modified.
   * 
   * @param attributeName
   *          an attribute name.
   * @return list of literals in this attribute. returns an empty list, of the attributes does not exist or does not
   *         have literals.
   */
  @SuppressWarnings("unchecked")
  public List<Literal> getLiterals(String attributeName) {
    if (hasAttribute(attributeName)) {
      return Collections.unmodifiableList(_record.getMetadata().getAttribute(attributeName).getLiterals());
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * check if a record annotation exists.
   * 
   * @param annotationName
   *          an annotation name
   * @return true, if a record annotation with this name exists, else false.
   */
  public boolean hasAnnotation(String annotationName) {
    return _record.getMetadata().hasAnnotation(annotationName);
  }

  /**
   * get number of record annotations.
   * 
   * @param annotationName
   *          an annotation name
   * @return number of record annotations with this name
   */
  public int annotationSize(String annotationName) {
    if (hasAnnotation(annotationName)) {
      return _record.getMetadata().getAnnotations(annotationName).size();
    }
    return 0;
  }

  /**
   * get a record annotation.
   * 
   * @param annotationName
   *          an annotation name
   * @return first record annotation with this name. null, if no such annotation exists.
   */
  public Annotation getAnnotation(String annotationName) {
    return _record.getMetadata().getAnnotation(annotationName);
  }

  /**
   * get list of record annotations. The list cannot be modified.
   * 
   * @param annotationName
   *          an annotation name
   * @return list of annotations with this name. The original collection of annotations is only copied to a list, if it
   *         is not actually a list already.
   */
  public List<Annotation> getAnnotations(String annotationName) {
    final Collection<Annotation> annotations = _record.getMetadata().getAnnotations(annotationName);
    return unmodifiableList(annotations);
  }

  /**
   * get a named value of record annotation.
   * 
   * @param annotationName
   *          annotation name.
   * @param valueName
   *          value name
   * @return named value of record annotation. null, if the annotation does not exists or does not have the value.
   */
  public String getAnnotationValue(String annotationName, String valueName) {
    final Annotation annotation = _record.getMetadata().getAnnotation(annotationName);
    if (annotation != null) {
      return annotation.getNamedValue(valueName);
    }
    return null;
  }

  /**
   * get anonymous value list of record annotation. The list cannot be modified.
   * 
   * @param annotationName
   *          annotation name.
   * @return list of anon values, of empty list, if annotation does not exist or has no values.
   */
  @SuppressWarnings("unchecked")
  public List<String> getAnnotationValues(String annotationName) {
    final Annotation annotation = _record.getMetadata().getAnnotation(annotationName);
    if (annotation != null) {
      final Collection<String> values = annotation.getAnonValues();
      return unmodifiableList(values);
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * check if an attribute annotation exists.
   * 
   * @param attributeName
   *          an attribute name
   * @param annotationName
   *          an annotation name
   * @return true if the attribute exists and has the annotation, else false.
   */
  public boolean hasAnnotation(String attributeName, String annotationName) {
    if (hasAttribute(attributeName)) {
      return _record.getMetadata().getAttribute(attributeName).hasAnnotation(annotationName);
    }
    return false;
  }

  /**
   * get number of attribute annotations with a given name.
   * 
   * @param attributeName
   *          attribute name
   * @param annotationName
   *          annotation name
   * @return number of matching attribute annotations, 0 if attribute does not exist.
   */
  public int annotationSize(String attributeName, String annotationName) {
    if (hasAnnotation(attributeName, annotationName)) {
      return _record.getMetadata().getAttribute(attributeName).getAnnotations(annotationName).size();
    }
    return 0;
  }

  /**
   * get first attribute annotation with a given name.
   * 
   * @param attributeName
   *          attribute name
   * @param annotationName
   *          annotation name
   * @return first named attribute annotation, null if no such attribute exists.
   */
  public Annotation getAnnotation(String attributeName, String annotationName) {
    if (hasAttribute(attributeName)) {
      return _record.getMetadata().getAttribute(attributeName).getAnnotation(annotationName);
    }
    return null;
  }

  /**
   * get all attribute annotations with a given name. The list cannot be modified.
   * 
   * @param attributeName
   *          attribute name
   * @param annotationName
   *          annotation name
   * @return list of named attribute annotation, empty list if no such attribute exists.
   */
  @SuppressWarnings("unchecked")
  public List<Annotation> getAnnotations(String attributeName, String annotationName) {
    if (hasAnnotation(attributeName, annotationName)) {
      final Collection<Annotation> annotations =
        _record.getMetadata().getAttribute(attributeName).getAnnotations(annotationName);
      return unmodifiableList(annotations);
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * get terms list for an attribute.
   * 
   * @param attributeName
   *          name of attribute.
   * @return list of terms
   */
  public Terms getTerms(String attributeName) {
    return new Terms(attributeName, getAnnotations(attributeName, SearchAnnotations.TERMS));
  }

  /**
   * 
   * @return underlying record.
   */
  public Record getRecord() {
    return _record;
  }

  /**
   * get named value of result annotation.
   * 
   * @param valueName
   *          value name.
   * @return value string of null, if not present
   */
  public String getResultAnnotationValue(String valueName) {
    return getAnnotationValue(SearchAnnotations.RESULT, valueName);
  }

  /**
   * get named value of result annotation, converted to an Integer.
   * 
   * @param valueName
   *          value name.
   * @return value string of null, if not present or not parseable as an integer.
   */
  public Integer getResultAnnotationIntValue(String valueName) {
    final String value = getResultAnnotationValue(valueName);
    if (value != null) {
      try {
        return Integer.valueOf(value);
      } catch (Exception e) {
        // value is not an integer, ignore and return null
        e = null;
      }
    }
    return null;
  }

  /**
   * get named value of result annotation, converted to an Double.
   * 
   * @param valueName
   *          value name.
   * @return value string of null, if not present or not parseable as an Double.
   */
  public Double getResultAnnotationFloatValue(String valueName) {
    final String value = getResultAnnotationValue(valueName);
    if (value != null) {
      try {
        return Double.valueOf(value);
      } catch (Exception e) {
        // value is not an integer, ignore and return null
        e = null;
      }
    }
    return null;
  }

  /**
   * convert the collection to a list, if it is not actually a list already, and create a unmodifiable list from it.
   * 
   * @param collection
   *          a collection
   * @param <T>
   *          type of collection elements.
   * @return unmodifiable list with the elements of the collection
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> unmodifiableList(Collection<? extends T> collection) {
    List<? extends T> list = null;
    if (collection == null) {
      return Collections.EMPTY_LIST;
    }
    if (collection instanceof List) {
      list = (List<? extends T>) collection;
    } else {
      list = new ArrayList<T>(collection);
    }
    return Collections.unmodifiableList(list);
  }

}
