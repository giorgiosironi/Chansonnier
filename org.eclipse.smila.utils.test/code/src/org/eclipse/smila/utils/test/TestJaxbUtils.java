/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import junit.framework.TestCase;

import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.eclipse.smila.utils.test.jaxb.ObjectFactory;
import org.eclipse.smila.utils.test.jaxb.TestComplexType;
import org.eclipse.smila.utils.test.jaxb.TestElement;
import org.eclipse.smila.utils.xml.SchemaUtils;
import org.xml.sax.SAXException;

/**
 * The Class TestJaxb.
 */
public class TestJaxbUtils extends TestCase {

  /**
   * The Constant JAXB_CONTEXT.
   */
  private static final String JAXB_CONTEXT = "org.eclipse.smila.utils.test.jaxb";

  /**
   * The Constant SCHEMA_PATH.
   */
  private static final String SCHEMA_PATH = "/schemas/test.xsd";

  /**
   * The Constant ATTR_VALUE.
   */
  private static final String ATTR_VALUE = "my value";

  /**
   * Test unmarshall by schema.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallBySchema() throws JAXBException, SAXException {
    // JaxbUtils.createValidatingMarshaller(context, schema)
    final Schema schema = SchemaUtils.loadSchema(AllTests.BUNDLE_ID, SCHEMA_PATH);
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxb.xml");
    final Object o = JaxbUtils.unmarshall(//
      JAXB_CONTEXT,//
      getClass().getClassLoader(),//
      schema,//
      inputStream);
    assertNotNull(o);
    assertTrue(TestElement.class.isAssignableFrom(o.getClass()));
    final TestElement e = (TestElement) o;
    assertEquals(e.getValue(), ATTR_VALUE);
  }

  /**
   * Test unmarshall ex by schema.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallExBySchema() throws JAXBException, SAXException {
    final Schema schema = null;
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxb.xml");
    Object o = null;
    try {
      o = JaxbUtils.unmarshall(//
        JAXB_CONTEXT,//
        getClass().getClassLoader(),//
        schema,//
        inputStream);
      throw new AssertionError();
    } catch (final IllegalArgumentException e) {
      ;// ok
    }
    assertNull(o);
  }

  /**
   * Test unmarshall ex by xml.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallExByXml() throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(AllTests.BUNDLE_ID, SCHEMA_PATH);
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxbEx.xml");
    Object o = null;
    try {
      o = JaxbUtils.unmarshall(//
        JAXB_CONTEXT,//
        getClass().getClassLoader(),//
        schema,//
        inputStream);
      throw new AssertionError();
    } catch (final UnmarshalException e) {
      ;// ok
    }
    assertNull(o);
  }

  /**
   * Test unmarshall ex by valid xml.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallExByValidXml() throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(AllTests.BUNDLE_ID, SCHEMA_PATH);
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxbEx2.xml");
    Object o = null;
    try {
      o = JaxbUtils.unmarshall(//
        JAXB_CONTEXT,//
        getClass().getClassLoader(),//
        schema,//
        inputStream);
      throw new AssertionError();
    } catch (final UnmarshalException e) {
      ;// ok
    }
    assertNull(o);
  }

  /**
   * Test unmarshall ex by valid xml.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallExByValidXml2() throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(AllTests.BUNDLE_ID, SCHEMA_PATH);
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxbEx3.xml");
    Object o = null;
    try {
      o = JaxbUtils.unmarshall(//
        JAXB_CONTEXT,//
        getClass().getClassLoader(),//
        schema,//
        inputStream);
      throw new AssertionError();
    } catch (final UnmarshalException e) {
      ;// ok
    }
    assertNull(o);
  }

  /**
   * Test unmarshall by schema name.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallBySchemaName() throws JAXBException, SAXException {
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxb.xml");
    final Object o = JaxbUtils.unmarshall(//
      AllTests.BUNDLE_ID, //   
      JAXB_CONTEXT,//
      getClass().getClassLoader(),//
      SCHEMA_PATH,//
      inputStream);
    assertNotNull(o);
    assertTrue(TestElement.class.isAssignableFrom(o.getClass()));
    final TestElement e = (TestElement) o;
    assertEquals(e.getValue(), ATTR_VALUE);
  }

  /**
   * Test unmarshall by schema name2.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testUnmarshallBySchemaName2() throws JAXBException, SAXException {
    final InputStream inputStream = TestJaxbUtils.class.getResourceAsStream("TestJaxb2.xml");
    final Object o = JaxbUtils.unmarshall(//
      AllTests.BUNDLE_ID, //   
      JAXB_CONTEXT,//
      getClass().getClassLoader(),//
      SCHEMA_PATH,//
      inputStream);
    assertNotNull(o);
    assertTrue(TestComplexType.class.isAssignableFrom(o.getClass()));
    final TestComplexType e = (TestComplexType) o;
    assertEquals(e.getValue(), ATTR_VALUE);
  }

  /**
   * Test marshall.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testMarshallSchemaPath() throws JAXBException, SAXException {
    final ObjectFactory factory = new ObjectFactory();
    final TestElement element = factory.createTestElement();
    element.setValue("TEST");
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    JaxbUtils.marshall(//
      element,// 
      AllTests.BUNDLE_ID,//
      JAXB_CONTEXT,//
      getClass().getClassLoader(),//
      SCHEMA_PATH,//
      bos);
  }

  /**
   * Test marshall schema.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testMarshallSchema() throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(AllTests.BUNDLE_ID, SCHEMA_PATH);
    final ObjectFactory factory = new ObjectFactory();
    final TestComplexType type = factory.createTestComplexType();
    type.setValue("TEST");
    final JAXBElement<TestComplexType> element = factory.createTestElement2(type);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    JaxbUtils.marshall(//
      element,// 
      JAXB_CONTEXT,//
      getClass().getClassLoader(),//
      schema,//
      bos);
  }

  /**
   * Test marshall ex by schema.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testMarshallExBySchema() throws JAXBException, SAXException {
    final ObjectFactory factory = new ObjectFactory();
    final TestElement element = factory.createTestElement();
    element.setValue("TEST");
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final Schema schema = null;
    try {
      JaxbUtils.marshall(//
        element,// 
        JAXB_CONTEXT,//
        getClass().getClassLoader(),//
        schema,//
        bos);
      throw new AssertionError();
    } catch (final IllegalArgumentException e) {
      ;// ok
    }
  }

  /**
   * Test create unmarshaller.
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testCreateUnmarshaller() throws JAXBException, SAXException {
    final JAXBContext context = JAXBContext.newInstance(JAXB_CONTEXT, getClass().getClassLoader());
    final Unmarshaller unmarshaller =
      JaxbUtils.createValidatingUnmarshaller(context, AllTests.BUNDLE_ID, SCHEMA_PATH);
    assertNotNull(unmarshaller);
  }

}
