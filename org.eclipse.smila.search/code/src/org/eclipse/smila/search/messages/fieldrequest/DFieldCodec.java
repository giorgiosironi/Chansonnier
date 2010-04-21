/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.messages.fieldrequest;

import org.eclipse.smila.search.utils.search.DSearchException;

/**
 * @author August Georg Schmidt (BROX)
 */
public abstract class DFieldCodec {

  /**
   * 
   */
  private DFieldCodec() {
    super();
  }

  /**
   * Encode DField object into XML.
   * 
   * @param field
   *          Object to encode.
   * @param parent
   *          Parent of object.
   * @return Encoded element.
   * @throws FieldRequestException
   *           Unable to encode object.
   */
  public static org.w3c.dom.Element encode(final DField field, final org.w3c.dom.Element parent)
    throws FieldRequestException {

    if (parent == null) {
      throw new FieldRequestException("parameter must not be null [parent]");
    }

    if (field == null) {
      throw new FieldRequestException("parameter must not be null [field]");
    }
    final org.w3c.dom.Document doc = parent.getOwnerDocument();
    final org.w3c.dom.Element el = doc.createElementNS(DAnyFinderFieldRequestCodec.NS, "Field");

    // set custom attributes
    el.setAttribute("ReferenceID", field.getReferenceID());

    // create elements
    try {
      org.eclipse.smila.search.utils.search.DFieldCodec.encode(field.getField(), el);
    } catch (DSearchException exception) {
      throw new FieldRequestException("unable to encode field.", exception);
    }

    // insert into dom tree
    parent.appendChild(el);
    return el;
  }

  /**
   * Decode XML element into DField.
   * 
   * @param element
   *          Element to decode.
   * @return Decoded element as DField.
   * @throws FieldRequestException
   *           Unable to decode XML element.
   */
  public static DField decode(final org.w3c.dom.Element element) throws FieldRequestException {

    if (element == null) {
      throw new FieldRequestException("parameter must not be null [element]");
    }

    final DField field = new DField();

    // resolve custom attributes
    field.setReferenceID(element.getAttribute("ReferenceID"));

    // resolve custom elements
    final org.w3c.dom.NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof org.w3c.dom.Element)) {
        continue;
      }
      final org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

      if ("Field".equals(el.getLocalName())) {
        try {
          final org.eclipse.smila.search.utils.search.DField ssField =
            org.eclipse.smila.search.utils.search.DFieldCodec.decode(el);
          field.setField(ssField);
        } catch (DSearchException exception) {
          throw new FieldRequestException("unable to decode field.", exception);
        }
      }
    }

    return field;
  }
}
