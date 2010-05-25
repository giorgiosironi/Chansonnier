/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public abstract class DIndexFieldCodec {

  /**
   * Constructor.
   */
  private DIndexFieldCodec() {

  }

  /**
   * @param dIndexField -
   * @param parent -
   * @return Element
   * @throws ISException -
   */
  protected static Element encode(DIndexField dIndexField, Element parent) throws ISException {

    if (parent == null) {
      throw new ISException("parameter must not be null [parent]");
    }

    if (dIndexField == null) {
      throw new ISException("parameter must not be null [dIndexField]");
    }
    final Document doc = parent.getOwnerDocument();
    final Element el = doc.createElementNS(DIndexStructureCodec.NS, "IndexField");

    // set custom attributes
    el.setAttribute("Name", dIndexField.getName());
    el.setAttribute("Type", dIndexField.getType());
    el.setAttribute("FieldNo", dIndexField.getFieldNo() + "");
    el.setAttribute("StoreText", XMLUtils.encodeBoolean(dIndexField.getStoreText()));
    el.setAttribute("Tokenize", XMLUtils.encodeBoolean(dIndexField.getTokenize()));
    el.setAttribute("IndexValue", XMLUtils.encodeBoolean(dIndexField.getIndexValue()));

    // create elements
    if (dIndexField.getAnalyzer() != null) {
      DAnalyzerCodec.encode(dIndexField.getAnalyzer(), el);
    }

    // insert into dom tree
    parent.appendChild(el);
    return el;
  }

  public static DIndexField decode(org.w3c.dom.Element element) throws ISException {

    if (element == null) {
      throw new ISException("parameter must not be null [element]");
    }

    final DIndexField dIndexField = new DIndexField();

    // resolve custom attributes
    dIndexField.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));
    dIndexField.setName(element.getAttribute("Name"));
    dIndexField.setType(element.getAttribute("Type"));
    dIndexField.setTokenize(XMLUtils.decodeBoolean(element.getAttribute("Tokenize")));
    dIndexField.setIndexValue(XMLUtils.decodeBoolean(element.getAttribute("IndexValue")));
    dIndexField.setStoreText(XMLUtils.decodeBoolean(element.getAttribute("StoreText")));

    // resolve custom elements
    final org.w3c.dom.NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof org.w3c.dom.Element)) {
        continue;
      }
      final org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

      if ("Analyzer".equals(el.getLocalName())) {
        dIndexField.setAnalyzer(DAnalyzerCodec.decode(el));
      }
    }

    return dIndexField;
  }
}
