/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.test;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.pipelets.CreateResourcePipelet;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 * Tests for {@link org.eclipse.smila.ontology.pipelets.CreateResourcePipelet}.
 *
 * @author jschumacher
 *
 */
public class TestCreateResourcePipelet extends AOntologyWorkflowTest {
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
   */
  @Override
  protected String getPipelineName() {
    // TODO Auto-generated method stub
    return "CreateResourcePipeline";
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
   * create new resources using the default name property.
   *
   * @throws Exception
   *           test fails
   */
  public void testNewResourceByLabel() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testNewResourceByLabel");
    addString(id, "CountryName", "The Shire");
    addString(id, "CountryName", "Gondor");
    executeWorkflow(id);
    final List<Literal> literals = getBlackboard().getLiterals(id, new Path("CountryUri"));
    assertNotNull(literals);
    assertEquals(2, literals.size());
    assertEquals("urn:TheShire", literals.get(0).getStringValue());
    assertEquals("urn:Gondor", literals.get(1).getStringValue());
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      List<Statement> stmts =
        conn.getStatements(null, RDF.TYPE, conn.getValueFactory().createURI(FACTBOOK_BASEURI + "Country"), false)
          .asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      for (final Statement statement : stmts) {
        assertTrue("urn:TheShire".equals(statement.getSubject().stringValue())
          || "urn:Gondor".equals(statement.getSubject().stringValue()));
      }
      stmts = conn.getStatements(null, RDFS.LABEL, null, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      for (final Statement statement : stmts) {
        if (statement.getSubject().stringValue().equals("urn:TheShire")) {
          assertEquals("The Shire", statement.getObject().stringValue());
        } else if (statement.getSubject().stringValue().equals("urn:Gondor")) {
          assertEquals("Gondor", statement.getObject().stringValue());
        } else {
          fail("unexpected statement: " + statement);
        }
      }
    } finally {
      conn.close();
    }
  }

  /**
   * create new resources using a custom name property.
   *
   * @throws Exception
   *           test fails
   */
  public void testNewResourceByName() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testNewResourceByName");
    addString(id, "CountryName", "The Shire");
    addString(id, "CountryName", "Gondor");
    final Annotation parameters = getBlackboard().createAnnotation(id);
    parameters.setNamedValue(CreateResourcePipelet.PARAM_LABELPREDICATE, "ciafb:Name");
    getBlackboard().addAnnotation(id, null, "parameters", parameters);
    executeWorkflow(id);
    final List<Literal> literals = getBlackboard().getLiterals(id, new Path("CountryUri"));
    assertNotNull(literals);
    assertEquals(2, literals.size());
    assertEquals("urn:TheShire", literals.get(0).getStringValue());
    assertEquals("urn:Gondor", literals.get(1).getStringValue());
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      List<Statement> stmts =
        conn.getStatements(null, RDF.TYPE, conn.getValueFactory().createURI(FACTBOOK_BASEURI + "Country"), false)
          .asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      for (final Statement statement : stmts) {
        assertTrue("urn:TheShire".equals(statement.getSubject().stringValue())
          || "urn:Gondor".equals(statement.getSubject().stringValue()));
      }
      stmts =
        conn.getStatements(null, conn.getValueFactory().createURI(FACTBOOK_BASEURI + "Name"), null, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      for (final Statement statement : stmts) {
        if (statement.getSubject().stringValue().equals("urn:TheShire")) {
          assertEquals("The Shire", statement.getObject().stringValue());
        } else if (statement.getSubject().stringValue().equals("urn:Gondor")) {
          assertEquals("Gondor", statement.getObject().stringValue());
        } else {
          fail("unexpected statement: " + statement);
        }
      }
    } finally {
      conn.close();
    }
  }

  /**
   * find existing resources using the default name property.
   *
   * @throws Exception
   *           test fails
   */
  public void testFindResourceByLabel() throws Exception {
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      final ValueFactory vf = conn.getValueFactory();
      final URI uri1 = vf.createURI("uri:country:" + UUID.randomUUID());
      final URI uri2 = vf.createURI("uri:country:" + UUID.randomUUID());
      final URI type = vf.createURI(FACTBOOK_BASEURI + "Country");
      conn.add(uri1, RDF.TYPE, type);
      conn.add(uri2, RDF.TYPE, type);
      conn.add(uri1, RDFS.LABEL, vf.createLiteral("The Shire"));
      conn.add(uri2, RDFS.LABEL, vf.createLiteral("Gondor"));
      conn.commit();
      final Id id = createBlackboardRecord(getClass().getName(), "testNewResourceByLabel");
      addString(id, "CountryName", "The Shire");
      addString(id, "CountryName", "Gondor");
      executeWorkflow(id);
      logRepositoryExport(conn);
      final List<Literal> literals = getBlackboard().getLiterals(id, new Path("CountryUri"));
      assertNotNull(literals);
      assertEquals(2, literals.size());
      assertEquals(uri1.stringValue(), literals.get(0).getStringValue());
      assertEquals(uri2.stringValue(), literals.get(1).getStringValue());
      final List<Statement> stmts = conn.getStatements(null, RDF.TYPE, type, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      for (final Statement statement : stmts) {
        assertTrue(uri1.equals(statement.getSubject()) || uri2.equals(statement.getSubject()));
      }
    } finally {
      conn.close();
    }
  }

  /**
   * find existing resources using a custom name property.
   *
   * @throws Exception
   *           test fails
   */
  public void testFindResourceByName() throws Exception {
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      final ValueFactory vf = conn.getValueFactory();
      final URI uri1 = vf.createURI("uri:country:" + UUID.randomUUID());
      final URI uri2 = vf.createURI("uri:country:" + UUID.randomUUID());
      final URI type = vf.createURI(FACTBOOK_BASEURI + "Country");
      final URI name = vf.createURI(FACTBOOK_BASEURI + "Name");
      conn.add(uri1, RDF.TYPE, type);
      conn.add(uri2, RDF.TYPE, type);
      conn.add(uri1, name, vf.createLiteral("The Shire"));
      conn.add(uri2, name, vf.createLiteral("Gondor"));
      conn.commit();
      final Id id = createBlackboardRecord(getClass().getName(), "testNewResourceByLabel");
      addString(id, "CountryName", "The Shire");
      addString(id, "CountryName", "Gondor");
      final Annotation parameters = getBlackboard().createAnnotation(id);
      parameters.setNamedValue(CreateResourcePipelet.PARAM_LABELPREDICATE, "ciafb:Name");
      getBlackboard().addAnnotation(id, null, "parameters", parameters);
      executeWorkflow(id);
      logRepositoryExport(conn);
      final List<Literal> literals = getBlackboard().getLiterals(id, new Path("CountryUri"));
      assertNotNull(literals);
      assertEquals(2, literals.size());
      assertEquals(uri1.stringValue(), literals.get(0).getStringValue());
      assertEquals(uri2.stringValue(), literals.get(1).getStringValue());
      final List<Statement> stmts = conn.getStatements(null, RDF.TYPE, type, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      for (final Statement statement : stmts) {
        assertTrue(uri1.equals(statement.getSubject()) || uri2.equals(statement.getSubject()));
      }
    } finally {
      conn.close();
    }
  }

}
