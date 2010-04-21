/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.test;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.pipelets.CreateRelationPipelet;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 * Test for {@link org.eclipse.smila.ontology.pipelets.CreateRelationPipelet}.
 *
 * @author jschumacher
 *
 */
public class TestCreateRelationPipelet extends AOntologyWorkflowTest {
  /**
   * base uri for test RDFs.
   */
  public static final String FACTBOOK_BASEURI = "http://www.cia.gov/cia/publications/factbook#";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest#getPipelineName()
   */
  @Override
  protected String getPipelineName() {
    return "CreateRelationPipeline";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.ontology.test.AOntologyWorkflowTest#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    conn.clear();
    conn.setNamespace("ciafb", FACTBOOK_BASEURI);
    _log.info("Loading schema file ...");
    final InputStream file = ConfigUtils.getConfigStream(SesameOntologyManager.BUNDLE_ID, "CIA-onto-enhanced.rdf");
    conn.add(file, FACTBOOK_BASEURI, RDFFormat.RDFXML);
    file.close();
    conn.commit();
    _log.info("... done");
    _log.info("Checking namespaces:");
    final RepositoryResult<Namespace> namespaces = conn.getNamespaces();
    for (final Namespace ns : namespaces.asList()) {
      _log.info("Namespace " + ns.getPrefix() + " is " + ns.getName());
    }
    conn.close();
  }

  /**
   * test single relation.
   *
   * @throws Exception
   *           test fails
   */
  public void testSingleRelation() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testSingleRelation");
    addResource(id, "Country1", "urn:Gondor");
    addResource(id, "Country2", "urn:Mordor");
    executeWorkflow(id);
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      final List<Statement> stmts =
        conn.getStatements(null, conn.getValueFactory().createURI(FACTBOOK_BASEURI + "Bordering_country"), null,
          false).asList();
      assertNotNull(stmts);
      assertEquals(1, stmts.size());
      final Statement stmt = stmts.get(0);
      assertEquals("urn:Gondor", stmt.getSubject().stringValue());
      assertTrue(stmt.getObject() instanceof URI);
      assertEquals("urn:Mordor", stmt.getObject().stringValue());
    } finally {
      conn.close();
    }
  }

  /**
   * test single relation.
   *
   * @throws Exception
   *           test fails
   */
  public void testLiteralRelation() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testSingleRelation");
    addResource(id, "Country1", "urn:Gondor");
    addString(id, "Name", "Gondor");
    final Annotation parameters = getBlackboard().createAnnotation(id);
    parameters.setNamedValue(CreateRelationPipelet.PARAM_OBJECTATTRIBUTE, "Name");
    parameters.setNamedValue(CreateRelationPipelet.PARAM_PREDICATEURI, "ciafb:Name");
    getBlackboard().addAnnotation(id, null, "parameters", parameters);
    executeWorkflow(id);
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      final List<Statement> stmts =
        conn.getStatements(null, conn.getValueFactory().createURI(FACTBOOK_BASEURI + "Name"), null, false).asList();
      assertNotNull(stmts);
      assertEquals(1, stmts.size());
      final Statement stmt = stmts.get(0);
      assertEquals("urn:Gondor", stmt.getSubject().stringValue());
      assertTrue(stmt.getObject() instanceof Literal);
      assertEquals("Gondor", stmt.getObject().stringValue());
    } finally {
      conn.close();
    }

  }

  /**
   * test multiple relations.
   *
   * @throws Exception
   *           test fails
   */
  public void testMultipleRelations() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testMultipleRelations");
    addResource(id, "Country1", "urn:Gondor");
    addResource(id, "Country1", "urn:Rohan");
    addResource(id, "Country2", "urn:Mordor");
    addResource(id, "Country2", "urn:Wilderland");
    executeWorkflow(id);
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      logRepositoryExport(conn);
      final List<Statement> stmts =
        conn.getStatements(null, conn.getValueFactory().createURI(FACTBOOK_BASEURI + "Bordering_country"), null,
          false).asList();
      assertNotNull(stmts);
      final int expectedNoOfStatements = 4;
      assertEquals(expectedNoOfStatements, stmts.size());
      for (final Statement stmt : stmts) {
        assertTrue("urn:Gondor".equals(stmt.getSubject().stringValue())
          || "urn:Rohan".equals(stmt.getSubject().stringValue()));
        assertTrue(stmt.getObject() instanceof URI);
        assertTrue("urn:Mordor".equals(stmt.getObject().stringValue())
          || "urn:Wilderland".equals(stmt.getObject().stringValue()));
      }
    } finally {
      conn.close();
    }
  }

}
