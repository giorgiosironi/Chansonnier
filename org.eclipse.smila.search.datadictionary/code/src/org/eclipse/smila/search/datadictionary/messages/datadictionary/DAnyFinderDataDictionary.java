/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.datadictionary;

/**
 * Title: Any Finder Description: Copyright: Copyright (c) 2000 Company: BROX IT-Solutions GmbH
 * 
 * @author brox IT-Solutions GmbH
 * @version 1.0
 */

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

public class DAnyFinderDataDictionary {

  private final Hashtable<String, DIndex> _indices = new Hashtable<String, DIndex>();

  /**
   * Constructor
   */
  public DAnyFinderDataDictionary() {
  }

  public void addIndex(DIndex dIndex) {
    _indices.put(dIndex.getName(), dIndex);
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }// End Method equals

  public DIndex getIndex(String name) {
    return _indices.get(name);
  }

  public Enumeration<DIndex> getIndices() {
    return _indices.elements();
  }

  public int getIndicesCount() {
    return _indices.size();
  }

  public void removeIndex(DIndex dIndex) {
    _indices.remove(dIndex.getName());
  }

  @Override
  public String toString() {
    try {
      final Element el = DAnyFinderDataDictionaryCodec.encode(this).getDocumentElement();
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

} // End class def.

