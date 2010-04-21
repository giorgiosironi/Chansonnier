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
package org.eclipse.smila.datamodel.id;

/**
 * Helper class to decouple Id interfaces better from default implementation. This makes r_osgi work better (-;
 * 
 * TODO: Make this configurable? Then it would make sense to have such a class.
 * 
 * @author jschumacher
 * 
 */
class IdFactoryCreator {
  /**
   * Class name of default RecordFactory.
   */
  private static final String DEFAULT_IDFACTORY_CLASSNAME =
    "org.eclipse.smila.datamodel.id.impl.DefaultIdFactoryImpl";

  /**
   * @return instance of the default RecordFactory.
   */
  public static IdFactory createDefaultFactory() {
    try {
      return (IdFactory) Class.forName(DEFAULT_IDFACTORY_CLASSNAME).newInstance();
    } catch (Throwable ex) {
      System.out.println("FATAL ERROR: Could not create DefaultIdFactory - nothing will work probably.");
      return null;
    }
  }
}
