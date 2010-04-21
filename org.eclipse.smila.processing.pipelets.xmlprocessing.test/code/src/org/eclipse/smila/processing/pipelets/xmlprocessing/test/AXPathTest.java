/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.test;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.pipelets.ATransformationPipelet;
import org.eclipse.smila.processing.pipelets.test.APipeletTest;
import org.eclipse.smila.processing.pipelets.xmlprocessing.AXmlTransformationPipelet;

/**
 * Abstract base test class for XPath tests..
 */
public abstract class AXPathTest extends APipeletTest {

  /**
   * Filter by Attribute.
   * 
   * @param pipelet
   *          the AXmlTransformationPipelet
   * @param config
   *          the configuration to load
   * @param input
   *          the test input
   * @return a String
   * @throws Exception
   *           if any error occurs
   */
  protected String filterByAttribute(AXmlTransformationPipelet pipelet, String config, String input)
    throws Exception {
    // load configuration
    final PipeletConfiguration configuration =
      loadPipeletConfiguration("org.eclipse.smila.processing.pipelets.xmlprocessing", config);
    pipelet.configure(configuration);

    // prepare test data
    final Id id = createBlackboardRecord("testSource", "testId");
    final Id[] recordIds = new Id[] { id };
    final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    literal
      .setStringValue(IOUtils.toString(new FileInputStream(input), ATransformationPipelet.ENCODING_ATTACHMENT));
    getBlackboard().setLiteral(id, pipelet.getInputPath(), literal);

    // execute
    pipelet.process(getBlackboard(), recordIds);

    // check result
    assertTrue(getBlackboard().hasAttribute(id, pipelet.getOutputPath()));
    final Literal result = getBlackboard().getLiteral(id, pipelet.getOutputPath());
    assertNotNull(result);
    final String xmlString = result.toString();
    assertNotNull(xmlString);
    return xmlString;
  }

  /**
   * Filter by Attachment.
   * 
   * @param pipelet
   *          the AXmlTransformationPipelet
   * @param config
   *          the configuration to load
   * @param input
   *          the test input
   * @return a String
   * @throws Exception
   *           if any error occurs
   */
  protected String filterByAttachment(AXmlTransformationPipelet pipelet, String config, String input)
    throws Exception {
    // load configuration
    final PipeletConfiguration configuration =
      loadPipeletConfiguration("org.eclipse.smila.processing.pipelets.xmlprocessing", config);
    pipelet.configure(configuration);

    // prepare test data
    final Id id = createBlackboardRecord("testSource", "testId");
    final Id[] recordIds = new Id[] { id };
    getBlackboard().setAttachment(id, pipelet.getInputName(), IOUtils.toByteArray(new FileInputStream(input)));

    // execute
    pipelet.process(getBlackboard(), recordIds);

    // check result
    assertTrue(getBlackboard().hasAttachment(id, pipelet.getOutputName()));
    final byte[] result = getBlackboard().getAttachment(id, pipelet.getOutputName());
    assertNotNull(result);
    final String xmlString = new String(result, ATransformationPipelet.ENCODING_ATTACHMENT);
    assertNotNull(xmlString);
    return xmlString;
  }

  /**
   * Removes the BOM from a UTF-8 String.
   * 
   * @param stringWithBom
   *          the String with BOM
   * @return a String without the BOM
   */
  protected String removeBOM(String stringWithBom) {
    return stringWithBom.substring(1);
  }
}
