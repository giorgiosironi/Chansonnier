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

package org.eclipse.smila.search.api.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smila.datamodel.record.dom.RecordBuilder;
import org.eclipse.smila.search.api.SearchResult;
import org.eclipse.smila.search.api.SearchService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * create an XML document from a search result.
 *
 * @author jschumacher
 *
 */
public class ResultDocumentBuilder {

  /**
   * DOM builder factory.
   */
  private DocumentBuilderFactory _builderFactory;

  /**
   * used for creating Record DOM elements.
   */
  private final RecordBuilder _recordBuilder = new RecordBuilder();

  /**
   * create instance.
   */
  public ResultDocumentBuilder() {
    _builderFactory = DocumentBuilderFactory.newInstance();
    _builderFactory.setNamespaceAware(true);
  }

  /**
   * build &lt;proc:SearchResult&gt; DOM document from search result.
   *
   * @param result
   *          search result
   * @return DOM docoument
   * @throws ParserConfigurationException
   *           error creating document.
   */
  public Document buildResult(final SearchResult result) throws ParserConfigurationException {
    final Document doc = _builderFactory.newDocumentBuilder().newDocument();
    final Element root = doc.createElementNS(SearchService.NAMESPACE_SEARCH, SearchService.TAG_SEARCHRESULT);
    doc.appendChild(root);
    final Element query = doc.createElementNS(SearchService.NAMESPACE_SEARCH, SearchService.TAG_QUERY);
    root.appendChild(query);
    final Element workflowName =
      doc.createElementNS(SearchService.NAMESPACE_SEARCH, SearchService.TAG_WORKFLOWNAME);
    workflowName.appendChild(doc.createTextNode(result.getWorkflowName()));
    query.appendChild(workflowName);
    if (result.getQuery() != null) {
      query.appendChild(_recordBuilder.buildRecord(doc, result.getQuery()));
    }
    if (result.getRecords() != null) {
      _recordBuilder.appendRecordList(root, result.getRecords());
    }
    return doc;
  }

  /**
   * @param ex
   *          exception
   * @return DOM document containing message and stack trace of the exception.
   * @throws ParserConfigurationException
   *           error creating document.
   */
  public Document buildError(final Exception ex) throws ParserConfigurationException {
    final Document doc = _builderFactory.newDocumentBuilder().newDocument();
    final Element root = doc.createElementNS(SearchService.NAMESPACE_SEARCH, SearchService.TAG_ERROR);
    doc.appendChild(root);
    final Element errorMessage = doc.createElementNS(SearchService.NAMESPACE_SEARCH, SearchService.TAG_MESSAGE);
    root.appendChild(errorMessage);
    Throwable cause = ex;
    String message = ex.getLocalizedMessage();
    while (cause.getCause() != null && cause.getCause() != cause) {
      cause = cause.getCause();
      if (cause.getLocalizedMessage() != null && cause.getLocalizedMessage().length() > 0) {
        message = cause.getLocalizedMessage();
      }
    }
    errorMessage.appendChild(doc.createTextNode(message));
    final Element errorDetails = doc.createElementNS(SearchService.NAMESPACE_SEARCH, SearchService.TAG_DETAILS);
    root.appendChild(errorDetails);
    final StringWriter stacktrace = new StringWriter();
    final PrintWriter writer = new PrintWriter(stacktrace);
    ex.printStackTrace(writer);
    writer.close();
    errorDetails.appendChild(doc.createTextNode(stacktrace.toString()));
    return doc;
  }
}
