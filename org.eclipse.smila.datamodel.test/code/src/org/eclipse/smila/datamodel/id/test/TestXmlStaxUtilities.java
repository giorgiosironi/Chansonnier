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

package org.eclipse.smila.datamodel.id.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.stax.IdReader;
import org.eclipse.smila.datamodel.id.stax.IdWriter;

/**
 * Test StAX transformation of Ids.
 * 
 * @author jschumacher
 * 
 */
public class TestXmlStaxUtilities extends TestCase {

  /**
   * StAX writer factory.
   */
  private XMLOutputFactory _outputFactory = XMLOutputFactory.newInstance();

  /**
   * StAX reader factory.
   */
  private XMLInputFactory _inputFactory = XMLInputFactory.newInstance();

  /**
   * Id Writer.
   */
  private IdWriter _idWriter = new IdWriter(true);

  /**
   * Id Reader.
   */
  private IdReader _idReader = new IdReader();

  /**
   * write using StAX and read it again.
   *
   * @param id
   *          id to transform
   * @return parsed id
   * @throws Exception
   *           error in transformation
   */
  private Id writeAndRead(final Id id) throws Exception {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final XMLStreamWriter writer = _outputFactory.createXMLStreamWriter(out, "utf-8");
    writer.writeStartDocument();
    _idWriter.writeId(writer, id);
    writer.writeEndDocument();
    writer.close();
    out.close();

    final String xmlId = out.toString("utf-8");
    System.out.println("XML Id: " + xmlId);

    final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    final XMLStreamReader reader = _inputFactory.createXMLStreamReader(in);
    reader.nextTag(); // to Id tag.
    final Id parsedId = _idReader.readId(reader);
    assertNotNull(parsedId);
    return parsedId;
  }

  /**
   * test SourceObjectIdSimpleKey.
   *
   * @throws Exception
   *           test fails
   */
  public void testSourceObjectIdSimpleKey() throws Exception {
    final Id id1 = IdCreator.createSourceObjectIdSimpleKey();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);
  }

  /**
   * test SourceObjectIdSimpleNamedKey.
   *
   * @throws Exception
   *           test fails
   */
  public void testSourceObjectIdSimpleNamedKey() throws Exception {
    final Id id1 = IdCreator.createSourceObjectIdSimpleNamedKey();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);
  }

  /**
   * test SourceObjectIdCompositeKey.
   *
   * @throws Exception
   *           test fails
   */
  public void testSourceObjectIdCompositeKey() throws Exception {
    final Id id1 = IdCreator.createSourceObjectIdCompositeKey();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Element1Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testElement1Id() throws Exception {
    final Id id1 = IdCreator.createElement1Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Element2Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testElement2Id() throws Exception {
    final Id id1 = IdCreator.createElement2Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Fragment1Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testFragment1Id() throws Exception {
    final Id id1 = IdCreator.createFragment1Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Fragment2Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testFragment2Id() throws Exception {
    final Id id1 = IdCreator.createFragment2Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Element1Fragment1Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testElement1Fragment1Id() throws Exception {
    final Id id1 = IdCreator.createElement1Fragment1Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Element1Fragment2Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testElement1Fragment2Id() throws Exception {
    final Id id1 = IdCreator.createElement1Fragment2Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Element2Fragment1Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testElement2Fragment1Id() throws Exception {
    final Id id1 = IdCreator.createElement2Fragment1Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test Element2Fragment2Id.
   *
   * @throws Exception
   *           test fails
   */
  public void testElement2Fragment2Id() throws Exception {
    final Id id1 = IdCreator.createElement2Fragment2Id();
    final Id id2 = writeAndRead(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }
}
