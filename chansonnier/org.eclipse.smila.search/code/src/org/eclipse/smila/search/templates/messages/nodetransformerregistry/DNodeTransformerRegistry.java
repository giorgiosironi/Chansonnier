/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.nodetransformerregistry;

import java.util.Iterator;
import java.util.Hashtable;

public class DNodeTransformerRegistry {
  private Hashtable<String, DNodeTransformer> _nodeTransformers = new Hashtable<String, DNodeTransformer>();

  public DNodeTransformerRegistry() {
  }

  public void addNodeTransformer(DNodeTransformer dNodeTransformer) {
    _nodeTransformers.put(dNodeTransformer.getName(), dNodeTransformer);
  }

  public void removeNodeTransformer(DNodeTransformer dNodeTransformer) {
    _nodeTransformers.remove(dNodeTransformer.getName());
  }

  public Iterator getNodeTransformers() {
    return _nodeTransformers.values().iterator();
  }

  public int getNodeTransformerCount() {
    return _nodeTransformers.size();
  }

  public DNodeTransformer getNodeTransformer(String name) {
    return (DNodeTransformer) _nodeTransformers.get(name);
  }
}
