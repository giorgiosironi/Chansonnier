/***********************************************************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.search.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.lucene.LuceneIndexService;
import org.eclipse.smila.search.api.SearchService;
import org.eclipse.smila.search.api.helper.QueryBuilder;
import org.eclipse.smila.search.api.internal.ResultDocumentBuilder;
import org.eclipse.smila.search.servlet.activator.Activator;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.eclipse.smila.utils.xml.XMLUtilsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author jschumacher
 *
 */
public class SMILASearchServlet extends HttpServlet {

  /**
   * default pipeline name.
   */
  public static final String DEFAULT_PIPELINE = "SearchPipeline";

  /**
   * default stylesheet name.
   */
  public static final String DEFAULT_STYLESHEET = "SMILASearchDefault";

  /**
   * Element name for the index names list.
   */
  public static final String ELEMENT_INDEX_NAMES = "IndexNames";

  /**
   * Element name for a index names list entry.
   */
  public static final String ELEMENT_INDEX_NAME = "IndexName";

  /**
   * because it's serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Logging.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * helper for parsing multipart requests, used to include binary attachments.
   */
  private ServletFileUpload _fileUpload;

  /**
   * helper for converting errors to XML.
   */
  private ResultDocumentBuilder _errorBuilder;

  /**
   * {@inheritDoc}
   *
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPut(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response);
  }

  /**
   * extract query parameters from request, create SMILA Query record and send it to a SearchService, transform the DOM
   * result to HTML using an XSLT stylesheet.
   *
   * @param request
   *          HTTP request
   * @param response
   *          HTTP response
   * @throws ServletException
   *           error during processing
   * @throws IOException
   *           error writing result to response stream
   */
  protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {
    boolean debug = false;
    try {
      request.setCharacterEncoding("UTF-8");
    } catch (final UnsupportedEncodingException e) {
      throw new ServletException("unable to set request encoding to UTF-8");
    }
    final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    QueryBuilder query = null;
    if (isMultipart) {
      try {
        final List items = getFileUploadParser().parseRequest(request);
        final MultiPartRequestParser parser = new MultiPartRequestParser(DEFAULT_PIPELINE);
        query = parser.parse(items);
      } catch (final FileUploadException ex) {
        throw new ServletException("error parsing multipart request", ex);
      }

    } else {
      // TODO: get default pipeline name from configuration
      final HttpRequestParser parser = new HttpRequestParser(DEFAULT_PIPELINE);
      query = parser.parse(request);
    }
    final Boolean debugParam = query.getParameters().getBooleanParameter("DEBUG");
    if (debugParam != null) {
      debug = debugParam.booleanValue();
    }
    final SearchService searchService = Activator.getSearchService();
    Document resultDoc = null;
    try {
      if (searchService == null) {
        resultDoc =
          getErrorBuilder().buildError(
            new ServletException(
              "The SearchService is not available. Please wait a moment and try again."));
      } else {
        resultDoc = query.executeRequestXml(searchService);
        appendIndexList(resultDoc);
      }
    } catch (final ParserConfigurationException ex) {
      throw new ServletException(
        "Error creating an XML result to display. Something is completely wrong in the SMILA setup.", ex);
    }
    String stylesheet = query.getParameters().getParameter("style");
    if (StringUtils.isEmpty(stylesheet)) {
      // TODO: get default stylesheet name from configuration
      stylesheet = DEFAULT_STYLESHEET;
    }
    try {
      byte[] result = null;
      if (debug) {
        response.setContentType("text/xml");
        result = XMLUtils.stream(resultDoc.getDocumentElement(), false);
      } else {
        response.setContentType("text/html;charset=UTF-8");
        result = transform(resultDoc, stylesheet);
      }
      response.getOutputStream().write(result);
      response.getOutputStream().flush();
    } catch (final XMLUtilsException e) {
      if (_log.isErrorEnabled()) {
        _log.error(e);
      }
    }
  }

