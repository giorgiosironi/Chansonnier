/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.id.stax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.datamodel.id.dom.IdParser;

/**
 * StAX based Id reader. Should give better performance than the DOM based IdParser.
 *
 * @author jschumacher
 *
 */
public class IdReader {

  /**
   * my object factory.
   */
  private IdFactory _idFactory = IdFactory.DEFAULT_INSTANCE;

  /**
   * create default instance.
   */
  public IdReader() {
    super();
  }

  /**
   * @param idFactory
   *          Id factory to use.
   */
  public IdReader(final IdFactory idFactory) {
    this();
    _idFactory = idFactory;
  }

  /**
   * read Id from the XML stream. The stream must be currently at the Id start tag.
   *
   * @param staxReader
   *          source XML stream
   * @return Id read from stream or null, if stream is not currently at a Id start tag.
   * @throws XMLStreamException
   *           StAX error.
   */
  public Id readId(final XMLStreamReader staxReader) throws XMLStreamException {
    Id id = null;
    if (isStartTag(staxReader, IdParser.TAG_ID)) {
      List<Key> elementKeys = null;
      List<String> fragmentNames = null;
      staxReader.nextTag(); // start Source
      final String source = staxReader.getElementText();
      final Key key = readKey(staxReader);
      if (isStartTag(staxReader, IdParser.TAG_ELEMENT)) {
        elementKeys = new ArrayList<Key>();
        while (isStartTag(staxReader, IdParser.TAG_ELEMENT)) {
          final Key elementKey = readKey(staxReader);
          if (elementKey != null) {
            elementKeys.add(elementKey);
          }
        }
        // leave the <Element> stack
        while (IdParser.TAG_ELEMENT.equals(staxReader.getLocalName())) {
          staxReader.nextTag();
        }
      }
      if (isStartTag(staxReader, IdParser.TAG_FRAGMENT)) {
        fragmentNames = new ArrayList<String>();
        while (isStartTag(staxReader, IdParser.TAG_FRAGMENT)) {
          fragmentNames.add(staxReader.getElementText());
          staxReader.nextTag(); // to start tag.
        }
      }
      id = _idFactory.createId(source, key, elementKeys, fragmentNames);
    }
    return id;
  }

  /**
   * read a complete Key from the XML stream.
   *
   * @param staxReader
   *          source XML stream
   * @return parsed Key
   * @throws XMLStreamException
   *           StAX error.
   */
  private Key readKey(final XMLStreamReader staxReader) throws XMLStreamException {
    Map<String, String> keyValues = null;
    String value = null;
    String name = null;
    staxReader.nextTag(); // to start tag - we are on </Source> or <Element> currently.
    while (isStartTag(staxReader, IdParser.TAG_KEY)) {
      if (keyValues == null && value != null) {
        keyValues = new HashMap<String, String>();
        keyValues.put(name, value);
      }
      name = staxReader.getAttributeValue(null, IdParser.ATTRIBUTE_NAME);
      value = staxReader.getElementText();
      if (keyValues != null) {
        keyValues.put(name, value);
      }
      staxReader.nextTag(); // to start tag
    }
    if (keyValues == null) {
      return _idFactory.createKey(name, value);
    } else {
      return _idFactory.createKey(keyValues);
    }
  }

  /**
   *
   * @param staxReader
   *          source XML stream
   * @param tagName
   *          tag name
   * @return true if we are currently at a start tag with the specificied name
   */
  private boolean isStartTag(final XMLStreamReader staxReader, final String tagName) {
    return staxReader.isStartElement() && tagName.equals(staxReader.getLocalName());
  }

}
