/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets.test;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.pipelets.ATransformationPipelet;
import org.eclipse.smila.processing.pipelets.HtmlToTextPipelet;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * 
 * @author jschumacher
 * 
 */
public class TestHtmlToTextPipelet extends APipeletTest {
  /**
   * bundle name for config loading.
   */
  public static final String CONFIG_BUNDLE = "org.eclipse.smila.processing.pipelets";

  /**
   * name of configuration to work on attachments.
   */
  public static final String CONFIG_ATTACHMENT = "html-to-text-by-attachment.xml";

  /**
   * name of configuration to work on attributes.
   */
  public static final String CONFIG_ATTRIBUTE = "html-to-text-by-attribute.xml";

  /**
   * name of configuration that removes header tags.
   */
  public static final String CONFIG_REMOVE = "html-to-text-remove-headers.xml";

  /**
   * name of configuration that extracts metadata.
   */
  public static final String CONFIG_METADATA = "html-to-text-metadata.xml";

  /**
   * name of directory containing test html files.
   */
  public static final String CONFIG_DATADIR = "html";

  /** The log. */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * create and configure HtmlToText pipelet.
   * 
   * @param configName
   *          name of config.
   * @return configured pipelet.
   * @throws ProcessingException
   *           error configuring pipelet
   * @throws JAXBException
   *           error loading config
   */
  public HtmlToTextPipelet createPipelet(String configName) throws ProcessingException, JAXBException {
    final HtmlToTextPipelet pipelet = new HtmlToTextPipelet();
    configurePipelet(pipelet, CONFIG_BUNDLE, configName);
    return pipelet;
  }

  /**
   * a very simple first test with attributes.
   * 
   * @throws Exception
   *           test failed
   */
  public void testHelloWorldAttribute() throws Exception {
    final HtmlToTextPipelet pipelet = createPipelet(CONFIG_ATTRIBUTE);
    final Id id = createBlackboardRecord("htmltotext", "hello-world-attribute");
    final Literal htmlLiteral = getBlackboard().createLiteral(id);
    htmlLiteral.setStringValue("<html>Hello World!</html>");
    getBlackboard().setLiteral(id, pipelet.getInputPath(), htmlLiteral);
    pipelet.process(getBlackboard(), new Id[] { id });
    final Literal textLiteral = getBlackboard().getLiteral(id, pipelet.getOutputPath());
    assertNotNull(textLiteral);
    assertEquals("Hello World!", textLiteral.getStringValue());
  }

  /**
   * a very simple first test with attributes with HTML entities.
   * 
   * @throws Exception
   *           test failed
   */
  public void testHelloWorldUmlautAttribute() throws Exception {
    final HtmlToTextPipelet pipelet = createPipelet(CONFIG_ATTRIBUTE);
    final Id id = createBlackboardRecord("htmltotext", "hello-world-attribute");
    final Literal htmlLiteral = getBlackboard().createLiteral(id);
    htmlLiteral.setStringValue("<html>H&auml;llo W&ouml;rld!</html>");
    getBlackboard().setLiteral(id, pipelet.getInputPath(), htmlLiteral);
    pipelet.process(getBlackboard(), new Id[] { id });
    final Literal textLiteral = getBlackboard().getLiteral(id, pipelet.getOutputPath());
    assertNotNull(textLiteral);
    assertEquals("H\u00e4llo W\u00f6rld!", textLiteral.getStringValue());
  }

  /**
   * a very simple first test with attachments.
   * 
   * @throws Exception
   *           test failed
   */
  public void testHelloWorldAttachment() throws Exception {
    final HtmlToTextPipelet pipelet = createPipelet(CONFIG_ATTACHMENT);
    final Id id = createBlackboardRecord("htmltotext", "hello-world-attachment");
    final byte[] html = "<html>Hello World!</html>".getBytes(ATransformationPipelet.ENCODING_ATTACHMENT);
    getBlackboard().setAttachment(id, pipelet.getInputName(), html);
    pipelet.process(getBlackboard(), new Id[] { id });
    final byte[] text = getBlackboard().getAttachment(id, pipelet.getOutputName());
    assertEquals("Hello World!", new String(text, ATransformationPipelet.ENCODING_ATTACHMENT));
  }

  /**
   * a very simple first test with attachments with HTML entities.
   * 
   * @throws Exception
   *           test failed
   */
  public void testHelloWorldUmlautAttachment() throws Exception {
    final HtmlToTextPipelet pipelet = createPipelet(CONFIG_ATTACHMENT);
    final Id id = createBlackboardRecord("htmltotext", "hello-world-attachment");
    final byte[] html = "<html>H&auml;llo W&ouml;rld!</html>".getBytes(ATransformationPipelet.ENCODING_ATTACHMENT);
    getBlackboard().setAttachment(id, pipelet.getInputName(), html);
    pipelet.process(getBlackboard(), new Id[] { id });
    final byte[] text = getBlackboard().getAttachment(id, pipelet.getOutputName());
    assertEquals("H\u00e4llo W\u00f6rld!", new String(text, ATransformationPipelet.ENCODING_ATTACHMENT));
  }

