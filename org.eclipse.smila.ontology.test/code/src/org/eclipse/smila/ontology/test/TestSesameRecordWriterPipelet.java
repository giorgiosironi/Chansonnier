/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.test;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.ontology.records.SesameRecordHelper;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * basic test for access to Sesame repositories via SesameRecordWriterPipelet.
 *
 * @author jschumacher
 *
 */
public class TestSesameRecordWriterPipelet extends AOntologyWorkflowTest {

  /**
   * base uri for test RDFs.
   */
  private static final String FACTBOOK_BASEURI = "http://www.cia.gov/cia/publications/factbook#";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    prepareRepository("native");
    prepareRepository("memory");
  }

  /**
   * clear repository and declare rdf and ciafb namespace.
   *
   * @param name
   *          repository name
   * @throws RepositoryException
   *           preparation fails
   */
  private void prepareRepository(final String name) throws RepositoryException {
    _log.info("Clearing repository [" + name + "] ...");
    final RepositoryConnection conn = _ontology.getConnection(name);
    conn.clear();
    conn.setNamespace("rdf", RDF.NAMESPACE);
    conn.setNamespace("ciafb", FACTBOOK_BASEURI);
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
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest#getPipelineName()
   */
  @Override
  protected String getPipelineName() {
    return "SesameWritePipeline";
  }

  /**
   * write a record to a resource in sesame identified by the key value of the SMILA id.
   *
   * @throws Exception
   *           test fails
   */
  public void testWriteWithID() throws Exception {
    final Id id =
      createBlackboardRecord(getClass().getName(), "http://www.odci.gov/cia/publications/factbook/geos/shire.html");

    addResource(id, SesameRecordHelper.ATTRIBUTE_TYPE, FACTBOOK_BASEURI + "Country");
    final Literal enName = addString(id, "ciafb:Name", "The Shire");
    SesameRecordHelper.setLanguage(enName, "en");
    final Literal deName = addString(id, "ciafb:Name", "Auenland");
    SesameRecordHelper.setLanguage(deName, "de");
    addResource(id, "ciafb:Flag", "http://www.odci.gov/cia/publications/factbook/flags/shire-lgflag.jpg");
    addResource(id, "ciafb:Map", "http://www.odci.gov/cia/publications/factbook/flags/shire-map.jpg");
    addString(id, "ciafb:Location", "Northwest of Middle Earth");
    addString(id, "ciafb:Bordering_country", "Breeland");
    addString(id, "ciafb:Bordering_country", "Minhiriath");
    addString(id, "ciafb:Natural_resources", "Tobacco");
    addString(id, "ciafb:Natural_resources", "Mushroom");

    final Id[] result = getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { id });
    assertNotNull(result);
    assertEquals(1, result.length);
    assertEquals(id, result[0]);

    RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      logRepositoryExport(conn);
      checkShireStatements(conn);
    } finally {
      conn.close();
    }

    conn = _ontology.getConnection("memory");
    try {
      logRepositoryExport(conn);
      checkShireStatements(conn);
    } finally {
      conn.close();
    }
  }

  /**
   * check statements of ID test.
   *
   * @param conn
   *          repository to test
   * @throws RepositoryException
   *           test fails
   */
  private void checkShireStatements(final RepositoryConnection conn) throws RepositoryException {
    final URI subject =
      conn.getValueFactory().createURI("http://www.odci.gov/cia/publications/factbook/geos/shire.html");
    final RepositoryResult<Statement> stmts = conn.getStatements(subject, null, null, false);
    assertNotNull(stmts);
    int count = 0;
    while (stmts.hasNext()) {
      final Statement stmt = stmts.next();
      final String predicate = stmt.getPredicate().stringValue();
      final String object = stmt.getObject().stringValue();
      count++;
      if (predicate.equals(RDF.TYPE.stringValue())) {
        assertEquals(FACTBOOK_BASEURI + "Country", object);
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Name")) {
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
        final String language = ((org.openrdf.model.Literal) stmt.getObject()).getLanguage();
        assertNotNull(language);
        if ("de".equals(language)) {
          assertEquals("Auenland", object);
        } else if ("en".equals(language)) {
          assertEquals("The Shire", object);
        } else {
          fail("unknown language " + language);
        }
      } else if (predicate.equals(FACTBOOK_BASEURI + "Location")) {
        assertEquals("Northwest of Middle Earth", object);
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Map")) {
        assertEquals("http://www.odci.gov/cia/publications/factbook/flags/shire-map.jpg", object);
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Flag")) {
        assertEquals("http://www.odci.gov/cia/publications/factbook/flags/shire-lgflag.jpg", object);
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Bordering_country")) {
        assertTrue("Breeland".equals(object) || "Minhiriath".equals(object));
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Natural_resources")) {
        assertTrue("Mushroom".equals(object) || "Tobacco".equals(object));
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
      } else {
        fail("predicate " + predicate + " should not be set.");
      }
    }
    final int expectedStatementCount = 10;
    assertEquals(expectedStatementCount, count);
  }

  /**
   * write a record to a resource in sesame identified by the key value of atribute rdf:about.
   *
   * @throws Exception
   *           test fails
   */
  public void testWriteWithAttribute() throws Exception {
    final Id id = createURIAttributeRecord("http://www.odci.gov/cia/publications/factbook/geos/mordor.html");

    addResource(id, SesameRecordHelper.ATTRIBUTE_TYPE, FACTBOOK_BASEURI + "Country");
    addString(id, "ciafb:Name", "Mordor");
    addResource(id, "ciafb:Flag", "http://www.odci.gov/cia/publications/factbook/flags/mordor-lgflag.jpg");
    addResource(id, "ciafb:Map", "http://www.odci.gov/cia/publications/factbook/flags/mordor-map.jpg");
    addString(id, "ciafb:Location", "Southeast of Middle Earth");
    addResource(id, "ciafb:Bordering_country", "http://www.odci.gov/cia/publications/factbook/geos/gondor");
    addResource(id, "ciafb:Bordering_country", "http://www.odci.gov/cia/publications/factbook/geos/wilderland");
    addString(id, "ciafb:Natural_resources", "Smoke");
    addString(id, "ciafb:Natural_resources", "Fire");

    final Id[] result = getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { id });
    assertNotNull(result);
    assertEquals(1, result.length);
    assertEquals(id, result[0]);

    RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      logRepositoryExport(conn);
      checkMordorStatements(conn);
    } finally {
      conn.close();
    }

    conn = _ontology.getConnection("memory");
    try {
      logRepositoryExport(conn);
      checkMordorStatements(conn);
    } finally {
      conn.close();
    }
  }

  /**
   * check statements of uri attribute test.
   *
   * @param conn
   *          repository to test
   * @throws RepositoryException
   *           test fails
   */
  private void checkMordorStatements(final RepositoryConnection conn) throws RepositoryException {
    final URI subject =
      conn.getValueFactory().createURI("http://www.odci.gov/cia/publications/factbook/geos/mordor.html");
    final RepositoryResult<Statement> stmts = conn.getStatements(subject, null, null, false);
    assertNotNull(stmts);
    int count = 0;
    while (stmts.hasNext()) {
      final Statement stmt = stmts.next();
      final String predicate = stmt.getPredicate().stringValue();
      final String object = stmt.getObject().stringValue();
      count++;
      if (predicate.equals(RDF.TYPE.stringValue())) {
        assertEquals(FACTBOOK_BASEURI + "Country", object);
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Name")) {
        assertEquals("Mordor", object);
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Location")) {
        assertEquals("Southeast of Middle Earth", object);
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Map")) {
        assertEquals("http://www.odci.gov/cia/publications/factbook/flags/mordor-map.jpg", object);
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Flag")) {
        assertEquals("http://www.odci.gov/cia/publications/factbook/flags/mordor-lgflag.jpg", object);
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Bordering_country")) {
        assertTrue("http://www.odci.gov/cia/publications/factbook/geos/gondor".equals(object)
          || "http://www.odci.gov/cia/publications/factbook/geos/wilderland".equals(object));
        assertTrue(stmt.getObject() instanceof URI);
      } else if (predicate.equals(FACTBOOK_BASEURI + "Natural_resources")) {
        assertTrue("Smoke".equals(object) || "Fire".equals(object));
        assertTrue(stmt.getObject() instanceof org.openrdf.model.Literal);
      } else {
        fail("predicate " + predicate + " should not be set.");
      }
    }
    final int expectedStatementCount = 9;
    assertEquals(expectedStatementCount, count);
  }

  /**
   * test complete removal of a resource.
   *
   * @throws Exception
   *           test fails
   */
  public void testClearResource() throws Exception {
    final Id id =
      createBlackboardRecord(getClass().getName(),
        "http://www.odci.gov/cia/publications/factbook/geos/wilderland.html");
    SesameRecordHelper.addClearFlag(getBlackboard().getRecord(id).getMetadata());
    logRecord(id);
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      final ValueFactory vf = conn.getValueFactory();
      final URI uri = vf.createURI(id.getKey().getKey());
      conn.add(uri, RDF.TYPE, vf.createURI(FACTBOOK_BASEURI + "Country"));
      conn.add(uri, RDFS.LABEL, vf.createLiteral("Wilderland"));
      conn.add(vf.createURI("http://www.odci.gov/cia/publications/factbook/geos/gondor"), vf
        .createURI(FACTBOOK_BASEURI + "Bordering_country"), uri);
      conn.commit();
      List<Statement> stmts = conn.getStatements(uri, null, null, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      stmts = conn.getStatements(null, null, uri, false).asList();
      assertNotNull(stmts);
      assertEquals(1, stmts.size());
      executeWorkflow(id);
      stmts = conn.getStatements(uri, null, null, false).asList();
      assertNotNull(stmts);
      assertEquals(0, stmts.size());
      stmts = conn.getStatements(null, null, uri, false).asList();
      assertNotNull(stmts);
      assertEquals(0, stmts.size());
    } finally {
      conn.close();
    }
  }

  /**
   * test complete removal of a resource.
   *
   * @throws Exception
   *           test fails
   */
  public void testClearProperty() throws Exception {
    final Id id =
      createBlackboardRecord(getClass().getName(),
        "http://www.odci.gov/cia/publications/factbook/geos/mirkwood.html");
    addResource(id, SesameRecordHelper.ATTRIBUTE_TYPE, FACTBOOK_BASEURI + "Country");
    addString(id, "ciafb:Name", "Mirkwood");
    logRecord(id);
    executeWorkflow(id);
    RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      final ValueFactory vf = conn.getValueFactory();
      final URI uri = vf.createURI(id.getKey().getKey());
      List<Statement> stmts = conn.getStatements(uri, null, null, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      stmts = conn.getStatements(uri, vf.createURI(FACTBOOK_BASEURI + "Name"), null, false).asList();
      assertNotNull(stmts);
      assertEquals(1, stmts.size());
      assertEquals("Mirkwood", stmts.get(0).getObject().stringValue());
    } finally {
      conn.close();
    }

    final Literal newName = getBlackboard().createLiteral(id);
    newName.setStringValue("Duesterwald");
    SesameRecordHelper.addClearFlag(getBlackboard().getRecord(id).getMetadata(), "ciafb:Name");
    getBlackboard().setLiteral(id, new Path("ciafb:Name"), newName);
    executeWorkflow(id);

    conn = _ontology.getDefaultConnection();
    try {
      final ValueFactory vf = conn.getValueFactory();
      final URI uri = vf.createURI(id.getKey().getKey());
      List<Statement> stmts = conn.getStatements(uri, null, null, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      stmts = conn.getStatements(uri, vf.createURI(FACTBOOK_BASEURI + "Name"), null, false).asList();
      assertNotNull(stmts);
      assertEquals(1, stmts.size());
      assertEquals("Duesterwald", stmts.get(0).getObject().stringValue());
    } finally {
      conn.close();
    }
  }

  /**
   * test writing of reverse properties.
   *
   * @throws Exception
   *           test fails
   */
  public void testReverseProperty() throws Exception {
    final Id id =
      createBlackboardRecord(getClass().getName(),
        "http://www.odci.gov/cia/publications/factbook/geos/wilderland.html");
    addResource(id, SesameRecordHelper.ATTRIBUTE_TYPE, FACTBOOK_BASEURI + "Country");
    addString(id, "ciafb:Name", "Wilderland");
    addResource(id, "ciafb:Bordering_country", "http://www.odci.gov/cia/publications/factbook/geos/gondor");
    SesameRecordHelper.addReverseFlag(getBlackboard().getRecord(id).getMetadata(), "ciafb:Bordering_country");
    logRecord(id);
    executeWorkflow(id);
    final RepositoryConnection conn = _ontology.getDefaultConnection();
    try {
      logRepositoryExport(conn);
      final ValueFactory vf = conn.getValueFactory();
      final URI uri = vf.createURI(id.getKey().getKey());
      final URI predicate = vf.createURI(FACTBOOK_BASEURI + "Bordering_country");
      List<Statement> stmts = conn.getStatements(uri, null, null, false).asList();
      assertNotNull(stmts);
      assertEquals(2, stmts.size());
      stmts = conn.getStatements(uri, predicate, null, false).asList();
      assertNotNull(stmts);
      assertEquals(0, stmts.size());
      stmts = conn.getStatements(null, predicate, uri, false).asList();
      assertNotNull(stmts);
      assertEquals(1, stmts.size());
      assertEquals("http://www.odci.gov/cia/publications/factbook/geos/gondor", stmts.get(0).getSubject()
        .stringValue());
    } finally {
      conn.close();
    }
  }

}
