/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.w3c.dom.Element;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IIndexStructureAccess {

  DIndexStructure decode(Element eIndexStructure) throws ISException;

  Element encode(DIndexStructure dIndexStructure, Element element) throws ISException;

}
