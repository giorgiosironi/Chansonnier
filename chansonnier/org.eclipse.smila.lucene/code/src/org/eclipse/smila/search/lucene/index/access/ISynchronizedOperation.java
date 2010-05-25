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
 * The Interface ISynchronizedOperation.
 * 
 * @param <InitObject>
 *          class of initialized object.
 * @param <ReturnedType>
 *          class of returned type.
 */
public interface ISynchronizedOperation<InitObject, ReturnedType> {

  /**
   * Process.
   * 
   * @param object
   *          the object
   * 
   * @return true, if successful
   * 
   * @throws IndexException
   *           the index exception
   */
  ReturnedType process(InitObject object) throws IndexException;
}
