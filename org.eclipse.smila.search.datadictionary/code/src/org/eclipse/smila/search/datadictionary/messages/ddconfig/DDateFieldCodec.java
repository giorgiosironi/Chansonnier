/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.smila.search.utils.search.DSearchException;
import org.eclipse.smila.search.utils.search.IDFParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DDateFieldCodec {

  public static DFieldConfig decode(Element element) throws ConfigurationException {

    final DDateField dDateField = new DDateField();

    DFieldConfigCodec.decodeStandardValues(dDateField, element);

    try {
      final NodeList nl = element.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        final String ln = nl.item(i).getLocalName();
        if (ln != null && ln.equals("Parameter")) {
          dDateField.setParameter((IDFParameter) org.eclipse.smila.search.utils.search.DFieldCodec
            .getParameter((Element) nl.item(i)));
        }
      }
    } catch (final DSearchException e) {
      throw new ConfigurationException(e.getMessage());
    }

    return dDateField;
  } // End Method def.

  public static Element encode(DDateField dDateField, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DConfigurationCodec.NS, "FieldConfig");
    element.appendChild(el);

    el.setAttribute("xsi:type", "FTDate");

    DFieldConfigCodec.encodeStandardValues(dDateField, el);

    if (dDateField.getParameter() != null) {
      final Class encoder = dDateField.getParameter().getCodecClass();
      Method m = null;

      if (encoder != null) {
        try {
          m = encoder.getMethod("encode", new Class[] { IDFParameter.class, Element.class });
        } catch (final NoSuchMethodException e) {
          throw new ConfigurationException("unable to locate encoder method", e);
        }

        try {
          m.invoke(null, new Object[] { dDateField.getParameter(), el });
        } catch (final InvocationTargetException e) {
          throw new ConfigurationException("unable to invoke encoder method", e);
        } catch (final IllegalAccessException e) {
          throw new ConfigurationException("unable to invoke encoder method", e);
        }
      } else {
        throw new ConfigurationException("encoder for parameter must not be equal to null");
      }
    }

    return el;
  }
}
