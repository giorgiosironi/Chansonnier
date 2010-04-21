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
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Pipelet that removes a selected element from an XML document. The possible properties are:
 * <ul>
 * <li>elementId: the id of the XML element</li>
 * <li>inputName: name of the Attribute/Attachment to read the XML Document from</li>
 * <li>outputName: name of the Attribute/Attachment to store the manipulated XML Document in</li>
 * <li>inputType: the type (Attribute or Attachment of the inputName</li>
 * <li>outputType: the type (Attribute or Attachment of the outputtName</li>
 * </ul>
 */
public class RemoveElementFromXMLPipelet extends AXmlTransformationPipelet {
  /**
   * Property for the Id of the XML element..
   */
  public static final String PROP_ELEMENT_ID = "elementId";

  /**
   * The element Id.
   */
  private String _elementId;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    _elementId = (String) configuration.getPropertyFirstValueNotNull(PROP_ELEMENT_ID);
    if (_elementId.trim().length() == 0) {
      throw new ProcessingException("Property " + PROP_ELEMENT_ID + " must not be an empty String");
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
            final Element el = inputDocument.getElementById(_elementId);
            if (el != null) {
              final Node parent = el.getParentNode();
              parent.removeChild(el);
            }            
            storeDocument(blackboard, id, inputDocument);
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
