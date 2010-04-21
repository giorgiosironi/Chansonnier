/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * This class holds data to construct and set the parser in XMLUtils.
 */
public class XMLUtilsConfig {
  /**
   * 
   */
  private Hashtable<String, Boolean> _feature;

  /*********************************************************************************************************************
   * Shorthand for
   * 
   * <pre>
   * XMLUtilsConfig(true, false).
   * </pre>
   * 
   * This means that validation and stripping of <i>empty</i> text nodes is turned on by default.
   * ************************************************************************
   */
  public XMLUtilsConfig() {
    this(true, false);
  }

  /**
   * Validation by XML Schema is always turned on.
   * 
   * @param validate
   *          boolean flag if to validate
   * @param includeIgnorableWhitespace
   *          boolean flag if ti include ignorable whitespaces
   */
  public XMLUtilsConfig(final boolean validate, final boolean includeIgnorableWhitespace) {
    _feature = new Hashtable<String, Boolean>();

    setValidate(validate);
    setIncludeIgnorabelWhitespace(includeIgnorableWhitespace);

    setFeature("http://apache.org/xml/features/validation/schema", validate);
  }

  /**
   * Shorthand for
   * 
   * <pre>
   * getFeature(&quot;http://apache.org/xml/features/dom/include-ignorable-whitespace&quot;)
   * </pre>.
   * 
   * @return a Boolean
   */
  public Boolean getIncludeIgnorabelWhitespace() {
    return _feature.get("http://apache.org/xml/features/dom/include-ignorable-whitespace");
  }

  /**
   * Shorthand for
   * 
   * <pre>
   * setFeature(&quot;http://apache.org/xml/features/dom/include-ignorable-whitespace&quot;, value)
   * </pre>. If this is turned on then validation must be turned on eventually too other wise an exception will be
   * thrown when given this as a paramter to a parse()-method in XMLUtils.
   * 
   * @param value
   *          boolean flag
   */
  public void setIncludeIgnorabelWhitespace(final boolean value) {
    _feature.put("http://apache.org/xml/features/dom/include-ignorable-whitespace", Boolean.valueOf(value));
  }

  /**
   * Shorthand for
   * 
   * <pre>
   * getFeature(&quot;http://xml.org/sax/features/validation&quot;)
   * </pre>.
   * 
   * @return a Boolean
   */
  public Boolean getValidate() {
    return _feature.get("http://xml.org/sax/features/validation");
  }

  /**
   * Shorthand for
   * 
   * <pre>
   * setFeature(&quot;http://xml.org/sax/features/validation&quot;, value)
   * </pre>.
   * 
   * @param value boolean flag
   */
  public void setValidate(final boolean value) {
    _feature.put("http://xml.org/sax/features/validation", Boolean.valueOf(value));
  }

  /**
   * These are the same features as allowed by the used parser. However, it's not checked here if they are supported or
   * not.
   * @param feature the feature
   * @param value the boolean value
   */
  public void setFeature(final String feature, final boolean value) {
    _feature.put(feature, Boolean.valueOf(value));
  }

  /**
   * Returns all features as String-Objects in this iterator. Used in conjuction with {@link #getFeatureValue()} it is
   * possible to retrieve all set features and their values.
   * @return an Iterator
   */
  public Iterator<String> getFeatures() {
    return _feature.keySet().iterator();
  }

  /**
   * Returns the value of a given feature. Used in conjuction with {@link #getFeatures()} it is possible to retrieve all
   * set features and their values.
   * @param feature the feature
   * @return a boolean
   */
  public boolean getFeatureValue(final String feature) {
    final Boolean b = _feature.get(feature);
    return b.booleanValue();
  }

}
