/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class EIFActivator implements BundleActivator {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.search";

  /**
   * Whether schemas have been initialized.
   */
  private static boolean schemasInitialized;

  /**
   * Bundle context.
   */
  public static BundleContext s_bundleContext;

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    s_bundleContext = context;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    ; // do nothing
  }

  public synchronized static void registerSchemas() {

    if (schemasInitialized) {
      return;
    }

    final Log log = LogFactory.getLog(EIFActivator.class);

    try {
      final String[] schemas =
        { "ParameterDescriptions.xsd", "ParameterSet.xsd", "ParameterDefinition.xsd", "ErrorMessage.xsd",
          "AnyFinderSearchDateFieldParameter.xsd", "AnyFinderSearchNumberFieldParameter.xsd",
          "AnyFinderSearchTextFieldParameter.xsd", "AnyFinderEngineData.xsd", "SearchParameterObjects.xsd",
          "DataDictionaryConfiguration.xsd", "DataDictionaryConnection.xsd", "IndexStructure.xsd", "Queue.xsd",
          "RapidDeployerAdvancedSearchTemplateFields.xsd", "RapidDeployerIndexStructure.xsd",
          "RecordTransformationDefinition.xsd", "RecordTransformationProcess.xsd", "RecordTransformationSet.xsd",
          "SimpleTypeDefs.xsd", "AnyFinderAdvancedSearch.xsd", "AnyFinderDataDictionary.xsd",
          "AnyFinderSearch.xsd", "FieldTemplates.xsd", "SearchTemplates.xsd", "AnyFinderFieldRequest.xsd",
          "HighlightingTransformerRegistry.xsd", "NodeTransformerRegistry.xsd", "TransformerRegistry.xsd" };

      XMLUtils.clearGrammarCache();
      // read all schemas in defined order
      for (final String schema : schemas) {
        XMLUtils.loadSchema("../xml/" + schema, EIFActivator.s_bundleContext);
      }
      schemasInitialized = true;
    } catch (final Exception exception) {
      if (log.isErrorEnabled()) {
        log.error(exception);
      }
    }
  }

}
