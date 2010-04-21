/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH) - initial API and implementation (based on aperture test by DS)
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.test;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestConverterPipeline.
 */
public class TestConverterPipeline extends DeclarativeServiceTestCase {

  /**
   * name of pipeline to test.
   */
  protected static final String PIPELINE_NAME = "MimeTypeIdentifyPipeline";

  /**
   * WorkflowProcessor instance to test.
   */
  protected WorkflowProcessor _processor;

  /**
   * The _blackboard.
   */
  protected Blackboard _blackboard;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.pipelets.documentconverters.simple.test.AbstractTestBase #setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    _processor = getService(WorkflowProcessor.class);
    assertNotNull("no WorkflowProcessor service found.", _processor);
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _processor = null;
    _blackboard = null;
    super.tearDown();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.aperture.test.ApertureTestBase#extract(java.lang.String)
   */
  protected String extract(final String extension) throws Exception {
    final Id request = createBlackboardRecord("source", "key:" + extension, extension);
    final Id[] result = process(request);
    assertEquals(1, result.length);
    assertEquals(request, result[0]);
    final Literal mimeType = _blackboard.getLiteral(request, new Path("MimeType"));
    assertNotNull("literal mime type must not be null", mimeType);
    return new String(mimeType.getStringValue());
  }

  /**
   * create a new record on the blackboard.
   * 
   * @param source
   *          source value of ID
   * @param key
   *          key value of ID
   * @param extension
   *          the file extension
   * 
   * @return id of created record.
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  protected Id createBlackboardRecord(final String source, final String key, final String extension)
    throws BlackboardAccessException {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, key);
    _blackboard.invalidate(id);
    _blackboard.create(id);
    final Literal fileExtension = _blackboard.createLiteral(id);
    fileExtension.setStringValue(extension);
    _blackboard.setLiteral(id, new Path("FileExtension"), fileExtension);
    return id;
  }

  /**
   * Process.
   * 
   * @param id
   *          the id
   * 
   * @return the id[]
   * 
   * @throws Exception
   *           the exception
   */
  protected Id[] process(final Id id) throws Exception {
    return _processor.process(PIPELINE_NAME, _blackboard, new Id[] { id });
  }

  /**
   * Test txt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testTXT() throws Exception {
    executeTest("txt", "text/plain");
  }

  /**
   * Test utf-8 txt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testUTF8TXT() throws Exception {
    executeTest("txt", "text/plain");
  }

  /**
   * Test html.
   * 
   * @throws Exception
   *           the exception
   */
  public void testHTML() throws Exception {
    executeTest("html", "text/html");
  }

  /**
   * Test pdf.
   * 
   * @throws Exception
   *           the exception
   */
  public void testPDF() throws Exception {
    executeTest("pdf", "application/pdf");
  }

  /**
   * Test rtf.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRTF() throws Exception {
    executeTest("rtf", "text/rtf");
  }

  /**
   * Test xml.
   * 
   * @throws Exception
   *           the exception
   */
  public void testXML() throws Exception {
    executeTest("xml", "text/xml");
  }

  /**
   * Test msoffic e2003 doc.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2003DOC() throws Exception {
    executeTest("doc", "application/vnd.ms-word");
  }

  /**
   * Test msoffic e2003 ppt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2003PPT() throws Exception {
    executeTest("ppt", "application/vnd.ms-powerpoint");
  }

  /**
   * Test msoffic e2003 xls.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2003XLS() throws Exception {
    executeTest("xls", "application/vnd.ms-excel");
  }

  /**
   * Test msoffic e2007 docx.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2007DOCX() throws Exception {
    executeTest("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
  }

  /**
   * Test openoffic e24 odp.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOPENOFFICE24ODP() throws Exception {
    executeTest("odp", "application/vnd.oasis.opendocument.presentation");
  }

  /**
   * Test openoffic e22 ods.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOPENOFFICE22ODS() throws Exception {
    executeTest("ods", "application/vnd.oasis.opendocument.spreadsheet");
  }

  /**
   * Test openoffic e22 odt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOPENOFFICE22ODT() throws Exception {
    executeTest("odt", "application/vnd.oasis.opendocument.text");
  }

  /**
   * Execute test.
   * 
   * @param extension
   *          File extension.
   * @param expectedMimeType
   *          Expected MIME type.
   * 
   * @throws Exception
   *           the exception
   */
  protected void executeTest(final String extension, final String expectedMimeType) throws Exception {
    try {
      final String result = extract(extension);
      assertNotNull(result);
      assertEquals("resolved mime type [" + result + "] must be equal [" + expectedMimeType + "]", result,
        expectedMimeType);
    } catch (final Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

}
