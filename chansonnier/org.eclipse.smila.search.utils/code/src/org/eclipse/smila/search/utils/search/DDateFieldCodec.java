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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class DDateFieldCodec {

  protected static DDateField decode(Element element) throws DSearchException {

    final DDateField dDateField = new DDateField();

    DFieldCodec.decodeStandardValues(dDateField, element);

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // get date minimum information
    GregorianCalendar c = new GregorianCalendar();
    try {
      c.setTime(sdf.parse(element.getAttribute("Minimum")));
    } catch (final ParseException e) {
      throw new DSearchException("unable to decode date value [Minimum]");
    }
    dDateField.setDateMin(c);

    // get date maximum information
    c = new GregorianCalendar();
    try {
      c.setTime(sdf.parse(element.getAttribute("Maximum")));
    } catch (final ParseException e) {
      throw new DSearchException("unable to decode date value [Maximum]");
    }
    dDateField.setDateMax(c);

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Parameter".equals(nl.item(i).getLocalName())) {
        dDateField.setParameter((IDFParameter) DFieldCodec.getParameter((Element) nl.item(i)));
      }
    }

    return dDateField;
  } // End Method def.

  protected static Element encode(DDateField dDateField, Element element) throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DFieldCodec.NS, "Field");

    el.setAttribute("xsi:type", "FTDate");

    DFieldCodec.encodeStandardValues(dDateField, el);

    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = null;
    cal = dDateField.getDateMin();
    if (cal != null) {
      el.setAttribute("Minimum", formatter.format(cal.getTime()));
    } else {
      throw new DSearchException("minimum value not defined in date field");
    }

    cal = dDateField.getDateMax();
    if (cal != null) {
      el.setAttribute("Maximum", formatter.format(cal.getTime()));
    } else {
      throw new DSearchException("maximum value not defined in date field");
    }

    if (dDateField.getParameter() != null) {
      final Class encoder = dDateField.getParameter().getCodecClass();
      Method m = null;

      if (encoder != null) {
        try {
          m = encoder.getMethod("encode", new Class[] { IDFParameter.class, Element.class });
        } catch (final NoSuchMethodException e) {
          throw new DSearchException("unable to locate encoder method", e);
        }

        try {
          m.invoke(null, new Object[] { dDateField.getParameter(), el });
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
