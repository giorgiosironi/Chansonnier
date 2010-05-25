/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.utils.xml;

/**
 * Schema resolver interface.
 * 
 * @author August Georg Schmidt (BROX)
 */
public interface SchemaResolver {

  /**
   * @param schemaName
   *          Schema name.
   * @return Schema or null if not responsible for schema resolution.
   */
  byte[] getSchemaByName(String schemaName);

}
