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
import org.eclipse.smila.processing.pipelets.ATransformationPipelet;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XMLUtils;
import org.w3c.dom.Document;

/**
 * Abstract base class for XML processing pipelets. 
 */
public abstract class AXmlTransformationPipelet extends ATransformationPipelet {
  
  /**
   * Creates a Document from an Attribute or Attachment.
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the Id of the record
   * @return a Document
   * @throws Exception
   *           if any error occurs
   */
  protected Document createDocument(final Blackboard blackboard, final Id id) throws Exception {
    Document inputDocument = null;
    if (isReadFromAttribute()) {
      final Literal literal = blackboard.getLiteral(id, _inputPath);
      if (literal != null) {
        final String value = literal.getStringValue();
        if (value != null) {
          inputDocument = XMLUtils.parse(value.getBytes(ENCODING_ATTACHMENT), false);
        }
      }
    } else {
      final byte[] value = blackboard.getAttachment(id, _inputName);
      inputDocument = XMLUtils.parse(value, false);
    }
    return inputDocument;
  }

  /**
   * Stores a Document in an Attribute or Attachment.
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the Id of the record
   * @param doc
   *          the Document to save
   * @throws Exception
   *           if any error occurs
   */
  protected void storeDocument(final Blackboard blackboard, final Id id, final Document doc) throws Exception {
    if (isStoreInAttribute()) {
      final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
      literal.setStringValue(XMLUtils.documentToString(doc));
      blackboard.setLiteral(id, _outputPath, literal);
    } else {
      final byte[] attachment = XMLUtils.documentToBytes(doc);      
      blackboard.setAttachment(id, _outputName, attachment);
    }
  }
}
