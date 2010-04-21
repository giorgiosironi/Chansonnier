/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.search.utils.search.DSearchException;
import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformerCodec;
import org.eclipse.smila.search.utils.search.parameterobjects.DTransformerCodec;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
public abstract class DTemplateFieldCodec {

  /**
   * Constructor.
   */
  private DTemplateFieldCodec() {

  }

  /**
   * @param dTemplateField -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DTemplateField dTemplateField, Element parent) throws AdvSearchException {
    if (dTemplateField instanceof DTextTemplateField) {
      return DTextTemplateFieldCodec.encode((DTextTemplateField) dTemplateField, parent);
    } else if (dTemplateField instanceof DNumberTemplateField) {
      return DNumberTemplateFieldCodec.encode((DNumberTemplateField) dTemplateField, parent);
    } else { // if (dTemplateField instanceof DDateTemplateField) {
      return DDateTemplateFieldCodec.encode((DDateTemplateField) dTemplateField, parent);
    }
  }

  public static void encodeStandardValues(DTemplateField dTemplateField, Element element) throws AdvSearchException {
    element.setAttribute("FieldNo", dTemplateField.getFieldNo() + "");
    element.setAttribute("SourceFieldNo", dTemplateField.getSourceFieldNo() + "");

    if (dTemplateField.getNodeTransformer() != null) {
      try {
        DNodeTransformerCodec.encode(dTemplateField.getNodeTransformer(), element);
      } catch (DSearchException e) {
        throw new AdvSearchException("Unable to encode NodeTransformer for field " + dTemplateField.getFieldNo()
          + ": " + e.getMessage());
      }
    }

    if (dTemplateField.getTransformer() != null) {
      try {
        DTransformerCodec.encode(dTemplateField.getTransformer(), element);
      } catch (DSearchException e) {
        throw new AdvSearchException("Unable to encode Transformer for field " + dTemplateField.getFieldNo() + ": "
          + e.getMessage());
      }
    }
  }

  public static void decodeStandardValues(DTemplateField dTemplateField, Element element) throws AdvSearchException {

    dTemplateField.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));
    dTemplateField.setSourceFieldNo(Integer.parseInt(element.getAttribute("SourceFieldNo")));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("NodeTransformer".equals(nl.item(i).getLocalName())) {
        try {
          dTemplateField.setNodeTransformer(DNodeTransformerCodec.decode((Element) nl.item(i)));
        } catch (DSearchException e) {
          throw new AdvSearchException("Unable to encode NodeTransformer for field " + dTemplateField.getFieldNo()
            + ": " + e.getMessage());
        }
      } else if ("Transformer".equals(nl.item(i).getLocalName())) {
        try {
          dTemplateField.setTransformer(DTransformerCodec.decode((Element) nl.item(i)));
        } catch (DSearchException e) {
          throw new AdvSearchException("Unable to encode Transformer for field " + dTemplateField.getFieldNo()
            + ": " + e.getMessage());
        }
      }
    }
  }

  public static DTemplateField decode(Element element) throws AdvSearchException {
    final String type = element.getAttribute("xsi:type");
    if (type.equals("TextTemplateField")) {
      return DTextTemplateFieldCodec.decode(element);
    } else if (type.equals("NumberTemplateField")) {
      return DNumberTemplateFieldCodec.decode(element);
    } else { // if (type.equals("DateTemplateField")) {
      return DDateTemplateFieldCodec.decode(element);
    }
  }
}
