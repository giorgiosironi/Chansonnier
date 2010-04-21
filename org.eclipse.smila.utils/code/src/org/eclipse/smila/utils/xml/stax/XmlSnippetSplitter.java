/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.utils.xml.stax;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

/**
 * StAX based XML splitter, that parses a XML stream and splits it into multiple XML snippets described by begin and end
 * tags.
 */
public class XmlSnippetSplitter {

  /**
   * The XMLInputFactory.
   */
  private final XMLInputFactory _inputFactory = XMLInputFactory.newInstance();

  /**
   * The XMLOutputFactory.
   */
  private final XMLOutputFactory _outputFactory = XMLOutputFactory.newInstance();

  /**
   * Callback to the XmlSnippetHandler.
   */
  private XmlSnippetHandler _snippetHandler;

  /**
   * The begin of a snippet.
   */
  private MarkerTag _begin;

  /**
   * The end of a snippet.
   */
  private MarkerTag _end;

  /**
   * Conversion Constructor.
   * 
   * @param snippetHandlet
   *          the XmlSnippetHandler to handle splitted xml snippets
   * @param begin
   *          the MarkerTag that describes the begin of a snippet
   * @param end
   *          the MarkerTag that describes the end of a snippet
   */
  public XmlSnippetSplitter(final XmlSnippetHandler snippetHandlet, final MarkerTag begin, final MarkerTag end) {
    // check parameters
    if (snippetHandlet == null) {
      throw new IllegalArgumentException("parameter snippetHandler is null");
    }
    if (begin == null) {
      throw new IllegalArgumentException("parameter begin is null");
    }
    if (end == null) {
      throw new IllegalArgumentException("parameter end is null");
    }

    _snippetHandler = snippetHandlet;
    _begin = begin;
    _end = end;
  }

  /**
   * Reads in the given InputStream and parses it for XML snippets describe by the begin and end marker tags. Closes the
   * inputStream when finished.
   * 
   * @param inputStream
   *          the InputStream to read from
   * @throws XMLStreamException
   *           the StAX Exception
   */
  public void read(final InputStream inputStream) throws XMLStreamException {
    if (inputStream != null) {
      try {
        final XMLEventReader eventReader = _inputFactory.createXMLEventReader(inputStream);
        parse(eventReader);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
    } // if
  }

  /**
   * Parse for xml snippets. If a snippet is found the registered XmlSnippethandler is called.
   * 
   * @param eventReader
   *          the XMLEventReader
   * @throws XMLStreamException
   *           StAX error.
   */
  private void parse(final XMLEventReader eventReader) throws XMLStreamException {
    ByteArrayOutputStream outputStream = null;
    XMLEventWriter eventWriter = null;
    try {
      while (eventReader.hasNext()) {
        final XMLEvent event = eventReader.nextEvent();
        if (isSnippetBegin(event)) {
          // begin of snippet
          outputStream = new ByteArrayOutputStream();
          eventWriter = _outputFactory.createXMLEventWriter(outputStream);
          eventWriter.add(event);
        } else if (isSnippetEnd(event)) {
          // end of snippet
          if (eventWriter != null) {
            eventWriter.add(event);
            eventWriter.close();
            _snippetHandler.handleSnippet(outputStream.toByteArray());
            // reset eventWriter and outputStream
            eventWriter = null;
            outputStream = null;
          } // if
        } else if (eventWriter != null) {
          eventWriter.add(event);
        }
      } // while
    } finally {
      if (eventWriter != null) {
        eventWriter.close();
      }
    }
  }

  /**
   * Checks if the current tag is the begin of a snippet.
   * 
   * @param event
   *          the XMLEvent
   * @return true if the tag name and the start/end tag settings of the _begin MarkerTag match the current events
   *         properties.
   */
  private boolean isSnippetBegin(final XMLEvent event) {
    boolean condition = false;
    if (_begin.isEndTag()) {
      condition = event.isEndElement();
    } else {
      condition = event.isStartElement();
    }
    return condition && _begin.getName().equals(event.asStartElement().getName().getLocalPart());
  }

  /**
   * Checks if the current tag is the end of a snippet.
   * 
   * @param event
   *          the XMLEvent
   * @return true if the tag name and the start/end tag settings of the _end MarkerTag match the current events
   *         properties.
   */
  private boolean isSnippetEnd(final XMLEvent event) {
    boolean condition = false;
    if (_end.isEndTag()) {
      condition = event.isEndElement();
    } else {
      condition = event.isStartElement();
    }
    return condition && _end.getName().equals(event.asEndElement().getName().getLocalPart());
  }
}
