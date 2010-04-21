/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.datadictionary;

import org.eclipse.smila.search.datadictionary.DataDictionaryAccess;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.ConfigurationException;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DConfigurationCodec;
import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Title: Any Finder Description: Copyright: Copyright (c) 2000 Company: BROX IT-Solutions GmbH
 * 
 * @author brox IT-Solutions GmbH
 * @version 1.0
 */
public abstract class DIndexCodec {

  public static DIndex decode(final Element element) throws DDException {

    final DIndex dIndex = new DIndex();

    // get name information
    if (!element.getAttribute("Name").toString().equals("")) {
      dIndex.setName(element.getAttribute("Name"));
    }

    // get flush information
    final String forceFlush = element.getAttribute("ForceFlush").toString();
    if (!forceFlush.equals("")) {
      dIndex.setForceFlush(Boolean.valueOf(forceFlush));
    }
    if (!element.getAttribute("RAMBufferSize").toString().equals("")) {
      dIndex.setRamBufferSize(Integer.valueOf(element.getAttribute("RAMBufferSize")));
    }
    if (!element.getAttribute("MaxBufferedDocs").toString().equals("")) {
      dIndex.setMaxBufferedDocs(Integer.valueOf(element.getAttribute("MaxBufferedDocs")));
    }
    if (!element.getAttribute("MaxBufferedDeleteTerms").toString().equals("")) {
      dIndex.setMaxBufferedDeleteTerms(Integer.valueOf(element.getAttribute("MaxBufferedDeleteTerms")));
    }

    final DataDictionaryAccess dataDictionaryAccess = DataDictionaryAccess.getInstance();
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Connection".equals(nl.item(i).getLocalName())) {
        dIndex.setConnection(dataDictionaryAccess.decodeConnection((Element) nl.item(i)));
      } else if ("IndexStructure".equals(nl.item(i).getLocalName())) {
        try {
          final IndexStructureAccess indexStructureAccess = IndexStructureAccess.getInstance();
          dIndex.setIndexStructure(indexStructureAccess.decode((Element) nl.item(i)));
        } catch (final ISException e) {
          throw new DDException(e.getMessage());
        }
      } else if ("Configuration".equals(nl.item(i).getLocalName())) {
        try {
          dIndex.setConfiguration(DConfigurationCodec.decode((Element) nl.item(i)));
        } catch (final ConfigurationException e) {
          throw new DDException("unable to resolve configuration [" + e.getMessage() + "]");
        }
      }
    }
    return dIndex;
  } // End Method decode

  public static Element encode(final DIndex dIndex, final Element element) throws DDException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DAnyFinderDataDictionaryCodec.NS, "Index");

    Attr attr = null;

    if (dIndex.getName() != null) {
      attr = doc.createAttribute("Name");
      attr.setValue(dIndex.getName());
      el.setAttributeNode(attr);
    } else {
      // todo: Error handling
    }

    attr = doc.createAttribute("ForceFlush");
    attr.setValue(Boolean.toString(dIndex.isForceFlush()));
    el.setAttributeNode(attr);

    if (dIndex.getRamBufferSize() != null) {
      attr = doc.createAttribute("RAMBufferSize");
      attr.setValue(dIndex.getRamBufferSize().toString());
      el.setAttributeNode(attr);
    }
    if (dIndex.getMaxBufferedDocs() != null) {
      attr = doc.createAttribute("MaxBufferedDocs");
      attr.setValue(dIndex.getMaxBufferedDocs().toString());
      el.setAttributeNode(attr);
    }
    if (dIndex.getMaxBufferedDeleteTerms() != null) {
      attr = doc.createAttribute("MaxBufferedDeleteTerms");
      attr.setValue(dIndex.getMaxBufferedDeleteTerms().toString());
      el.setAttributeNode(attr);
    }

    final DataDictionaryAccess dataDictionaryAccess = DataDictionaryAccess.getInstance();
    dataDictionaryAccess.encodeConnection(dIndex.getConnection(), el);

    // persist IndexStructure
    try {
      final IndexStructureAccess indexStructureAccess = IndexStructureAccess.getInstance();
      indexStructureAccess.encode(dIndex.getIndexStructure(), el);
    } catch (final ISException e) {
      throw new DDException(e.getMessage());
    }

    try {
      DConfigurationCodec.encode(dIndex.getConfiguration(), el);
    } catch (final ConfigurationException e) {
      throw new DDException("unable to encode configuration [" + e.getMessage() + "]");
    }

    element.appendChild(el);
    return el;
  }

} // End Class Def.
