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

public abstract class DNumberFieldCodec {

  protected static DNumberField decode(Element element) throws DSearchException {

    final DNumberField dNumberField = new DNumberField();

    DFieldCodec.decodeStandardValues(dNumberField, element);

    dNumberField.setMin(Long.decode(element.getAttribute("Minimum")));
    dNumberField.setMax(Long.decode(element.getAttribute("Maximum")));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Parameter".equals(nl.item(i).getLocalName())) {
        dNumberField.setParameter((INFParameter) DFieldCodec.getParameter((Element) nl.item(i)));
      }
    }

    return dNumberField;
  } // End Method def.

  protected static Element encode(DNumberField dNumberField, Element element) throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DFieldCodec.NS, "Field");

    el.setAttribute("xsi:type", "FTNumber");

    DFieldCodec.encodeStandardValues(dNumberField, el);

    if (dNumberField.getMin() != null) {
      el.setAttribute("Minimum", dNumberField.getMin().toString());
    } else {
      throw new DSearchException("minimum value not defined in number field");
    }

    if (dNumberField.getMax() != null) {
      el.setAttribute("Maximum", dNumberField.getMax().toString());
    } else {
      throw new DSearchException("maximum value not defined in number field");
    }

    if (dNumberField.getParameter() != null) {
      final Class encoder = dNumberField.getParameter().getCodecClass();
      Method m = null;

      if (encoder != null) {
        try {
          m = encoder.getMethod("encode", new Class[] { INFParameter.class, Element.class });
        } catch (final NoSuchMethodException e) {
          throw new DSearchException("unable to locate encoder method", e);
        }

        try {
          m.invoke(null, new Object[] { dNumberField.getParameter(), el });
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
