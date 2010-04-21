/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io.test;

import junit.framework.TestCase;

import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.config.ObjectFactory;

/**
 * The Class TestBinaryStorageConfiguration.
 * 
 * @author Alexander Eliseyev
 */
public class TestBinaryStorageConfiguration extends TestCase {

  /**
   * Test binary storage configuration.
   * 
   * @throws Exception
   *           the exception
   */
  public void testBinaryStorageConfiguration() throws Exception {
    final ObjectFactory configurationFactory = new ObjectFactory();    
    final BinaryStorageConfiguration configuration = configurationFactory.createBinaryStorageConfiguration();
    
    assertNotNull(configuration);
    
    configuration.setImplementationClass("org.eclipse.smila.binarystorage.SomeImpl");
    configuration.setMountPoint("mountPoint");
    configuration.setName("name");
    configuration.setProvider("provider");
    configuration.setTempFileName("tempFileName");
    configuration.setPath("path");
    configuration.setUser("user");
    configuration.setTempPath("tempPath");
    configuration.setPathDepth(new Integer(1));
    
    assertEquals("org.eclipse.smila.binarystorage.SomeImpl", configuration.getImplementationClass());
    assertEquals("mountPoint", configuration.getMountPoint());
    assertEquals("name", configuration.getName());
    assertEquals("provider", configuration.getProvider());
    assertEquals("tempFileName", configuration.getTempFileName());
    assertEquals("path", configuration.getPath());
    assertEquals("user", configuration.getUser());
    assertEquals("tempPath", configuration.getTempPath());
    assertEquals(new Integer(1), configuration.getPathDepth());
  }

}
