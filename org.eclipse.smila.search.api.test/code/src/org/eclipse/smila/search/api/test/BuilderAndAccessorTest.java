/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.search.api.test;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.processing.parameters.SearchAnnotations.FilterMode;
import org.eclipse.smila.processing.parameters.SearchAnnotations.FilterType;
import org.eclipse.smila.processing.parameters.SearchParameters.OrderMode;
import org.eclipse.smila.search.api.SearchResult;
import org.eclipse.smila.search.api.helper.QueryBuilder;
import org.eclipse.smila.search.api.helper.QueryRecordAccessor;
import org.eclipse.smila.search.api.helper.ResultAccessor;
import org.eclipse.smila.search.api.internal.SearchResultImpl;

/**
 * test query building and reading.
 *
 * @author jschumacher
 *
 */
public class BuilderAndAccessorTest extends TestCase {
  /**
   * test setting of parameters and accessing them.
   *
   * @throws Exception
   *           test fails.
   */
  public void testParameters() throws Exception {
    final QueryBuilder builder = new QueryBuilder("pipeline");
    builder.setLanguage("en").setQuery("what am i thinking of").setResultSize(2).setResultOffset(1).setThreshold(1);
    builder.setParameter("single", "value").addParameter("multi", "value1").addParameter("multi", "value2");
    builder.addOrderBy("date", OrderMode.DESC).addOrderBy("size", OrderMode.ASC);
    builder.getParameters().setBooleanParameter("bool", true);
    final Record record = builder.getQuery();
    assertNotNull(record);
    final QueryRecordAccessor accessor = new QueryRecordAccessor(record);
    assertEquals(1, accessor.annotationSize(SearchParameters.PARAMETERS));
    assertEquals("en", accessor.getLanguage());
    assertEquals("what am i thinking of", accessor.getQuery());
    assertEquals(Integer.valueOf(2), accessor.getResultSize());
    assertEquals(Integer.valueOf(1), accessor.getResultOffset());
    assertEquals(Double.valueOf(1.0), accessor.getThreshold());
    assertEquals("value", accessor.getParameter("single"));
    final List<String> values = accessor.getParameters("multi");
    assertNotNull(values);
    assertEquals(2, values.size());
    assertEquals("value1", values.get(0));
    assertEquals("value2", values.get(1));
    final Iterator<String> orderBy = accessor.getOrderByAttributeNames();
    assertTrue(orderBy.hasNext());
    String attributeName = orderBy.next();
    assertEquals("date", attributeName);
    assertEquals(OrderMode.DESC, accessor.getOrderMode(attributeName));
    assertTrue(orderBy.hasNext());
    attributeName = orderBy.next();
    assertEquals("size", attributeName);
    assertEquals(OrderMode.ASC, accessor.getOrderMode(attributeName));
    assertFalse(orderBy.hasNext());

    assertEquals("true", accessor.getParameter("bool"));
    assertTrue(builder.getParameters().getBooleanParameter("bool"));
  }

  /**
   * test setting of attribute values and accessing them.
   *
   * @throws Exception
   *           test fails.
   */
  public void testAttributes() throws Exception {
    final QueryBuilder builder = new QueryBuilder("pipeline");
    builder.addLiteral("string", "string").addLiteral("int", Integer.valueOf(1)).addLiteral("float",
      Double.valueOf(2)).addLiteral("bool", Boolean.TRUE);
    final Date now = new Date();
    builder.addLiteral("date", now);
    builder.addLiteral("multi", "value1").addLiteral("multi", "value2").addLiteral("multi", "value3");
    final Record record = builder.getQuery();
    assertNotNull(record);
    final QueryRecordAccessor accessor = new QueryRecordAccessor(record);
    assertEquals(1, accessor.literalSize("string"));
    assertEquals("string", accessor.getLiteral("string").getValue());
    assertEquals(1, accessor.literalSize("int"));
    assertEquals(Long.valueOf(1), accessor.getLiteral("int").getValue());
    assertEquals(1, accessor.literalSize("float"));
    assertEquals(Double.valueOf(2), accessor.getLiteral("float").getValue());
    assertEquals(1, accessor.literalSize("bool"));
    assertEquals(Boolean.TRUE, accessor.getLiteral("bool").getValue());
    assertEquals(1, accessor.literalSize("date"));
    assertEquals(now, accessor.getLiteral("date").getValue());
    final List<Literal> values = accessor.getLiterals("multi");
    final int expectedSize = 3;
    assertEquals(expectedSize, accessor.literalSize("multi"));
    assertEquals(expectedSize, values.size());
    assertEquals("value1", values.get(0).getValue());
    assertEquals("value2", values.get(1).getValue());
    assertEquals("value3", values.get(2).getValue());

    assertFalse(accessor.hasLiterals("no-values"));
    assertEquals(0, accessor.literalSize("no-values"));
    assertNull(accessor.getLiteral("no-values"));
    assertEquals(0, accessor.getLiterals("no-values").size());

  }


