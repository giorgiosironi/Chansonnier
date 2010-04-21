/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.search.utils.advsearch.IAdvSearch;
import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public interface IAdvSearchAccess {

  /**
   * Namespace.
   */
  String NS = "http://www.anyfinder.de/AdvancedSearch";

  /**
   * Decode advanced search.
   * 
   * @param eAdvSearch
   *          Advanced search element.
   * @return Decoded advanced search.
   * @throws AdvSearchException
   *           Unable to decode advanced search.
   */
  IAdvSearch decode(Element eAdvSearch) throws AdvSearchException;

  /**
   * Encode advanced search.
   * 
   * @param dAnyFinderAdvSearch
   *          Advanced search.
   * @return Encoded advanced search.
   * @throws AdvSearchException
   *           Unable to encode advanced search.
   */
  Document encode(IAdvSearch dAnyFinderAdvSearch) throws AdvSearchException;

  /**
   * Encode advanced search.
   * 
   * @param dAnyFinderAdvSearch
   *          Advanced search.
   * @param element
   *          Parent element.
   * @return Encoded advanced search.
   * @throws AdvSearchException
   *           Unable to encode advanced search.
   */
  Element encode(IAdvSearch dAnyFinderAdvSearch, Element element) throws AdvSearchException;

  /**
   * Decode term.
   * 
   * @param eTerm
   *          Term element.
   * @return Decoded term.
   * @throws AdvSearchException
   *           Unable to decode term.
   */
  ITerm decodeTerm(Element eTerm) throws AdvSearchException;

  /**
   * Encode term.
   * 
   * @param dTerm
   *          Term to encode.
   * @param element
   *          Parent element.
   * @return Encoded element.
   * @throws AdvSearchException
   *           Unable to encode element.
   */
  Element encodeTerm(ITerm dTerm, Element element) throws AdvSearchException;

}
