/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XMLUtils;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Pipelet that extracts elements selected by XPath and converts them in appropriate data types (Boolean, Double,
 * String). The possible properties are:
 * <ul>
 * <li>xpath: the XPath</li>
 * <li>seperator: the seperator (optional)</li>
 * <li>namespace: the namespace (optional)</li>
 * <li>inputName: name of the Attribute/Attachment to read the XML Document from</li>
 * <li>outputName: name of the Attribute/Attachment to store the extracted value in</li>
 * <li>inputType: the type (Attribute or Attachment of the inputName</li>
 * <li>outputType: the type (Attribute or Attachment of the outputtName</li>
 * </ul>
 */
public class XPathExtractorPipelet extends AXmlTransformationPipelet {
  /**
   * The name of the XSLT file used for the transformation.
   */
  public static final String PROP_XPATH = "xpath";

  /**
   * The seperator property.
   */
  public static final String PROP_SEPERATOR = "seperator";

  /**
   * The namespace property.
   */
  public static final String PROP_NAMESPACE = "namespace";

  /**
   * The XPath.
   */
  private String _xpath;

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
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    _xpath = (String) configuration.getPropertyFirstValueNotNull(PROP_XPATH);
    if (_xpath.trim().length() == 0) {
      throw new ProcessingException("Property " + PROP_XPATH + " must not be an empty String");
    }
    _separator = (String) configuration.getPropertyFirstValue(PROP_SEPERATOR);
    if (_separator == null) {
      _separator = "";
    }
    _namespace = (String) configuration.getPropertyFirstValue(PROP_NAMESPACE);
    if (_namespace == null) {
      _namespace = "";
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
      for (Id id : recordIds) {
        try {
          final Document inputDocument = createDocument(blackboard, id);
          if (inputDocument != null) {
            final Object result =
              XPathUtils.queryForIndexField(inputDocument, _xpath, _namespaceElement, _separator);
            if (result != null) {
              if (isStoreInAttribute()) {
                final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
                if (result instanceof Boolean) {
                  literal.setBoolValue((Boolean) result);
                } else if (result instanceof Double) {
                  literal.setFpValue((Double) result);
                } else if (result instanceof String) {
                  literal.setStringValue((String) result);
                } else {
                  literal.setStringValue(result.toString());
                }
                blackboard.setLiteral(id, _outputPath, literal);
              } else {
                storeResult(blackboard, id, result.toString());
              }
            }
          }
        } catch (Exception e) {
          if (_log.isWarnEnabled()) {
            _log.warn("unable to transform document " + id, e);
          }
        }
      } // for
    } // if
    return recordIds;
  }
}
