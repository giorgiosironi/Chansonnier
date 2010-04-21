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
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.pipelets.ATransformationPipelet;
import org.eclipse.smila.processing.pipelets.test.APipeletTest;
import org.eclipse.smila.processing.pipelets.xmlprocessing.RemoveElementFromXMLPipelet;

/**
 * Test the emoveElementFromXMLPipelet.
 */
public class TestRemoveElementFromXMLPipelet extends APipeletTest {

  /**
   * Constant for the xml document to transform.
   */
  private static final String AUTHOR_XML = "./configuration/data/author_id.xml";

  /**
   * Constant for the expected xml document result.
   */
  private static final String AUTHOR_REMOVED_XML = "./configuration/data/author_id_removed.xml";

  /**
   * Test RemoveElementFromXMLPipelet with Attributes.
   * 
   * @throws Exception
   *           test fails
   */
  public void testXSLTbyAttribute() throws Exception {
    // load configuration
    final PipeletConfiguration configuration =
      loadPipeletConfiguration("org.eclipse.smila.processing.pipelets.xmlprocessing",
        "TestRemoveElementFromXMLPipeletByAttribute.xml");
    final RemoveElementFromXMLPipelet pipelet = new RemoveElementFromXMLPipelet();
    pipelet.configure(configuration);

    // prepare test data
    final Id id = createBlackboardRecord("testSource", "testId");
    final Id[] recordIds = new Id[] { id };
    final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    literal.setStringValue(IOUtils.toString(new FileInputStream(AUTHOR_XML),
      ATransformationPipelet.ENCODING_ATTACHMENT));
    getBlackboard().setLiteral(id, pipelet.getInputPath(), literal);

    // execute
    pipelet.process(getBlackboard(), recordIds);

    // check result
    assertTrue(getBlackboard().hasAttribute(id, pipelet.getOutputPath()));
    final Literal result = getBlackboard().getLiteral(id, pipelet.getOutputPath());
    assertNotNull(result);
    final String xmlString = result.toString();
    assertNotNull(xmlString);
    final String expected =
      removeBOM(IOUtils.toString(new FileInputStream(AUTHOR_REMOVED_XML),
        ATransformationPipelet.ENCODING_ATTACHMENT));
    final Diff diff = XMLUnit.compareXML(expected, xmlString);
    assertTrue(diff.identical());
  }

  /**
   * Test RemoveElementFromXMLPipelet with Attachments.
   * 
   * @throws Exception
   *           test fails
   */
  public void testXSLTbyAttachment() throws Exception {
    // load configuration
    final PipeletConfiguration configuration =
      loadPipeletConfiguration("org.eclipse.smila.processing.pipelets.xmlprocessing",
        "TestRemoveElementFromXMLPipeletByAttachment.xml");
    final RemoveElementFromXMLPipelet pipelet = new RemoveElementFromXMLPipelet();
    pipelet.configure(configuration);

    // prepare test data
    final Id id = createBlackboardRecord("testSource", "testId");
    final Id[] recordIds = new Id[] { id };
    getBlackboard().setAttachment(id, pipelet.getInputName(), IOUtils.toByteArray((new FileInputStream(AUTHOR_XML))));

    // execute
    pipelet.process(getBlackboard(), recordIds);

    // check result
    assertTrue(getBlackboard().hasAttachment(id, pipelet.getOutputName()));
    final byte[] result = getBlackboard().getAttachment(id, pipelet.getOutputName());
    assertNotNull(result);
    final String xmlString = new String(result, ATransformationPipelet.ENCODING_ATTACHMENT);
    assertNotNull(xmlString);
    final String expected =
      removeBOM(IOUtils.toString(new FileInputStream(AUTHOR_REMOVED_XML),
        ATransformationPipelet.ENCODING_ATTACHMENT));
    final Diff diff = XMLUnit.compareXML(expected, xmlString);
    assertTrue(diff.identical());
  }

  /**
   * Removes the BOM from a UTF-8 String.
   * 
   * @param stringWithBom
   *          the String with BOM
   * @return a String without the BOM
   */
  private String removeBOM(String stringWithBom) {
    return stringWithBom.substring(1);
  }
}
