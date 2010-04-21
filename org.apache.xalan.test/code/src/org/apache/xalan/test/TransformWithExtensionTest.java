/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.apache.xalan.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class TransformWithExtensionTest.
 */
public class TransformWithExtensionTest extends TestCase {

  /**
   * Filename of foo.xsl.
   */
  private static final String FOO_XSL_FILE_NAME = "fooExt.xslt";

  /**
   * The Constant FOO_XSL_FILE_NAME2.
   */
  private static final String FOO_XSL_FILE_NAME2 = "fooExt2.xslt";

  /**
   * Filename of foo.xml.
   */
  private static final String FOO_XML_FILE_NAME = "foo.xml";

  /**
   * Filename of foo.out.
   */
  private static final String FOO_OUT_FILE = "fooExt.out";

  /**
   * The Constant FOO_OUT_FILE2.
   */
  private static final String FOO_OUT_FILE2 = "fooExt2.out";

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Test transform xml document.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  public final void testTransformWithExtension() throws Exception {
    final TransformerFactory tFactory = TransformerFactory.newInstance();
    assertTrue(tFactory instanceof org.apache.xalan.processor.TransformerFactoryImpl);

    final Transformer transformer =
      tFactory.newTransformer(new StreamSource(TransformWithExtensionTest.class
        .getResourceAsStream(FOO_XSL_FILE_NAME)));
    assertTrue(transformer instanceof org.apache.xalan.transformer.TransformerImpl);

    // its not required - only for testing that ext class was loaded!
    final ClassLoader classLoader = transformer.getClass().getClassLoader();
    final Class clazz = classLoader.loadClass("org.apache.xalan.test.ExtensionSample");
    assertNotNull(clazz);

    final ByteArrayOutputStream fooOutOS = new ByteArrayOutputStream();
    final Source source = new StreamSource(TransformWithExtensionTest.class.getResourceAsStream(FOO_XML_FILE_NAME));
    final Result result = new StreamResult(fooOutOS);
    transformer.transform(source, result);

    final String resultStr = fooOutOS.toString();
    _log.info("result=" + resultStr);

    final InputStream origFooOutIS = TransformWithExtensionTest.class.getResourceAsStream(FOO_OUT_FILE);
    final StringBuffer origFooOutContent = new StringBuffer();
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(origFooOutIS));
    String line = null;
    while ((line = bufferedReader.readLine()) != null) {
      origFooOutContent.append(line);
    }
    assertEquals(origFooOutContent.toString(), resultStr);
  }

  /**
   * Test transform with extension2.
   * 
   * @throws Exception
   *           the exception
   */
  public final void testTransformWithExtension2() throws Exception {
    final TransformerFactory tFactory = TransformerFactory.newInstance();
    assertTrue(tFactory instanceof org.apache.xalan.processor.TransformerFactoryImpl);

    final Transformer transformer =
      tFactory.newTransformer(new StreamSource(TransformWithExtensionTest.class
        .getResourceAsStream(FOO_XSL_FILE_NAME2)));
    assertTrue(transformer instanceof org.apache.xalan.transformer.TransformerImpl);

    // its not required - only for testing that ext class was loaded!
    // final ClassLoader classLoader = transformer.getClass().getClassLoader();
    // final Class clazz = classLoader.loadClass("org.apache.xalan.test.ExtensionSample");
    // assertNotNull(clazz);

    final ByteArrayOutputStream fooOutOS = new ByteArrayOutputStream();
    final Source source = new StreamSource(TransformWithExtensionTest.class.getResourceAsStream(FOO_XML_FILE_NAME));
    final Result result = new StreamResult(fooOutOS);
    transformer.transform(source, result);

    final String resultStr = fooOutOS.toString();
    _log.info("result=" + resultStr);

    final InputStream origFooOutIS = TransformWithExtensionTest.class.getResourceAsStream(FOO_OUT_FILE2);
    final StringBuffer origFooOutContent = new StringBuffer();
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(origFooOutIS));
    String line = null;
    while ((line = bufferedReader.readLine()) != null) {
      origFooOutContent.append(line);
    }
    assertEquals(origFooOutContent.toString(), resultStr);
  }

}
