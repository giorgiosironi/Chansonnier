/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.searchresult;

import java.io.Serializable;

/**
 * Data structure for a highlighting value.
 * 
 * @author brox IT-Solutions GmbH
 */
public class DHighLighted implements Serializable, Cloneable {

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Text.
   */
  private String _text;

  /**
   * Score.
   */
  private int _score;

  /**
   * Default Constructor.
   */
  public DHighLighted() {
  }

  /**
   * Conversion Constructor.
   * 
   * @param text
   *          the text to highlight
   * @param score
   *          the score of the text
   */
  public DHighLighted(String text, int score) {
    this.setText(text);
    this.setScore(score);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {

    DHighLighted highLighted = null;
    try {
      highLighted = (DHighLighted) super.clone();
    } catch (final CloneNotSupportedException ex) {
      throw new RuntimeException("unable to clone highlighted");
    }

    return highLighted;
  }

  /**
   * Returns the score.
   * 
   * @return the score
   */
  public int getScore() {
    return this._score;
  }

  /**
   * Returns the text.
   * 
   * @return the text
   */
  public String getText() {
    return this._text;
  }

  /**
   * Sets the score.
   * 
   * @param score
   *          the score
   */
  public void setScore(int score) {
    this._score = score;
  }

  /**
   * Sets the text.
   * 
   * @param text
   *          the text
   */
  public void setText(String text) {
    this._text = text;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Text: " + _text + " Score: " + Integer.toString(_score);
  }
}
