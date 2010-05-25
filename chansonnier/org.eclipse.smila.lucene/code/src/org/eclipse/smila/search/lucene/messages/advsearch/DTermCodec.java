/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class DTermCodec {

  /**
   * Constructor.
   */
  private DTermCodec() {

  }

  /**
   * @param dTerm -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DTerm dTerm, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Term"));

    // set children and atts
    final DTermContent term = (DTermContent) dTerm.getTerm();
    if (term instanceof DTextField) {
      DTextFieldCodec.encode(dTerm.getTextField(), me);
    } else if (term instanceof DNumField) {
      DNumFieldCodec.encode(dTerm.getNumField(), me);
    } else if (term instanceof DDateField) {
      DDateFieldCodec.encode(dTerm.getDateField(), me);
    } else if (term instanceof DOP1) {
      DOP1Codec.encode(dTerm.getOP1(), me);
    } else if (term instanceof DOPN) {
      DOPNCodec.encode(dTerm.getOpN(), me);
    } else if (term instanceof DWMEAN) {
      DWMEANCodec.encode(dTerm.getWMEAN(), me);
    } else if (term instanceof DTemplateField) {
      DTemplateFieldCodec.encode(dTerm.getTemplateField(), me);
    } else {
      throw new AdvSearchException("unsupported term type [" + term.getClass() + "]");
    }

    return me;
  }

  /**
   * @param eTerm -
   * @return DTerm
   * @throws AdvSearchException -
   */
  public static DTerm decode(Element eTerm) throws AdvSearchException {
    final DTerm dTerm = new DTerm();

    // decode Term
    final NodeList nl = eTerm.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
        continue;
      }
      if ("Field".equals(nl.item(i).getLocalName())) {
        final Element elField = (Element) nl.item(i);
        String type = elField.getAttribute("xsi:type");
        type = XMLUtils.getLocalPart(type);

        if (type.equals("TextField")) {
          dTerm.setTerm(DTextFieldCodec.decode(elField));
        } else if (type.equals("NumField")) {
          dTerm.setTerm(DNumFieldCodec.decode(elField));
        } else if (type.equals("DateField")) {
          dTerm.setTerm(DDateFieldCodec.decode(elField));
        } else if (type.endsWith("TemplateField")) {
          dTerm.setTerm(DTemplateFieldCodec.decode(elField));
        }
      } else if ("OP_1".equals(nl.item(i).getLocalName())) {
        dTerm.setTerm(DOP1Codec.decode((Element) nl.item(i)));
      } else if ("OP_N".equals(nl.item(i).getLocalName())) {
        dTerm.setTerm(DOPNCodec.decode((Element) nl.item(i)));
      } else if ("WMEAN".equals(nl.item(i).getLocalName())) {
        dTerm.setTerm(DWMEANCodec.decode((Element) nl.item(i)));
      }
    }

    if (dTerm.getTerm() == null) {
      throw new AdvSearchException("Expected child in Term missing!");
    }
    return dTerm;
  }
}
