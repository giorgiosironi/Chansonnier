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

package org.eclipse.smila.processing.pipelets.test;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

/**
 * Test the SetAnnotationPipeline. This pipeline sets some annotations on the input record using the
 * SetAnnotationsPipelet.
 * 
 * @author jschumacher
 * 
 */
public class TestSetAnnotationPipeline extends AWorkflowProcessorTest {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "SetAnnotationPipeline";

  /**
   * @return name of pipeline to test
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
  public void testSetAnnotation() throws Exception {
    final Id request = createBlackboardRecord("source", "key");
    final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
    assertEquals(1, result.length);
    assertEquals(request, result[0]);

    final Path path = new Path();
    Annotation annotation = getBlackboard().getAnnotation(result[0], path, "annotation");
    assertNotNull(annotation);
    assertEquals(2, annotation.anonValuesSize());
    assertTrue(annotation.getAnonValues().contains("anonvalue1"));
    assertTrue(annotation.getAnonValues().contains("anonvalue2"));
    assertEquals(2, annotation.namedValuesSize());
    assertEquals("namedvalue1", annotation.getNamedValue("name1"));
    assertEquals("namedvalue2", annotation.getNamedValue("name2"));

    path.add("attribute", PathStep.ATTRIBUTE_ANNOTATION);
    annotation = getBlackboard().getAnnotation(result[0], path, "annotation");
    assertNotNull(annotation);
    assertEquals(2, annotation.anonValuesSize());
    assertTrue(annotation.getAnonValues().contains("anonvalue1"));
    assertTrue(annotation.getAnonValues().contains("anonvalue2"));
    assertEquals(2, annotation.namedValuesSize());
    assertEquals("namedvalue1", annotation.getNamedValue("name1"));
    assertEquals("namedvalue2", annotation.getNamedValue("name2"));
  }
}
