/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)

 * This File is based on the ParseImpl.java from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team 
 **********************************************************************************************************************/
/**
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.eclipse.smila.connectivity.framework.crawler.web.parse;

/**
 * The result of parsing a page's raw content.
 * 
 * @see Parser#getParse(Content)
 */
public class ParseImpl implements Parse {

  /**
   * Text.
   */
  private String _text;

  /** The data. */
  private ParseData _data;

  /**
   * Empty constructor.
   */
  public ParseImpl() {
  }

  /**
   * Creates new object from the given parse result.
   * 
   * @param parse
   *          parsing result
   */
  public ParseImpl(Parse parse) {
    this(parse.getText(), parse.getData());
  }

  /**
   * Creates new object with empty text.
   * 
   * @param data
   *          ParseData
   */
  public ParseImpl(ParseData data) {
    _data = data;
    _text = "";
  }

  /**
   * Creates new object from given text and parse data.
   * 
   * @param text
   *          text
   * 
   * @param data
   *          parse data
   */
  public ParseImpl(String text, ParseData data) {
    _data = data;
    _text = text;
  }

  /**
   * {@inheritDoc}
   */
  public ParseData getData() {
    return _data;
  }

  /**
   * {@inheritDoc}
   */
  public String getText() {
    return _text;
  }

}
