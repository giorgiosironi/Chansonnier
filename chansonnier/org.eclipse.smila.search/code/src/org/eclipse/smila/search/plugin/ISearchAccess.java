/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

import org.eclipse.smila.search.utils.search.IDFParameter;
import org.eclipse.smila.search.utils.search.INFParameter;
import org.eclipse.smila.search.utils.search.ITFParameter;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ISearchAccess {

  Class getCodecClass(ITFParameter parameter);

  Class getCodecClass(IDFParameter parameter);

  Class getCodecClass(INFParameter parameter);

}
