/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformerCodec;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class DFieldCodec {

  public static final String NS = "http://www.anyfinder.de/Search";
  
  public static DField decode(final Element element) throws DSearchException {
    String type = element.getAttribute("xsi:type");

    if (type.indexOf(":") != -1) {
      final String[] tokens = type.split(":");
      type = tokens[tokens.length - 1];
    }

    if (type.equals("FTText")) {
      return DTextFieldCodec.decode(element);
    } else if (type.equals("FTDate")) {
      return DDateFieldCodec.decode(element);
    } else { // if (type.equals("FTNumber")) {
      return DNumberFieldCodec.decode(element);
    }
  } // End Method def.

  public static void decodeStandardValues(final DField dField, final Element element) throws DSearchException {
    dField.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));

    String type = element.getAttribute("xsi:type");
    if (type.indexOf(":") != -1) {
      final String[] tokens = type.split(":");
      type = tokens[tokens.length - 1];
    }

    dField.setType(type);

    if (element.hasAttribute("Weight")) {
      try {
        dField.setWeight(new Integer(element.getAttribute("Weight")));
      } catch (final NumberFormatException nfe) {
        throw new DSearchException("Unable to parse Weight value: '" + element.getAttribute("Weight")
          + "' is out of range or not a valid number");
      }
    }
    if (element.hasAttribute("ParameterDescriptor")) {
      dField.setParameterDescriptor(element.getAttribute("ParameterDescriptor"));
    }
    if (element.hasAttribute("FieldTemplate")) {
      dField.setFieldTemplate(element.getAttribute("FieldTemplate"));
    }
    if (element.hasAttribute("Constraint")) {
      dField.setConstraint(element.getAttribute("Constraint"));
    }

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("NodeTransformer".equals(nl.item(i).getLocalName())) {
        dField.setNodeTransformer(DNodeTransformerCodec.decode((Element) nl.item(i)));
      }
    }

  }

  public static Element encode(final DField dField, final Element element) throws DSearchException {
    if (dField instanceof DTextField) {
      return DTextFieldCodec.encode((DTextField) dField, element);
    } else if (dField instanceof DNumberField) {
      return DNumberFieldCodec.encode((DNumberField) dField, element);
    } else { // if (dField instanceof DDateField) {
      return DDateFieldCodec.encode((DDateField) dField, element);
    }
  }

  public static void encodeStandardValues(final DField dField, final Element element) throws DSearchException {
    element.setAttribute("FieldNo", dField.getFieldNo() + "");

    if (dField.getWeight() != null) {
      element.setAttribute("Weight", dField.getWeight() + "");
    }
    if (dField.getParameterDescriptor() != null) {
      element.setAttribute("ParameterDescriptor", dField.getParameterDescriptor());
    }
    if (dField.getFieldTemplate() != null) {
      element.setAttribute("FieldTemplate", dField.getFieldTemplate());
    }
    if (dField.getConstraint() != null) {
      element.setAttribute("Constraint", dField.getConstraint());
    }

    if (dField.getNodeTransformer() != null) {
      DNodeTransformerCodec.encode(dField.getNodeTransformer(), element);
    }
  }

  public static IParameter getParameter(final Element element) throws DSearchException {
    if (!element.getNamespaceURI().equals(NS) && element.getLocalName().equals("Parameter")) {
      final String codecClass = element.getAttribute("CodecClass");

      if ((codecClass != null) && (!codecClass.trim().equals(""))) {
        Class decoder = null;
        try {
          final SearchAccess searchAccess = SearchAccess.getInstance();
          decoder = searchAccess.getCodecClass(codecClass);
        } catch (final ClassNotFoundException e) {
          throw new DSearchException("unable to locate decoder class", e);
        }

        Method m = null;
        try {
          m = decoder.getMethod("decode", new Class[] { org.w3c.dom.Element.class });
        } catch (final NoSuchMethodException e) {
          throw new DSearchException("unable to locate decoder method", e);
        }

        try {
          return (IParameter) m.invoke(null, new Object[] { element });
        } catch (final InvocationTargetException e) {
          if (e.getCause() != null) {
            throw new DSearchException("unable to invoke decoder method", e.getCause());
          } else {
            throw new DSearchException("unable to invoke decoder method", e);
          }
        } catch (final IllegalAccessException e) {
          throw new DSearchException("unable to invoke decoder method", e);
        }
      }
    }

    return null;
  }

} // End class def.

