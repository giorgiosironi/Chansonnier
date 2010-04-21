/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.id.dom;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.utils.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A utility class to create DOM representations of SMILA record Ids.
 * 
 * @author jschumacher
 */
public class IdBuilder {

  /** SMILA Id XML namespace URI, "http://www.eclipse.org/smila/id". */
  public static final String NAMESPACE_ID = IdParser.NAMESPACE_ID;

  /** attribute "xmlns:id" for specification if SMILA Id XML namespace URI. */
  public static final String ATTRIBUTE_XMLNSID = XMLConstants.XMLNS_ATTRIBUTE; // + ":id";

  /** namespace prefix "id:" for Id XML elements. */
  public static final String PREFIX_ID = ""; // "id:";

  /** qualified tag name of Id elements: "id:Id". */
  public static final String TAG_ID = PREFIX_ID + IdParser.TAG_ID;

  /** qualified tag name of Id list elements: "id:IdList". */
  public static final String TAG_IDLIST = PREFIX_ID + IdParser.TAG_IDLIST;

  /** qualified tag name of source elements: "id:Source". */
  public static final String TAG_SOURCE = PREFIX_ID + IdParser.TAG_SOURCE;

  /** qualified tag name of key elements: "id:Key". */
  public static final String TAG_KEY = PREFIX_ID + IdParser.TAG_KEY;

  /** qualified tag name of container element elements: "id:Element". */
  public static final String TAG_ELEMENT = PREFIX_ID + IdParser.TAG_ELEMENT;

  /** qualified tag name of fragment elements: "id:Fragment". */
  public static final String TAG_FRAGMENT = PREFIX_ID + IdParser.TAG_FRAGMENT;

  /** attribute name of key name attribtue: "verson". */
  public static final String ATTRIBUTE_VERSION = IdParser.ATTRIBUTE_VERSION;

  /** attribute name of key name attribtue: "name". */
  public static final String ATTRIBUTE_NAME = IdParser.ATTRIBUTE_NAME;

  /** version of Id XMLs created by this builder: "1.0". */
  public static final String SCHEMA_VERSION_ID = "1.0";

  /** switch to true to add newlines for better readability, but poorer performance. */
  private boolean _printPretty;

  /**
   * create new IdBuilder.
   */
  public IdBuilder() {
    // nothing to do
  }

  /**
   * create new IdBuilder with custom printPretty flag.
   * 
   * @param printPretty
   *          printPretty flag
   */
  public IdBuilder(final boolean printPretty) {
    _printPretty = printPretty;
  }

