/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * A XslTransformer.
 */
public class XslTransformer {

  /**
   * Transformer factory.
   */
  private static TransformerFactory s_transformerFactory = TransformerFactory.newInstance();

  /**
   * Templates.
   */
  private final Hashtable<String, SubTemplates> _templates = new Hashtable<String, SubTemplates>();

  /**
   * Default Constructor.
   */
  public XslTransformer() {
  }

  /**
   * 
   * @author stuc07
   *
   */
  private class SubTemplates {
    /**
     * The templates.
     */
    private Templates _templates;

    /**
     * The timestamp.
     */
    private long _timestamp;

    /**
     * Conversion Constructor.
     * @param templates the templates
     * @param timestamp the timestamp
     */
    public SubTemplates(Templates templates, long timestamp) {
      this._templates = templates;
      this._timestamp = timestamp;
    }

    /**
     * Gets the templates.
     * @return the Templates
     */
    public Templates getTemplates() {
      return _templates;
    }

    /**
     * Sets the templates.
     * @param templates the templates
     */
    public void setTemplates(Templates templates) {
      this._templates = templates;
    }

    /**
     * Gets the timestamp.
     * @return a long containing the timestamp
     */
    public long getTimestamp() {
      return _timestamp;
    }

    /**
     * Sets  the timestamp.
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) {
      this._timestamp = timestamp;
    }
  }

  /**
   * Transforms a Document using the given xslFile.
   * @param document the Document to transform
   * @param xslFile the xslFile
   * @return the transformed Document
   * @throws XslException if any error occurs
   * @throws XmlException if any error occurs
   * @throws IOException if any error occurs
   */
  public Document transform(Document document, String xslFile) throws XslException, XmlException, IOException {
    try {
      final Transformer transformer = getTransformer(xslFile);
      final Source source = new DOMSource(document);
      final Document resultDocument = XMLUtils.newDocument();
      final DOMResult result = new DOMResult(resultDocument);
      transformer.transform(source, result);
      return resultDocument;
    } catch (final TransformerException e) {
      throw new XslException(e);
    }
  }

  /**
   * Get XSLT transformer.
   * 
   * @param xslFile
   *          XSLT file.
   * @return Transformer.
   * @throws XslException
   *           Unable to get transformer.
   * @throws IOException
   *           Unable to work with xslFile.
   */
  private Transformer getTransformer(String xslFile) throws XslException, IOException {
    SubTemplates subTemplates = _templates.get(xslFile);
    try {
      final File file = new File(xslFile);
      if (!file.exists()) {
        throw new FileNotFoundException(xslFile);
      }
      if (subTemplates == null || (subTemplates.getTimestamp() != file.lastModified())) {
        final Source source = new StreamSource(file);
        final Templates template = s_transformerFactory.newTemplates(source);
        final SubTemplates newSubTemplates = new SubTemplates(template, file.lastModified());
        _templates.put(xslFile, newSubTemplates);

        subTemplates = newSubTemplates;
      }
      return subTemplates.getTemplates().newTransformer();
    } catch (final TransformerConfigurationException e) {
      throw new XslException(e);
    }
  }
}
