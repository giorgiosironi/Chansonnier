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

package org.eclipse.smila.datamodel.record;

import java.util.Collection;
import java.util.Iterator;

/**
 * common interface of record parts that can have annotations.
 * 
 * @author jschumacher
 * 
 */
public interface Annotatable {
  /**
   * Check if this object has annotations attached.
   * 
   * @return true if annotations are attached, else false.
   */
  boolean hasAnnotations();

  /**
   * Check if this object has an annotation of the specified name.
   * 
   * @param name
   *          name of annotation to check for.
   * @return true if an annotation of this name exists, else false.
   */
  boolean hasAnnotation(String name);

  /**
   * get number of annotations.
   * 
   * @return number of annotations.
   */
  int annotationsSize();

  /**
   * get an iterator on the names of all attached annotations.
   * 
   * @return an iterator on annotation names.
   */
  Iterator<String> getAnnotationNames();

  /**
   * get the list of annotations for the specified name.
   * 
   * @param name
   *          name of annotation.
   * @return list of annotations with this name. Null, if no annotations of this name exists.
   */
  Collection<Annotation> getAnnotations(String name);

  /**
   * get the first annotation for the specified name. Makes usually only sense if is sure that only one annotation with
   * this name can exist, because some implementations might not be able to guarantee the order of annotations.
   * 
   * @param name
   *          name of annotation.
   * @return the first annotation in the list of known annotations. Null, if no annotations of this name exist.
   */
  Annotation getAnnotation(String name);

  /**
   * set the list of annotations for a given name. All existing annotations of this name are replaced.
   * 
   * @param name
   *          name of annotation
   * @param annotations
   *          new list of annotations to set
   */
  void setAnnotations(String name, Collection<? extends Annotation> annotations);

  /**
   * set a single annotation for the specified name. All existing annotations of this name are replaced.
   * 
   * @param name
   *          name of annotation
   * @param annotation
   *          new annotation to set.
   */
  void setAnnotation(String name, Annotation annotation);

  /**
   * add a single annotation for the specified name. The list of currently existing annotation is extended.
   * 
   * @param name
   *          name of annotation
   * @param annotation
   *          new annotation to add.
   */
  void addAnnotation(String name, Annotation annotation);

  /**
   * remove all current annotations of the specified name.
   * 
   * @param name
   *          name of annotation
   */
  void removeAnnotations(String name);

  /**
   * remove all current annotations.
   */
  void removeAnnotations();
}
