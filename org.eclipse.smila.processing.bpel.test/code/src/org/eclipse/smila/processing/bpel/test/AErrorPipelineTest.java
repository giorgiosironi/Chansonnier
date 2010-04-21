/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel.test;

import java.util.concurrent.TimeoutException;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.ode.ODEServerException;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.bpel.pipelet.ErrorPipelet;
import org.eclipse.smila.processing.bpel.pipelet.ErrorPipelet.ErrorMode;

/**
 * Base class for error pipeline tests.
 *
 * @author jschumacher
 *
 */
public abstract class AErrorPipelineTest extends AWorkflowProcessorTest {
  /**
   * test pipelet throwing processing exception.
   *
   * @throws Exception
   *           test fails
   */
  public void testProcessingException() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    setErrorModeAnnotation(request, ErrorMode.PROCESSING);
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ProcessingException);
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * test pipelet throwing runtime exception.
   *
   * @throws Exception
   *           test fails
   */
  public void testRuntimeException() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    setErrorModeAnnotation(request, ErrorMode.RUNTIME);
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ProcessingException);
      assertNotNull(ex.getCause().getCause());
      assertTrue(ex.getCause().getCause() instanceof NullPointerException);
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * test pipelet returning an invalid ID.
   *
   * @throws Exception
   *           test fails
   */
  public void testInvalidId() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    setErrorModeAnnotation(request, ErrorMode.INVALID_ID);
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ProcessingException);
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * test pipelet returning a null result.
   *
   * @throws Exception
   *           test fails
   */
  public void testNullResult() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    setErrorModeAnnotation(request, ErrorMode.NULL_RESULT);
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ProcessingException);
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * test pipelet returning an null ID.
   *
   * @throws Exception
   *           test fails
   */
  public void testNullId() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    setErrorModeAnnotation(request, ErrorMode.NULL_ID);
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ProcessingException);
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * test timeout.
   *
   * @throws Exception
   *           test fails
   */
  public void testTimeout() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    setErrorModeAnnotation(request, ErrorMode.TIMEOUT);
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ODEServerException);
      assertNotNull(ex.getCause().getCause());
      assertTrue(ex.getCause().getCause() instanceof TimeoutException);
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * test invocation of an unknown pipelet.
   *
   * @throws Exception
   *           test fails
   */
  public void testUnknown() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    try {
      getProcessor().process(getPipelineName(), getBlackboard(), new Id[] { request });
      fail("Exception expected");
    } catch (final ProcessingException ex) {
      assertNotNull(ex.getCause());
      assertTrue(ex.getCause() instanceof ProcessingException);
      assertNull(ex.getCause().getCause());
    } catch (final Exception ex) {
      fail("Wrong exception");
    }
  }

  /**
   * set the error mode annotation.
   *
   * @param id
   *          record id
   * @param errorMode
   *          error mode
   * @throws BlackboardAccessException
   *           error on blackboard.
   */
  private void setErrorModeAnnotation(final Id id, final ErrorMode errorMode) throws BlackboardAccessException {
    final Annotation anno = getBlackboard().createAnnotation(id);
    anno.setNamedValue(ErrorPipelet.NAME_MODE, errorMode.name());
    getBlackboard().setAnnotation(id, null, ErrorPipelet.class.getName(), anno);
  }
}
