/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.templates.transformer;

class PhraseInformation {

  private String _phrase;

  private String _token;

  private boolean _filter;

  PhraseInformation(String token) {
    if (token == null) {
      throw new NullPointerException("token");
    }

    token = token.trim();
    if (token.startsWith("-")) {
      _filter = true;
      _phrase = token.substring(1).trim();
      _token = "-" + _phrase;
    } else {
      _phrase = token.trim();
      _token = _phrase;
    }
  }

  public boolean isFilter() {
    return _filter;
  }

  public void setFilter(boolean filter) {
    this._filter = filter;
  }

  public String getPhrase() {
    return _phrase;
  }

  public void setPhrase(String phrase) {
    this._phrase = phrase;
  }

  public String getToken() {
    return _token;
  }

  public void setToken(String token) {
    this._token = token;
  }

  @Override
  public int hashCode() {
    return _token.hashCode();
  }

  @Override
  public String toString() {
    return _token.toString();
  }

  @Override
  public boolean equals(Object object) {

    if (object == null) {
      return false;
    }

    if (object instanceof PhraseInformation) {
      return equals((PhraseInformation) object);
    }
    return false;
  }

  private boolean equals(PhraseInformation phraseInformation) {

    if (phraseInformation == null) {
      return false;
    }

    return getToken().equals(phraseInformation.getToken());
  }
}
