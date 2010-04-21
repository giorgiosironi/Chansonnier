/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.tools.DatamodelSerializationUtils;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.records.SesameRecordHelper;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;

/**
 * base class for tests that use the ontology via pipelines.
 * 
 * @author jschumacher
 * 
 */
public abstract class AOntologyWorkflowTest extends AWorkflowProcessorTest {
  /**
   * number of minutes per hour.
   */
  public static final int MINUTES_PER_HOUR = 60;

  /**
   * number of milliseconds per hour, used to convert java.util timezone offsets to hour offsets.
   */
  public static final int MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR * 60 * 1000;

  /**
   * sesame service.
   */
  protected SesameOntologyManager _ontology;

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
    _ontology = getService(SesameOntologyManager.class);
    assertNotNull(_ontology);
  }

  /**
   * write an RDFXML export of the repository to debug log (if enabled).
   *
   * @param conn
   *          repository
   * @throws RepositoryException
   *           error accessing repository
   * @throws RDFHandlerException
   *           error writing RDF
   * @throws IOException
   *           should not happen ...
   */
  protected void logRepositoryExport(final RepositoryConnection conn) throws RepositoryException,
    RDFHandlerException, IOException {
    if (_log.isDebugEnabled()) {
      final StringWriter rdfString = new StringWriter();
      final RDFXMLPrettyWriter writer = new RDFXMLPrettyWriter(rdfString);
      conn.export(writer);
      writer.close();
      _log.debug(rdfString.toString());
    }
  }

  /**
   * write record XML to debug log, if enabled.
   *
   * @param id
   *          record ID
   * @throws BlackboardAccessException
   *           something fails.
   */
  protected void logRecord(final Id id) throws BlackboardAccessException {
    if (_log.isDebugEnabled()) {
      _log.debug(DatamodelSerializationUtils.serialize2string(getBlackboard().getRecord(id)));
    }
  }

  /**
   * run test pipeline.
   *
   * @param id
   *          record ID
   * @throws ProcessingException
   *           something fails.
   */
  protected void executeWorkflow(final Id id) throws ProcessingException {
    final Id[] result = getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { id });
    assertNotNull(result);
    assertEquals(1, result.length);
    assertEquals(id, result[0]);
  }

  /**
   * create a record with the resource URI as rdf:about attribute.
   *
   * @param uri
   *          resource URI
   * @return record ID
   * @throws BlackboardAccessException
   *           something fails.
   */
  protected Id createURIAttributeRecord(final String uri) throws BlackboardAccessException {
    final Id id = createBlackboardRecord(getClass().getName(), UUID.randomUUID().toString());
    addResource(id, SesameRecordHelper.ATTRIBUTE_URI, uri);
    return id;
  }

  /**
   * add a string attribute value to record on blackboard.
   *
   * @param id
   *          record id
   * @param name
   *          attribute name
   * @param value
   *          string value
   * @return the new liteal
   * @throws BlackboardAccessException
   *           writing to blackboard failed.
   */
  protected Literal addString(final Id id, final String name, final String value) throws BlackboardAccessException {
    final Path path = new Path();
    path.add(name);
    final Literal literal = getBlackboard().createLiteral(id);
    literal.setStringValue(value);
    getBlackboard().addLiteral(id, path, literal);
    return literal;
  }

  /**
   * add a resource attribute value to record on blackboard.
   *
   * @param id
   *          record id
   * @param name
   *          attribute name
   * @param value
   *          resource value (URI)
   * @return the new liteal
   * @throws BlackboardAccessException
   *           writing to blackboard failed.
   */
  protected Literal addResource(final Id id, final String name, final String value)
    throws BlackboardAccessException {
    final Literal literal = addString(id, name, value);
    literal.setSemanticType(SesameRecordHelper.SEMTYPE_RESOURCE);
    return literal;
  }

}
