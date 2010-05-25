/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.records;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.ontology.SesameOntologyManager;

/**
 * Helper class defining constants and utility methods for the mapping of SMILA records to Sesame ontology objects.
 *
 * @author jschumacher
 *
 */
public abstract class SesameRecordHelper {
  /**
   * dummy source value for IDs of newly created records.
   */
  public static final String SESAME_SOURCE = "SESAME";

  /**
   * name of attribute that contains the URI of the resource represented by a record.
   */
  public static final String ATTRIBUTE_URI = "rdf:about";

  /**
   * path of attribute that contains the URI of the resource represented by a record.
   */
  public static final Path PATH_URI = new Path();

  /**
   * name of attribute that contains the RDF type of the resource represented by a record.
   */
  public static final String ATTRIBUTE_TYPE = "rdf:type";

  /**
   * path of attribute that contains the RDF type of the resource represented by a record.
   */
  public static final Path PATH_TYPE = new Path();

  /**
   * semantic type of string literals that correspond to resource property values in MDS.
   */
  public static final String SEMTYPE_RESOURCE = "rdfs:Resource";

  /**
   * name of annotation containing the locale of a statement.
   */
  public static final String ANNOTATION_LANG = "xml:lang";

  /**
   * name of the attribute annotation that describes additional modes for writing properties.
   */
  public static final String ANNOTATION_MODE = SesameOntologyManager.BUNDLE_ID;

  /**
   * name of annotation value describing that a property should be cleared before adding new values.
   */
  public static final String ANNOVALUENAME_CLEAR = "clear";

  /**
   * value of annotation describing that a property should be written reverse.
   */
  public static final String ANNOVALUE_REVERSE = "reverse";

  /**
   * locale value for "all locales".
   */
  public static final String LOCALE_ALL = "ALL";

  static {
    PATH_URI.add(ATTRIBUTE_URI);
    PATH_TYPE.add(ATTRIBUTE_TYPE);
  }

  /**
   * prevent instance creation.
   */
  private SesameRecordHelper() {
    // no instances should be created.
  }

  /**
   * set language annotation to literal.
   *
   * @param literal
   *          literal
   * @param language
   *          language string
   */
  public static void setLanguage(final Literal literal, final String language) {
    if (language != null) {
      final Annotation annotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
      annotation.addAnonValue(language);
      literal.setAnnotation(ANNOTATION_LANG, annotation);
    }
  }

  /**
   * get language annotation from literal, if present.
   *
   * @param literal
   *          literal
   * @return language of literal, or null, if not set.
   */
  public static String getLanguage(final Literal literal) {
    if (literal.hasAnnotation(ANNOTATION_LANG)) {
      final Annotation annotation = literal.getAnnotation(ANNOTATION_LANG);
      if (annotation.hasAnonValues()) {
        return annotation.getAnonValues().iterator().next();
      }
    }
    return null;
  }

  /**
   * create or reuse an attribute.
   *
   * @param mobject
   *          metadata object
   * @param attributeName
   *          name of attribute (no path)
   * @return the attribute instance or null, if attribute name is null.
   */
  public static Attribute getAttribute(final MObject mobject, final String attributeName) {
    if (attributeName == null) {
      return null;
    }
    Attribute attribute = mobject.getAttribute(attributeName);
    if (attribute == null) {
      attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
      mobject.setAttribute(attributeName, attribute);
    }
    return attribute;
  }

  /**
   * access or create a new annotation to the annotatable (usually an attribute, literal or mobject) used to modify the
   * operation of {@link SesameRecordWriter}.
   *
   * @param annotatable
   *          annotatable object
   * @return the annotation
   */
  public static Annotation getModeAnnotation(final Annotatable annotatable) {
    Annotation annotation = annotatable.getAnnotation(ANNOTATION_MODE);
    if (annotation == null) {
      annotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
      annotatable.addAnnotation(ANNOTATION_MODE, annotation);
    }
    return annotation;

  }

  /**
   * access or create a new annotation to named attribute of mobject.
   *
   * @param mobject
   *          metadata object
   * @param attributeName
   *          name of attribute (no path)
   * @return the annotation or null, if attribute name was null.
   */
  public static Annotation getModeAnnotation(final MObject mobject, final String attributeName) {
    final Attribute attribute = getAttribute(mobject, attributeName);
    if (attribute == null) {
      return null;
    }
    return getModeAnnotation(attribute);
  }

  /**
   * add clear flag to metadata object.
   *
   * @param mobject
   *          metadata object.
   * @return the mode annotation of the metadata object
   */
  public static Annotation addClearFlag(final MObject mobject) {
    final Annotation annotation = getModeAnnotation(mobject);
    annotation.setNamedValue(ANNOVALUENAME_CLEAR, LOCALE_ALL);
    return annotation;
  }

  /**
   * add clear flag to attribute of metadata object.
   *
   * @param mobject
   *          metadata object
   * @param attributeName
   *          name of attribute (no path)
   * @return the mode annotation of the attribute or null, if attribute name was null.
   */
  public static Annotation addClearFlag(final MObject mobject, final String attributeName) {
    return addClearFlag(mobject, attributeName, LOCALE_ALL);
  }

  /**
   * add clear flag for given language to attribute of metadata object.
   *
   * @param mobject
   *          metadata object
   * @param attributeName
   *          name of attribute (no path)
   * @param language
   *          to clear
   * @return the mode annotation of the attribute or null, if attribute name was null.
   */
  public static Annotation addClearFlag(final MObject mobject, final String attributeName, String language) {
    if (language == null) {
      language = LOCALE_ALL;
    }
    final Annotation annotation = getModeAnnotation(mobject, attributeName);
    if (annotation == null) {
      return null;
    }
    annotation.setNamedValue(ANNOVALUENAME_CLEAR, language);
    return annotation;
  }

  /**
   * add reverse flag to mode annotation of attribute.
   * 
   * @param mobject
   * @param attributeName
   * @param mobject
   *          metadata object
   * @param attributeName
   *          name of attribute (no path)
   * @return the mode annotation of the attribute or null, if attribute name was null.
   */
  public static Annotation addReverseFlag(final MObject mobject, final String attributeName) {
    final Annotation annotation = getModeAnnotation(mobject, attributeName);
    if (annotation == null) {
      return null;
    }
    annotation.addAnonValue(ANNOVALUE_REVERSE);
    return annotation;
  }

}
