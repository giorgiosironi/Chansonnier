/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.test;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.records.SesameRecordHelper;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.openrdf.model.Namespace;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 * basic test for access to Sesame repositories via SesameRecordReaderPipelet.
 *
 * @author jschumacher
 *
 */
public class TestSesameRecordReaderPipelet extends AOntologyWorkflowTest {

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
    final RepositoryConnection conn = _ontology.getConnection(getRepositoryName());
    conn.clear();
    _log.info("Loading schema file ...");
    InputStream file = ConfigUtils.getConfigStream(SesameOntologyManager.BUNDLE_ID, "CIA-onto-enhanced.rdf");
    conn.add(file, FACTBOOK_BASEURI, RDFFormat.RDFXML);
    file.close();
    conn.commit();
    _log.info("... done");
    _log.info("Loading data file ...");
    file = ConfigUtils.getConfigStream(SesameOntologyManager.BUNDLE_ID, "CIA-facts-enhanced.rdf");
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
   * name of repository to prepare for reading.
   *
   * @return repository name.
   */
  protected String getRepositoryName() {
    return "native";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest#getPipelineName()
   */
  @Override
  protected String getPipelineName() {
    return "SesameReadPipeline";
  }

  /**
   * read a record from sesame identified by the key value of the SMILA id.
   *
   * @throws Exception
   *           test fails
   */
  public void testReadWithID() throws Exception {
    final Id id =
      createBlackboardRecord(getClass().getName(), "http://www.odci.gov/cia/publications/factbook/geos/af.html");
    executeWorkflow(id);
    logRecord(id);

    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_TYPE));
    Literal literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_TYPE);
    assertEquals(FACTBOOK_BASEURI + "Country", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_URI));
    literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_URI);
    assertEquals("http://www.odci.gov/cia/publications/factbook/geos/af.html", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    final Path path = new Path();
    path.add("ciafb:Name");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("Afghanistan", literal.getStringValue());

    path.up().add("ciafb:Flag");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("http://www.odci.gov/cia/publications/factbook/flags/af-lgflag.jpg", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    path.up().add("ciafb:Map");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("http://www.odci.gov/cia/publications/factbook/maps/af-map.jpg", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    path.up().add("ciafb:Location");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("Southern Asia, north and west of Pakistan, east of Iran", literal.getStringValue());

    path.up().add("ciafb:Bordering_country");
    final int expectedBorderingCountries = 6;
    assertEquals(expectedBorderingCountries, getBlackboard().getLiteralsSize(id, path));

    path.up().add("ciafb:Natural_resources");
    final int expectedNaturalResources = 12;
    assertEquals(expectedNaturalResources, getBlackboard().getLiteralsSize(id, path));
  }

  /**
   * read a record from sesame identified by the value of rdf:about attribute.
   *
   * @throws Exception
   *           test fails
   */
  public void testReadWithAttribute() throws Exception {
    final Id id = createURIAttributeRecord("http://www.odci.gov/cia/publications/factbook/geos/al.html");
    executeWorkflow(id);

    logRecord(id);

    Literal literal = null;
    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_TYPE));
    literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_TYPE);
    assertEquals(FACTBOOK_BASEURI + "Country", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_URI));
    literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_URI);
    assertEquals("http://www.odci.gov/cia/publications/factbook/geos/al.html", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    final Path path = new Path();
    path.add("ciafb:Name");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("Albania", literal.getStringValue());

    path.up().add("ciafb:Flag");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("http://www.odci.gov/cia/publications/factbook/flags/al-lgflag.jpg", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    path.up().add("ciafb:Map");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals("http://www.odci.gov/cia/publications/factbook/maps/al-map.jpg", literal.getStringValue());
    assertEquals(SesameRecordHelper.SEMTYPE_RESOURCE, literal.getSemanticType());

    path.up().add("ciafb:Location");
    assertEquals(1, getBlackboard().getLiteralsSize(id, path));
    literal = getBlackboard().getLiteral(id, path);
    assertEquals(
      "Southeastern Europe, bordering the Adriatic Sea and Ionian Sea, between Greece and Serbia and Montenegro",
      literal.getStringValue());

    path.up().add("ciafb:Bordering_country");
    final int expectedBorderingCountries = 3;
    assertEquals(expectedBorderingCountries, getBlackboard().getLiteralsSize(id, path));

    path.up().add("ciafb:Natural_resources");
    final int expectedNaturalResources = 7;
    assertEquals(expectedNaturalResources, getBlackboard().getLiteralsSize(id, path));
  }

}
