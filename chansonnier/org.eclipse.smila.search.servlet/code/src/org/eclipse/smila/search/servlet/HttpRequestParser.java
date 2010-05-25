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

package org.eclipse.smila.search.servlet;

import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.search.api.helper.QueryBuilder;

/**
 * fill SMILA query builder from servlet http request.
 * 
 * TODO: Parse and create attribute filters and ranking annotations.
 * 
 * @author jschumacher
 * 
 */
public class HttpRequestParser extends ARequestParser {
  /**
   * create new instance with default pipeline.
   * 
   * @param defaultPipeline
   *          default pipeline name to use, if request does not contain a pipeline parameter.
   */
  public HttpRequestParser(String defaultPipeline) {
    super(defaultPipeline);
  }

  /**
   * create QueryBuilder from http request parameters.
   * 
   * @param request
   *          http request
   * @return new query builder instance.
   * @throws ServletException
   *           error creating a valid query builder.
   */
  public QueryBuilder parse(HttpServletRequest request) throws ServletException {
    String pipeline = request.getParameter("pipeline");
    if (StringUtils.isEmpty(pipeline)) {
      pipeline = _defaultPipeline;
    }
    if (pipeline == null) {
      throw new ServletException("No pipeline name specified.");
    }
    final QueryBuilder builder = new QueryBuilder(pipeline);

    final Enumeration paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements()) {
      final String paramName = (String) paramNames.nextElement();
      final String[] paramValues = request.getParameterValues(paramName);
      if (paramValues != null && paramValues.length > 0) {
        processParameter(builder, paramName, paramValues);
      }
    }
    setupFilters(builder);
    setDefaultParameters(builder);
    return builder;
  }
}
