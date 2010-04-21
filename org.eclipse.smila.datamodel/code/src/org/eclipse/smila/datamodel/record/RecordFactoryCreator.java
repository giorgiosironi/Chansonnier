/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.datamodel.record;

/**
 * Helper class to decouple record interfaces better from default implementation. This makes r_osgi work better (-;
 * 
 * TODO: Make this configurable? Then it would make sense to have such a class.
 * 
 * @author jschumacher
 * 
 */
class RecordFactoryCreator {
  /**
   * Class name of default RecordFactory.
   */
  private static final String DEFAULT_RECORDFACTORY_CLASSNAME =
    "org.eclipse.smila.datamodel.record.impl.DefaultRecordFactoryImpl";

  /**
   * @return instance of the default RecordFactory.
   */
  public static RecordFactory createDefaultFactory() {
    try {
      return (RecordFactory) Class.forName(DEFAULT_RECORDFACTORY_CLASSNAME).newInstance();
    } catch (Throwable ex) {
      System.out.println("FATAL ERROR: Could not create DefaultRecordFactory - nothing will work probably.");
      return null;
    }
  }
}
