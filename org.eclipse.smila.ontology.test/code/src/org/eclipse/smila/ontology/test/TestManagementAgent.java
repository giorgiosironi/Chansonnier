/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;
import org.eclipse.smila.ontology.internal.SesameOntologyAgent;

/**
 * test functions of sesame management agent.
 * 
 * @author jschumacher
 * 
 */
public class TestManagementAgent extends TestCase {
  /**
   * sesame management agent.
   */
  private SesameOntologyAgent _agent;

  /**
   * get management agent.
   *
   * @throws Exception
   *           access fails
   */
  @Override
  protected void setUp() throws Exception {
    final ManagementAgentLocation agentLocation =
      ManagementRegistration.INSTANCE.getCategory("Ontology").getLocation("Sesame");
    _agent = (SesameOntologyAgent) ManagementRegistration.INSTANCE.getAgent(agentLocation);
    assertNotNull(_agent);
  }

  /**
   * check getRepositoryNames().
   *
   * @throws Exception
   *           test fails.
   */
  public void testGetRepositoryNames() throws Exception {
    final List<String> names = _agent.getRepositoryNames();
    assertNotNull(names);
    final Iterator<String> iter = names.iterator();
    assertEquals("memory", iter.next());
    assertEquals("native", iter.next());
    assertEquals("remote", iter.next());
    assertEquals("database", iter.next());
    assertFalse(iter.hasNext());
  }

  /**
   * check access to undefined repository.
   *
   * @throws Exception
   *           test fails
   */
  public void testUndefinedRepository() throws Exception {
    assertTrue(_agent.getSize("foobar") < 0);
    assertTrue(_agent.getNamespaces("foobar").isEmpty());
    assertTrue(_agent.getContexts("foobar").isEmpty());
    assertTrue(_agent.clear("foobar").indexOf("Error:") >= 0);
    assertTrue(_agent.importRDF("foobar", "no.file", "no:uri").indexOf("Error:") >= 0);
    assertTrue(_agent.exportRDF("foobar", "no.file").indexOf("Error:") >= 0);

  }

  /**
   * test control of a repository.
   *
   * @throws Exception
   *           test fails.
   */
  public void testRepositoryControl() throws Exception {
    assertTrue("clear failed", _agent.clear("memory").indexOf("Error:") < 0);
    assertTrue("importRDF failed", _agent.importRDF("memory",
      "configuration/org.eclipse.smila.ontology/CIA-onto-enhanced.rdf", TestRepositoryAccess.FACTBOOK_BASEURI)
      .indexOf("Error:") < 0);
    assertTrue(_agent.getSize("memory") > 0);
    assertTrue(_agent.getContexts("memory").isEmpty());
    assertFalse(_agent.getNamespaces("memory").isEmpty());
    final File temp1 = File.createTempFile("smila-ontology-export-test-", ".n3");
    temp1.deleteOnExit();
    assertTrue("exportRDF failed", _agent.exportRDF("memory", temp1.getAbsolutePath()).indexOf("Error:") < 0);
    assertTrue("export file does not exist", temp1.exists());
    assertTrue("export file too short", temp1.length() > 0);
    final File temp2 = File.createTempFile("smila-ontology-export-test-", ".rdf");
    temp2.deleteOnExit();
    assertTrue("exportRDF failed", _agent.exportRDF("memory", temp2.getAbsolutePath()).indexOf("Error:") < 0);
    assertTrue("export file does not exist", temp2.exists());
    assertTrue("export file too short", temp2.length() > 0);
    assertTrue("clear failed", _agent.clear("memory").indexOf("Error:") < 0);
    assertEquals(0, _agent.getSize("memory"));
  }
}
