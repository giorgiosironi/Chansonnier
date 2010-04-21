/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.ontology.config.HttpStore;
import org.eclipse.smila.ontology.config.MemoryStore;
import org.eclipse.smila.ontology.config.NativeStore;
import org.eclipse.smila.ontology.config.ObjectFactory;
import org.eclipse.smila.ontology.config.RdbmsStore;
import org.eclipse.smila.ontology.config.RepositoryConfig;
import org.eclipse.smila.ontology.config.SesameConfiguration;
import org.eclipse.smila.ontology.config.Stackable;
import org.eclipse.smila.ontology.internal.SesameConfigurationHandler;

/**
 * Test case for reading and writing configuration files for SesameOntologyManager.
 * 
 * @author jschumacher
 * 
 */
public class TestSesameConfig extends TestCase {
  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * test of configuration creation.
   *
   * @throws Exception
   *           test fails
   */
  public void testWrite() throws Exception {
    final SesameConfiguration config = createTestConfig();

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    SesameConfigurationHandler.writeConfiguration(config, out);
    out.close();
    if (_log.isDebugEnabled()) {
      _log.debug(new String(out.toByteArray()));
    }
    final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    final SesameConfiguration readConfig = SesameConfigurationHandler.readConfiguration(in);
    in.close();

    assertNotNull(readConfig);
    assertEquals(config.getDefault(), readConfig.getDefault());

    assertNotNull(readConfig.getRepositoryConfig());
    assertEquals(config.getRepositoryConfig().size(), config.getRepositoryConfig().size());

    for (int i = 0; i < config.getRepositoryConfig().size(); i++) {
      final RepositoryConfig expRepo = config.getRepositoryConfig().get(i);
      final RepositoryConfig readRepo = readConfig.getRepositoryConfig().get(i);
      assertEquals(expRepo.getName(), readRepo.getName());
      assertTrue((expRepo.getNativeStore() == null) == (readRepo.getNativeStore() == null));
      assertTrue((expRepo.getMemoryStore() == null) == (readRepo.getMemoryStore() == null));
      assertTrue((expRepo.getRdbmsStore() == null) == (readRepo.getRdbmsStore() == null));
      assertTrue((expRepo.getHttpStore() == null) == (readRepo.getHttpStore() == null));
      assertEquals(expRepo.getStackable().size(), readRepo.getStackable().size());
    }

    final Iterator<RepositoryConfig> expIter = config.getRepositoryConfig().iterator();
    final Iterator<RepositoryConfig> readIter = readConfig.getRepositoryConfig().iterator();
    final RepositoryConfig expMemRepo = expIter.next();
    final MemoryStore expMem = expMemRepo.getMemoryStore();
    final RepositoryConfig readMemRepo = readIter.next();
    final MemoryStore readMem = readMemRepo.getMemoryStore();
    assertEquals(expMem.isPersist(), readMem.isPersist());
    assertEquals(expMem.getSyncDelay(), readMem.getSyncDelay());
    assertEquals(expMemRepo.getStackable().get(0).getClassname(), readMemRepo.getStackable().get(0).getClassname());
    assertEquals(expMemRepo.getStackable().get(1).getClassname(), readMemRepo.getStackable().get(1).getClassname());

    final NativeStore expNative = expIter.next().getNativeStore();
    final NativeStore readNative = readIter.next().getNativeStore();
    assertEquals(expNative.isForceSync(), readNative.isForceSync());
    assertEquals(expNative.getIndexes(), readNative.getIndexes());

    final RdbmsStore expDb = expIter.next().getRdbmsStore();
    final RdbmsStore readDb = readIter.next().getRdbmsStore();
    assertEquals(expDb.getDriver(), readDb.getDriver());
    assertEquals(expDb.getUrl(), readDb.getUrl());
    assertEquals(expDb.getUser(), readDb.getUser());
    assertEquals(expDb.getPassword(), readDb.getPassword());
    assertEquals(expDb.isIndexed(), readDb.isIndexed());
    assertEquals(expDb.isSequenced(), readDb.isSequenced());
    assertEquals(expDb.getMaxTripleTables(), readDb.getMaxTripleTables());

    final HttpStore expHttp = expIter.next().getHttpStore();
    final HttpStore readHttp = readIter.next().getHttpStore();
    assertEquals(expHttp.getRepositoryId(), readHttp.getRepositoryId());
    assertEquals(expHttp.getUrl(), readHttp.getUrl());
    assertEquals(expHttp.getUser(), readHttp.getUser());
    assertEquals(expHttp.getPassword(), readHttp.getPassword());
  }

  /**
   *
   * @return test configuration
   */
  private SesameConfiguration createTestConfig() {
    final ObjectFactory factory = new ObjectFactory();
    final SesameConfiguration config = factory.createSesameConfiguration();
    config.setDefault("memory");

    RepositoryConfig repo = factory.createRepositoryConfig();
    repo.setName("memory");
    config.getRepositoryConfig().add(repo);
    final MemoryStore memStore = factory.createMemoryStore();
    repo.setMemoryStore(memStore);
    memStore.setPersist(false);
    memStore.setSyncDelay(-1);
    Stackable stackable = factory.createStackable();
    stackable.setClassname("org.openrdf.sail.inferencer.fc.DirectTypeHierarchyInferencer");
    repo.getStackable().add(stackable);
    stackable = factory.createStackable();
    stackable.setClassname("org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer");
    repo.getStackable().add(stackable);

    repo = factory.createRepositoryConfig();
    repo.setName("native");
    config.getRepositoryConfig().add(repo);
    final NativeStore natStore = factory.createNativeStore();
    repo.setNativeStore(natStore);
    natStore.setForceSync(false);
    natStore.setIndexes("spoc");

    repo = factory.createRepositoryConfig();
    repo.setName("db");
    config.getRepositoryConfig().add(repo);
    final RdbmsStore dbStore = factory.createRdbmsStore();
    repo.setRdbmsStore(dbStore);
    dbStore.setDriver("org.db.Driver");
    dbStore.setMaxTripleTables(2);
    dbStore.setIndexed(false);
    dbStore.setSequenced(false);
    dbStore.setUrl("jdbc:url");
    dbStore.setUser("homer");
    dbStore.setPassword("d'oh!");

    repo = factory.createRepositoryConfig();
    repo.setName("http");
    config.getRepositoryConfig().add(repo);
    final HttpStore httpStore = factory.createHttpStore();
    repo.setHttpStore(httpStore);
    httpStore.setRepositoryId("farfaraway");
    httpStore.setUrl("http://ontology:8080");
    httpStore.setUser("lisa");
    httpStore.setPassword("simpson");
    return config;
  }
}
