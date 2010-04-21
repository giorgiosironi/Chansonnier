/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.eclipse.smila.search.index.IndexException;

/**
 * The Interface ISynchronizedCondition.
 * 
 * @param <InitObject>
 *          class of object to initialize.
 */
public interface ISynchronizedCondition<InitObject> {

  /**
   * initialized object.
   * 
   * @return object initialized
   * 
   * @throws IndexException
   *           the index exception
   */
  InitObject initialize() throws IndexException;

}
