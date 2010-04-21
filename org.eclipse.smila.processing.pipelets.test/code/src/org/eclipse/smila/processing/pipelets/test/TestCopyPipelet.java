/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets.test;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.pipelets.CopyPipelet;

/**
 * Test case for the CopyPipelet.
 */
public class TestCopyPipelet extends APipeletTest {
  /**
   * bundle name for configuration loading.
   */
  public static final String CONFIG_BUNDLE = "org.eclipse.smila.processing.pipelets";

  /**
   * name of configuration to copy from attribute to attribute.
   */
  public static final String CONFIG_CP_ATTR_TO_ATTR = "cp-attribute-to-attribute.xml";

  /**
   * name of configuration to copy from attribute to attachment.
   */
  public static final String CONFIG_CP_ATTR_TO_ATTACH = "cp-attribute-to-attachment.xml";

  /**
   * name of configuration to copy from attachment to attribute.
   */
  public static final String CONFIG_CP_ATTACH_TO_ATTR = "cp-attachment-to-attribute.xml";

  /**
   * name of configuration to copy from attachment to attachment.
   */
  public static final String CONFIG_CP_ATTACH_TO_ATTACH = "cp-attachment-to-attachment.xml";

  /**
   * name of configuration to move from attribute to attribute.
   */
  public static final String CONFIG_MV_ATTR_TO_ATTR = "mv-attribute-to-attribute.xml";

  /**
   * name of configuration to move from attribute to attachment.
   */
  public static final String CONFIG_MV_ATTR_TO_ATTACH = "mv-attribute-to-attachment.xml";

  /**
   * name of configuration to move from attachment to attribute.
   */
  public static final String CONFIG_MV_ATTACH_TO_ATTR = "mv-attachment-to-attribute.xml";

  /**
   * name of configuration to move from attachment to attachment.
   */
  public static final String CONFIG_MV_ATTACH_TO_ATTACH = "mv-attachment-to-attachment.xml";

  /**
   * A test string used for attribute and attachments value.
   */
  private static final String TEXT = "This is a test string containing some data.";

  /**
   * create and configure CopyPipelet instance.
   * 
   * @param configName
   *          name of configuration file.
   * @return configured pipelet.
   * @throws ProcessingException
   *           error configuring pipelet
   * @throws JAXBException
   *           error loading configuration
   */
  public CopyPipelet createPipelet(final String configName) throws ProcessingException, JAXBException {
    final CopyPipelet pipelet = new CopyPipelet();
    configurePipelet(pipelet, CONFIG_BUNDLE, configName);
    return pipelet;
  }

