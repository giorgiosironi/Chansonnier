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

import java.util.List;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.bpel.pipelet.SplitterPipelet;

/**
 * Test the SplitterPipeline. This pipeline creates a fixed number of records from each incoming record by invoking the
 * {@link SplitterPipelet}.
 * 
 * @author jschumacher
 * 
 */
public class TestSplitterPipeline extends AWorkflowProcessorTest {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "SplitterPipeline";

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
   * test code.
   * 
   * @throws Exception
   *           test fails
   */
  public void testEchoID() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(SplitterPipelet.SPLIT_FACTOR, result.length);
    for (int i = 0; i < result.length; i++) {
      assertEquals(request, result[i].createCompoundId());
      assertEquals(1, result[i].getFragmentNames().size());
      assertEquals("fragment" + i, result[i].getFragmentNames().get(0));
    }
  }

  /**
   * test with attributes.
   * 
   * @throws Exception
   *           test fails
   */
  public void testEchoAttributes() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Literal singleValue = getBlackboard().createLiteral(request);
    singleValue.setStringValue("single value");
    final Path singleValuePath = new Path("single value attribute");
    getBlackboard().setLiteral(request, singleValuePath, singleValue);
    final Path multiValuePath = new Path("multi value attribute");
    final Literal multiValue1 = getBlackboard().createLiteral(request);
    multiValue1.setStringValue("multi value 1");
    final Literal multiValue2 = getBlackboard().createLiteral(request);
    multiValue2.setStringValue("multi value 2");
    getBlackboard().addLiteral(request, multiValuePath, multiValue1);
    getBlackboard().addLiteral(request, multiValuePath, multiValue2);
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(SplitterPipelet.SPLIT_FACTOR, result.length);
    for (int i = 0; i < result.length; i++) {
      assertEquals(request, result[i].createCompoundId());
      assertEquals(1, result[i].getFragmentNames().size());
      assertEquals("fragment" + i, result[i].getFragmentNames().get(0));
      assertTrue(getBlackboard().hasAttribute(result[i], singleValuePath));
      assertEquals(1, getBlackboard().getLiteralsSize(result[i], singleValuePath));
      assertEquals(singleValue, getBlackboard().getLiteral(result[i], singleValuePath));
      assertTrue(getBlackboard().hasAttribute(result[i], multiValuePath));
      assertEquals(2, getBlackboard().getLiteralsSize(result[i], multiValuePath));
      final List<Literal> multiValues = getBlackboard().getLiterals(result[i], multiValuePath);
      assertTrue(multiValues.contains(multiValue1));
      assertTrue(multiValues.contains(multiValue2));
    }
  }

}
