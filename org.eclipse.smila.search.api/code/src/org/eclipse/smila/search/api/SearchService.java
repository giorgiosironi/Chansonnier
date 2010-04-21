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
package org.eclipse.smila.search.api;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.ProcessingException;
import org.w3c.dom.Document;

/**
 * Interface of SMILA search services. The usual purpose of such a service is to put the query object on a blackboard,
 * create a search message to be executed with a {@link org.eclipse.smila.processing.WorkflowProcessor} and finally
 * create the {@link SearchResult} object
 *
 * For the structure of the query record see the Search API specificaton page in the Eclipse Wiki: <a
 * href="http://wiki.eclipse.org/SMILA/Specifications/Search_API">
 * http://wiki.eclipse.org/SMILA/Specifications/Search_API</a>.
 *
 * @author jschumacher
 *
 */
public interface SearchService {
  /**
   * namespace of search specific elements in XML result.
   */
  String NAMESPACE_SEARCH = "http://www.eclipse.org/smila/search";

  /**
   * local name of XML result's root element.
   */
  String TAG_SEARCHRESULT = "SearchResult";

  /**
   * local name of XML result's element containing the pipeline name.
   */
  String TAG_WORKFLOWNAME = "Workflow";

  /**
   * local name of XML result's element containing the pipeline name.
   */
  String TAG_QUERY = "Query";

  /**
   * local name of XML result's error root element.
   */
  String TAG_ERROR = "Error";

  /**
   * local name of XML result's error message element.
   */
  String TAG_MESSAGE = "Message";

  /**
   * local name of XML result's error details element.
   */
  String TAG_DETAILS = "Details";

  /**
   * Execute a query using the named workflow and create a SearchResult from the workflow result.
   *
   * @param workflowName
   *          name of workflow to process the search.
   * @param query
   *          query object describing the search.
   * @return search result.
   * @throws ProcessingException
   *           error while processing the request or preparing the input message or search result.
   */
  SearchResult search(String workflowName, Record query) throws ProcessingException;

  /**
   * Execute a query using the named workflow and return the search result as an XML document.
   *
   * @param workflowName
   *          name of workflow to process the search.
   * @param query
   *          query object describing the search.
   * @return search result as DOM document, or description of ProcessingException
   * @throws ParserConfigurationException
   *           error creating the XML result.
   */
  Document searchAsXml(String workflowName, Record query) throws ParserConfigurationException;

  /**
   * Execute a query using the named workflow and return the search result as an XML string.
   *
   * @param workflowName
   *          name of workflow to process the search.
   * @param query
   *          query object describing the search.
   * @return search result as XML string, or description of ProcessingException
   * @throws ParserConfigurationException
   *           error creating the XML result.
   */
  String searchAsXmlString(String workflowName, Record query) throws ParserConfigurationException;

}
