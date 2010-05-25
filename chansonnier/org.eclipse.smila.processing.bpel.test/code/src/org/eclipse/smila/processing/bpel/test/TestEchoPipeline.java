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

/**
 * Test the EchoPipeline. This pipeline just returns the given records.
 * 
 * @author jschumacher
 * 
 */
public class TestEchoPipeline extends AWorkflowProcessorTest {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "EchoPipeline";

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
  public void testEchoID() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(1, result.length);
    assertEquals(request, result[0]);
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
    assertEquals(1, result.length);
    assertEquals(request, result[0]);
    assertTrue(getBlackboard().hasAttribute(result[0], singleValuePath));
    assertEquals(1, getBlackboard().getLiteralsSize(result[0], singleValuePath));
    assertEquals(singleValue, getBlackboard().getLiteral(result[0], singleValuePath));
    assertTrue(getBlackboard().hasAttribute(result[0], multiValuePath));
    assertEquals(2, getBlackboard().getLiteralsSize(result[0], multiValuePath));
    final List<Literal> multiValues = getBlackboard().getLiterals(result[0], multiValuePath);
    assertTrue(multiValues.contains(multiValue1));
    assertTrue(multiValues.contains(multiValue2));
  }

}
