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
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.bpel.pipelet.AddLiteralPipelet;
import org.eclipse.smila.processing.bpel.pipelet.SearchTestPipelet;
import org.eclipse.smila.processing.bpel.pipelet.SplitterPipelet;

/**
 * Test search processing with services. The pipeline contains a standard service manipulating the query, then a search
 * service that creates a search result by splitting the query object and finally the same standard service than before
 * manipulates the result records.
 * 
 * 
 * @author jschumacher
 * 
 */
public class TestSearchServicePipeline extends AWorkflowProcessorTest {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "SearchServicePipeline";

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
    final Id query = createBlackboardRecord("source", "key");
    final SearchMessage message = new SearchMessage(query);
    final SearchMessage result = getProcessor().process(PIPELINE_NAME, getBlackboard(), message);
    assertTrue(result.hasQuery());
    assertEquals(query, result.getQuery());
    assertEquals(1, getBlackboard().getLiteralsSize(result.getQuery(), AddLiteralPipelet.ATTRIBUTE));
    assertEquals(AddLiteralPipelet.VALUE_PREFIX + 0, getBlackboard().getLiteral(result.getQuery(),
      AddLiteralPipelet.ATTRIBUTE).getStringValue());
    assertEquals(SplitterPipelet.SPLIT_FACTOR, result.getRecords().length);
    for (int i = 0; i < result.getRecords().length; i++) {
      final Id record = result.getRecords()[i];
      final String value = SearchTestPipelet.PREFIX + i;
      assertEquals(query, record.createCompoundId());
      assertEquals(1, record.getFragmentNames().size());
      assertEquals(value, record.getFragmentNames().get(0));
      assertEquals(2, getBlackboard().getLiteralsSize(record, AddLiteralPipelet.ATTRIBUTE));
      final List<Literal> literals = getBlackboard().getLiterals(record, AddLiteralPipelet.ATTRIBUTE);
      assertEquals(AddLiteralPipelet.VALUE_PREFIX + 0, literals.get(0).getStringValue());
      assertEquals(AddLiteralPipelet.VALUE_PREFIX + i, literals.get(1).getStringValue());
      assertEquals(1, getBlackboard().getLiteralsSize(record, SearchTestPipelet.ATTRIBUTE));
      assertEquals(value, getBlackboard().getLiteral(record, SearchTestPipelet.ATTRIBUTE).getStringValue());
    }
  }

  /**
   * test with attributes.
   * 
   * @throws Exception
   *           test fails
   */
  public void testEchoAttributes() throws Exception {
    final Id query = createBlackboardRecord("source", "key");
    final Literal singleValue = getBlackboard().createLiteral(query);
    singleValue.setStringValue("single value");
    final Path singleValuePath = new Path("single value attribute");
    getBlackboard().setLiteral(query, singleValuePath, singleValue);
    final Path multiValuePath = new Path("multi value attribute");
    final Literal multiValue1 = getBlackboard().createLiteral(query);
    multiValue1.setStringValue("multi value 1");
    final Literal multiValue2 = getBlackboard().createLiteral(query);
    multiValue2.setStringValue("multi value 2");
    getBlackboard().addLiteral(query, multiValuePath, multiValue1);
    getBlackboard().addLiteral(query, multiValuePath, multiValue2);
    final SearchMessage message = new SearchMessage(query);
    final SearchMessage result = getProcessor().process(PIPELINE_NAME, getBlackboard(), message);
    assertTrue(result.hasQuery());
    assertEquals(query, result.getQuery());
    assertEquals(1, getBlackboard().getLiteralsSize(result.getQuery(), AddLiteralPipelet.ATTRIBUTE));
    assertEquals(AddLiteralPipelet.VALUE_PREFIX + 0, getBlackboard().getLiteral(result.getQuery(),
      AddLiteralPipelet.ATTRIBUTE).getStringValue());
    assertEquals(SplitterPipelet.SPLIT_FACTOR, result.getRecords().length);
    for (int i = 0; i < result.getRecords().length; i++) {
      final Id record = result.getRecords()[i];
      final String value = SearchTestPipelet.PREFIX + i;
      assertEquals(query, record.createCompoundId());
      assertEquals(1, record.getFragmentNames().size());
      assertEquals(value, record.getFragmentNames().get(0));
      assertEquals(2, getBlackboard().getLiteralsSize(record, AddLiteralPipelet.ATTRIBUTE));
      final List<Literal> literals = getBlackboard().getLiterals(record, AddLiteralPipelet.ATTRIBUTE);
      assertEquals(AddLiteralPipelet.VALUE_PREFIX + 0, literals.get(0).getStringValue());
      assertEquals(AddLiteralPipelet.VALUE_PREFIX + i, literals.get(1).getStringValue());
      assertEquals(1, getBlackboard().getLiteralsSize(record, SearchTestPipelet.ATTRIBUTE));
      assertEquals(value, getBlackboard().getLiteral(record, SearchTestPipelet.ATTRIBUTE).getStringValue());
      assertTrue(getBlackboard().hasAttribute(record, singleValuePath));
      assertEquals(1, getBlackboard().getLiteralsSize(record, singleValuePath));
      assertEquals(singleValue, getBlackboard().getLiteral(record, singleValuePath));
      assertTrue(getBlackboard().hasAttribute(record, multiValuePath));
      assertEquals(2, getBlackboard().getLiteralsSize(record, multiValuePath));
      final List<Literal> multiValues = getBlackboard().getLiterals(record, multiValuePath);
      assertTrue(multiValues.contains(multiValue1));
      assertTrue(multiValues.contains(multiValue2));
    }
  }

}
