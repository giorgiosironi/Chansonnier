/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.fieldtemplates;

import org.eclipse.smila.search.plugin.Plugin;
import org.eclipse.smila.search.plugin.PluginFactory;
import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author August Georg Schmidt (BROX)
 */
abstract class DFieldTemplateCodec {

  /**
   * 
   */
  private DFieldTemplateCodec() {
  }

  /**
   * Encode DFieldTemplate object into XML.
   * 
   * @param dFieldTemplate
   *          Object to encode.
   * @param parent
   *          Parent of object.
   * @return Encoded element.
   * @throws DFieldTemplatesException
   *           Unable to encode object.
   */
  public static org.w3c.dom.Element encode(final DFieldTemplate dFieldTemplate, final org.w3c.dom.Element parent)
    throws DFieldTemplatesException {

    if (parent == null) {
      throw new DFieldTemplatesException("parameter must not be null [parent]");
    }

    if (dFieldTemplate == null) {
      throw new DFieldTemplatesException("parameter must not be null [dFieldTemplate]");
    }
    final org.w3c.dom.Document doc = parent.getOwnerDocument();
    final org.w3c.dom.Element el = doc.createElementNS(DFieldTemplatesCodec.NS, "FieldTemplate");

    // set custom attributes
    el.setAttribute("Name", dFieldTemplate.getName());

    // create elements
    Element elTemp = null;
    elTemp = doc.createElementNS(DFieldTemplatesCodec.NS, "Description");
    elTemp.appendChild(doc.createCDATASection(dFieldTemplate.getDescription()));
    el.appendChild(elTemp);

    DSelectorCodec.encode(dFieldTemplate.getSelector(), el);

    try {
      final Plugin plugin = PluginFactory.getPlugin();
      plugin.getAdvSearchAccess().encodeTerm(dFieldTemplate.getTerm(), el);
    } catch (AdvSearchException e) {
      throw new DFieldTemplatesException(e.getMessage());
    }

    // insert into dom tree
    parent.appendChild(el);
    return el;
  }

  /**
   * Decode XML element into DFieldTemplate.
   * 
   * @param element
   *          Element to decode.
   * @return Decoded element as DFieldTemplate.
   * @throws DFieldTemplatesException
   *           Unable to decode XML element.
   */
  public static DFieldTemplate decode(final org.w3c.dom.Element element) throws DFieldTemplatesException {

    if (element == null) {
      throw new DFieldTemplatesException("parameter must not be null [element]");
    }

    final DFieldTemplate dFieldTemplate = new DFieldTemplate();

    // resolve custom attributes
    dFieldTemplate.setName(element.getAttribute("Name"));

    // resolve custom elements
    final org.w3c.dom.NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof org.w3c.dom.Element)) {
        continue;
      }
      final org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

      if ("Description".equals(el.getLocalName())) {
        dFieldTemplate.setDescription(el.getTextContent());
      } else if ("Selector".equals(el.getLocalName())) {
        dFieldTemplate.setSelector(DSelectorCodec.decode(el));
      } else if ("Term".equals(el.getLocalName())) {
        try {
          XMLUtils.removeWhitespaceTextNodes(el);

          final Plugin plugin = PluginFactory.getPlugin();
          dFieldTemplate.setTerm(plugin.getAdvSearchAccess().decodeTerm(el));
        } catch (Throwable e) {
          throw new DFieldTemplatesException(e.getMessage());
        }
      }
    }

    return dFieldTemplate;
  }
}
