/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.search.highlighting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchProcessingService;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DHighlightingTransformer;
import org.eclipse.smila.search.highlighting.transformer.HighlightingTransformer;
import org.eclipse.smila.search.highlighting.transformer.HighlightingTransformerException;
import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.set.DBoolean;
import org.eclipse.smila.search.utils.param.set.DInteger;
import org.eclipse.smila.search.utils.param.set.DParameterSet;
import org.eclipse.smila.search.utils.param.set.DString;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * HighlightingService using HighlightingTransformer services.
 */
public class HighlightingService implements SearchProcessingService {

  /**
   * name of OSGi service reference to highlightingTransformer.
   */
  public static final String HIGHLIGHTING_TRANSFORMER_REFERENCE = "highlightingTransformer";

  /**
   * Constant for the OSGi property smila.highlighting.transformer.type.
   */
  public static final String PROPERTY_HIGHLIGHTING_TRANSFORMER_TYPE = "smila.highlighting.transformer.type";

  /**
   * Constant for the SearchAnnotation HighlightingTransformer.
   */
  public static final String HIGHLIGHTING_TRANSFORMER = "HighlightingTransformer";

  /**
   * Constant for the sub annotation "name" of annotation HighlightingTransformer.
   */
  public static final String HIGHLIGHTING_PARAMETER_NAME = "name";

  /**
   * Constant for the property name "value" of sub annotation "name".
   */
  public static final String HIGHLIGHTING_PARAMETER_VALUE = "value";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(HighlightingService.class);

  /**
   * Reference to the ComponentContext.
   */
  private ComponentContext _componentContext;

  /**
   * Map of ServiceReference to HighlightingTransformer.
   */
  private HashMap<String, ServiceReference> _highlightingTransformer;

  /**
   * DS activate method.
   * 
   * @param context
   *          ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void activate(final ComponentContext context) throws Exception {
    _componentContext = context;
    if (_log.isTraceEnabled()) {
      _log.trace("activating HighlightingService");
    }
  }

  /**
   * DS deactivate method.
   * 
   * @param context
   *          the ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void deactivate(final ComponentContext context) throws Exception {
    if (_highlightingTransformer != null) {
      _highlightingTransformer.clear();
      _highlightingTransformer = null;
    }
    _componentContext = null;
    if (_log.isTraceEnabled()) {
      _log.trace("deactivating HighlightingService");
    }
  }

  /**
   * add a search processing service as an OSGi service reference. To be used by Declarative Services as the bind
   * method.
   * 
   * @param serviceReference
   *          service reference to add.
   */
  protected void setHighlightingTransformer(final ServiceReference serviceReference) {
    if (_highlightingTransformer == null) {
      _highlightingTransformer = new HashMap<String, ServiceReference>();
    }

    if (serviceReference != null) {
      final String type = serviceReference.getProperty(PROPERTY_HIGHLIGHTING_TRANSFORMER_TYPE).toString();
      if (type == null) {
        _log
          .error("Cannot use highlighting transformer without property " + PROPERTY_HIGHLIGHTING_TRANSFORMER_TYPE);
      } else {
        _highlightingTransformer.put(type, serviceReference);
        if (_log.isInfoEnabled()) {
          _log.info("Registered highlighting transformer " + type);
        }
      }
    } // if
  }

