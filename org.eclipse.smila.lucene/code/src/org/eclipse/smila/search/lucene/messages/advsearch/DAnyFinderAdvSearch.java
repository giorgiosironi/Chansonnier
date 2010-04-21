/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.smila.search.utils.advsearch.IAdvSearch;
import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author gschmidt
 * 
 */
public class DAnyFinderAdvSearch implements IAdvSearch {

  /**
   * Query expressions.
   */
  private ArrayList<IQueryExpression> _queryExpressions = new ArrayList<IQueryExpression>();

  /**
   * Version.
   */
  private String _version;

  /**
   * Constructor.
   */
  public DAnyFinderAdvSearch() {
  }

  /**
   * Constructs a new AnyFinderAdvancedSearch and adds a QueryExpression.
   * 
   * @param dQueryExpression
   *          QueryExpression to be added.
   */
  public DAnyFinderAdvSearch(DQueryExpression dQueryExpression) {
    addQueryExpression(dQueryExpression);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   * @return Object
   */
  @Override
  public Object clone() {
    DAnyFinderAdvSearch obj = null;

    try {
      obj = (DAnyFinderAdvSearch) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DAnyFinderAdvSearch", e);
    }

    obj._queryExpressions = new ArrayList<IQueryExpression>();

    for (int i = 0; i < _queryExpressions.size(); i++) {
      obj.addQueryExpression((DQueryExpression) (((DQueryExpression) _queryExpressions.get(i)).clone()));
    }
    return obj;
  }

  /**
   * Adds a new QueryExpression to the search query.
   * 
   * @param dQueryExpression
   *          QueryExpression to be added.
   * @return IQueryExpression
   */
  public IQueryExpression addQueryExpression(IQueryExpression dQueryExpression) {
    _queryExpressions.add(dQueryExpression);
    return dQueryExpression;
  }

  /**
   * Sets the version of this search query.
   * 
   * @param aVersion
   *          String representing the version ID.
   */
  public void setVersion(String aVersion) {
    _version = aVersion;
  }

  /**
   * Determines the version information for this search query.
   * 
   * @return A string representing the version ID.
   */
  public String getVersion() {
    return _version;
  }

  /**
   * Sets a QueryExpression at a given index in this element's list. Any existing expression at that index will be
   * replaced.
   * 
   * @param index
   *          index in list where this QueryExpression will be set.
   * @param dQueryExpression
   *          QueryExpression to be set at that index.
   * @return IQueryExpression
   */
  public IQueryExpression setQueryExpression(int index, IQueryExpression dQueryExpression) {
    _queryExpressions.set(index, dQueryExpression);
    return dQueryExpression;
  }

  /**
   * This method gets a QueryExpression.
   * 
   * @param index
   *          Position of the QueryExpression to be retrieved.
   * @return IQueryExpression
   */
  public IQueryExpression getQueryExpression(int index) {
    if ((index < 0) || (index >= _queryExpressions.size())) {
      return null;
    }
    return _queryExpressions.get(index);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.utils.advsearch.IAdvSearch#getQueryExpressions()
   * 
   * @return Iterator
   */

  public Iterator getQueryExpressions() {
    return _queryExpressions.iterator();
  }

  /**
   * Returns this instance's contents as an XML String. {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    try {
      final Element el = DAnyFinderAdvSearchCodec.encode(this).getDocumentElement();
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Compares the string representations (as obtained by the toString() method) of this object and of the given object.
   * 
   * @param obj
   *          Object that this instance should be compared to.
   * @return boolean
   */
  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

  /**
   * Removes given QueryExpression from possible search queries..
   * 
   * @param dQueryExpression
   *          QueryExpression that should be removed.
   * @return IQueryExpression
   */
  public IQueryExpression removeQueryExpression(IQueryExpression dQueryExpression) {
    _queryExpressions.remove(dQueryExpression);
    return dQueryExpression;
  }

  /**
   * Removes given QueryExpression, represented by its index, from possible search queries..
   * 
   * @param index
   *          Index of QueryExpression that should be removed.
   * @return IQueryExpression
   */
  public IQueryExpression removeQueryExpression(int index) {
    return _queryExpressions.remove(index);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.utils.advsearch.IAdvSearch#getQueryExpressionCount()
   */
  public int getQueryExpressionCount() {
    return _queryExpressions.size();
  }

}
