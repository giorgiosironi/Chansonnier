/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.searchresult;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */

import java.io.Serializable;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

public class DHit implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private int _score;

  private int _hits;

  /**
   * Constructor
   */
  public DHit() {
  }

  public DHit(int score, int hits) {
    setScore(score);
    setHits(hits);
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  } // End Method equals

  public int getHits() {
    return _hits;
  }

  public int getScore() {
    return _score;
  }

  public void setHits(int hits) {
    this._hits = hits;
  }

  public void setScore(int score) {
    this._score = score;
  }

  @Override
  public String toString() {
    try {
      final Element el = DHitCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

} // End class def.
