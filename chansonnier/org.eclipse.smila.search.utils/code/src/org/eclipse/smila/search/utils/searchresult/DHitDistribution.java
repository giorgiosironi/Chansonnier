/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.searchresult;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

@SuppressWarnings("unchecked")
public class DHitDistribution implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final Vector _hits = new Vector();

  /**
   * Constructor
   */
  public DHitDistribution() {
  }

  public void addHit(DHit dHit) {
    _hits.add(dHit);
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  } // End Method equals

  public DHit getHit(int index) {
    return (DHit) _hits.get(index);
  }

  public Enumeration getHits() {
    return _hits.elements();
  }

  public int getHitsCount() {
    return _hits.size();
  }

  public void removeHit(int index) {
    _hits.remove(index);
  }

  @Override
  public String toString() {
    try {
      final Element el = DHitDistributionCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

} // End class def.
