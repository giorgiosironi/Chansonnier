/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author gschmidt
 * 
 */
abstract class DIndexFieldCodec {

  /**
   * Constructor.
   */
  private DIndexFieldCodec() {

  }

  /**
   * @param dIndexField -
   * @param element -
   * @return Element
   * @throws DSearchTemplatesException -
   */
  protected static Element encode(DIndexField dIndexField, Element element) throws DSearchTemplatesException {

    final Document doc = element.getOwnerDocument();
    final Element el = (Element) element.appendChild(doc.createElementNS(DSearchTemplatesCodec.NS, "IndexField"));

    el.setAttribute("Name", dIndexField.getName());
    el.setAttribute("FieldNo", dIndexField.getFieldNo() + "");

    return el;
  }

  /**
   * @param element -
   * @return DIndexField
   * @throws DSearchTemplatesException -
   */
  protected static DIndexField decode(Element element) throws DSearchTemplatesException {

    final DIndexField dIndexField = new DIndexField();

    dIndexField.setName(element.getAttribute("Name"));
    dIndexField.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));

    return dIndexField;
  }
}
