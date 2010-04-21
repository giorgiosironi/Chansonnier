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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.dom.IdBuilder;
import org.eclipse.smila.datamodel.id.dom.IdParser;
import org.eclipse.smila.utils.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Test DOM transformation of Ids.
 * 
 * @author jschumacher
 * 
 */
public class TestXmlDomUtilities extends TestCase {

  /**
   * used for creating Id DOM elements.
   */
  private final IdBuilder _builder = new IdBuilder(true);

  /**
   * used for creating Ids from DOM elements.
   */
  private final IdParser _parser = new IdParser();

  /**
   * build Id DOM, print it to a string, and parse it again.
   * 
   * @param id
   *          id to transform
   * @return parsed id
   * @throws Exception
   *           error in transformation
   */
  private Id buildAndParseDOM(Id id) throws Exception {
    final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    final Document document = builderFactory.newDocumentBuilder().newDocument();
    final Element rootElement = document.createElement("test");
    document.appendChild(rootElement);
    _builder.appendId(rootElement, id);

    final String xmlId = XmlHelper.toString(document);
    System.out.println("XML Id: " + xmlId);

    final DocumentBuilder domBuilder = builderFactory.newDocumentBuilder();
    final Document parsedDocument = domBuilder.parse(new InputSource(new StringReader(xmlId)));

    final String parsedXmlId = XmlHelper.toString(parsedDocument);
    System.out.println("Parsed XML Id: " + parsedXmlId);

    final Id parsedId = _parser.parseIdIn(parsedDocument.getDocumentElement());
    assertNotNull(parsedId);
    return parsedId;
  }

  /**
   * build IdList DOM, print it to a string, and parse it again.
   * 
   * @param idList
   *          id list to transform
   * @return parsed id list
   * @throws Exception
   *           error in transformation
   */
  private List<Id> buildAndParseDOM(List<Id> idList) throws Exception {
    final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    final Document document = builderFactory.newDocumentBuilder().newDocument();
    final Element rootElement = document.createElement("test");
    document.appendChild(rootElement);
    _builder.appendIdList(rootElement, idList);

    final String xmlId = XmlHelper.toString(document);
    System.out.println("XML Id List: " + xmlId);

    final DocumentBuilder builder = builderFactory.newDocumentBuilder();
    final Document parsedDocument = builder.parse(new InputSource(new StringReader(xmlId)));

    final String parsedXmlId = XmlHelper.toString(parsedDocument);
    System.out.println("Parsed XML Id List: " + parsedXmlId);

    final Element parsedRootElement = parsedDocument.getDocumentElement();
    final List<Id> parsedIds = _parser.parseIdsIn((Element) parsedRootElement.getFirstChild());
    assertNotNull(parsedIds);
    return parsedIds;
  }

  /**
   * test SourceObjectIdSimpleKey.
   * 
   * @throws Exception
   *           test fails
   */
  public void testSourceObjectIdSimpleKey() throws Exception {
    final Id id1 = IdCreator.createSourceObjectIdSimpleKey();
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
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
    final Id id2 = buildAndParseDOM(id1);
    TestIdEquality.checkIdEquality(id1, id2);

  }

  /**
   * test IdList.
   * 
   * @throws Exception
   *           test fails
   */
  public void testIdList() throws Exception {
    final List<Id> idList = new ArrayList<Id>();
    idList.add(IdCreator.createSourceObjectIdSimpleKey());
    idList.add(IdCreator.createSourceObjectIdSimpleNamedKey());
    idList.add(IdCreator.createSourceObjectIdCompositeKey());
    idList.add(IdCreator.createElement1Id());
    idList.add(IdCreator.createElement2Id());
    idList.add(IdCreator.createFragment1Id());
    idList.add(IdCreator.createFragment2Id());
    idList.add(IdCreator.createElement1Fragment1Id());
    idList.add(IdCreator.createElement1Fragment2Id());
    idList.add(IdCreator.createElement2Fragment1Id());
    idList.add(IdCreator.createElement2Fragment2Id());

    final List<Id> parsedIdList = buildAndParseDOM(idList);
    assertEquals(idList.size(), parsedIdList.size());
    for (int i = 0; i < idList.size(); i++) {
      TestIdEquality.checkIdEquality(idList.get(i), parsedIdList.get(i));
    }
  }
}
