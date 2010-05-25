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

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;

/**
 * Test the HelloWorldPipeline that invokes an external websevice. Not included in AllTests suite because you need to
 * start the Axis2 1.4 sample "java_first_jaxws" in an axis2server on the same machine to provide the invoked
 * webservice. See Axis2 distribution for more details.
 * 
 * @author jschumacher
 * 
 */
public class TestLocalHelloWorldPipeline extends AWorkflowProcessorTest {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "LocalHelloWorldPipeline";

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest#getPipelineName()
   */
  @Override
  protected String getPipelineName() {
    return PIPELINE_NAME;
  }

  /**
   * test with ID only.
   * 
   * @throws Exception
   *           test fails
   */
  public void testLocalHelloWorld() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Literal smila = getBlackboard().createLiteral(request);
    smila.setStringValue("SMILA");
    final Path path = new Path("workflow-attribute");
    getBlackboard().setLiteral(request, path, smila);
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(1, result.length);
    assertEquals(request, result[0]);
    final Literal hello = getBlackboard().getLiteral(result[0], path);
    assertNotNull(hello);
    assertEquals("Hello SMILA", hello.getStringValue());
  }

}
