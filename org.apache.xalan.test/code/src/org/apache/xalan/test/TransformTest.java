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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

/**
 * Transform test case.
 */
public class TransformTest extends TestCase {

  /**
   * Filename of foo.xsl.
   */
  private static final String FOO_XSL_FILE_NAME = "foo.xsl";

  /**
   * Filename of foo.xml.
   */
  private static final String FOO_XML_FILE_NAME = "foo.xml";

  /**
   * Filename of foo.out.
   */
  private static final String FOO_OUT_FILE = "foo.out";

  /**
   * Test transform xml document.
   */
  public final void testTransform() {

    try {
      final TransformerFactory tFactory = TransformerFactory.newInstance();
      assertTrue(tFactory instanceof org.apache.xalan.processor.TransformerFactoryImpl);

      final Transformer transformer =
        tFactory.newTransformer(new StreamSource(TransformTest.class.getResourceAsStream(FOO_XSL_FILE_NAME)));
      assertTrue(transformer instanceof org.apache.xalan.transformer.TransformerImpl);

      final ByteArrayOutputStream fooOutOS = new ByteArrayOutputStream();
      transformer.transform(new StreamSource(TransformTest.class.getResourceAsStream(FOO_XML_FILE_NAME)),
        new StreamResult(fooOutOS));

      final InputStream origFooOutIS = TransformTest.class.getResourceAsStream(FOO_OUT_FILE);
      final StringBuffer origFooOutContent = new StringBuffer();

      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(origFooOutIS));
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        origFooOutContent.append(line);
      }

      assertEquals(origFooOutContent.toString(), fooOutOS.toString());

    } catch (final TransformerException transformerException) {
      fail(transformerException.getMessage());
    } catch (final FileNotFoundException fileNotFoundException) {
      fail(fileNotFoundException.getMessage());
    } catch (final IOException exception) {
      fail(exception.getMessage());
    }
  }
}
