/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XMLUtils;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Pipelet that filters elements by XPath (either include or exclude mode). The possible properties are:
 * <ul>
 * <li>xpath: the XPaths (multivalue)</li>
 * <li>seperator: the seperator (optional)</li>
 * <li>namespace: the namespace (optional)</li>
 * <li>inputName: name of the Attribute/Attachment to read the XML Document from</li>
 * <li>outputName: name of the Attribute/Attachment to store the extracted value in</li>
 * <li>inputType: the type (Attribute or Attachment of the inputName</li>
 * <li>outputType: the type (Attribute or Attachment of the outputtName</li>
 * </ul>
 */
public class XPathFilterPipelet extends AXmlTransformationPipelet {

  /**
   * The XPathFilerMode, either include or exclude.
   * 
   * @author stuc07
   * 
   */
  public enum XPathFilerMode {
    /**
     * Inlcude Xpath expressions.
     */
    INCLUDE,
    /**
     * Exclude XPath expressions.
     */
    EXCLUDE
  }

  /**
   * The name of the XSLT file used for the transformation.
   */
  public static final String PROP_XPATH = "xpath";

  /**
   * Property for the XPathFilerMode to execute.
   */
  public static final String PROP_FILTER_MODE = "filterMode";

  /**
   * The seperator property.
   */
  public static final String PROP_SEPERATOR = "seperator";

  /**
   * The rootElement property. Only valid for filter mode include.
   */
  public static final String PROP_NAMESPACE = "namespace";

  /**
   * The namespace property.
   */
  public static final String PROP_ROOT_ELEMENT = "rootElement";

  /**
   * The XPath.
   */
  private String[] _xpaths;

  /**
   * The XPathFilerMode.
   */
  private XPathFilerMode _filterMode = XPathFilerMode.INCLUDE;

  /**
   * The seperator.
   */
  private String _separator;

  /**
   * The namespace.
   */
  private String _namespace;

  /**
   * The namespace element.
   */
  private Element _namespaceElement;

  /**
   * Name of the root element in the output document.
   */
  private String _root;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    _xpaths = configuration.getPropertyStringValues(PROP_XPATH);
    if (_xpaths == null || _xpaths.length == 0) {
      throw new ProcessingException("Property " + PROP_XPATH + " must not be Null or an empty String");
    }

    final String mode = (String) configuration.getPropertyFirstValue(PROP_FILTER_MODE);
    if (mode != null) {
      _filterMode = XPathFilerMode.valueOf(mode);
    }

    _separator = (String) configuration.getPropertyFirstValue(PROP_SEPERATOR);
    if (_separator == null) {
      _separator = "";
    }
    _namespace = (String) configuration.getPropertyFirstValue(PROP_NAMESPACE);
    if (_namespace == null) {
      _namespace = "";
    }

    if (XPathFilerMode.INCLUDE.equals(_filterMode)) {
      _root = (String) configuration.getPropertyFirstValue(PROP_ROOT_ELEMENT);
      if (_root == null || _root.trim().length() == 0) {
        throw new ProcessingException("Property " + PROP_ROOT_ELEMENT + " must not be Null or an empty String");
      }
    }

    final Document doc = XMLUtils.getDocument();
    _namespaceElement = doc.createElement("NamespaceDef");
    final String[] namespaces = _namespace.split(" ");
    for (int i = 0; i < namespaces.length; i++) {
      if (!"".equals(namespaces[i].trim())) {
        final String[] nsItems = namespaces[i].split("=");

        if (nsItems.length != 2) {
          throw new ProcessingException("Property " + PROP_NAMESPACE
            + " in invalid format [Namespace;ns1=val ns2=val]");
        }
        _namespaceElement.setAttribute("xmlns:" + nsItems[0], nsItems[1]);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (final Id id : recordIds) {
        try {
          final Document inputDocument = createDocument(blackboard, id);
          Document result;
          if (inputDocument != null) {
            if (XPathFilerMode.INCLUDE.equals(_filterMode)) {
              result = includeElements(inputDocument);
            } else {
              result = excludeElements(inputDocument);
            }
            storeDocument(blackboard, id, result);
          }
        } catch (final Exception e) {
          if (_log.isWarnEnabled()) {
            _log.warn("unable to transform document " + id, e);
          }
        }
      } // for
    } // if
    return recordIds;
  }

  /**
   * Include selected elements by XPath.
   * 
   * @param inputDocument
   *          the input Document
   * @return a Document
   * @throws ProcessingException
   *           if any error occurs
   */
  private Document includeElements(final Document inputDocument) throws ProcessingException {
    final Document doc = XMLUtils.getDocument();
    Element rootElement;
    rootElement = doc.createElement(_root);
    doc.appendChild(rootElement);
    try {
      for (final String xpath : _xpaths) {
        final XObject xobj = XPathAPI.eval(inputDocument, xpath, _namespaceElement);
        final NodeList nl = xobj.nodelist();

        for (int i = 0; i < nl.getLength(); i++) {
          final Node n = nl.item(i);
          rootElement.appendChild(doc.importNode(n, true));
        } // for
      } // for
    } catch (final TransformerException e) {
      throw new ProcessingException("Transformer exception: " + e.getMessage());
    }
    return doc;
  }

  /**
   * Exclude selected elements by XPath.
   * 
   * @param inputDocument
   *          the input Document
   * @return a Document
   */
  private Document excludeElements(Document inputDocument) {
    for (final String xpath : _xpaths) {
      XPathUtils.removeNodesByXPath(inputDocument, xpath, _namespaceElement);
    }
    return inputDocument;
  }
}
