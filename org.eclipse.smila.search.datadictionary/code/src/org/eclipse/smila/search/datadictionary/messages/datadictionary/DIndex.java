/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.datadictionary;

import org.eclipse.smila.search.datadictionary.messages.ddconfig.DConfiguration;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public class DIndex {

  private String name;

  private DConnection dConnection;

  private DConfiguration dConfiguration;

  private DIndexStructure dIndexStructure;

  // options for IndexWriter and flushing
  private boolean _forceFlush = true;

  private Integer _ramBufferSize = null;

  private Integer _maxBufferedDocs = null;

  private Integer _maxBufferedDeleteTerms = null;

  /**
   * Constructor
   */
  public DIndex() {
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }// End Method equals

  public DConfiguration getConfiguration() {
    return this.dConfiguration;
  }

  public DConnection getConnection() {
    return dConnection;
  }

  public DIndexStructure getIndexStructure() {
    return this.dIndexStructure;
  }

  public String getName() {
    return name;
  }

  public void setConfiguration(DConfiguration value) {
    this.dConfiguration = value;
  }

  public void setConnection(DConnection value) {
    this.dConnection = value;
  }

  public void setIndexStructure(DIndexStructure dIndexStructure) {
    this.dIndexStructure = dIndexStructure;
  }

  public void setName(String value) {
    this.name = value;
  }

  public Integer getRamBufferSize() {
    return _ramBufferSize;
  }

  public void setRamBufferSize(final Integer ramBufferSize) {
    _ramBufferSize = ramBufferSize;
  }

  public Integer getMaxBufferedDocs() {
    return _maxBufferedDocs;
  }

  public void setMaxBufferedDocs(final Integer maxBufferedDocs) {
    _maxBufferedDocs = maxBufferedDocs;
  }

  public Integer getMaxBufferedDeleteTerms() {
    return _maxBufferedDeleteTerms;
  }

  public void setMaxBufferedDeleteTerms(final Integer maxBufferedDeleteTerms) {
    _maxBufferedDeleteTerms = maxBufferedDeleteTerms;
  }

  public boolean isForceFlush() {
    return _forceFlush;
  }

  public void setForceFlush(final boolean forceFlush) {
    _forceFlush = forceFlush;
  }

  @Override
  public String toString() {
    try {
      final Element el = DIndexCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

} // End class def.