  /**
   * remove a search processing service. To be used by Declarative Services as the unbind method.
   * 
   * @param serviceReference
   *          service reference to remove.
   */
  protected void unsetHighlightingTransformer(final ServiceReference serviceReference) {
    if (_highlightingTransformer != null && serviceReference != null) {
      final String type = serviceReference.getProperty(PROPERTY_HIGHLIGHTING_TRANSFORMER_TYPE).toString();
      if (type != null) {
        _highlightingTransformer.remove(type);
        if (_log.isInfoEnabled()) {
          _log.info("Unregistered highlighting transformer " + type);
        }
      } // if
    } // if
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SearchProcessingService#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.processing.SearchMessage)
   */
  public SearchMessage process(final Blackboard blackboard, final SearchMessage message) throws ProcessingException {
    if (message.hasQuery() && message.hasRecords()) {
      try {
        // get any highlighting configurations from the query
        final Map<String, DHighlightingTransformer> highlightConfigs =
          getHighlightingTransforrmerConfigs(blackboard, message.getQuery());

        // check if any highlighting configurations exist
        if (!highlightConfigs.isEmpty()) {
          // do highlighting for each result record
          for (final Id id : message.getRecords()) {
            doHighlighting(blackboard, id, highlightConfigs);
          } // for
        } // if
      } catch (final Exception ex) {
        if (_log.isErrorEnabled()) {
          _log.error("error processing message " + message.getQuery(), ex);
        }
      }
    }
    return message;
  }

  /**
   * Checks if the query contains any Highlight annotations on attributes and converts them into
   * DHighlightingTransformer configuration objects.
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the Id of the query record
   * @return a Map of attribute names to DHighlightingTransformer configurations
   * @throws BlackboardAccessException
   *           if any record access error occurs
   */
  private Map<String, DHighlightingTransformer> getHighlightingTransforrmerConfigs(final Blackboard blackboard,
    final Id id) throws BlackboardAccessException {
    final HashMap<String, DHighlightingTransformer> highlightConfigs =
      new HashMap<String, DHighlightingTransformer>();

    final Iterator<String> attributeNames = blackboard.getAttributeNames(id);
    while (attributeNames.hasNext()) {
      final String attributeName = attributeNames.next();
      final Path path = new Path().add(attributeName, PathStep.ATTRIBUTE_ANNOTATION);
      if (blackboard.hasAnnotation(id, path, SearchAnnotations.HIGHLIGHT)) {
        final Annotation highlight = blackboard.getAnnotation(id, path, SearchAnnotations.HIGHLIGHT);
        final DHighlightingTransformer highlightConfig = createHighlightConfig(highlight);
        if (highlightConfig != null) {
          highlightConfigs.put(attributeName, highlightConfig);
        }
      }
    }
    return highlightConfigs;
  }

  /**
   * Executes highlighting for the record with the given Id using the given highlightConfigs.
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the Id of the record
   * @param highlightConfigs
   *          the highlightConfigs
   * @throws BlackboardAccessException
   *           if any record access error occurs
   * @throws HighlightingTransformerException
   *           if any highlight transformation error occurs
   * @throws ParameterException
   *           if any highlight configuration error occurs
   */
  private void doHighlighting(final Blackboard blackboard, final Id id,
    final Map<String, DHighlightingTransformer> highlightConfigs) throws BlackboardAccessException,
    HighlightingTransformerException, ParameterException {
    final Iterator<String> attributeNames = highlightConfigs.keySet().iterator();
    while (attributeNames.hasNext()) {
      final String attributeName = attributeNames.next();
      final Path path = new Path().add(attributeName, PathStep.ATTRIBUTE_ANNOTATION);
      if (blackboard.hasAnnotation(id, path, SearchAnnotations.HIGHLIGHT)) {
        final Annotation highlight = blackboard.getAnnotation(id, path, SearchAnnotations.HIGHLIGHT);
        final DHighlightingTransformer highlightConfig = highlightConfigs.get(attributeName);

        HighlightingTransformer transformer = null;
        if (_highlightingTransformer != null) {
          transformer =
            (HighlightingTransformer) _componentContext.locateService(HIGHLIGHTING_TRANSFORMER_REFERENCE,
              _highlightingTransformer.get(highlightConfig.getName()));
        }

        if (transformer != null) {
          blackboard.setAnnotation(id, path, SearchAnnotations.HIGHLIGHT, transformer.transform(highlight,
            highlightConfig.getParameterSet()));
        } else if (_log.isWarnEnabled()) {
          _log.warn("Could not find a HighlightingTransformer with name " + highlightConfig.getName());
        }
      } // if
    } // while
  }

  /**
   * Creates a DHighlightingTransformer from a given "highlight" annotation.
   * 
   * @param highlight
   *          the highlight annotation
   * @return the DHighlightingTransformer or null
   */
  private DHighlightingTransformer createHighlightConfig(final Annotation highlight) {
    if (highlight.hasAnnotation(HIGHLIGHTING_TRANSFORMER)) {
      final Annotation highlightingTransformer = highlight.getAnnotation(HIGHLIGHTING_TRANSFORMER);
      final DHighlightingTransformer config = new DHighlightingTransformer();
      config.setName(highlightingTransformer.getNamedValue(HIGHLIGHTING_PARAMETER_NAME));
      final DParameterSet parameterSet = createParameterSet(highlightingTransformer);
      config.setParameterSet(parameterSet);
      return config;
    }
    return null;
  }

  /**
   * Creates a DParameterSet from a given "HighlightingTransformer" annotation.
   * 
   * @param highlightingTransformer
   *          the HighlightingTransformer annotation
   * @return a DParameterSet
   */
  private DParameterSet createParameterSet(final Annotation highlightingTransformer) {
    final DParameterSet parameterSet = new DParameterSet();
    final Iterator<String> paramNames = highlightingTransformer.getAnnotationNames();
    while (paramNames.hasNext()) {
      final String paramName = paramNames.next();
      final Annotation parameter = highlightingTransformer.getAnnotation(paramName);
      final String value = parameter.getNamedValue(HIGHLIGHTING_PARAMETER_VALUE);

      if ("MarkupPrefix".equals(paramName)) {
        parameterSet.addParameter(createDString(paramName, value));
      } else if ("MarkupSuffix".equals(paramName)) {
        parameterSet.addParameter(createDString(paramName, value));
      } else if ("MaxLength".equals(paramName)) {
        parameterSet.addParameter(createDInteger(paramName, Integer.parseInt(value)));
      } else if ("MaxHLElements".equals(paramName)) {
        parameterSet.addParameter(createDInteger(paramName, Integer.parseInt(value)));
      } else if ("MaxPrecedingCharacters".equals(paramName)) {
        parameterSet.addParameter(createDInteger(paramName, Integer.parseInt(value)));
      } else if ("MaxSucceedingCharacters".equals(paramName)) {
        parameterSet.addParameter(createDInteger(paramName, Integer.parseInt(value)));
      } else if ("PrecedingCharacters".equals(paramName)) {
        parameterSet.addParameter(createDString(paramName, value));
      } else if ("SucceedingCharacters".equals(paramName)) {
        parameterSet.addParameter(createDString(paramName, value));
      } else if ("SortAlgorithm".equals(paramName)) {
        parameterSet.addParameter(createDString(paramName, value));
      } else if ("TextHandling".equals(paramName)) {
        parameterSet.addParameter(createDString(paramName, value));
      } else if ("HLElementFilter".equals(paramName)) {
        parameterSet.addParameter(createDBoolean(paramName, Boolean.parseBoolean(value)));
      } else {
        if (_log.isWarnEnabled()) {
          _log.warn("unknown HighlightingTransformer parameter name " + paramName);
        }
      }
    }
    return parameterSet;
  }

  /**
   * Creates a DInteger parameter.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   * @return a DInteger
   */
  private DInteger createDInteger(final String name, final int value) {
    final DInteger param = new DInteger();
    param.setName(name);
    param.setType("Integer");
    param.setValue(value);
    return param;
  }

  /**
   * Creates a DString parameter.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   * @return a DString
   */
  private DString createDString(final String name, final String value) {
    final DString param = new DString();
    param.setName(name);
    param.setType("String");
    param.setValue(value);
    return param;
  }

  /**
   * Creates a DBoolean parameter.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   * @return a DBoolean
   */
  private DBoolean createDBoolean(final String name, final boolean value) {
    final DBoolean param = new DBoolean();
    param.setName(name);
    param.setType("Boolean");
    param.setValue(value);
    return param;
  }
}
