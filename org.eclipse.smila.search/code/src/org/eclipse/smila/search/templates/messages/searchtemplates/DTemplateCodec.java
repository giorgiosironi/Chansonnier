/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import org.eclipse.smila.search.plugin.Plugin;
import org.eclipse.smila.search.plugin.PluginFactory;
import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author gschmidt
 * 
 */
abstract class DTemplateCodec {

  /**
   * Constructor.
   */
  private DTemplateCodec() {

  }

  /**
   * @param dTemplate -
   * @param element -
   * @return Element
   * @throws DSearchTemplatesException -
   */
  protected static Element encode(DTemplate dTemplate, Element element) throws DSearchTemplatesException {

    final Document doc = element.getOwnerDocument();
    final Element el = (Element) element.appendChild(doc.createElementNS(DSearchTemplatesCodec.NS, "Template"));

    el.setAttribute("Name", dTemplate.getName());

    Element elTemp = null;
    elTemp = doc.createElementNS(DSearchTemplatesCodec.NS, "Description");
    elTemp.appendChild(doc.createTextNode(dTemplate.getDescription()));
    el.appendChild(elTemp);

    DSelectorCodec.encode(dTemplate.getSelector(), el);

    try {
      final Plugin plugin = PluginFactory.getPlugin();
      plugin.getAdvSearchAccess().encode(dTemplate.getAdvSearch(), el);
    } catch (final AdvSearchException e) {
      throw new DSearchTemplatesException(e.getMessage());
    }

    return el;
  }

  protected static DTemplate decode(Element element) throws DSearchTemplatesException {

    final DTemplate dTemplate = new DTemplate();

    dTemplate.setName(element.getAttribute("Name"));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Description".equals(nl.item(i).getLocalName())) {
        final Element elTemp = (Element) nl.item(i);
        if (elTemp.hasChildNodes()) {
          dTemplate.setDescription(((Text) elTemp.getFirstChild()).getNodeValue());
        } else {
          dTemplate.setDescription("");
        }
      } else if ("Selector".equals(nl.item(i).getLocalName())) {
        dTemplate.setSelector(DSelectorCodec.decode((Element) nl.item(i)));
      } else if ("AnyFinderAdvancedSearch".equals(nl.item(i).getLocalName())) {
        try {
          final Element el = (Element) nl.item(i);
          XMLUtils.removeWhitespaceTextNodes(el);

          final Plugin plugin = PluginFactory.getPlugin();
          dTemplate.setAdvSearch(plugin.getAdvSearchAccess().decode(el));
        } catch (final Throwable e) {
          throw new DSearchTemplatesException(e.getMessage());
        }
      }

    }
    return dTemplate;
  }
}
