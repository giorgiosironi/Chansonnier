/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.messages.fieldrequest;

import java.util.Iterator;

/**
 * @author August Georg Schmidt (BROX)
 */
public abstract class DAnyFinderFieldRequestCodec {

  /**
   * Namespace.
   */
  public static final String NS = "http://www.anyfinder.de/FieldRequest";

  /**
   * 
   */
  private DAnyFinderFieldRequestCodec() {
    super();
  }

  /**
   * Encode DAnyFinderFieldRequest to Document.
   * 
   * @param fieldRequest
   *          Field request.
   * @return XML document.
   * @throws FieldRequestException
   *           Unable to encode DAnyFinderFieldRequest.
   */
  public static org.w3c.dom.Document encode(DAnyFinderFieldRequest fieldRequest) throws FieldRequestException {
    return encode(fieldRequest, org.eclipse.smila.utils.xml.XMLUtils.getDocument());
  }

  /**
   * Encode DAnyFinderFieldRequest to Document.
   * 
   * @param fieldRequest
   *          Field request.
   * @param doc
   *          Document.
   * @return XML document.
   * @throws FieldRequestException
   *           Unable to encode DAnyFinderFieldRequest.
   */
  public static org.w3c.dom.Document encode(DAnyFinderFieldRequest fieldRequest, org.w3c.dom.Document doc)
    throws FieldRequestException {
    if (fieldRequest == null) {
      throw new FieldRequestException("parameter must not be null [fieldRequest]");
    }

    if (doc == null) {
      throw new FieldRequestException("parameter must not be null [doc]");
    }
    final org.w3c.dom.Element root = doc.createElementNS(NS, "AnyFinderFieldRequest");
    doc.appendChild(root);

    // set NS attributes
    root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    root.setAttribute("xsi:schemaLocation", NS + " ../xml/AnyFinderFieldRequest.xsd");

    // set custom attributes
    root.setAttribute("IndexName", fieldRequest.getIndexName());

    // create elements
    for (final Iterator<DField> fields = fieldRequest.getFields(); fields.hasNext();) {
      final DField field = fields.next();
      DFieldCodec.encode(field, root);
    }

    return doc;
  }

  /**
   * Decode XML element into DAnyFinderFieldRequest.
   * 
   * @param element
   *          Element to decode.
   * @return Decoded element as DAnyFinderFieldRequest.
   * @throws FieldRequestException
   *           Unable to decode XML element.
   */
  public static DAnyFinderFieldRequest decode(final org.w3c.dom.Element element) throws FieldRequestException {

    if (element == null) {
      throw new FieldRequestException("parameter must not be null [element]");
    }

    final DAnyFinderFieldRequest fieldRequest = new DAnyFinderFieldRequest();

    // resolve custom attributes
    fieldRequest.setIndexName(element.getAttribute("IndexName"));

    // resolve custom elements
    final org.w3c.dom.NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof org.w3c.dom.Element)) {
        continue;
      }
      final org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

      if ("Field".equals(el.getLocalName())) {
        fieldRequest.addField(DFieldCodec.decode(el));
      }
    }

    return fieldRequest;
  }
}
