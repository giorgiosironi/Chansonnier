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
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XslTransformer;
import org.w3c.dom.Document;

/**
 * Pipelet that performs an XSL transformation on an attribute or attachment value. The possible properties are:
 * <ul>
 * <li>xslFile: the name of the XSLT file</li>
 * <li>inputName: name of the Attribute/Attachment to apply the XSL transformation to</li>
 * <li>outputName: name of the Attribute/Attachment to store the XSL transformation in</li>
 * <li>inputType: the type (Attribute or Attachment of the inputName</li>
 * <li>outputType: the type (Attribute or Attachment of the outputtName</li>
 * </ul>
 */
public class XslTransformationPipelet extends AXmlTransformationPipelet {
  /**
   * The name of the XSLT file used for the transformation.
   */
  public static final String PROP_XSL_FILE = "xslFile";

  /**
   * XSLT file.
   */
  private String _xslFile;

  /**
   * XSLT transformer.
   */
  private XslTransformer _xslTransformer;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    _xslFile = (String) configuration.getPropertyFirstValueNotNull(PROP_XSL_FILE);
    _xslTransformer = new XslTransformer();    
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
            final Document result = _xslTransformer.transform(inputDocument, _xslFile);
            storeDocument(blackboard, id, result);
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
