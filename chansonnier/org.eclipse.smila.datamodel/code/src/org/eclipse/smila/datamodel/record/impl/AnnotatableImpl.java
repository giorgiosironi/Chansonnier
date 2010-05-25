/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.utils.collections.MultiValueMap;

/**
 * Abstract base class for SMILA data model default implentation: Everything is annotatable (except records).
 * 
 * @author jschumacher
 * 
 */
public abstract class AnnotatableImpl implements Annotatable, Serializable {
  /**
   * because it is serializable ...
   */
  private static final long serialVersionUID = 1L;

  /**
   * map containing annotations.
   */
  private MultiValueMap<String, Annotation> _annotationMap;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#addAnnotation(java.lang.String,
   *      org.eclipse.smila.datamodel.record.Annotation)
   */
  public void addAnnotation(String name, Annotation annotation) {
    if (annotation instanceof AnnotationImpl) {
      getAnnotationMap().add(name, annotation);
    } else {
      throw new IllegalArgumentException("cannot add annotations of class " + annotation.getClass().getName());
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#getAnnotation(java.lang.String)
   */
  public Annotation getAnnotation(String name) {
    if (hasAnnotation(name)) {
      return _annotationMap.getValues(name).iterator().next();
    } else {
      return null;
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#getAnnotationNames()
   */
  public Iterator<String> getAnnotationNames() {
    if (hasAnnotations()) {
      return _annotationMap.keySet().iterator();
    } else {
      return EmptyIterator.INSTANCE;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#getAnnotations(java.lang.String)
   */
  public Collection<Annotation> getAnnotations(String name) {
    if (hasAnnotations() && _annotationMap.containsKey(name)) {
      return _annotationMap.getValues(name);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#hasAnnotation(java.lang.String)
   */
  public boolean hasAnnotation(String name) {
    return hasAnnotations() && _annotationMap.containsKey(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#hasAnnotations()
   */
  public boolean hasAnnotations() {
    return _annotationMap != null && !_annotationMap.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#removeAnnotations(java.lang.String)
   */
  public void removeAnnotations(String name) {
    if (_annotationMap != null) {
      _annotationMap.remove(name);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#removeAnnotations()
   */
  public void removeAnnotations() {
    _annotationMap = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#setAnnotation(java.lang.String,
   *      org.eclipse.smila.datamodel.record.Annotation)
   */
  public void setAnnotation(String name, Annotation annotation) {
    removeAnnotations(name);
    addAnnotation(name, annotation);

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#setAnnotations(java.lang.String, java.util.List)
   */
  public void setAnnotations(String name, Collection<? extends Annotation> annotations) {
    removeAnnotations(name);
    for (Annotation annotation : annotations) {
      addAnnotation(name, annotation);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotatable#annotationsSize()
   */
  public int annotationsSize() {
    if (hasAnnotations()) {
      return _annotationMap.size();
    } else {
      return 0;
    }
  }

  /**
   * access the annotation map, create one first if it does not exist yet.
   * 
   * @return annotation map
   */
  private MultiValueMap<String, Annotation> getAnnotationMap() {
    if (_annotationMap == null) {
      _annotationMap = new MultiValueMap<String, Annotation>();
    }
    return _annotationMap;
  }

}
