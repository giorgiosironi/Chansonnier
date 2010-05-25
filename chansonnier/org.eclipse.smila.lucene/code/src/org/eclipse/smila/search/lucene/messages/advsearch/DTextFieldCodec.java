/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author gschmidt
 * 
 */
abstract class DTextFieldCodec {

  /**
   * Constructor.
   */
  private DTextFieldCodec() {

  }

  /**
   * @param dTextField -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DTextField dTextField, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Field"));
    me.setAttribute("xsi:type", "TextField");

    // set children and atts
    me.setAttribute("FieldNo", dTextField.getFieldNo() + "");
    me.setAttribute("Fuzzy", dTextField.getFuzzyAsString());
    me.setAttribute("ParseWildcards", dTextField.getParseWildcardsAsString());
    me.setAttribute("Slop", dTextField.getSlop() + "");

    Element child = null;
    child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Text"));
    child.appendChild(doc.createTextNode(dTextField.getText()));

    return me;
  }

  /**
   * @param eTextField -
   * @return DTextField
   * @throws AdvSearchException -
   */
  public static DTextField decode(Element eTextField) throws AdvSearchException {

    // decode Term
    try {
      final DTextField dTextField = new DTextField();

      dTextField.setFieldNo(Integer.parseInt(eTextField.getAttribute("FieldNo")));
      dTextField.setFuzzy(eTextField.getAttribute("Fuzzy"));
      dTextField.setParseWildcards(eTextField.getAttribute("ParseWildcards"));
      dTextField.setSlop(Integer.parseInt(eTextField.getAttribute("Slop")));

      // load index fields
      final NodeList nl = eTextField.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        if ("Text".equals(nl.item(i).getLocalName())) {
          if (nl.item(i).hasChildNodes()) {
            dTextField.setText(((Text) nl.item(i).getFirstChild()).getData());
          } else {
            dTextField.setText("");
          }
        }
      }

      return dTextField;
    } catch (final Exception e) {
      throw new AdvSearchException("Error in attributes to TextField!", e);
    }

  }

}
