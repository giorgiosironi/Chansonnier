/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.templates.messages.nodetransformerregistry.DNodeTransformer;
import org.eclipse.smila.search.templates.messages.nodetransformerregistry.DNodeTransformerRegistry;
import org.eclipse.smila.search.utils.param.ParameterSet;
import org.eclipse.smila.search.utils.param.set.DParameterSet;

/**
 * @author gschmidt
 * 
 */
public abstract class NodeTransformerRegistryController {

  /**
   * Constructor.
   */
  private NodeTransformerRegistryController() {

  }

  /**
   * @param urn -
   * @param ic -
   * @return NodeTransformer
   * @throws NodeTransformerException -
   */
  public static NodeTransformer getNodeTransformer(String urn, IndexConnection ic) throws NodeTransformerException {
    final Log log = LogFactory.getLog(NodeTransformerRegistryController.class);
    if (urn == null || urn.equals("")) {
      urn = "urn:SimpleNodeTransformer";
    }
    try {
      final NodeTransformerType nodeTransformerType = NodeTransformerType.getTypes().get(urn);
      // final DNodeTransformer dNodeTransformerReg = registry.getNodeTransformer(urn);
      if (nodeTransformerType == null) {
        throw new NodeTransformerException("node transformer not found [" + urn + "]");
      }
      final NodeTransformer nodeTransformer = nodeTransformerType.loadNodeTransformer();
      nodeTransformer.setIndexConnection(ic);
      return nodeTransformer;
    } catch (final NodeTransformerException e) {
      throw e;
    } catch (final Exception e) {
      log.error("unable to instanciate node transformer [" + urn + "]", e);
      throw new NodeTransformerException("unable to instanciate node transformer [" + urn + "]", e);
    }

  }

  /**
   * @param dNodeTransformer -
   * @param ic -
   * @return NodeTransformer
   * @throws NodeTransformerException -
   */
  public static NodeTransformer getNodeTransformer(
    org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformer dNodeTransformer,
    IndexConnection ic) throws NodeTransformerException {
    final Log log = LogFactory.getLog(NodeTransformerRegistryController.class);
    if (dNodeTransformer == null) {
      return getNodeTransformer("urn:SimpleNodeTransformer", ic);
    }
    try {
      final NodeTransformerType nodeTransformerType =
        NodeTransformerType.getTypes().get(dNodeTransformer.getName());
      if (nodeTransformerType == null) {
        throw new NodeTransformerException("node transformer not found [" + dNodeTransformer.getName() + "]");
      }
      final NodeTransformer nodeTransformer = nodeTransformerType.loadNodeTransformer();
      final DParameterSet dParameterSet = dNodeTransformer.getParameterSet();
      if (dParameterSet != null) {
        final ParameterSet paramSet = new ParameterSet(dParameterSet, nodeTransformerType.getParameterDefinition());
        nodeTransformer.setParameterSet(paramSet);
      }
      nodeTransformer.setIndexConnection(ic);
      return nodeTransformer;
    } catch (final NodeTransformerException e) {
      throw e;
    } catch (final Exception e) {
      log.error("unable to instanciate node transformer [" + dNodeTransformer.getName() + "]", e);
      throw new NodeTransformerException("unable to instanciate node transformer [" + dNodeTransformer.getName()
        + "]", e);
    }
  }

  /**
   * @return DNodeTransformerRegistry
   */
  public static DNodeTransformerRegistry getNodeTransformer() {
    final Log log = LogFactory.getLog(NodeTransformerRegistryController.class);
    final Hashtable<String, NodeTransformerType> nodeTransformers = NodeTransformerType.getTypes();
    final DNodeTransformerRegistry registry = new DNodeTransformerRegistry();
    for (final NodeTransformerType nodeTransformerType : nodeTransformers.values()) {
      final DNodeTransformer nt = new DNodeTransformer();
      nt.setClassName(nodeTransformerType.getClass().getName());
      nt.setDescription(nodeTransformerType.getName());
      nt.setName(nodeTransformerType.getName());
      try {
        nt.setParameterDefinition(nodeTransformerType.getParameterDefinition());
      } catch (final NodeTransformerException exception) {
        if (log.isErrorEnabled()) {
          log.error(exception);
        }
      }
      registry.addNodeTransformer(nt);
    }

    return registry;
  }
}
