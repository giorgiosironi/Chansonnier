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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.utils.search.DSearchException;
import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformerCodec;
import org.eclipse.smila.search.utils.search.parameterobjects.DTransformerCodec;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 */
public final class DFieldConfigCodec {

  /**
   * Namespace.
   */
  public static final String NS = "http://www.anyfinder.de/DataDictionary/Configuration";

  /**
   * Decode field config.
   * 
   * @param element
   *          Element.
   * @return Decoded field config.
   * @throws ConfigurationException
   *           Unable to decode field config.
   */
  protected static DFieldConfig decode(Element element) throws ConfigurationException {

    final String type = element.getAttribute("xsi:type");
    DFieldConfig fc = null;
    if (type.equals("FTText")) {
      fc = DTextFieldCodec.decode(element);
    } else if (type.equals("FTDate")) {
      fc = DDateFieldCodec.decode(element);
    } else if (type.equals("FTNumber")) {
      fc = DNumberFieldCodec.decode(element);
    } else {
      throw new ConfigurationException("Unknown field type '" + type + "'");
    }
    fc.setType(type);
    return fc;
  } // End Method def.

  /**
   * Decode standard values of a field config.
   * 
   * @param dField
   *          Field config.
   * @param element
   *          Element.
   * @throws ConfigurationException
   *           Unable to decode field config.
   */
  public static void decodeStandardValues(DFieldConfig dField, Element element) throws ConfigurationException {
    final Log log = LogFactory.getLog(DFieldConfigCodec.class);

    if (element.hasAttribute("Weight")) {
      dField.setWeight(new Integer(element.getAttribute("Weight")));
    }

    if (element.hasAttribute("FieldTemplate")) {
      dField.setFieldTemplate(element.getAttribute("FieldTemplate"));
    }
    if (element.hasAttribute("Constraint")) {
      dField.setConstraint(element.getAttribute("Constraint"));
    }

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof Element)) {
        continue;
      }
      final Element el = (Element) nl.item(i);

      if ("Transformer".equals(el.getLocalName())) {
        try {
          dField.setTransformer(DTransformerCodec.decode(el));
        } catch (final DSearchException e) {
          log.error("Unable to decode Transformer: " + e.getMessage(), e);
          throw new ConfigurationException("Unable to decode Transformer: " + e.getMessage());
        }
      } else if ("NodeTransformer".equals(el.getLocalName())) {
        try {
          dField.setNodeTransformer(DNodeTransformerCodec.decode(el));
        } catch (final DSearchException e) {
          log.error("Unable to decode NodeTransformer: " + e.getMessage(), e);
          throw new ConfigurationException("Unable to decode NodeTransformer: " + e.getMessage());
        }
      }
    }

  }

  /**
   * Encode field config.
   * 
   * @param dField
   *          Field config.
   * @param element
   *          Parent.
   * @return Element.
   * @throws ConfigurationException
   *           Unable to encode field config.
   */
  protected static Element encode(DFieldConfig dField, Element element) throws ConfigurationException {

    // check for special parameter implementation
    // invoke special implementation
    if (dField instanceof ICodecClass) {
      final ICodecClass iCodecClass = (ICodecClass) dField;
      final Class encoder = iCodecClass.getCodecClass();
      Method m = null;

      try {
        if (dField instanceof DTextField) {
          m = encoder.getMethod("encode", new Class[] { DTextField.class, Element.class });
        } else if (dField instanceof DNumberField) {
          m = encoder.getMethod("encode", new Class[] { DNumberField.class, Element.class });
        } else { // if (dField instanceof DDateField) {
          m = encoder.getMethod("encode", new Class[] { DDateField.class, Element.class });
        }
      } catch (final NoSuchMethodException e) {
        throw new ConfigurationException("unable to locate encoder method", e);
      }

      try {
        return (Element) m.invoke(null, new Object[] { dField, element });
      } catch (final InvocationTargetException e) {
        throw new ConfigurationException("unable to invoke encoder method", e);
      } catch (final IllegalAccessException e) {
        throw new ConfigurationException("unable to invoke encoder method", e);
      }
    }

    if (dField instanceof DDateField) {
      return DDateFieldCodec.encode((DDateField) dField, element);
    } else if (dField instanceof DNumberField) {
      return DNumberFieldCodec.encode((DNumberField) dField, element);
    } else if (dField instanceof DTextField) {
      return DTextFieldCodec.encode((DTextField) dField, element);
    } else if (dField == null) {
      return null;
    } else {
      throw new ConfigurationException("Unknown field element " + dField.getClass());
    }
  }

  /**
   * Decode standard values of a field config.
   * 
   * @param dField
   *          Field config.
   * @param element
   *          Element.
   * @throws ConfigurationException
   *           Unable to decode standard values.
   */
  public static void encodeStandardValues(DFieldConfig dField, Element element) throws ConfigurationException {
    final Log log = LogFactory.getLog(DFieldConfigCodec.class);

    if (dField.getWeight() != null) {
      element.setAttribute("Weight", dField.getWeight() + "");
    }

    if (dField.getFieldTemplate() != null) {
      element.setAttribute("FieldTemplate", dField.getFieldTemplate());
    }
    if (dField.getConstraint() != null) {
      element.setAttribute("Constraint", dField.getConstraint());
    }

    if (dField.getNodeTransformer() != null) {
      try {
        DNodeTransformerCodec.encode(dField.getNodeTransformer(), element);
      } catch (final DSearchException e) {
        log.error("Unable to encode NodeTransformer: " + e.getMessage(), e);
        throw new ConfigurationException("Unable to encode NodeTransformer: " + e.getMessage());
      }
    }

    if (dField.getTransformer() != null) {
      try {
        DTransformerCodec.encode(dField.getTransformer(), element);
      } catch (final DSearchException e1) {
        log.error("Unable to encode Transformer: " + e1.getMessage(), e1);
        throw new ConfigurationException("Unable to encode Transformer: " + e1.getMessage());
      }
    }

  }

  /**
   * 
   */
  private DFieldConfigCodec() {
  }

}