  /**
   * test setting of attribute annotations and accessing them.
   *
   * @throws Exception
   *           test fails
   */
  public void testAnnotations() throws Exception {
    final QueryBuilder builder = new QueryBuilder("pipeline");

    final List<String> filterValues = Arrays.asList(new String[] { "one", "two", "three" });
    builder.addEnumFilter("all", FilterMode.ALL, filterValues);
    builder.addEnumFilter("any", FilterMode.ANY, filterValues);
    builder.addEnumFilter("only", FilterMode.ONLY, filterValues);
    builder.addEnumFilter("none", FilterMode.NONE, filterValues);
    builder.addRangeFilter("min", FilterMode.ALL, Integer.valueOf(0), null);
    builder.addRangeFilter("max", FilterMode.ALL, null, Integer.valueOf(1));
    builder.addRangeFilter("range", FilterMode.ALL, Integer.valueOf(0), Integer.valueOf(1));
    builder.setBoostFactor("boost", 1.0);
    builder.setRankingName("rank", "magic");
    builder.setRankingName(null, "global");
    builder.setBoostFactor("rank2", 1.0);
    builder.setRankingName("rank2", "manual");
    final Record record = builder.getQuery();
    assertNotNull(record);
    final QueryRecordAccessor accessor = new QueryRecordAccessor(record);
    assertEnumFilter(accessor, "all", FilterMode.ALL, filterValues);
    assertEnumFilter(accessor, "any", FilterMode.ANY, filterValues);
    assertEnumFilter(accessor, "only", FilterMode.ONLY, filterValues);
    assertEnumFilter(accessor, "none", FilterMode.NONE, filterValues);
    assertRangeFilter(accessor, "min", FilterMode.ALL, Integer.valueOf(0), null);
    assertRangeFilter(accessor, "max", FilterMode.ALL, null, Integer.valueOf(1));
    assertRangeFilter(accessor, "range", FilterMode.ALL, Integer.valueOf(0), Integer.valueOf(1));
    assertEquals(Double.valueOf(1), accessor.getBoostFactor("boost"));
    assertEquals("magic", accessor.getRankingName("rank"));
    assertEquals("magic", accessor.getRankingName("rank"));
    assertEquals("global", accessor.getRankingName(null));
    assertEquals("manual", accessor.getRankingName("rank2"));
    assertEquals(Double.valueOf(1), accessor.getBoostFactor("rank2"));
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testAnnotationSpecials() throws Exception {
    final QueryBuilder builder = new QueryBuilder("pipeline");
    final Annotation annotation = builder.getQuery().getFactory().createAnnotation();
    annotation.addAnonValue("value1");
    annotation.addAnonValue("value2");
    builder.getQuery().getMetadata().setAnnotation("values", annotation);
    builder.addAnnotationNamedValue("attribute", new String[] { "annotation" }, "name", "value");
    final MObject metadata = builder.getQuery().getMetadata();
    assertTrue(metadata.hasAttribute("attribute"));
    assertTrue(metadata.getAttribute("attribute").hasAnnotation("annotation"));
    assertEquals("value", metadata.getAttribute("attribute").getAnnotation("annotation").getNamedValue("name"));

    final QueryRecordAccessor accessor = new QueryRecordAccessor(builder.getQuery());
    assertEquals(1, accessor.getAnnotations("values").size());
    final List<String> values = accessor.getAnnotationValues("values");
    assertEquals(2, values.size());
    assertEquals("value1", values.get(0));
    assertEquals("value2", values.get(1));
    final List<String> noValues = accessor.getAnnotationValues("no-values");
    assertEquals(0, noValues.size());
    assertNull(accessor.getAnnotationValue("values", "no-name"));
    assertNull(accessor.getAnnotationValue("no-values", "no-name"));

    assertTrue(accessor.hasAnnotation("attribute", "annotation"));
    assertFalse(accessor.hasAnnotation("attribute", "no-annotation"));
    assertFalse(accessor.hasAnnotation("no-attribute", "no-annotation"));

    assertEquals(1, accessor.annotationSize("attribute", "annotation"));
    assertEquals(0, accessor.annotationSize("attribute", "no-annotation"));
    assertEquals(0, accessor.annotationSize("no-attribute", "no-annotation"));

    assertNull(accessor.getAnnotation("attribute", "no-annotation"));
    assertNull(accessor.getAnnotation("no-attribute", "no-annotation"));

    assertTrue(accessor.getAnnotations("attribute", "no-annotation").isEmpty());
    assertTrue(accessor.getAnnotations("no-attribute", "no-annotation").isEmpty());

  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testEmptyResult() throws Exception {
    final SearchResult impl = new SearchResultImpl(null, null);
    final ResultAccessor accessor = new ResultAccessor(impl);
    assertNotNull(accessor.getResult());
    assertNull(accessor.getWorkflowName());
    assertFalse(accessor.hasQuery());
    assertNull(accessor.getQuery());
    assertFalse(accessor.hasRecords());
    assertEquals(0, accessor.recordsSize());
    assertNull(accessor.getResultRecord(0));
  }

  /**
   * check enum filter.
   *
   * @param accessor
   *          accessor
   * @param attributeName
   *          attribute name
   * @param mode
   *          expected filter mode
   * @param filterValues
   *          expected filter values.
   */
  private void assertEnumFilter(final QueryRecordAccessor accessor, final String attributeName,
    final FilterMode mode, final List<String> filterValues) {
    assertTrue(accessor.hasAnnotation(attributeName, SearchAnnotations.FILTER));
    assertEquals(1, accessor.annotationSize(attributeName, SearchAnnotations.FILTER));
    assertEquals(1, accessor.getAnnotations(attributeName, SearchAnnotations.FILTER).size());
    final Annotation filter = accessor.getAnnotation(attributeName, SearchAnnotations.FILTER);
    assertNotNull(filter);
    assertEquals(FilterType.ENUMERATION.toString(), filter.getNamedValue(SearchAnnotations.FILTER_TYPE));
    assertEquals(mode.toString(), filter.getNamedValue(SearchAnnotations.FILTER_MODE));
    assertEquals(filterValues, filter.getAnonValues());
  }

  /**
   * check range filter.
   *
   * @param accessor
   *          accessor
   * @param attributeName
   *          attribute name
   * @param mode
   *          expected filter mode
   * @param min
   *          expected lower bound
   * @param max
   *          expected upper bound
   */
  private void assertRangeFilter(final QueryRecordAccessor accessor, final String attributeName,
    final FilterMode mode, final Integer min, final Integer max) {
    assertEquals(1, accessor.annotationSize(attributeName, SearchAnnotations.FILTER));
    assertEquals(1, accessor.getAnnotations(attributeName, SearchAnnotations.FILTER).size());
    final Annotation filter = accessor.getAnnotation(attributeName, SearchAnnotations.FILTER);
    assertNotNull(filter);
    assertEquals(FilterType.RANGE.toString(), filter.getNamedValue(SearchAnnotations.FILTER_TYPE));
    assertEquals(mode.toString(), filter.getNamedValue(SearchAnnotations.FILTER_MODE));
    if (min == null) {
      assertNull(filter.getNamedValue(SearchAnnotations.FILTER_MIN));
    } else {
      assertEquals(min.toString(), filter.getNamedValue(SearchAnnotations.FILTER_MIN));
    }
    if (max == null) {
      assertNull(filter.getNamedValue(SearchAnnotations.FILTER_MAX));
    } else {
      assertEquals(max.toString(), filter.getNamedValue(SearchAnnotations.FILTER_MAX));
    }
  }

}
