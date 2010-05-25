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
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.bpel.pipelet.SplitterPipelet;

/**
 * Test the SplitterPipeline. This pipeline creates a fixed number of records from each incoming record by invoking the
 * {@link SplitterPipelet}.
 * 
 * @author jschumacher
 * 
 */
public class TestConditionalPipeline extends AWorkflowProcessorTest {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "ConditionalPipeline";

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
   * test without tested attribute value.
   * 
   * @throws Exception
   *           test fails
   */
  public void testID() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(1, result.length);
    assertEquals(request, result[0]);
  }

  /**
   * test with attribute value fulfilling condition.
   * 
   * @throws Exception
   *           test fails
   */
  public void testSplitAttribute() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Literal singleValue = getBlackboard().createLiteral(request);
    singleValue.setStringValue("split");
    final Path singleValuePath = new Path("workflow-attribute");
    getBlackboard().setLiteral(request, singleValuePath, singleValue);
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(SplitterPipelet.SPLIT_FACTOR, result.length);
    for (int i = 0; i < result.length; i++) {
      assertEquals(request, result[i].createCompoundId());
      assertEquals(1, result[i].getFragmentNames().size());
      assertEquals("fragment" + i, result[i].getFragmentNames().get(0));
      assertTrue(getBlackboard().hasAttribute(result[i], singleValuePath));
      assertEquals(1, getBlackboard().getLiteralsSize(result[i], singleValuePath));
      assertEquals(singleValue, getBlackboard().getLiteral(result[i], singleValuePath));
    }
  }

  /**
   * test with annotation value fulfilling condition.
   * 
   * @throws Exception
   *           test fails
   */
  public void testSplitAnnotation() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Annotation annotation = getBlackboard().createAnnotation(request);
    annotation.addAnonValue("split");
    getBlackboard().setAnnotation(request, null, "workflow-annotation", annotation);
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(SplitterPipelet.SPLIT_FACTOR, result.length);
    for (int i = 0; i < result.length; i++) {
      assertEquals(request, result[i].createCompoundId());
      assertEquals(1, result[i].getFragmentNames().size());
      assertEquals("fragment" + i, result[i].getFragmentNames().get(0));
      assertTrue(getBlackboard().hasAnnotation(result[i], null, "workflow-annotation"));
      final Annotation checkAnnotation = getBlackboard().getAnnotation(result[i], null, "workflow-annotation");
      assertEquals(annotation.anonValuesSize(), checkAnnotation.anonValuesSize());
      assertEquals(annotation.getAnonValues(), checkAnnotation.getAnonValues());
    }
  }

  /**
   * test with annotation and attribute value fulfilling condition.
   * 
   * @throws Exception
   *           test fails
   */
  public void testDoubleSplit() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Annotation annotation = getBlackboard().createAnnotation(request);
    annotation.addAnonValue("split");
    getBlackboard().setAnnotation(request, null, "workflow-annotation", annotation);
    final Literal singleValue = getBlackboard().createLiteral(request);
    singleValue.setStringValue("split");
    final Path singleValuePath = new Path("workflow-attribute");
    getBlackboard().setLiteral(request, singleValuePath, singleValue);
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(SplitterPipelet.SPLIT_FACTOR * SplitterPipelet.SPLIT_FACTOR, result.length);
    for (int i = 0; i < result.length; i++) {
      assertEquals(request, result[i].createCompoundId().createCompoundId());
      assertEquals(2, result[i].getFragmentNames().size());
      assertEquals("fragment" + (i / SplitterPipelet.SPLIT_FACTOR), result[i].getFragmentNames().get(0));
      assertEquals("fragment" + (i % SplitterPipelet.SPLIT_FACTOR), result[i].getFragmentNames().get(1));
      assertTrue(getBlackboard().hasAnnotation(result[i], null, "workflow-annotation"));
      final Annotation checkAnnotation = getBlackboard().getAnnotation(result[i], null, "workflow-annotation");
      assertEquals(annotation.anonValuesSize(), checkAnnotation.anonValuesSize());
      assertEquals(annotation.getAnonValues(), checkAnnotation.getAnonValues());
      assertTrue(getBlackboard().hasAttribute(result[i], singleValuePath));
      assertEquals(1, getBlackboard().getLiteralsSize(result[i], singleValuePath));
      assertEquals(singleValue, getBlackboard().getLiteral(result[i], singleValuePath));
    }
  }

  /**
   * test with attribute and annotation value not fulfilling condition.
   * 
   * @throws Exception
   *           test fails
   */
  public void testNoSplit() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Literal singleValue = getBlackboard().createLiteral(request);
    singleValue.setStringValue("no-split");
    final Path singleValuePath = new Path("workflow-attribute");
    getBlackboard().setLiteral(request, singleValuePath, singleValue);
    final Annotation annotation = getBlackboard().createAnnotation(request);
    annotation.addAnonValue("no-split");
    getBlackboard().setAnnotation(request, null, "workflow-annotation", annotation);
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(1, result.length);
    assertEquals(request, result[0]);
    assertTrue(getBlackboard().hasAttribute(result[0], singleValuePath));
    assertEquals(1, getBlackboard().getLiteralsSize(result[0], singleValuePath));
    assertEquals(singleValue, getBlackboard().getLiteral(result[0], singleValuePath));
    assertTrue(getBlackboard().hasAnnotation(result[0], null, "workflow-annotation"));
    final Annotation checkAnnotation = getBlackboard().getAnnotation(result[0], null, "workflow-annotation");
    assertEquals(annotation.anonValuesSize(), checkAnnotation.anonValuesSize());
    assertEquals(annotation.getAnonValues(), checkAnnotation.getAnonValues());
  }

}
