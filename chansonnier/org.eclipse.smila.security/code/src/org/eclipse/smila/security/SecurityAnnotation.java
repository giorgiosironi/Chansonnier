/***********************************************************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.security;

import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.security.SecurityAnnotations.AccessRightType;
import org.eclipse.smila.security.SecurityAnnotations.EntityType;

/**
 * 
 */
public final class SecurityAnnotation {

  /**
   * The metadata object to set the security annotations on.
   */
  private MObject _mObject;

  /**
   * the security annotation.
   */
  private Annotation _annotation;

  /**
   * The record factory to use to create annotation objects.
   */
  private RecordFactory _factory;

  /**
   * Conversion Constructor.
   * 
   * @param record
   *          the record to contain the security annotation
   */
  public SecurityAnnotation(final Record record) {
    this(record.getMetadata(), record.getFactory());
  }

  /**
   * Conversion Constructor.
   * 
   * @param mObject
   *          the mObject to contain the security annotation
   */
  public SecurityAnnotation(final MObject mObject) {
    this(mObject, null);
  }

  /**
   * Conversion Constructor.
   * 
   * @param mObject
   *          the mObject to contain the security annotation
   * @param factory
   *          the RecordFactory to use for annotation creation
   */
  public SecurityAnnotation(final MObject mObject, RecordFactory factory) {
    _mObject = mObject;
    if (factory != null) {
      _factory = factory;
    } else {
      _factory = RecordFactory.DEFAULT_INSTANCE;
    }
  }

  /**
   * Returns the SecurityAnnotations.ACCESS_RIGHTS annotation. If it does not exist it is created.
   * 
   * @return the SecurityAnnotations.ACCESS_RIGHTS annotation
   */
  public Annotation getAccessRights() {
    if (_annotation == null) {
      _annotation = ensureAnnotation(_mObject, SecurityAnnotations.ACCESS_RIGHTS);
    }
    return _annotation;
  }

  /**
   * Returns the security annotation of the given accessRightType. If it does not exist it is created.
   * 
   * @param accessRightType
   *          the AccessRightType
   * @return the security annotation
   */
  public Annotation getAccessRights(AccessRightType accessRightType) {
    return ensureAnnotation(getAccessRights(), accessRightType.name());
  }

  /**
   * Returns the security annotation of the given accessRightType and entityType. If it does not exist it is created.
   * 
   * @param accessRightType
   *          the AccessRightType
   * @param entityType
   *          the EntityType
   * @return the security annotation
   */
  public Annotation getAccessRights(AccessRightType accessRightType, EntityType entityType) {
    final Annotation annotation = ensureAnnotation(getAccessRights(), accessRightType.name());
    return ensureAnnotation(annotation, entityType.name());
  }

  /**
   * Adds the given entity as a value to the security annotation specified by accessRightType and entityType. Non
   * existing annotations are created.
   * 
   * @param accessRightType
   *          the AccessRightType
   * @param entityType
   *          the EntityType
   * @param entity
   *          the value to add
   */
  public void add(AccessRightType accessRightType, EntityType entityType, String entity) {
    final Annotation annotation = getAccessRights(accessRightType, entityType);
    annotation.addAnonValue(entity);
  }

  /**
   * Removes the SecurityAnnotations.ACCESS_RIGHTS annotation and all sub annotations.
   */
  public void remove() {
    _mObject.removeAnnotations(SecurityAnnotations.ACCESS_RIGHTS);
  }

  /**
   * Removes the security annotation with the given accessRightType and all it's sub annotations.
   * 
   * @param accessRightType
   *          the AccessRightType
   */
  public void remove(AccessRightType accessRightType) {
    getAccessRights().removeAnnotations(accessRightType.name());
  }

  /**
   * Removes the security annotation with the given accessRightType and entityType and all it's sub annotations.
   * 
   * @param accessRightType
   *          the AccessRightType
   * @param entityType
   *          the EntityType
   */
  public void remove(AccessRightType accessRightType, EntityType entityType) {
    getAccessRights(accessRightType).removeAnnotations(entityType.name());
  }

  /**
   * Removes the given entity from the security annotation of the given accessRightType and entityType.
   * 
   * @param accessRightType
   *          the AccessRightType
   * @param entityType
   *          the EntityType
   * @param entity
   *          the value to remove
   */
  public void remove(AccessRightType accessRightType, EntityType entityType, String entity) {
    getAccessRights(accessRightType, entityType).removeAnonValue(entity);
  }

  /**
   * Ensures that the given annotatable contains the annotation with the given name. If not the annotation is created.
   * 
   * @param annotatable
   *          the Annotatable
   * @param name
   *          the name of the annotation
   * @return the Annotation object
   */
  private Annotation ensureAnnotation(Annotatable annotatable, String name) {
    if (annotatable.hasAnnotation(name)) {
      return annotatable.getAnnotation(name);
    } else {
      final Annotation annotation = _factory.createAnnotation();
      annotatable.setAnnotation(name, annotation);
      return annotation;
    }
  }

}
