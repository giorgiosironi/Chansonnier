/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.test;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.ontology.records.SesameRecordHelper;

/**
 * Tests for {@link org.eclipse.smila.ontology.pipelets.CreateFileUriPipelet}.
 *
 * @author jschumacher
 *
 */
public class TestCreateFileUriPipelet extends AOntologyWorkflowTest {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getPipelineName() {
    // TODO Auto-generated method stub
    return "CreateFileUriPipeline";
  }

  /**
   * check creation of file uri from relative windows path.
   *
   * @throws Exception
   *           test fails
   */
  public void testRelativeWindowsPath() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "directory\\file.txt");
    executeWorkflow(id);
    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_URI));
    final Literal literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_URI);
    assertTrue(literal.getStringValue().startsWith("file:/"));
    assertTrue(literal.getStringValue().endsWith("directory/file.txt"));
  }

  /**
   * check creation of file uri from absolute windows path.
   *
   * @throws Exception
   *           test fails
   */
  public void testAbsoluteWindowsPath() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "Q:\\directory\\file.txt");
    executeWorkflow(id);
    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_URI));
    final Literal literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_URI);
    assertTrue(literal.getStringValue().startsWith("file:/"));
    assertTrue(literal.getStringValue().endsWith("Q:/directory/file.txt"));
  }

  /**
   * check creation of file uri from relative unix path.
   *
   * @throws Exception
   *           test fails
   */
  public void testRelativeUnixPath() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "directory/file.txt");
    executeWorkflow(id);
    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_URI));
    final Literal literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_URI);
    assertTrue(literal.getStringValue().startsWith("file:/"));
    assertTrue(literal.getStringValue().endsWith("directory/file.txt"));
  }

  /**
   * check creation of file uri from absolute unix path.
   *
   * @throws Exception
   *           test fails
   */
  public void testAbsoluteUnixPath() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "/directory/file.txt");
    executeWorkflow(id);
    assertEquals(1, getBlackboard().getLiteralsSize(id, SesameRecordHelper.PATH_URI));
    final Literal literal = getBlackboard().getLiteral(id, SesameRecordHelper.PATH_URI);
    assertTrue(literal.getStringValue().startsWith("file:/"));
    assertTrue(literal.getStringValue().endsWith("/directory/file.txt"));
  }


}
