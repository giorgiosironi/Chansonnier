/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import org.eclipse.smila.search.lucene.messages.advsearch.DAnyFinderAdvSearch;
import org.eclipse.smila.search.lucene.messages.advsearch.DAnyFinderAdvSearchCodec;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.messages.advsearch.DTermCodec;
import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.search.utils.advsearch.IAdvSearch;
import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AdvSearchAccess extends org.eclipse.smila.search.utils.advsearch.AdvSearchAccess {

  /**
   * 
   */
  public AdvSearchAccess() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IAdvSearchAccess#decode(org.w3c.dom.Element)
   */
  @Override
  public IAdvSearch decode(Element eAdvSearch) throws AdvSearchException {
    return DAnyFinderAdvSearchCodec.decode(eAdvSearch);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IAdvSearchAccess#encode(org.eclipse.smila.search.utils.advsearch.IAdvSearch)
   */
  @Override
  public Document encode(IAdvSearch dAnyFinderAdvSearch) throws AdvSearchException {
    if (!(dAnyFinderAdvSearch instanceof DAnyFinderAdvSearch)) {
      throw new AdvSearchException("invalid class type for encoding [" + dAnyFinderAdvSearch.getClass().getName()
        + "]");
    }
    return DAnyFinderAdvSearchCodec.encode((DAnyFinderAdvSearch) dAnyFinderAdvSearch);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IAdvSearchAccess#encode(org.eclipse.smila.search.utils.advsearch.IAdvSearch,
   *      org.w3c.dom.Element)
   */
  @Override
  public Element encode(IAdvSearch dAnyFinderAdvSearch, Element element) throws AdvSearchException {

    if (!(dAnyFinderAdvSearch instanceof DAnyFinderAdvSearch)) {
      throw new AdvSearchException("invalid class type for encoding [" + dAnyFinderAdvSearch.getClass().getName()
        + "]");
    }
    return DAnyFinderAdvSearchCodec.encode((DAnyFinderAdvSearch) dAnyFinderAdvSearch, element);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IAdvSearchAccess#decode(org.w3c.dom.Element)
   */
  @Override
  public ITerm decodeTerm(Element eTerm) throws AdvSearchException {
    return DTermCodec.decode(eTerm);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IAdvSearchAccess#encode(org.eclipse.smila.search.utils.advsearch.IAdvSearch,
   *      org.w3c.dom.Element)
   */
  @Override
  public Element encodeTerm(ITerm dTerm, Element element) throws AdvSearchException {

    if (!(dTerm instanceof DTerm)) {
      throw new AdvSearchException("invalid class type for encoding [" + dTerm.getClass().getName() + "]");
    }
    return DTermCodec.encode((DTerm) dTerm, element);
  }

}
