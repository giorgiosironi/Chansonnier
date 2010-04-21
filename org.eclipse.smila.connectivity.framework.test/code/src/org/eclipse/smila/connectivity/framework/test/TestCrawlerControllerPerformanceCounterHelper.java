/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

// CHECKSTYLE:OFF

import junit.framework.TestCase;

/**
 * // TODO The Class TestCrawlerControllerPerformanceCounterHelper.
 * 
 * @author Alexander Eliseyev
 */
public class TestCrawlerControllerPerformanceCounterHelper extends TestCase {

  public void testDummy() {
    // new test for new helper?
  }

  // /** The Constant CONNECTION_ID. */
  // private static final String CONNECTION_ID = "testId";
  //
  // /** The Constant CRAWLER_HASHCODE. */
  // private static final int CRAWLER_HASHCODE = 777;
  //
  // /** The Constant GLOBAL_MASK. */
  // private static final String GLOBAL_MASK = "Crawlers,crawler=%s,name=Total,counter=%s";
  //
  // /** The Constant LOCAL_MASK. */
  // private static final String LOCAL_MASK = "Crawlers,crawler=%s,name=Instances,hash=%s,counter=%s";
  //
  // /** The Constant TOTAL_MASK. */
  // private static final String TOTAL_MASK = "Total,counter=%s";
  //
  // /** The Constant CATEGORY_NAME. */
  // private static final String CATEGORY_NAME = "SMILA Crawlers";
  //  
  // /** The controller performance counter helper. */
  // private CrawlerControllerPerformanceCounterHelper _controllerPerformanceCounterHelper;
  //
  // /** The Enum CounterScope. */
  // private enum CounterScope {
  // GLOBAL, LOCAL, TOTAL
  // }
  //
  // /** The Class CounterScopesValue. */
  // private class CounterScopesValue {
  // double total;
  // double local;
  // double global;
  // }
  //  
  // /**
  // * Test critical exception counter.
  // */
  // public void testCriticalExceptionCounter() {
  // final String counterType = "exceptions(critical)";
  // final CounterScopesValue value = getNextValue(counterType);
  //
  // _controllerPerformanceCounterHelper.incrementCriticalExceptionCounter();
  // sampleAll(counterType);
  //    
  // assertValueIsGreaterBy(value, getNextValue(counterType), 1);
  // }
  //  
  // /**
  // * Test exception counter.
  // */
  // public void testExceptionCounter() {
  // final String counterType = "exceptions(non-critical)";
  // final CounterScopesValue value = getNextValue(counterType);
  //
  // _controllerPerformanceCounterHelper.incrementExceptionCounter();
  // sampleAll(counterType);
  //    
  // assertValueIsGreaterBy(value, getNextValue(counterType), 1);
  // }
  //  
  // /**
  // * Test delta index counter.
  // */
  // public void testDeltaIndexCounter() {
  // final String counterType = "delta-indices";
  // final CounterScopesValue value = getNextValue(counterType);
  //    
  // _controllerPerformanceCounterHelper.incrementDeltaIndexCounter();
  // sampleAll(counterType);
  //    
  // assertValueIsGreaterBy(value, getNextValue(counterType), 1);
  //    
  // _controllerPerformanceCounterHelper.incrementDeltaIndexCounter(2);
  // sampleAll(counterType);
  //    
  // assertValueIsGreaterBy(value, getNextValue(counterType), 3);
  // }
  //  
  // /**
  // * Assert value greather by.
  // *
  // * @param value
  // * the value
  // * @param nextValue
  // * the next value
  // * @param greaterBy
  // * the greater by
  // */
  // private static void assertValueIsGreaterBy(CounterScopesValue value, CounterScopesValue nextValue, double
  // greaterBy) {
  // assertEquals(value.total + greaterBy, nextValue.total);
  // assertEquals(value.local + greaterBy, nextValue.total);
  // assertEquals(value.global + greaterBy, nextValue.global);
  // }
  //  
  // /**
  // * Gets the next value.
  // *
  // * @param name
  // * the name
  // *
  // * @return the next value
  // */
  // private CounterScopesValue getNextValue(String name) {
  // final CounterScopesValue value = new CounterScopesValue();
  // value.total = getNextValue(name, CounterScope.TOTAL);
  // value.local = getNextValue(name, CounterScope.LOCAL);
  // value.global = getNextValue(name, CounterScope.GLOBAL);
  // return value;
  // }
  //  
  // /**
  // * Gets the next value.
  // *
  // * @param name
  // * the name
  // * @param counterScope
  // * the counter scope
  // *
  // * @return the next value
  // */
  // private double getNextValue(String name, CounterScope counterScope) {
  // try {
  // final PerformanceCounter counter = CounterRegistry.INSTANCE.getCounter(CATEGORY_NAME,
  // getRegisteredCounterName(name, counterScope));
  // assertNotNull(counter);
  // return counter.getNextValue();
  // } catch (final CounterDoesntExistException e) {
  // fail(e.getMessage());
  // return 0;
  // }
  // }
  //  
  // /**
  // * Sample all.
  // *
  // * @param name
  // * the name
  // */
  // private void sampleAll(String name) {
  // sample(name, CounterScope.TOTAL);
  // sample(name, CounterScope.LOCAL);
  // sample(name, CounterScope.GLOBAL);
  // }
  //  
  // /**
  // * Sample.
  // *
  // * @param name
  // * the name
  // * @param counterScope
  // * the counter scope
  // */
  // private void sample(String name, CounterScope counterScope) {
  // try {
  // final PerformanceCounter counter = CounterRegistry.INSTANCE.getCounter(CATEGORY_NAME,
  // getRegisteredCounterName(name, counterScope));
  // assertNotNull(counter);
  // counter.getNextPerformanceSample();
  // } catch (final CounterDoesntExistException e) {
  // fail(e.getMessage());
  // }
  // }
  //  
  // /**
  // * Gets the registered counter name.
  // *
  // * @param name
  // * the name
  // * @param counterScope
  // * the counter type
  // *
  // * @return the registered counter name
  // */
  // private String getRegisteredCounterName(String name, CounterScope counterScope) {
  // switch (counterScope) {
  // case GLOBAL:
  // return String.format(GLOBAL_MASK, CONNECTION_ID, name);
  // case LOCAL:
  // return String.format(LOCAL_MASK, CONNECTION_ID, String.valueOf(CRAWLER_HASHCODE), name);
  // case TOTAL:
  // return String.format(TOTAL_MASK, name);
  // default:
  // throw new IllegalArgumentException(counterScope.toString());
  // }
  // }
  //
  // /**
  // * {@inheritDoc}
  // */
  // protected void setUp() throws Exception {
  // super.setUp();
  //
  // final DataConnectionID connectionID = new DataConnectionID();
  // connectionID.setId(CONNECTION_ID);
  // connectionID.setType(DataConnectionType.AGENT);
  //
  // final DataSourceConnectionConfig configuration = new DataSourceConnectionConfig();
  // configuration.setDataConnectionID(connectionID);
  //
  // _controllerPerformanceCounterHelper =
  // new CrawlerControllerPerformanceCounterHelper(configuration, CRAWLER_HASHCODE);
  // }

}

// CHECKTYLE:ON
