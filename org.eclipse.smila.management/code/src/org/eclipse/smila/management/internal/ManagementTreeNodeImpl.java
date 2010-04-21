/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.internal;

import org.eclipse.smila.management.ManagementTreeNode;

/**
 * The Class ManagementTreeNodeImpl.
 */
public abstract class ManagementTreeNodeImpl implements ManagementTreeNode {

  /**
   * The _path.
   */
  protected String _path;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementTreeNode#getPath()
   */
  public String getPath() {
    return _path;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof ManagementTreeNode) {
      final ManagementTreeNode node = (ManagementTreeNode) obj;
      return getPath().equals(node.getPath());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getPath().hashCode();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getPath();
  }

}
