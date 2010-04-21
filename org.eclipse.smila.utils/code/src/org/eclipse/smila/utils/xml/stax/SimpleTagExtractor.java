/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.utils.xml.stax;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

/**
 * 
 */
public class SimpleTagExtractor implements XmlSnippetHandler {

  private boolean _removeTags;

  private ArrayList<String> _tags;

  public SimpleTagExtractor(final boolean removeTags) {
    _removeTags = removeTags;
  }

  public synchronized List<String> getTags(final String tagName, final InputStream inputStream)
    throws XMLStreamException {
    _tags = new ArrayList<String>();
    final MarkerTag begin = new MarkerTag(tagName, false);
    final MarkerTag end = new MarkerTag(tagName, true);
    final XmlSnippetSplitter splitter = new XmlSnippetSplitter(this, begin, end);
    splitter.read(inputStream);

    return _tags;
  }

  /**
   * 
   * @param snippet
   *          a byte[] containing the the xml snippet
   */
  public void handleSnippet(final byte[] snippet) {
    String tag = new String(snippet);
    if (_removeTags) {
      final int beginIndex = tag.indexOf(">") + 1;
      final int endIndex = tag.indexOf("</");
      tag = tag.substring(beginIndex, endIndex);
    }
    _tags.add(tag);
  }

}
