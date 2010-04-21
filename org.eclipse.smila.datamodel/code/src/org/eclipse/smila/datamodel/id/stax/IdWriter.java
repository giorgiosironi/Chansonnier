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

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.datamodel.id.dom.IdBuilder;

/**
 * StAX based Id writer. Should give better performance than the DOM based IdBuilder.
 *
 * @author jschumacher
 *
 */
public class IdWriter {

  /**
   * if true "print pretty", i.e. add newlines after each tag.
   */
  private boolean _printPretty;

  /**
   * create default instance.
   */
  public IdWriter() {
  }

  /**
   * @param printPretty
   *          set to true to add newlines after each element tag.
   */
  public IdWriter(final boolean printPretty) {
    this();
    _printPretty = printPretty;
  }

  /**
   * write an Id to my XML stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param id
   *          the Id to write
   * @throws XMLStreamException
   *           StAX error
   */
  public void writeId(final XMLStreamWriter staxWriter, final Id id) throws XMLStreamException {
    staxWriter.setDefaultNamespace(IdBuilder.NAMESPACE_ID);
    writeStartElement(staxWriter, IdBuilder.TAG_ID);
    staxWriter.writeDefaultNamespace(IdBuilder.NAMESPACE_ID);
    staxWriter.writeAttribute(IdBuilder.ATTRIBUTE_VERSION, IdBuilder.SCHEMA_VERSION_ID);
    newline(staxWriter);
    writeSource(staxWriter, id);
    writeKey(staxWriter, id.getKey());

    if (id.hasElementKeys()) {
      int elementCount = 0;
      for (final Key key : id.getElementKeys()) {
        writeStartElement(staxWriter, IdBuilder.TAG_ELEMENT);
        elementCount++;
        newline(staxWriter);
        writeKey(staxWriter, key);
      }
      // close all <Element> tags again.
      for (int i = 0; i < elementCount; i++) {
        writeEndElement(staxWriter);
      }
    }

    if (id.hasFragmentNames()) {
      for (final String fragment : id.getFragmentNames()) {
        writeTextElement(staxWriter, IdBuilder.TAG_FRAGMENT, fragment);
      }
    }
    writeEndElement(staxWriter);
  }

  /**
   * append source element to the XML stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param id
   *          Id to write.
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeSource(final XMLStreamWriter staxWriter, final Id id) throws XMLStreamException {
    writeTextElement(staxWriter, IdBuilder.TAG_SOURCE, id.getSource());
  }

  /**
   * append a list of Key elements for the given key.
   *
   * @param staxWriter
   *          target XML stream
   * @param key
   *          the Key to transform
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeKey(final XMLStreamWriter staxWriter, final Key key) throws XMLStreamException {
    if (key.isCompositeKey()) {
      for (final Iterator<String> keyNames = key.getKeyNames(); keyNames.hasNext();) {
        final String keyName = keyNames.next();
        writeKeyValue(staxWriter, keyName, key.getKey(keyName));
      }
    } else {
      writeKeyValue(staxWriter, key.getKeyName(), key.getKey());
    }

  }

  /**
   * write a single Key element.
   *
   * @param staxWriter
   *          target XML stream
   * @param keyName
   *          the name of the key, can be null
   * @param keyValue
   *          the key value.
   * @throws XMLStreamException
   *           StAX error.
   */
  private void writeKeyValue(final XMLStreamWriter staxWriter, final String keyName, final String keyValue)
    throws XMLStreamException {
    writeStartElement(staxWriter, IdBuilder.TAG_KEY);
    if (keyName != null) {
      staxWriter.writeAttribute(IdBuilder.ATTRIBUTE_NAME, keyName);
    }
    staxWriter.writeCharacters(keyValue);
    writeEndElement(staxWriter);
  }

  /**
   * write a text element with given name and value.
   *
   * @param staxWriter
   *          target XML stream
   * @param tagName
   *          tag name
   * @param value
   *          content characters
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeTextElement(final XMLStreamWriter staxWriter, final String tagName, final String value)
    throws XMLStreamException {
    writeStartElement(staxWriter, tagName);
    staxWriter.writeCharacters(value);
    writeEndElement(staxWriter);
  }

  /**
   * start an element with the tag name and the default namespace.
   *
   * @param staxWriter
   *          target XML stream
   * @param tagName
   *          tag name
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeStartElement(final XMLStreamWriter staxWriter, final String tagName) throws XMLStreamException {
    staxWriter.writeStartElement(IdBuilder.NAMESPACE_ID, tagName);
  }

  /**
   * end the current element, optionally append a newline.
   *
   * @param staxWriter
   *          target XML stream
   * @throws XMLStreamException
   *           StAX error.
   */
  private void writeEndElement(final XMLStreamWriter staxWriter) throws XMLStreamException {
    staxWriter.writeEndElement();
    newline(staxWriter);
  }

  /**
   * append a newline text if printPretty is activated.
   *
   * @param staxWriter
   *          target XML stream
   * @throws XMLStreamException
   *           StAX error
   */
  private void newline(final XMLStreamWriter staxWriter) throws XMLStreamException {
    if (_printPretty) {
      staxWriter.writeCharacters("\n");
    }
  }

}
