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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * wrapper for annotations describing search facets (grouping, categorization, questions).
 * 
 * @author jschumacher
 * 
 */
public class Facets extends AnnotationListAccessor {
  /**
   * create instance from data.
   * 
   * @param attributeName
   *          name of faceted attribute
   * @param annotations
   *          list of facet annotations.
   */
  public Facets(String attributeName, List<Annotation> annotations) {
    super(attributeName, annotations);
  }

  /**
   * create instance from data.
   * 
   * @param attributeName
   *          name of faceted attribute
   * @param annotations
   *          collection of facet annotations.
   */
  public Facets(String attributeName, Collection<Annotation> annotations) {
    super(attributeName, annotations);
  }

  /**
   * get name property of n'th facet.
   * 
   * @param index
   *          position in facet list.
   * @return name of facet, or null for invalid indexes
   */
  public String getName(int index) {
    return getProperty(index, SearchAnnotations.FACET_NAME);
  }

  /**
   * get value of string filter property of n'th facet.
   * 
   * @param index
   *          position in facet list.
   * @return string filter of facet, or null for invalid indexes.
   */
  public String getStringFilter(int index) {
    return getProperty(index, SearchAnnotations.FACET_FILTER);
  }

  /**
   * get filter subannotation of n'th facet.
   * 
   * @param index
   *          position in facet list.
   * @return object filter of facet, or null for invalid indexes.
   */
  public Annotation getObjectFilter(int index) {
    return getAnnotation(index, SearchAnnotations.FACET_FILTER);
  }

  /**
   * get count property of n'th facet.
   * 
   * @param index
   *          position in facet list.
   * @return count of facet, or null for invalid indexes
   */
  public Integer getCount(int index) {
    return getIntProperty(index, SearchAnnotations.FACET_COUNT);
  }

  /**
   * check if the n'th facet has subfacets.
   * 
   * @param index
   *          position in facet list.
   * @return true if the n'th facet has subfacets, else false.
   */
  public boolean hasSubFacets(int index) {
    final Annotation subAnnotation = getAnnotation(index, SearchAnnotations.FACETS);
    return subAnnotation != null;
  }

  /**
   * get subfacets of n'th facet.
   * 
   * @param index
   *          position in facet list.
   * @return map of attribute names to subfacet lists. If no subfacets exists, an empty map is returned.
   */
  public Map<String, Facets> getSubFacets(int index) {
    final Map<String, Facets> subFacets = new HashMap<String, Facets>();
    final Annotation subAnnotation = getAnnotation(index, SearchAnnotations.FACETS);
    if (subAnnotation != null) {
      final Iterator<String> attributeNames = subAnnotation.getAnnotationNames();
      while (attributeNames.hasNext()) {
        final String attributeName = attributeNames.next();
        final Collection<Annotation> facetAnnotations = subAnnotation.getAnnotations(attributeName);
        final Facets facets = new Facets(attributeName, (List<Annotation>) facetAnnotations);
        subFacets.put(attributeName, facets);
      }
    }
    return subFacets;
  }
}
