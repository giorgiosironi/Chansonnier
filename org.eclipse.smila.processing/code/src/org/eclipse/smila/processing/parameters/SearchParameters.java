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
package org.eclipse.smila.processing.parameters;

/**
 * Constants for names and values of service/pipelet parameters commonly used in search pipelines.
 * 
 * @author jschumacher
 * 
 */
public final class SearchParameters {

  /**
   * name of the record level annotation that contains all service/pipelet runtime parameters.
   */
  public static final String PARAMETERS = "parameters";

  /**
   * name of the "query" string parameter.
   */
  public static final String QUERY = "query";

  /**
   * name of the "resultAttributes" parameter.
   */
  public static final String RESULTATTRIBUTES = "resultAttributes";
  
  /**
   * name of the "resultSize" parameter.
   */
  public static final String RESULTSIZE = "resultSize";

  /**
   * name of the "resultOffset" parameter.
   */
  public static final String RESULTOFFSET = "resultOffset";

  /**
   * name ofthe "threshold" parameter.
   */
  public static final String THRESHOLD = "threshold";

  /**
   * name of the "language" parameter.
   */
  public static final String LANGUAGE = "language";

  /**
   * name of the "indexName" parameter.
   */
  public static final String INDEXNAME = "indexName";

  /**
   * name of the "orderBy" parameter subannotations.
   */
  public static final String ORDERBY = "orderBy";

  /**
   * name of the "attribute" parameter of an "orderBy" annotation.
   */
  public static final String ORDERBY_ATTRIBUTE = "attribute";

  /**
   * name of the "mode" parameter of an "orderBy" annotation.
   */
  public static final String ORDERBY_MODE = "mode";

  /**
   * default "resultSize" value: 10.
   */
  public static final int DEFAULT_RESULTSIZE = 10;

  /**
   * default "resultOffset" value: 0.
   */
  public static final int DEFAULT_RESULTOFFSET = 0;

  /**
   * default "threshold "value: 0.0.
   */
  public static final double DEFAULT_THRESHOLD = 0.0;

  /**
   * Values for the "orderBy"/"mode" parameter: Sort ascending or descending.
   */
  public enum OrderMode {
    /**
     * sort ascending.
     */
    ASC,
    /**
     * sort descending.
     */
    DESC
  }

  /**
   * prevent instance creation.
   */
  private SearchParameters() {
    // prevent instance creation
  }

}
