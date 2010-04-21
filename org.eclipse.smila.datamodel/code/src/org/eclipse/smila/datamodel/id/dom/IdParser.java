/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.id.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.utils.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class for creating SMILA record Ids from DOM elements.
 * 
 * @author jschumacher
 * 
 */
public class IdParser {
  /**
   * SMILA Id XML namespace URI, "http://www.eclipse.org/smila/id".
   */
  public static final String NAMESPACE_ID = "http://www.eclipse.org/smila/id";

  /**
   * lcoal tag name of Id elements: "Id".
   */
  public static final String TAG_ID = "Id";

  /**
   * local tag name of Id list elements: "IdList".
   */
  public static final String TAG_IDLIST = "IdList";

  /**
   * local tag name of source elements: "Source".
   */
  public static final String TAG_SOURCE = "Source";

  /**
   * local tag name of key elements: "Key".
   */
  public static final String TAG_KEY = "Key";

  /**
   * local tag name of container element elements: "Element".
   */
  public static final String TAG_ELEMENT = "Element";

  /**
   * local tag name of fragment elements: "Fragment".
   */
  public static final String TAG_FRAGMENT = "Fragment";

  /**
   * attribute name of version attribtue: "version".
   */
  public static final String ATTRIBUTE_VERSION = "version";

  /**
   * attribute name of key name attribtue: "name".
   */
  public static final String ATTRIBUTE_NAME = "name";

  /**
   * the Id factory to use for creating the Id.
   */
  private final IdFactory _factory;

  /**
   * create IdParser with default Id factory.
   */
  public IdParser() {
    _factory = IdFactory.DEFAULT_INSTANCE;
  }

  /**
   * create IdParser with a given Id factory.
   * 
   * @param customFactory
   *          the Id factory to use for creating the Id object.
   */
  public IdParser(final IdFactory customFactory) {
    this._factory = customFactory;
  }

  /**
   * Find the first child Id element and parse the Id it describes. If no Id element is found, null is returned.
   * 
   * @param parentElement
   *          the element under which to search for Ids
   * @return the first Id found under the parentElement.
   */
  public Id parseIdIn(final Element parentElement) {
    final NodeList children = parentElement.getChildNodes();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element idElement = (Element) childNode;
          final String localName = idElement.getLocalName();
          // if (localName == null) {
          // localName = idElement.getTagName();
          // }
          if (TAG_ID.equals(localName)) {
            return parseIdFrom(idElement);
          }
        }
      }
    }
    return null;
  }

  /**
   * Find all child Id elements and parse the Id it describes. If no Id element is found, null is returned.
   * 
   * @param parentElement
   *          the element under which to search for Ids
   * @return all Ids found under the parentElement.
   */
  public List<Id> parseIdsIn(final Element parentElement) {
    final NodeList children = parentElement.getChildNodes();
    final List<Id> ids = new ArrayList<Id>();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element idElement = (Element) childNode;
          if (TAG_ID.equals(idElement.getLocalName())) {
            ids.add(parseIdFrom(idElement));
          }
        }
      }
    }
    return ids;
  }

  /**
   * Expects a valid id:Id element and creates an Id object from it.
   * 
   * @param idElement
   *          an id:Id element to parse.
   * @return the Id described by the given element
   */
  public Id parseIdFrom(final Element idElement) {
    final NodeList nodes = idElement.getChildNodes();
    String source = null;
    final Map<String, String> keyValues = new HashMap<String, String>();
    final List<Key> elementKeys = new ArrayList<Key>();
    final List<String> fragmentNames = new ArrayList<String>();

    for (int i = 0; i < nodes.getLength(); i++) {
      final Node node = nodes.item(i);
      if (node instanceof Element) {
        final Element element = (Element) node;
        final String elementName = element.getLocalName();
        if (TAG_SOURCE.equals(elementName)) {
          source = element.getTextContent();
        } else if (TAG_KEY.equals(elementName)) {
          parseKeyValue(keyValues, element);
        } else if (TAG_ELEMENT.equals(elementName)) {
          parseElement(elementKeys, element);
        } else if (TAG_FRAGMENT.equals(elementName)) {
          fragmentNames.add(element.getTextContent());
        }
      }
    }
    final Key sourceKey = _factory.createKey(keyValues);
    return _factory.createId(source, sourceKey, elementKeys, fragmentNames);
  }

  /**
   * Creates an Id object by parsing an Id xml representation.
   * @param xmlString the xml string to parse
   * @return and Id object
   * @throws ParserConfigurationException if any error occurs while parsing
   * @throws IOException if any error occurs while parsing
   * @throws SAXException if any error occurs while parsing
   */
  public Id parseIdFrom(final String xmlString) throws ParserConfigurationException, IOException, SAXException {
    final Document document = XmlHelper.parse(xmlString);
    return parseIdFrom(document.getDocumentElement());
  }

  /**
   * expects a valid id:Element, creates the described container element keys and adds them to the given key list.
   * 
   * @param elementKeys
   *          list to add the container element keys to.
   * @param elementElement
   *          the id:Element element.
   */
  private void parseElement(final List<Key> elementKeys, final Element elementElement) {
    final NodeList nodes = elementElement.getChildNodes();
    final Map<String, String> elementKeyValues = new HashMap<String, String>();
    for (int i = 0; i < nodes.getLength(); i++) {
      final Node node = nodes.item(i);
      if (node instanceof Element) {
        final Element subElement = (Element) node;
        final String elementName = subElement.getLocalName();
        if (TAG_KEY.equals(elementName)) {
          parseKeyValue(elementKeyValues, subElement);
        }
        if (TAG_ELEMENT.equals(elementName)) {
          final Key elementKey = _factory.createKey(elementKeyValues);
          elementKeys.add(elementKey);
          parseElement(elementKeys, subElement);
          return;
        }
      }
    }
  }

  /**
   * parse an id:Key element and add the key-value-mapping to the given map. the key name is null, if the element has no
   * "name" attribute.
   * 
   * @param keyValues
   *          the key name-value-map to add to
   * @param element
   *          the id:Key element to parse.
   */
  private void parseKeyValue(final Map<String, String> keyValues, final Element element) {
    String keyName = element.getAttribute(ATTRIBUTE_NAME);
    if (StringUtils.isBlank(keyName)) {
      keyName = null;
    }
    keyValues.put(keyName, element.getTextContent());
  }
}
