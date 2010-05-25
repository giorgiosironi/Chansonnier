/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.search.utils.param.def.DParameterDefinition;
import org.eclipse.smila.search.utils.param.def.DParameterDefinitionCodec;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public final class NodeTransformerType {

  public static final String EXTENSION_POINT = "org.eclipse.smila.search.templates.nodetransformer";

  /**
   * Cached Node Transformer types.
   */
  private static Hashtable<String, NodeTransformerType> s_cachedNodeTransformerTypes;

  /**
   * Unknown Node Transformer type.
   */
  public static final NodeTransformerType UNKNOWN = new NodeTransformerType();

  /**
   * Name attribute.
   */
  private static final String ATT_NAME = "Name";

  /**
   * IRM class attribute.
   */
  private static final String ATT_CLASS_NAME = "ClassName";

  /**
   * NodeTransformer Tag.
   */
  private static final String TAG_NODE_TRANSFORMER = "NodeTransformer";

  /**
   * Name.
   */
  private final String _name;

  /**
   * Class name.
   */
  private final String _className;

  /**
   * Ordinal.
   */
  private final int _ordinal;

  /**
   * Configuration element.
   */
  private final IConfigurationElement _configurationElement;

  /**
   * NodeTransformer.
   */
  private NodeTransformer _nodeTransformer;

  /**
   * Create an unknown IRMType.
   */
  private NodeTransformerType() {
    _configurationElement = null;
    _ordinal = 0;
    _name = "Unknown";
    _className = null;
    // _processClass = null;
    // _processCodecClass = null;
    // _sourceFieldClass = null;
    // _sourceFieldCodecClass = null;
  }

  /**
   * Create a IRMType based on configuration entries.
   * 
   * @param configurationElement
   *          Configuration entry.
   * @param ordinal
   *          Ordinal.
   */
  private NodeTransformerType(IConfigurationElement configurationElement, int ordinal) {
    _configurationElement = configurationElement;
    _ordinal = ordinal;
    _name = getAttribute(configurationElement, ATT_NAME, null);
    _className = getAttribute(configurationElement, ATT_CLASS_NAME, null);
    // _sourceFieldClass = getAttribute(configurationElement, ATT_SOURCE_FIELD_CLASS, null);
    // _sourceFieldCodecClass = getAttribute(configurationElement, ATT_SOURCE_FIELD_CODEC_CLASS, null);
    // _processClass = getAttribute(configurationElement, ATT_PROCESS_CLASS, null);
    // _processCodecClass = getAttribute(configurationElement, ATT_PROCESS_CODEC_CLASS, null);
  }

  /**
   * Get attribute value from configuration.
   * 
   * @param configurationElement
   *          Configuration element.
   * @param name
   *          Name.
   * @param defaultValue
   *          Default value.
   * @return Attribute or default value.
   */
  private static String getAttribute(IConfigurationElement configurationElement, String name, String defaultValue) {
    final String value = configurationElement.getAttribute(name);
    if (value != null) {
      return value;
    }
    if (defaultValue != null) {
      return defaultValue;
    }

    throw new IllegalArgumentException("Missing " + name + " attribute");
  }

  /**
   * Get all available IRM types.
   * 
   * @return NodeTransformer types.
   */
  public static Hashtable<String, NodeTransformerType> getTypes() {

    if (s_cachedNodeTransformerTypes != null) {
      return s_cachedNodeTransformerTypes;
    }

    s_cachedNodeTransformerTypes = new Hashtable<String, NodeTransformerType>();
    final List<NodeTransformerType> found = new ArrayList<NodeTransformerType>();
    // TODO: Check why the next line is needed.
    // found.add(UNKNOWN);
    final IExtension[] extensions =
      Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT).getExtensions();
    for (int i = 0; i < extensions.length; i++) {
      final IExtension extension = extensions[i];
      final IConfigurationElement[] configElements = extension.getConfigurationElements();

      for (int j = 0; j < configElements.length; j++) {
        final IConfigurationElement configurationElement = configElements[j];
        final NodeTransformerType proxy = parseType(configurationElement, found.size());
        if (proxy != null) {
          s_cachedNodeTransformerTypes.put(proxy.getName(), proxy);
        }
      }
    }

    return s_cachedNodeTransformerTypes;
  }

  /**
   * Load a IRM reference.
   * 
   * @return IRM.
   * @throws NodeTransformerException
   *           Unable to load IRM.
   */
  public NodeTransformer loadNodeTransformer() throws NodeTransformerException {

    if (_nodeTransformer != null) {
      return _nodeTransformer;
    }

    final Log log = LogFactory.getLog(NodeTransformerType.class);

    try {
      final Object obj = _configurationElement.createExecutableExtension(ATT_CLASS_NAME);
      _nodeTransformer = (NodeTransformer) obj;
      return _nodeTransformer;
    } catch (final Exception exception) {
      if (log.isErrorEnabled()) {
        if (_configurationElement != null) {
          log
            .error(("Failed to instantiate node transformer: " + _configurationElement.getAttribute(ATT_CLASS_NAME)
              + " in uri: " + _name + " in plugin: " + _configurationElement.getDeclaringExtension()
              .getNamespaceIdentifier()));
        } else {
          log.error("Unknown!");
        }
      }
      throw new NodeTransformerException("unable to load node transformer", exception);
    }
  }

  /**
   * Parse configuration and return according IRMType.
   * 
   * @param configurationElement
   *          Configuration element.
   * @param ordinal
   *          Ordinal.
   * @return NodeTransformerType.
   */
  public static NodeTransformerType parseType(IConfigurationElement configurationElement, int ordinal) {

    if (!configurationElement.getName().equals(TAG_NODE_TRANSFORMER)) {
      return null;
    }

    final Log log = LogFactory.getLog(NodeTransformerType.class);

    try {
      return new NodeTransformerType(configurationElement, ordinal);
    } catch (final Exception e) {
      if (log.isErrorEnabled()) {
        String name = configurationElement.getAttribute(ATT_NAME);
        if (name == null) {
          name = "[missing attribute name]";
        }
        final String msg =
          "Failed to load StrategyType named " + name + " in "
            + configurationElement.getDeclaringExtension().getNamespaceIdentifier();
        log.error(msg, e);
      }
      return null;
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return _name;
  }

  /**
   * @return DParamterDefinition
   * @throws NodeTransformerException -
   */
  public DParameterDefinition getParameterDefinition() throws NodeTransformerException {

    final NodeTransformer nodeTransformer = loadNodeTransformer();

    final InputStream inputStream = nodeTransformer.getParameterDefinition();
    try {
      final Document document = XMLUtils.parse(inputStream, true);
      final DParameterDefinition parameterDefinition =
        DParameterDefinitionCodec.decode(document.getDocumentElement());

      return parameterDefinition;
    } catch (final Exception exception) {
      throw new NodeTransformerException("unable to aquire parameter definition", exception);
    }
  }

}
