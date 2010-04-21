/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.xml.SchemaResolver;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class CoreSchemaResolver implements SchemaResolver, ICoreSchemaResolver {

  private static final String[] KNOWN_SCHEMAS =
    { "ParameterDescriptions.xsd", "ParameterSet.xsd", "ParameterDefinition.xsd", "ErrorMessage.xsd",
      "AnyFinderEngineData.xsd", "SearchParameterObjects.xsd", "DataDictionaryConfiguration.xsd", "Queue.xsd",
      "RapidDeployerAdvancedSearchTemplateFields.xsd", "RapidDeployerIndexStructure.xsd",
      "RecordTransformationDefinition.xsd", "RecordTransformationProcess.xsd", "RecordTransformationSet.xsd",
      "SimpleTypeDefs.xsd", "AnyFinderDataDictionary.xsd", "AnyFinderSearch.xsd", "FieldTemplates.xsd",
      "SearchTemplates.xsd", "AnyFinderFieldRequest.xsd", "NodeTransformerRegistry.xsd",
      "HighlightingTransformerRegistry.xsd", "TransformerRegistry.xsd" };

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.xml.SchemaResolver#getSchemaByName(java.lang.String)
   */
  public byte[] getSchemaByName(final String schemaName) {

    final Log log = LogFactory.getLog(getClass());

    final Set<String> schemas = new HashSet<String>();
    for (final String schema : KNOWN_SCHEMAS) {
      schemas.add("../xml/" + schema);
    }

    if (!schemas.contains(schemaName)) {
      // not responsible for schema loading.
      return null;
    }

    final File folder = ConfigUtils.getConfigFolder(EIFActivator.BUNDLE_NAME, "xml");
    if (folder == null) {
      if (log.isErrorEnabled()) {
        log.error("unable to locate configuration folder [" + EIFActivator.BUNDLE_NAME + "/xml]");
      }
      return null;
    }

    FileInputStream fis = null;
    final String fileName = folder.getPath() + "/" + schemaName;
    try {
      fis = new FileInputStream(fileName);
      final byte[] schema = IOUtils.toByteArray(fis);
      return schema;
    } catch (final Exception exception) {
      if (log.isErrorEnabled()) {
        log.error("unable to read schema file [" + fileName + "]", exception);
      }
      return null;
    } finally {
      IOUtils.closeQuietly(fis);
    }
  }

}
