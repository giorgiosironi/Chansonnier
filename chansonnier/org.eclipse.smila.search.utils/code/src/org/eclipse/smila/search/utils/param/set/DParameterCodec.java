/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DParameterCodec {

  /**
   * Constructor.
   */
  private DParameterCodec() {

  }

  /**
   * @param element -
   * @return DParameter
   * @throws ParameterException -
   */
  public static DParameter decode(Element element) throws ParameterException {

    String type = element.getAttribute("xsi:type");
    type = XMLUtils.getLocalPart(type);

    DParameter dParameter = null;

    if ("Float".equals(type)) {
      dParameter = DFloatCodec.decode(element);
    } else if ("Date".equals(type)) {
      dParameter = DDateCodec.decode(element);
    } else if ("String".equals(type)) {
      dParameter = DStringCodec.decode(element);
    } else if ("Integer".equals(type)) {
      dParameter = DIntegerCodec.decode(element);
    } else if ("Boolean".equals(type)) {
      dParameter = DBooleanCodec.decode(element);
    } else if ("FloatList".equals(type)) {
      dParameter = DFloatListCodec.decode(element);
    } else if ("DateList".equals(type)) {
      dParameter = DDateListCodec.decode(element);
    } else if ("StringList".equals(type)) {
      dParameter = DStringListCodec.decode(element);
    } else if ("IntegerList".equals(type)) {
      dParameter = DIntegerListCodec.decode(element);
    } else {
      throw new ParameterException("Unknown parameter type [" + type + "]");
    }

    dParameter.setName(element.getAttribute("Name"));
    dParameter.setType(type);

    return dParameter;
  } // End Method def.

  public static Element encode(DParameter dParameter, Element element) throws ParameterException {

    Element el = null;

    if (dParameter instanceof DFloat) {
      el = DFloatCodec.encode((DFloat) dParameter, element);
    } else if (dParameter instanceof DDate) {
      el = DDateCodec.encode((DDate) dParameter, element);
    } else if (dParameter instanceof DString) {
      el = DStringCodec.encode((DString) dParameter, element);
    } else if (dParameter instanceof DInteger) {
      el = DIntegerCodec.encode((DInteger) dParameter, element);
    } else if (dParameter instanceof DBoolean) {
      el = DBooleanCodec.encode((DBoolean) dParameter, element);
    } else if (dParameter instanceof DFloatList) {
      el = DFloatListCodec.encode((DFloatList) dParameter, element);
    } else if (dParameter instanceof DDateList) {
      el = DDateListCodec.encode((DDateList) dParameter, element);
    } else if (dParameter instanceof DStringList) {
      el = DStringListCodec.encode((DStringList) dParameter, element);
    } else if (dParameter instanceof DIntegerList) {
      el = DIntegerListCodec.encode((DIntegerList) dParameter, element);
    } else {
      throw new ParameterException("Unknown parameter type [" + dParameter.getClass() + "]");
    }

    el.setAttribute("Name", dParameter.getName());

    // just to go sure enumerations are streamed correctly
    String type = dParameter.getType();
    if (dParameter.getType().equals("Enumeration")) {
      if (dParameter instanceof DString) {
        type = "String";
      } else {
        type = "StringList";
      }
    }

    el.setAttribute("xsi:type", type);

    element.appendChild(el);
    return el;
  }

}