  /**
   * transform DOM documment using stylesheet.
   *
   * @param doc
   *          DOM search result document
   * @param stylesheet
   *          stylesheet name (without .xsl suffix)
   * @return HTML result
   * @throws ServletException
   *           error during transformation
   */
  protected byte[] transform(final Document doc, final String stylesheet) throws ServletException {
    final DOMSource xmlDomSource = new DOMSource(doc);

    // sw recieves the transformation's result.
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final StreamResult stream = new StreamResult(baos);

    // transform via StreamResult into the StringWriter
    InputStream is = null;
    try {
      is = ConfigUtils.getConfigStream(Activator.BUNDLE_NAME, stylesheet + ".xsl");
      final Document xslDoc = XMLUtils.parse(is, false);
      final Transformer transformer = getXSLTransformer(xslDoc);
      transformer.setParameter("stylesheet", stylesheet);
      transformer.transform(xmlDomSource, stream);
    } catch (final XMLUtilsException xue) {
      throw new ServletException("the stylesheet is not valid [" + stylesheet + ".xsl]", xue);
    } catch (final TransformerException te) {
      throw new ServletException("Error ocured while transforming!", te);
    } finally {
      IOUtils.closeQuietly(is);
    }

    return baos.toByteArray();
  }

  /**
   * create XSL transformer for stylesheet.
   *
   * @param xslDoc
   *          XSL DOM document
   * @return XSL transformer
   * @throws ServletException
   *           error.
   */
  protected Transformer getXSLTransformer(final Document xslDoc) throws ServletException {
    final TransformerFactory tFactory = TransformerFactory.newInstance();
    if (tFactory.getFeature(DOMSource.FEATURE) && tFactory.getFeature(StreamResult.FEATURE)) {

      final DOMSource xslDomSource = new DOMSource(xslDoc);

      try {
        return tFactory.newTransformer(xslDomSource);
      } catch (final TransformerConfigurationException e) {
        throw new ServletException("error while creating the transformer", e);
      }
    } else {
      throw new ServletException("the transformer [" + tFactory.getClass().getName()
        + "] doesn't support the used DOMSource or StreamResult");
    }
  }

  /**
   * Appends a list of index names to the document.
   *
   * @param doc
   *          the document to append the list to
   */
  protected void appendIndexList(final Document doc) {
    final LuceneIndexService indexService = Activator.getLuceneIndexService();
    if (indexService != null) {
      final Iterator<String> indexNames = indexService.getIndexNames();
      if (indexNames != null) {
        final Element indexNamesElement = doc.createElementNS(SearchService.NAMESPACE_SEARCH, ELEMENT_INDEX_NAMES);
        while (indexNames.hasNext()) {
          final String indexName = indexNames.next();
          if (indexName != null) {
            final Element nameElement = doc.createElement(ELEMENT_INDEX_NAME);
            nameElement.setTextContent(indexName);
            indexNamesElement.appendChild(nameElement);
          }
        }
        doc.getDocumentElement().appendChild(indexNamesElement);
      }
    }
  }

  /**
   * ensure and return a file upload parser.
   *
   * @return FileUpload helper.
   */
  protected synchronized ServletFileUpload getFileUploadParser() {
    if (_fileUpload == null) {
      // Create a factory for disk-based file items
      final FileItemFactory factory = new DiskFileItemFactory(Integer.MAX_VALUE, null);
      // Create a new file upload handler
      _fileUpload = new ServletFileUpload(factory);
      // Parse the request. List of FileItem
    }
    return _fileUpload;
  }

  /**
   * ensure and return a ResultDocumentBuilder.
   *
   * @return ResultDocumentBuilder helper.
   */
  protected synchronized ResultDocumentBuilder getErrorBuilder() {
    if (_errorBuilder == null) {
      _errorBuilder = new ResultDocumentBuilder();
    }
    return _errorBuilder;
  }
}
