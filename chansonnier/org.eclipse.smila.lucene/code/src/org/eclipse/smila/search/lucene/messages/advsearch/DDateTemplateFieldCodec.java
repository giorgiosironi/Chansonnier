/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.lucene.tools.search.lucene.DDateFieldParameter;
import org.eclipse.smila.search.lucene.tools.search.lucene.DDateFieldParameterCodec;
import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.search.utils.search.DSearchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DDateTemplateFieldCodec {

  /**
   * Constructor.
   */
  private DDateTemplateFieldCodec() {

  }

  /**
   * @param dTemplateField -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DDateTemplateField dTemplateField, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Field"));
    me.setAttribute("xsi:type", "DateTemplateField");

    DTemplateFieldCodec.encodeStandardValues(dTemplateField, me);

    if (dTemplateField.getParameter() != null) {
      try {
        DDateFieldParameterCodec.encode(dTemplateField.getParameter(), me);
      } catch (final DSearchException ex) {
        throw new AdvSearchException("unable to encode parameter of template field [" + dTemplateField.getFieldNo()
          + "]");
      }
    }

    return me;
  }

  public static DDateTemplateField decode(Element element) throws AdvSearchException {

    final DDateTemplateField dTemplateField = new DDateTemplateField();

    DTemplateFieldCodec.decodeStandardValues(dTemplateField, element);

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Parameter".equals(nl.item(i).getLocalName())) {
        try {
          dTemplateField.setParameter((DDateFieldParameter) DDateFieldParameterCodec.decode((Element) nl.item(i)));
        } catch (final DSearchException ex) {
          throw new AdvSearchException("unable to decode parameter of template field ["
            + dTemplateField.getFieldNo() + "]");
        }
      }
    }

    return dTemplateField;

  }
}