  /**
   * Test copy attribute to attribute.
   * 
   * @throws Exception
   *           test failed
   */
  public void testCopyAttributeToAttribute() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_CP_ATTR_TO_ATTR);
    final Id id = createBlackboardRecord("copy", "attribute-attribute");
    setAttribute(id, pipelet.getInputPath());
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(TEXT, textLiteral.getStringValue());
  }

  /**
   * Test copy attribute to attachment.
   * 
   * @throws Exception
   *           test failed
   */
  public void testCopyAttributeToAttachment() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_CP_ATTR_TO_ATTACH);
    final Id id = createBlackboardRecord("copy", "attribute-attachment");
    setAttribute(id, pipelet.getInputPath());
    pipelet.process(getBlackboard(), new Id[] { id });

    final String text = getAttachment(id, pipelet.getOutputName());
    assertEquals(TEXT, text);
  }

  /**
   * Test copy attachment to attribute.
   * 
   * @throws Exception
   *           test failed
   */
  public void testCopyAttachmentToAttribute() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_CP_ATTACH_TO_ATTR);
    final Id id = createBlackboardRecord("copy", "attachment-attribute");
    setAttachment(id, pipelet.getInputName());
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(TEXT, textLiteral.getStringValue());
  }

  /**
   * Test copy attachment to attachment.
   * 
   * @throws Exception
   *           test failed
   */
  public void testCopyAttachmentToAttachmeent() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_CP_ATTACH_TO_ATTACH);
    final Id id = createBlackboardRecord("copy", "attachment-attachment");
    setAttachment(id, pipelet.getInputName());
    pipelet.process(getBlackboard(), new Id[] { id });

    final String text = getAttachment(id, pipelet.getOutputName());
    assertEquals(TEXT, text);
  }

  /**
   * Test move attribute to attribute.
   * 
   * @throws Exception
   *           test failed
   */
  public void testMoveAttributeToAttribute() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_MV_ATTR_TO_ATTR);
    final Id id = createBlackboardRecord("move", "attribute-attribute");
    setAttribute(id, pipelet.getInputPath());
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(TEXT, textLiteral.getStringValue());

    final Literal deletedLiteral = getAttribute(id, pipelet.getInputPath());
    assertNull(deletedLiteral);
  }

  /**
   * Test move attribute to attachment.
   * 
   * @throws Exception
   *           test failed
   */
  public void testMoveAttributeToAttachment() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_MV_ATTR_TO_ATTACH);
    final Id id = createBlackboardRecord("move", "attribute-attachment");
    setAttribute(id, pipelet.getInputPath());
    pipelet.process(getBlackboard(), new Id[] { id });

    final String text = getAttachment(id, pipelet.getOutputName());
    assertEquals(TEXT, text);

    final Literal deletedLiteral = getAttribute(id, pipelet.getInputPath());
    assertNull(deletedLiteral);
  }

  /**
   * Test move attachment to attribute.
   * 
   * @throws Exception
   *           test failed
   */
  public void testMoveAttachmentToAttribute() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_MV_ATTACH_TO_ATTR);
    final Id id = createBlackboardRecord("move", "attachment-attribute");
    setAttachment(id, pipelet.getInputName());
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(TEXT, textLiteral.getStringValue());

    try {
      getAttachment(id, pipelet.getInputName());
      fail("expected BlackboardAccessException");
    } catch (BlackboardAccessException e) {
      assertEquals("Record with idHash = " + id.getIdHash() + " doesn't have the attachment ["
        + pipelet.getInputName() + "]", e.getMessage());
    }
  }

  /**
   * Test move attachment to attachment.
   * 
   * @throws Exception
   *           test failed
   */
  public void testMoveAttachmentToAttachment() throws Exception {
    final CopyPipelet pipelet = createPipelet(CONFIG_MV_ATTACH_TO_ATTACH);
    final Id id = createBlackboardRecord("move", "attachment-attachment");
    setAttachment(id, pipelet.getInputName());
    pipelet.process(getBlackboard(), new Id[] { id });

    final String text = getAttachment(id, pipelet.getOutputName());
    assertEquals(TEXT, text);

    try {
      getAttachment(id, pipelet.getInputName());
      fail("expected BlackboardAccessException");
    } catch (BlackboardAccessException e) {
      assertEquals("Record with idHash = " + id.getIdHash() + " doesn't have the attachment ["
        + pipelet.getInputName() + "]", e.getMessage());
    }
  }

  /**
   * Set the attribute value.
   * 
   * @param id
   *          the record id
   * @param path
   *          the attribute path
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private void setAttribute(final Id id, final Path path) throws BlackboardAccessException {
    final Literal literal = getBlackboard().createLiteral(id);
    literal.setStringValue(TEXT);
    getBlackboard().setLiteral(id, path, literal);
  }

  /**
   * Set the attachment value.
   * 
   * @param id
   *          the record id
   * @param name
   *          name of the attachment
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws UnsupportedEncodingException
   *           if any error occurs
   */
  private void setAttachment(final Id id, final String name) throws BlackboardAccessException,
    UnsupportedEncodingException {
    getBlackboard().setAttachment(id, name, TEXT.getBytes("utf-8"));
  }

  /**
   * Gets the attribute value.
   * 
   * @param id
   *          the record id
   * @param path
   *          the attribute path
   * @return a Literal containing the attribute value
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private Literal getAttribute(final Id id, final Path path) throws BlackboardAccessException {
    return getBlackboard().getLiteral(id, path);
  }

  /**
   * Gets the attachment value.
   * 
   * @param id
   *          the record id
   * @param name
   *          the name of the attachment
   * @return a String
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws UnsupportedEncodingException
   *           if any error occurs
   */
  private String getAttachment(final Id id, final String name) throws BlackboardAccessException,
    UnsupportedEncodingException {
    final byte[] bytes = getBlackboard().getAttachment(id, name);
    if (bytes != null) {
      return new String(bytes, "utf-8");
    }
    return null;
  }
}
