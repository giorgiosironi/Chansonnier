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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 * basic test for access to Sesame repositories via SesameOntologyManager service.
 *
 * @author jschumacher
 *
 */
public class TestRepositoryAccess extends DeclarativeServiceTestCase {

  /**
   * base uri for test RDFs.
   */
  public static final String FACTBOOK_BASEURI = "http://www.cia.gov/cia/publications/factbook#";

  /**
   * sesame service.
   */
  private SesameOntologyManager _service;

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
    _service = getService(SesameOntologyManager.class);
    assertNotNull(_service);
  }

  /**
   * test exception for unknown repository name.
   *
   * @throws Exception
   *           test fails.
   */
  public void testUnknownRepository() throws Exception {
    try {
      final RepositoryConnection repConn = _service.getConnection("foobar");
      fail("missing exception");
      repConn.close();
    } catch (final Exception ex) {
      assertTrue(ex instanceof RepositoryException);
    }
  }

  /**
   * test access to default repository.
   *
   * @throws Exception
   *           test fails.
   */
  public void testDefaultRepository() throws Exception {
    final RepositoryConnection repConn = _service.getDefaultConnection();
    assertNotNull(repConn);
    repConn.close();
  }

  /**
   * test access to memory store.
   *
   * @throws Exception
   *           test fails
   */
  public void testMemoryStore() throws Exception {
    RepositoryConnection repConn = null;
    try {
      repConn = _service.getConnection("memory");
      testConnection(repConn);
    } finally {
      if (repConn != null) {
        repConn.close();
      }
    }
  }

  /**
   * test access to native store.
   *
   * @throws Exception
   *           test fails
   */
  public void testNativeStore() throws Exception {
    RepositoryConnection repConn = null;
    try {
      repConn = _service.getConnection("native");
      testConnection(repConn);
    } finally {
      if (repConn != null) {
        repConn.close();
      }
    }
  }

  /**
   * test access to database store. Needs a running PostgreSQL server on the same machine. See
   * configuration/org.eclipse.smila.ontology/sesameConfig.xml for details of expected DB setup. Therefore it is
   * disabled by default.
   *
   * @throws Exception
   *           test fails
   */
  public void dontTestRdbmsStore() throws Exception {
    RepositoryConnection repConn = null;
    try {
      repConn = _service.getConnection("database");
      testConnection(repConn);
    } finally {
      if (repConn != null) {
        repConn.close();
      }
    }
  }

  /**
   * test a repository connection: clear, load rdfs, get statements, execute SPARQL query.
   *
   * @param repConn
   *          repository connection
   * @throws Exception
   *           test fails
   */
  private void testConnection(final RepositoryConnection repConn) throws Exception {
    final long start = System.currentTimeMillis();
    assertNotNull(repConn);
    _log.info("clear repository");
    repConn.clear();

    final URI countryClass = loadAndTestSchema(repConn);
    loadAndTestData(repConn, countryClass);
    final long end = System.currentTimeMillis();
    _log.info("Ontology test runtime: " + (end - start) + " ms.");
  }

  /**
   * load CIA factbook schema and get URI of class Country.
   *
   * @param repConn
   *          repository to use
   * @return country URI
   * @throws Exception
   *           test fails
   */
  private URI loadAndTestSchema(final RepositoryConnection repConn) throws Exception {
    InputStream schemaFile = null;
    try {
      _log.info("load schema");
      schemaFile = ConfigUtils.getConfigStream(SesameOntologyManager.BUNDLE_ID, "CIA-onto-enhanced.rdf");
      repConn.add(schemaFile, FACTBOOK_BASEURI, RDFFormat.RDFXML);
      repConn.commit();
      _log.info("get classes");
      RepositoryResult<Statement> result = repConn.getStatements(null, RDF.TYPE, RDFS.CLASS, false);
      assertNotNull(result);
      List<Statement> resultList = result.asList();
      final int expectedClassCount = 10;
      assertEquals(expectedClassCount, resultList.size());
      URI countryClass = null;
      for (final Statement s : resultList) {
        final Resource aClass = s.getSubject();
        _log.info("have class: " + aClass);
        if (aClass instanceof URI) {
          final URI uri = (URI) aClass;
          if ("Country".equals(uri.getLocalName())) {
            countryClass = uri;
          }
        }
      }
      assertNotNull(countryClass);
      result = repConn.getStatements(null, RDFS.SUBCLASSOF, null, false);
      assertNotNull(result);
      resultList = result.asList();
      final int expectedSubClassCount = 9;
      assertEquals(expectedSubClassCount, resultList.size());
      for (final Statement s : resultList) {
        _log.info(s.getSubject() + " is subclass of " + s.getObject());
      }
      final RepositoryResult<Namespace> namespaces = repConn.getNamespaces();
      for (final Namespace ns : namespaces.asList()) {
        _log.info("Namespace " + ns.getPrefix() + " is " + ns.getName());
      }
      return countryClass;
    } finally {
      IOUtils.closeQuietly(schemaFile);
    }
  }

  /**
   * load CIA factbook data and perform a SPARQL query.
   *
   * @param repConn
   *          repository to use
   * @param countryClass
   *          country URI
   * @throws Exception
   *           test fails
   */
  private void loadAndTestData(final RepositoryConnection repConn, final URI countryClass) throws Exception {
    InputStream dataFile = null;
    try {
      _log.info("load data");
      dataFile = ConfigUtils.getConfigStream(SesameOntologyManager.BUNDLE_ID, "CIA-facts-enhanced.rdf");
      repConn.add(dataFile, FACTBOOK_BASEURI, RDFFormat.RDFXML);
      repConn.commit();
      final RepositoryResult<Statement> result = repConn.getStatements(null, RDF.TYPE, countryClass, false);
      assertNotNull(result);
      final List<Statement> resultList = result.asList();
      final int expectedCountryCount = 7;
      assertEquals(expectedCountryCount, resultList.size());
      for (final Statement s : resultList) {
        _log.info("Country: " + s.getSubject());
      }
      final StringBuilder sparql = new StringBuilder("PREFIX : <").append(FACTBOOK_BASEURI).append("> SELECT * ");
      sparql.append("{?c <").append(RDF.TYPE).append("> :Country . ");
      sparql.append("?c :Name ?n }");
      _log.info("SPARQL: " + sparql);
      final TupleQuery query = repConn.prepareTupleQuery(QueryLanguage.SPARQL, sparql.toString());
      final TupleQueryResult tupleResult = query.evaluate();
      int count = 0;
      while (tupleResult.hasNext()) {
        final BindingSet binding = tupleResult.next();
        _log.info("Country " + binding.getValue("c") + " has name " + binding.getValue("n"));
        count++;
      }
      tupleResult.close();
      assertEquals(expectedCountryCount, count);
      final RepositoryResult<Namespace> namespaces = repConn.getNamespaces();
      for (final Namespace ns : namespaces.asList()) {
        _log.info("Namespace " + ns.getPrefix() + " is " + ns.getName());
      }
    } finally {
      IOUtils.closeQuietly(dataFile);
    }
  }
}
