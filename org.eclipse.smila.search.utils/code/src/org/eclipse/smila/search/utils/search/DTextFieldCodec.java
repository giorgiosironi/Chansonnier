/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class DTextFieldCodec {

  protected static DTextField decode(Element element) throws DSearchException {

    final DTextField dTextField = new DTextField();

    DFieldCodec.decodeStandardValues(dTextField, element);

    dTextField.setText(element.getAttribute("Text"));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Parameter".equals(nl.item(i).getLocalName())) {
        dTextField.setParameter((ITFParameter) DFieldCodec.getParameter((Element) nl.item(i)));
      }
    }

    return dTextField;
  } // End Method def.

  protected static Element encode(DTextField dTextField, Element element) throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DFieldCodec.NS, "Field");

    el.setAttribute("xsi:type", "FTText");

    DFieldCodec.encodeStandardValues(dTextField, el);

    el.setAttribute("Text", dTextField.getText());

    if (dTextField.getParameter() != null) {
      final Class encoder = dTextField.getParameter().getCodecClass();
      Method m = null;

      if (encoder != null) {
        try {
          m = encoder.getMethod("encode", new Class[] { ITFParameter.class, Element.class });
        } catch (final NoSuchMethodException e) {
          throw new DSearchException("unable to locate encoder method", e);
        }

        try {
          m.invoke(null, new Object[] { dTextField.getParameter(), el });
        } catch (final InvocationTargetException e) {
          throw new DSearchException("unable to invoke encoder method", e);
        } catch (final IllegalAccessException e) {
          throw new DSearchException("unable to invoke encoder method", e);
        }
      }
    }

    element.appendChild(el);
    return el;
  }
} // End class def.
