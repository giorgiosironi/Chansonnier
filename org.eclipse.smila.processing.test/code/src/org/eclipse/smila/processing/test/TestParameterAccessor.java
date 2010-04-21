/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfigurationLoader;
import org.eclipse.smila.processing.parameters.MissingParameterException;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * Base class for WorkflowProcessor tests.
 *
 * @author jschumacher
 *
 */
public class TestParameterAccessor extends DeclarativeServiceTestCase {

  /**
   * BlackboardService instance to use.
   */
  private Blackboard _blackboard;

  /**
   * Check if WorkflowProcessor service is active. Wait up to 30 seconds for start. Fail, if no service is starting.
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testSingleStringParameter() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testSingleStringParameter");
    final Annotation parameters = _blackboard.createAnnotation(id);
    parameters.setNamedValue("name", "value");
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);
    final ParameterAccessor accessor = new ParameterAccessor(_blackboard);
    accessor.setCurrentRecord(id);
    assertEquals("value", accessor.getParameter("name", "default"));
    assertEquals("value", accessor.getRequiredParameter("name"));
    assertMissingRequiredParameter(accessor, "no-name");
    assertEquals("default", accessor.getParameter("no-name", "default"));
    _blackboard.invalidate(id);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testSingleStringParameterWithQuery() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testSingleStringParameterWithQuery");
    final Annotation parameters = _blackboard.createAnnotation(id);
    parameters.setNamedValue("name", "value1");
    parameters.setNamedValue("record-name", "value2");
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);

    final Id query = createBlackboardRecord(getClass().getName(), "testSingleStringParameterWithQuery-Query");
    final Annotation queryParameters = _blackboard.createAnnotation(id);
    queryParameters.setNamedValue("name", "value3");
    queryParameters.setNamedValue("query-name", "value4");
    _blackboard.setAnnotation(query, null, SearchParameters.PARAMETERS, queryParameters);

    final ParameterAccessor accessor = new ParameterAccessor(_blackboard, query);
    assertEquals("value3", accessor.getParameter("name", "default"));
    assertEquals("default", accessor.getParameter("record-name", "default"));
    assertEquals("value4", accessor.getParameter("query-name", "default"));
    assertEquals("default", accessor.getParameter("no-name", "default"));
    assertEquals("value3", accessor.getRequiredParameter("name"));
    assertMissingRequiredParameter(accessor, "record-name");
    assertEquals("value4", accessor.getRequiredParameter("query-name"));
    assertMissingRequiredParameter(accessor, "no-name");

    accessor.setCurrentRecord(id);
    assertEquals("value1", accessor.getParameter("name", "default"));
    assertEquals("value2", accessor.getParameter("record-name", "default"));
    assertEquals("value4", accessor.getParameter("query-name", "default"));
    assertEquals("default", accessor.getParameter("no-name", "default"));
    assertEquals("value1", accessor.getRequiredParameter("name"));
    assertEquals("value2", accessor.getRequiredParameter("record-name"));
    assertEquals("value4", accessor.getRequiredParameter("query-name"));
    assertMissingRequiredParameter(accessor, "no-name");
    _blackboard.invalidate(id);
    _blackboard.invalidate(query);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testSingleStringParameterWithQueryOrConfig() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testSingleStringParameterWithQueryOrConfig");
    final Annotation parameters = _blackboard.createAnnotation(id);
    parameters.setNamedValue("name", "value1");
    parameters.setNamedValue("record-name", "value2");
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);

    final Id query =
      createBlackboardRecord(getClass().getName(), "testSingleStringParameterWithQueryOrConfig-Query");
    final Annotation queryParameters = _blackboard.createAnnotation(id);
    queryParameters.setNamedValue("name", "value3");
    queryParameters.setNamedValue("query-name", "value4");
    _blackboard.setAnnotation(query, null, SearchParameters.PARAMETERS, queryParameters);

    final PipeletConfiguration config = loadConfig("TestParameterAccessorConfig.xml");

    ParameterAccessor accessor = new ParameterAccessor(_blackboard);
    accessor.setPipeletConfiguration(config);
    assertEquals("value5-1", accessor.getParameter("name", "default"));
    assertEquals("default", accessor.getParameter("record-name", "default"));
    assertEquals("default", accessor.getParameter("query-name", "default"));
    assertEquals("value6-1", accessor.getParameter("config-name", "default"));
    assertEquals("default", accessor.getParameter("no-name", "default"));
    assertEquals("value5-1", accessor.getRequiredParameter("name"));
    assertMissingRequiredParameter(accessor, "record-name");
    assertMissingRequiredParameter(accessor, "query-name");
    assertEquals("value6-1", accessor.getRequiredParameter("config-name"));
    assertMissingRequiredParameter(accessor, "no-name");

    accessor.setCurrentRecord(id);
    assertEquals("value1", accessor.getParameter("name", "default"));
    assertEquals("value2", accessor.getParameter("record-name", "default"));
    assertEquals("default", accessor.getParameter("query-name", "default"));
    assertEquals("value6-1", accessor.getParameter("config-name", "default"));
    assertEquals("default", accessor.getParameter("no-name", "default"));
    assertEquals("value1", accessor.getRequiredParameter("name"));
    assertEquals("value2", accessor.getRequiredParameter("record-name"));
    assertMissingRequiredParameter(accessor, "query-name");
    assertEquals("value6-1", accessor.getRequiredParameter("config-name"));
    assertMissingRequiredParameter(accessor, "no-name");

    accessor = new ParameterAccessor(_blackboard, query);
    accessor.setPipeletConfiguration(config);
    assertEquals("value3", accessor.getParameter("name", "default"));
    assertEquals("default", accessor.getParameter("record-name", "default"));
    assertEquals("value4", accessor.getParameter("query-name", "default"));
    assertEquals("value6-1", accessor.getParameter("config-name", "default"));
    assertEquals("default", accessor.getParameter("no-name", "default"));
    assertEquals("value3", accessor.getRequiredParameter("name"));
    assertMissingRequiredParameter(accessor, "record-name");
    assertEquals("value4", accessor.getRequiredParameter("query-name"));
    assertEquals("value6-1", accessor.getRequiredParameter("config-name"));
    assertMissingRequiredParameter(accessor, "no-name");

    accessor.setCurrentRecord(id);
    assertEquals("value1", accessor.getParameter("name", "default"));
    assertEquals("value2", accessor.getParameter("record-name", "default"));
    assertEquals("value4", accessor.getParameter("query-name", "default"));
    assertEquals("value6-1", accessor.getParameter("config-name", "default"));
    assertEquals("default", accessor.getParameter("no-name", "default"));
    assertEquals("value1", accessor.getRequiredParameter("name"));
    assertEquals("value2", accessor.getRequiredParameter("record-name"));
    assertEquals("value4", accessor.getRequiredParameter("query-name"));
    assertEquals("value6-1", accessor.getRequiredParameter("config-name"));
    assertMissingRequiredParameter(accessor, "no-name");
    _blackboard.invalidate(id);
    _blackboard.invalidate(query);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testStringListParameter() throws Exception {
    final List<String> expectedValues = Arrays.asList(new String[] { "value1", "value2" });
    final Id id = createBlackboardRecord(getClass().getName(), "testMultipleStringParameter");
    final Annotation parameters = _blackboard.createAnnotation(id);
    addParameters(id, parameters, "name", expectedValues);
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);
    final ParameterAccessor accessor = new ParameterAccessor(_blackboard);
    accessor.setCurrentRecord(id);
    assertEquals(expectedValues, accessor.getParameters("name"));
    assertEquals(expectedValues, accessor.getRequiredParameters("name"));
    assertMissingRequiredParameters(accessor, "no-name");
    assertTrue(accessor.getParameters("no-name").isEmpty());
    _blackboard.invalidate(id);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testStringListParameterWithQuery() throws Exception {
    final List<String> expectedValues1 = Arrays.asList(new String[] { "value1-1", "value1-2" });
    final List<String> expectedValues2 = Arrays.asList(new String[] { "value2-1", "value2-2" });
    final List<String> expectedValues3 = Arrays.asList(new String[] { "value3-1", "value3-2" });
    final List<String> expectedValues4 = Arrays.asList(new String[] { "value4-1", "value4-2" });

    final Id id = createBlackboardRecord(getClass().getName(), "testStringListParameterWithQuery");
    final Annotation parameters = _blackboard.createAnnotation(id);
    addParameters(id, parameters, "name", expectedValues1);
    addParameters(id, parameters, "record-name", expectedValues2);
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);

    final Id query = createBlackboardRecord(getClass().getName(), "testStringListParameterWithQuery-Query");
    final Annotation queryParameters = _blackboard.createAnnotation(id);
    addParameters(query, queryParameters, "name", expectedValues3);
    addParameters(query, queryParameters, "query-name", expectedValues4);
    _blackboard.setAnnotation(query, null, SearchParameters.PARAMETERS, queryParameters);

    final ParameterAccessor accessor = new ParameterAccessor(_blackboard, query);
    assertEquals(expectedValues3, accessor.getParameters("name"));
    assertTrue(accessor.getParameters("record-name").isEmpty());
    assertEquals(expectedValues4, accessor.getParameters("query-name"));
    assertTrue(accessor.getParameters("no-name").isEmpty());
    assertEquals(expectedValues3, accessor.getRequiredParameters("name"));
    assertMissingRequiredParameters(accessor, "record-name");
    assertEquals(expectedValues4, accessor.getRequiredParameters("query-name"));
    assertMissingRequiredParameters(accessor, "no-name");

    accessor.setCurrentRecord(id);
    assertEquals(expectedValues1, accessor.getParameters("name"));
    assertEquals(expectedValues2, accessor.getParameters("record-name"));
    assertEquals(expectedValues4, accessor.getParameters("query-name"));
    assertTrue(accessor.getParameters("no-name").isEmpty());
    assertEquals(expectedValues1, accessor.getRequiredParameters("name"));
    assertEquals(expectedValues2, accessor.getRequiredParameters("record-name"));
    assertEquals(expectedValues4, accessor.getRequiredParameters("query-name"));
    assertMissingRequiredParameters(accessor, "no-name");

    _blackboard.invalidate(id);
    _blackboard.invalidate(query);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testStringListParameterWithQueryOrConfig() throws Exception {
    final List<String> expectedValues1 = Arrays.asList(new String[] { "value1-1", "value1-2" });
    final List<String> expectedValues2 = Arrays.asList(new String[] { "value2-1", "value2-2" });
    final List<String> expectedValues3 = Arrays.asList(new String[] { "value3-1", "value3-2" });
    final List<String> expectedValues4 = Arrays.asList(new String[] { "value4-1", "value4-2" });
    final List<String> expectedValues5 = Arrays.asList(new String[] { "value5-1", "value5-2" });
    final List<String> expectedValues6 = Arrays.asList(new String[] { "value6-1", "value6-2" });

    final Id id = createBlackboardRecord(getClass().getName(), "testStringListParameterWithQueryOrConfig");
    final Annotation parameters = _blackboard.createAnnotation(id);
    addParameters(id, parameters, "name", expectedValues1);
    addParameters(id, parameters, "record-name", expectedValues2);
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);

    final Id query = createBlackboardRecord(getClass().getName(), "testStringListParameterWithQueryOrConfig-Query");
    final Annotation queryParameters = _blackboard.createAnnotation(id);
    addParameters(query, queryParameters, "name", expectedValues3);
    addParameters(query, queryParameters, "query-name", expectedValues4);
    _blackboard.setAnnotation(query, null, SearchParameters.PARAMETERS, queryParameters);

    final PipeletConfiguration config = loadConfig("TestParameterAccessorConfig.xml");
    ParameterAccessor accessor = new ParameterAccessor(_blackboard);
    accessor.setPipeletConfiguration(config);
    assertEquals(expectedValues5, accessor.getParameters("name"));
    assertTrue(accessor.getParameters("record-name").isEmpty());
    assertTrue(accessor.getParameters("query-name").isEmpty());
    assertEquals(expectedValues6, accessor.getParameters("config-name"));
    assertTrue(accessor.getParameters("no-name").isEmpty());
    assertEquals(expectedValues5, accessor.getRequiredParameters("name"));
    assertMissingRequiredParameters(accessor, "record-name");
    assertMissingRequiredParameters(accessor, "query-name");
    assertEquals(expectedValues6, accessor.getRequiredParameters("config-name"));
    assertMissingRequiredParameters(accessor, "no-name");

    accessor.setCurrentRecord(id);
    assertEquals(expectedValues1, accessor.getParameters("name"));
    assertEquals(expectedValues2, accessor.getParameters("record-name"));
    assertTrue(accessor.getParameters("query-name").isEmpty());
    assertEquals(expectedValues6, accessor.getParameters("config-name"));
    assertTrue(accessor.getParameters("no-name").isEmpty());
    assertEquals(expectedValues1, accessor.getRequiredParameters("name"));
    assertEquals(expectedValues2, accessor.getRequiredParameters("record-name"));
    assertMissingRequiredParameters(accessor, "query-name");
    assertEquals(expectedValues6, accessor.getRequiredParameters("config-name"));
    assertMissingRequiredParameters(accessor, "no-name");

    accessor = new ParameterAccessor(_blackboard, query);
    accessor.setPipeletConfiguration(config);
    assertEquals(expectedValues3, accessor.getParameters("name"));
    assertTrue(accessor.getParameters("record-name").isEmpty());
    assertEquals(expectedValues4, accessor.getParameters("query-name"));
    assertEquals(expectedValues6, accessor.getParameters("config-name"));
    assertTrue(accessor.getParameters("no-name").isEmpty());
    assertEquals(expectedValues3, accessor.getRequiredParameters("name"));
    assertMissingRequiredParameters(accessor, "record-name");
    assertEquals(expectedValues4, accessor.getRequiredParameters("query-name"));
    assertEquals(expectedValues6, accessor.getRequiredParameters("config-name"));
    assertMissingRequiredParameters(accessor, "no-name");

    accessor.setCurrentRecord(id);
    assertEquals(expectedValues1, accessor.getParameters("name"));
    assertEquals(expectedValues2, accessor.getParameters("record-name"));
    assertEquals(expectedValues4, accessor.getParameters("query-name"));
    assertEquals(expectedValues6, accessor.getParameters("config-name"));
    assertTrue(accessor.getParameters("no-name").isEmpty());
    assertEquals(expectedValues1, accessor.getRequiredParameters("name"));
    assertEquals(expectedValues2, accessor.getRequiredParameters("record-name"));
    assertEquals(expectedValues4, accessor.getRequiredParameters("query-name"));
    assertEquals(expectedValues6, accessor.getRequiredParameters("config-name"));
    assertMissingRequiredParameters(accessor, "no-name");

    _blackboard.invalidate(id);
    _blackboard.invalidate(query);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testDatatypes() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testDatatypes");
    final Annotation parameters = _blackboard.createAnnotation(id);
    parameters.setNamedValue("int", "1");
    parameters.setNamedValue("float", Double.toString(Math.PI));
    parameters.setNamedValue("bool", "true");
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);
    final ParameterAccessor accessor = new ParameterAccessor(_blackboard, id);
    assertEquals(1, accessor.getIntParameter("int", 0).intValue());
    assertEquals(Math.PI, accessor.getFloatParameter("float", 0.0).doubleValue());
    assertEquals(true, accessor.getBooleanParameter("bool", false).booleanValue());
    assertEquals(1, accessor.getRequiredIntParameter("int").intValue());
    assertEquals(Math.PI, accessor.getRequiredFloatParameter("float").doubleValue());
    assertEquals(true, accessor.getRequiredBooleanParameter("bool").booleanValue());
    assertEquals(2, accessor.getIntParameter("no-int", 2).intValue());
    assertEquals(Math.E, accessor.getFloatParameter("no-float", Math.E).doubleValue());
    assertEquals(false, accessor.getBooleanParameter("no-bool", false).booleanValue());
    _blackboard.invalidate(id);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testKnownParameters() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testKnownParameters");
    final Annotation parameters = _blackboard.createAnnotation(id);
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);
    final ParameterAccessor accessor = new ParameterAccessor(_blackboard, id);
    assertNull(accessor.getQuery());
    assertEquals(SearchParameters.DEFAULT_RESULTSIZE, accessor.getResultSize());
    assertEquals(SearchParameters.DEFAULT_RESULTOFFSET, accessor.getResultOffset());
    assertEquals(SearchParameters.DEFAULT_THRESHOLD, accessor.getThreshold());
    assertNull(accessor.getLanguage());
    assertNull(accessor.getIndexName());
    assertTrue(accessor.getResultAttributes().isEmpty());

    parameters.setNamedValue(SearchParameters.QUERY, "Hello World");
    parameters.setNamedValue(SearchParameters.RESULTSIZE, "2");
    parameters.setNamedValue(SearchParameters.RESULTOFFSET, "1");
    parameters.setNamedValue(SearchParameters.THRESHOLD, "0.5");
    parameters.setNamedValue(SearchParameters.LANGUAGE, "en");
    parameters.setNamedValue(SearchParameters.INDEXNAME, "my-index");
    final List<String> expectedAttributes = Arrays.asList(new String[] { "title", "uri" });
    addParameters(id, parameters, SearchParameters.RESULTATTRIBUTES, expectedAttributes);

    assertEquals("Hello World", accessor.getQuery());
    assertEquals(2, accessor.getResultSize());
    assertEquals(1, accessor.getResultOffset());
    assertEquals(1.0 / 2.0, accessor.getThreshold());
    assertEquals("en", accessor.getLanguage());
    assertEquals("my-index", accessor.getIndexName());
    assertEquals(expectedAttributes, accessor.getResultAttributes());
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testOrderBy() throws Exception {
    final Id id = createBlackboardRecord(getClass().getName(), "testOrderBy");
    final Annotation parameters = _blackboard.createAnnotation(id);
    final Annotation orderBy1 = _blackboard.createAnnotation(id);
    orderBy1.setNamedValue(SearchParameters.ORDERBY_ATTRIBUTE, "att1");
    orderBy1.setNamedValue(SearchParameters.ORDERBY_MODE, SearchParameters.OrderMode.ASC.toString());
    parameters.addAnnotation(SearchParameters.ORDERBY, orderBy1);
    final Annotation orderBy2 = _blackboard.createAnnotation(id);
    orderBy2.setNamedValue(SearchParameters.ORDERBY_ATTRIBUTE, "att2");
    orderBy2.setNamedValue(SearchParameters.ORDERBY_MODE, SearchParameters.OrderMode.DESC.toString());
    parameters.addAnnotation(SearchParameters.ORDERBY, orderBy2);
    _blackboard.setAnnotation(id, null, SearchParameters.PARAMETERS, parameters);

    final ParameterAccessor accessor = new ParameterAccessor(_blackboard, id);
    final Iterator<String> names = accessor.getOrderByAttributeNames();
    assertTrue(names.hasNext());
    String name = names.next();
    assertEquals("att1", name);
    assertEquals(SearchParameters.OrderMode.ASC, accessor.getOrderMode(name));
    assertTrue(names.hasNext());
    name = names.next();
    assertEquals("att2", name);
    assertEquals(SearchParameters.OrderMode.DESC, accessor.getOrderMode(name));
    assertFalse(names.hasNext());

    _blackboard.invalidate(id);
  }

  /**
   * create a new record on the blackboard.
   *
   * @param source
   *          source value of ID
   * @param key
   *          key value of ID
   * @return id of created record.
   */
  protected Id createBlackboardRecord(final String source, final String key) {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, key);
    _log.info("Invalidating and re-creating test record on blackboard.");
    _log.info("This may cause an exception to be logged that can be safely ignored.");
    _blackboard.invalidate(id);
    _blackboard.create(id);
    return id;
  }

  /**
   * add list parameter.
   * 
   * @param id
   *          record id
   * @param parameters
   *          parameter annotation
   * @param name
   *          parameter name
   * @param values
   *          parameter values
   * @throws BlackboardAccessException
   *           error creating the annotation
   */
  private void addParameters(final Id id, final Annotation parameters, final String name, final List<String> values)
    throws BlackboardAccessException {
    final Annotation listParam = _blackboard.createAnnotation(id);
    for (final String value : values) {
      listParam.addAnonValue(value);
    }
    parameters.setAnnotation(name, listParam);
  }

  /**
   * assert that getRequiredParameter throws the correct exception.
   *
   * @param accessor
   *          parameter accessor
   * @param name
   *          parameter name
   */
  private void assertMissingRequiredParameter(final ParameterAccessor accessor, final String name) {
    try {
      accessor.getRequiredParameter(name);
      fail("expected exception missing");
    } catch (final Exception ex) {
      assertTrue(ex instanceof MissingParameterException);
    }
  }

  /**
   * assert that getRequiredParameters throws the correct exception.
   *
   * @param accessor
   *          parameter accessor
   * @param name
   *          parameter name
   */
  private void assertMissingRequiredParameters(final ParameterAccessor accessor, final String name) {
    try {
      accessor.getRequiredParameters(name);
      fail("expected exception missing");
    } catch (final Exception ex) {
      assertTrue(ex instanceof MissingParameterException);
    }
  }

  /**
   * load pipelet config for tests from config folder.
   *
   * @param filename
   *          config filename
   * @return parsed config
   * @throws JAXBException
   *           parse error
   * @throws IOException
   *           file not found
   */
  private PipeletConfiguration loadConfig(final String filename) throws JAXBException, IOException {
    final Unmarshaller unmarshaller = PipeletConfigurationLoader.createPipeletConfigurationUnmarshaller();
    final InputStream inputStream = new FileInputStream(new File(ConfigUtils.getConfigurationFolder(), filename));
    try {
      return (PipeletConfiguration) unmarshaller.unmarshal(inputStream);
    } finally {
      inputStream.close();
    }
  }

}
