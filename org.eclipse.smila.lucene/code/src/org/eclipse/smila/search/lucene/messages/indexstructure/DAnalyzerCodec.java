/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.set.DParameterSetCodec;
import org.w3c.dom.Element;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public abstract class DAnalyzerCodec {

  /**
   * Constructor.
   */
  private DAnalyzerCodec() {

  }

  public static org.w3c.dom.Element encode(DAnalyzer dAnalyzer, org.w3c.dom.Element parent) throws ISException {

    if (parent == null) {
      throw new ISException("parameter must not be null [parent]");
    }

    if (dAnalyzer == null) {
      throw new ISException("parameter must not be null [dAnalyzer]");
    }
    final org.w3c.dom.Document doc = parent.getOwnerDocument();
    final org.w3c.dom.Element el = doc.createElement("Analyzer");

    // set custom attributes
    el.setAttribute("ClassName", dAnalyzer.getClassName());

    // create elements
    if (dAnalyzer.getParameterSet() != null) {
      try {
        DParameterSetCodec.encode(dAnalyzer.getParameterSet(), el);
      } catch (final ParameterException e) {
        throw new ISException("unable to encode parameter set for analyzer '" + dAnalyzer.getClassName() + "'", e);
      }
    }

    // insert into dom tree
    parent.appendChild(el);
    return el;
  }

  public static DAnalyzer decode(org.w3c.dom.Element element) throws ISException {

    if (element == null) {
      throw new ISException("parameter must not be null [element]");
    }

    final DAnalyzer dAnalyzer = new DAnalyzer();

    // resolve custom attributes
    dAnalyzer.setClassName(element.getAttribute("ClassName"));

    // resolve custom elements
    final org.w3c.dom.NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof org.w3c.dom.Element)) {
        continue;
      }
      final org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

      if ("ParameterSet".equals(el.getLocalName())) {
        try {
          dAnalyzer.setParameterSet(DParameterSetCodec.decode((Element) nl.item(i)));
        } catch (final ParameterException e) {
          throw new ISException("unable to decode parameter set for analyzer '" + dAnalyzer.getClassName() + "'", e);
        }
      }
    }

    return dAnalyzer;
  }
}