  /**
   * a test of configurable content removing.
   * 
   * @throws Exception
   *           test failed
   */
  public void testRemoveHeaders() throws Exception {
    final HtmlToTextPipelet pipeletKeep = createPipelet(CONFIG_ATTACHMENT);
    final HtmlToTextPipelet pipeletRemove = createPipelet(CONFIG_REMOVE);

    Id id = createBlackboardRecord("htmltotext", "keep-headers");
    getBlackboard().setAttachmentFromStream(id, pipeletKeep.getInputName(),
      ConfigUtils.getConfigStream(CONFIG_BUNDLE, CONFIG_DATADIR + "/headers.html"));
    pipeletKeep.process(getBlackboard(), new Id[] { id });
    final byte[] textBytes = getBlackboard().getAttachment(id, pipeletKeep.getOutputName());
    assertNotNull(textBytes);
    final String textString = new String(textBytes, ATransformationPipelet.ENCODING_ATTACHMENT);
    assertTrue(textString.indexOf("Hello World!") > 0);
    assertTrue(textString.indexOf("Hello Earth!") > 0);
    assertTrue(textString.indexOf("Hello Europe!") > 0);
    assertTrue(textString.indexOf("Hello!") > 0);

    id = createBlackboardRecord("htmltotext", "remove-headers");
    getBlackboard().setAttachmentFromStream(id, pipeletKeep.getInputName(),
      ConfigUtils.getConfigStream(CONFIG_BUNDLE, CONFIG_DATADIR + "/headers.html"));
    pipeletRemove.process(getBlackboard(), new Id[] { id });
    final Literal textLiteral = getBlackboard().getLiteral(id, pipeletRemove.getOutputPath());
    assertNotNull(textLiteral);
    assertEquals("Hello!", textLiteral.getStringValue().trim());
  }

  /**
   * a test of metadata extraction.
   * 
   * @throws Exception
   *           test failed
   */
  public void testExtractMetadata() throws Exception {
    final HtmlToTextPipelet pipeletKeep = createPipelet(CONFIG_METADATA);

    final Id id = createBlackboardRecord("htmltotext", "extract-metadata");
    getBlackboard().setAttachmentFromStream(id, pipeletKeep.getInputName(),
      ConfigUtils.getConfigStream(CONFIG_BUNDLE, CONFIG_DATADIR + "/meta.html"));
    pipeletKeep.process(getBlackboard(), new Id[] { id });
    final Literal textLiteral = getBlackboard().getLiteral(id, pipeletKeep.getOutputPath());
    assertNotNull(textLiteral);
    assertEquals("Hello World!", textLiteral.getStringValue().trim());
    Path path = new Path("authors");
    assertEquals(3, getBlackboard().getLiteralsSize(id, path));
    assertEquals("me", getBlackboard().getLiteral(id, path).getStringValue());
    path.incIndex();
    assertEquals("you", getBlackboard().getLiteral(id, path).getStringValue());
    path.incIndex();
    assertEquals("boo", getBlackboard().getLiteral(id, path).getStringValue());
    path = new Path("keywords");
    assertEquals("cat", getBlackboard().getLiteral(id, path).getStringValue());
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
  }

  /**
   * test files in configuration/data directory using attributes. no semantic tests, just see if some problematic
   * documents are processed without error.
   * 
   * @throws Exception
   *           test failed
   */
  public void testDataDirAttribute() throws Exception {
    final HtmlToTextPipelet pipelet = createPipelet(CONFIG_ATTRIBUTE);
    final List<String> htmlfiles = ConfigUtils.getConfigEntries(CONFIG_BUNDLE, CONFIG_DATADIR);
    for (String filename : htmlfiles) {
      if (!filename.startsWith(".")) { // exclude .svn directory.
        final Id id = createBlackboardRecord("htmltotext", filename);
        final Literal htmlLiteral = getBlackboard().createLiteral(id);
        final InputStream htmlStream = ConfigUtils.getConfigStream(CONFIG_BUNDLE, CONFIG_DATADIR + "/" + filename);
        final String htmlString = IOUtils.toString(htmlStream, ATransformationPipelet.ENCODING_ATTACHMENT);
        htmlLiteral.setStringValue(htmlString);
        getBlackboard().setLiteral(id, pipelet.getInputPath(), htmlLiteral);
        pipelet.process(getBlackboard(), new Id[] { id });
        final Literal textLiteral = getBlackboard().getLiteral(id, pipelet.getOutputPath());
        assertNotNull(textLiteral);
        final String textString = textLiteral.getStringValue();
        assertNotNull(filename + ": null result", textString);
        _log.info(filename + ": " + textString);
      }
    }
  }

  /**
   * test files in configuration/data directory using attachments. no semantic tests, just see if some problematic
   * documents are processed without error.
   * 
   * @throws Exception
   *           test failed
   */
  public void testDataDirAttachment() throws Exception {
    final HtmlToTextPipelet pipelet = createPipelet(CONFIG_ATTACHMENT);
    final List<String> htmlfiles = ConfigUtils.getConfigEntries(CONFIG_BUNDLE, CONFIG_DATADIR);
    for (String filename : htmlfiles) {
      if (!filename.startsWith(".")) { // exclude .svn directory.
        final Id id = createBlackboardRecord("htmltotext", filename);
        final InputStream htmlStream = ConfigUtils.getConfigStream(CONFIG_BUNDLE, CONFIG_DATADIR + "/" + filename);
        getBlackboard().setAttachmentFromStream(id, pipelet.getInputName(), htmlStream);
        pipelet.process(getBlackboard(), new Id[] { id });
        final byte[] textBytes = getBlackboard().getAttachment(id, pipelet.getOutputName());
        assertNotNull(filename + ": null result", textBytes);
        final String textString = new String(textBytes, ATransformationPipelet.ENCODING_ATTACHMENT);
        _log.info(filename + ": " + textString);
      }
    }
  }

}
