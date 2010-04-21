/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.index;

import java.util.Vector;

/**
 * Index usage.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class IndexUsage {
  /**
   * Default timeout.
   */
  public static final int TIMEOUT = 5000;

  /**
   * Index name.
   */
  public String _indexName;

  /**
   * Usage of index.
   */
  public Usage _usage = Usage.Multi;

  /**
   * Index connection usages.
   */
  public Vector<IndexConnectionUsage> _indexConnectionUsages = new Vector<IndexConnectionUsage>(0);

  /**
   * Idle since.
   */
  public long _idleSince = System.currentTimeMillis();
}
