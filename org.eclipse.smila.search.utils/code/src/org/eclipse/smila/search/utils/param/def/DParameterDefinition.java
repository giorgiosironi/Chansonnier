/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.def;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.xml.XMLUtils;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
@SuppressWarnings("unchecked")
public class DParameterDefinition {

  private final Vector _parameters = new Vector();

  public static void main(String[] arg) {
    final Log log = LogFactory.getLog(DParameterDefinition.class);    
    try {
      final org.w3c.dom.Document d =
        XMLUtils.parse(new java.io.File("d:/anyfinder/af-engine-sdk/xml/param-testcase.xml"), true);
      final DParameterDefinition pset = DParameterDefinitionCodec.decode(d.getDocumentElement());

      final org.w3c.dom.Document d2 = XMLUtils.getDocument("top");
      final org.w3c.dom.Element e = d2.getDocumentElement();
      final org.w3c.dom.Element pelement = DParameterDefinitionCodec.encode(pset, e);

      XMLUtils.stream(pelement, false, "UTF-8", System.err);
    } catch (final Exception e) {
      if (log.isErrorEnabled()) {
        log.error(e);
      }
    }
  }

  public void addParameter(DParameter param) {
    _parameters.addElement(param);
  }

  public DParameter getParameter(String name) {
    for (int i = 0; i < _parameters.size(); i++) {
      final DParameter p = (DParameter) _parameters.elementAt(i);
      if (p.getName().equals(name)) {
        return p;
      }

    }
    return null;
  }

  public DParameter[] getParameters() {
    final DParameter[] params = new DParameter[_parameters.size()];
    _parameters.copyInto(params);
    return params;
  }

}