  /**
   * add an Id element for the given element as the final child. The Id element contains a xmlns:id specification for
   * the SMILA Id namespace
   * 
   * @param element
   *          DOM element to add the Id to
   * @param id
   *          The record Id to add.
   * 
   * @return the appended element
   */
  public Element appendId(final Element element, final Id id) {
    final Document factory = element.getOwnerDocument();
    final Element idElement = buildId(factory, id);
    idElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, ATTRIBUTE_XMLNSID, NAMESPACE_ID);
    newline(factory, element);
    element.appendChild(idElement);
    newline(factory, element);
    return idElement;
  }

  /**
   * Append id.
   * 
   * @param id
   *          the id
   * @param document
   *          the document
   * 
   * @return the element
   */
  public Element appendId(final Document document, final Id id) {
    final Element idElement = buildId(document, id);
    idElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, ATTRIBUTE_XMLNSID, NAMESPACE_ID);
    document.appendChild(idElement);
    return idElement;
  }

  /**
   * add an IdList element containing Id elements for the Ids in the given list to the given element as the final child.
   * The IdList element contains a xmlns:id specification for the SMILA Id namespace, the Id elements in the list depend
   * on it being in the Id list element.
   * 
   * @param element
   *          DOM element to add the Id list to
   * @param idList
   *          The record Ids to add.
   * 
   * @return the appended list element
   */
  public Element appendIdList(final Element element, final Iterable<Id> idList) {
    final Document factory = element.getOwnerDocument();
    final Element listElement = appendIdListElement(factory, element);
    for (Id id : idList) {
      final Element idElement = buildId(factory, id);
      listElement.appendChild(idElement);
      newline(factory, listElement);
    }
    return listElement;
  }

  /**
   * add an IdList element containing Id elements for the Ids in the given list to the given element as the final child.
   * The IdList element contains a xmlns:id specification for the SMILA Id namespace, the Id elements in the list depend
   * on it being in the Id list element.
   * 
   * @param element
   *          DOM element to add the Id list to
   * @param idList
   *          The record Ids to add.
   * 
   * @return the appended list element
   */
  public Element appendIdList(final Element element, final Id[] idList) {
    final Document factory = element.getOwnerDocument();
    final Element listElement = appendIdListElement(factory, element);
    for (Id id : idList) {
      final Element idElement = buildId(factory, id);
      listElement.appendChild(idElement);
      newline(factory, listElement);
    }
    return listElement;
  }

  /**
   * Creates an xml string representation of the given id.
   * 
   * @param id
   *          the Id
   * @return the xml String
   * @throws ParserConfigurationException
   *           if any error occurs
   * @throws IOException
   *           if any error occurs
   */
  public String idToString(Id id) throws ParserConfigurationException, IOException {
    if (id == null) {
      return "";
    }
    final Document document = XmlHelper.newDocument();
    final Element idElement = buildId(document, id);
    document.appendChild(idElement);
    final String xml = XmlHelper.toString(document, false);
    return xml;
  }

  /**
   * append IdList element to element.
   * 
   * @param factory
   *          DOM factory to use
   * @param element
   *          parent element
   * 
   * @return new IdList element
   */
  private Element appendIdListElement(final Document factory, final Element element) {
    final Element listElement = factory.createElementNS(NAMESPACE_ID, TAG_IDLIST);
    listElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, ATTRIBUTE_XMLNSID, NAMESPACE_ID);
    newline(factory, listElement);
    element.appendChild(listElement);
    return listElement;
  }

  /**
   * create an Id element without xmlns:id attribute for the SMILA Id namespace.
   * 
   * @param factory
   *          DOM document to use as factory.
   * @param id
   *          record Id
   * 
   * @return the Id element for the given Id
   */
  private Element buildId(final Document factory, final Id id) {
    final Element idElement = factory.createElementNS(NAMESPACE_ID, TAG_ID);
    idElement.setAttribute(ATTRIBUTE_VERSION, SCHEMA_VERSION_ID);
    newline(factory, idElement);
    appendTextElement(factory, idElement, TAG_SOURCE, id.getSource());

    final Key sourceKey = id.getKey();
    appendKey(factory, idElement, sourceKey);

    if (id.hasElementKeys()) {
      Element elementElement = factory.createElementNS(NAMESPACE_ID, TAG_ELEMENT);
      idElement.appendChild(elementElement);
      newline(factory, idElement);
      newline(factory, elementElement);
      for (Key key : id.getElementKeys()) {
        appendKey(factory, elementElement, key);
        final Element subElementElement = factory.createElementNS(NAMESPACE_ID, TAG_ELEMENT);
        newline(factory, subElementElement);
        elementElement.appendChild(subElementElement);
        newline(factory, elementElement);
        elementElement = subElementElement;
      }
    }

    if (id.hasFragmentNames()) {
      for (String fragment : id.getFragmentNames()) {
        appendTextElement(factory, idElement, TAG_FRAGMENT, fragment);
      }
    }
    return idElement;
  }

  /**
   * append a list of Key elements for the given key to the given element.
   * 
   * @param factory
   *          the DOM element factory to use
   * @param element
   *          the element to append the Key elements to
   * @param key
   *          the Key to transform
   */
  private void appendKey(final Document factory, final Element element, final Key key) {
    if (key.isCompositeKey()) {
      for (final Iterator<String> keyNames = key.getKeyNames(); keyNames.hasNext();) {
        final String keyName = keyNames.next();
        appendKeyValue(factory, element, keyName, key.getKey(keyName));
      }
    } else {
      appendKeyValue(factory, element, key.getKeyName(), key.getKey());
    }

  }

  /**
   * append a single Key element to the given element.
   * 
   * @param factory
   *          the DOM element factory to use
   * @param element
   *          the element to append the Key element to
   * @param keyName
   *          the name of the key, can be null
   * @param keyValue
   *          the key value.
   */
  private void appendKeyValue(final Document factory, final Element element, final String keyName,
    final String keyValue) {
    final Element keyElement = appendTextElement(factory, element, TAG_KEY, keyValue);
    if (keyName != null) {
      keyElement.setAttribute(ATTRIBUTE_NAME, keyName);
    }
  }

  /**
   * append an element containing a text node.
   * 
   * @param factory
   *          the DOM element factory to use
   * @param element
   *          the element to append to
   * @param name
   *          the qualified name of the new element
   * @param text
   *          the text content to add
   * 
   * @return the new element
   */
  private Element appendTextElement(final Document factory, final Element element, final String name,
    final String text) {
    final Element textElement = factory.createElementNS(NAMESPACE_ID, name);
    textElement.appendChild(factory.createTextNode(text));
    element.appendChild(textElement);
    newline(factory, element);
    return textElement;
  }

  /**
   * append a newline text element if printPretty is activated.
   * 
   * @param factory
   *          factory to use.
   * @param element
   *          element to append to.
   */
  private void newline(final Document factory, final Element element) {
    if (_printPretty) {
      element.appendChild(factory.createTextNode("\n"));
    }
  }

}
