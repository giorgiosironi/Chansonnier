/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.pipelets.SubAttributeExtractorPipelet;

/**
 * Test case for the SubAttributeExtractorPipelet.
 */
public class TestSubAttributeExtractorPipelet extends APipeletTest {
  /**
   * bundle name for configuration loading.
   */
  public static final String CONFIG_BUNDLE = "org.eclipse.smila.processing.pipelets";

  /**
   * name of configuration to extract the first value.
   */
  public static final String CONFIG_EXTRACT_FIRST = "sub-attribute-extractor-first.xml";

  /**
   * name of configuration to extract the last value.
   */
  public static final String CONFIG_EXTRACT_LAST = "sub-attribute-extractor-last.xml";

  /**
   * name of configuration to extract all values as list.
   */
  public static final String CONFIG_EXTRACT_LIST = "sub-attribute-extractor-list.xml";

  /**
   * name of configuration to extract all values as one.
   */
  public static final String CONFIG_EXTRACT_ONE = "sub-attribute-extractor-one.xml";

  /**
   * A set of test values.
   */
  private final String[] _values = { "This", "is", "a", "test" };

  /**
   * Another set of test values.
   */
  private final String[] _moreValues = { "Just", "another", "test", "case" };

  /**
   * create and configure SubAttributeExtractorPipelet instance.
   * 
   * @param configName
   *          name of configuration file.
   * @return configured pipelet.
   * @throws ProcessingException
   *           error configuring pipelet
   * @throws JAXBException
   *           error loading configuration
   */
  public SubAttributeExtractorPipelet createPipelet(final String configName) throws ProcessingException,
    JAXBException {
    final SubAttributeExtractorPipelet pipelet = new SubAttributeExtractorPipelet();
    configurePipelet(pipelet, CONFIG_BUNDLE, configName);
    return pipelet;
  }

  /**
   * Test to extract the first value.
   * 
   * @throws Exception
   *           test failed
   */
  public void testFlatExtractFirst() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_FIRST);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(_values[0], textLiteral.getStringValue());
  }

  /**
   * Test to extract the last value.
   * 
   * @throws Exception
   *           test failed
   */
  public void testFlatExtractLast() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_LAST);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(_values[_values.length - 1], textLiteral.getStringValue());
  }

  /**
   * Test to extract all values as a list.
   * 
   * @throws Exception
   *           test failed
   */
  public void testFlatExtractAllAsList() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_LIST);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    pipelet.process(getBlackboard(), new Id[] { id });

    final List<Literal> list = getBlackboard().getLiterals(id, pipelet.getOutputPath());
    assertEquals(_values.length, list.size());
  }

  /**
   * Test to extract all values as one.
   * 
   * @throws Exception
   *           test failed
   */
  public void testFlatExtractAllAsOne() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_ONE);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    pipelet.process(getBlackboard(), new Id[] { id });

    final String expectedValue =
      _values[0] + pipelet.getSeparator() + _values[1] + pipelet.getSeparator() + _values[2]
        + pipelet.getSeparator() + _values[_values.length - 1];
    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(expectedValue, textLiteral.getStringValue());
  }

  // TODO: the following tests are disabled, because the pipelet does not support extraction inside lists of MObjects,
  // yet. Activate these tests if the pipelet supports this feature.

  /**
   * Test to extract the first value.
   * 
   * @throws Exception
   *           test failed
   */
  public void /* test */DeepExtractFirst() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_FIRST);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    addMObject(getBlackboard().getRecord(id).getFactory(), id, pipelet.getInputPath(), createLiterals(id,
      _moreValues));
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(_values[0], textLiteral.getStringValue());
  }

  /**
   * Test to extract the last value.
   * 
   * @throws Exception
   *           test failed
   */
  public void /* test */DeepExtractLast() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_LAST);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    addMObject(getBlackboard().getRecord(id).getFactory(), id, pipelet.getInputPath(), createLiterals(id,
      _moreValues));
    pipelet.process(getBlackboard(), new Id[] { id });

    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(_moreValues[_moreValues.length - 1], textLiteral.getStringValue());

  }

  /**
   * Test to extract all values as a list.
   * 
   * @throws Exception
   *           test failed
   */
  public void /* test */DeepExtractList() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_LIST);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    addMObject(getBlackboard().getRecord(id).getFactory(), id, pipelet.getInputPath(), createLiterals(id,
      _moreValues));
    pipelet.process(getBlackboard(), new Id[] { id });

    final List<Literal> list = getBlackboard().getLiterals(id, pipelet.getOutputPath());
    assertEquals(_values.length + _moreValues.length, list.size());
  }

  /**
   * Test to extract all values as one.
   * 
   * @throws Exception
   *           test failed
   */
  public void /* test */DeepExtractAllAsOne() throws Exception {
    final SubAttributeExtractorPipelet pipelet = createPipelet(CONFIG_EXTRACT_ONE);
    final Id id = createBlackboardRecord("test", "record-with-sub-attribute");

    setAttribute(id, pipelet.getInputPath(), createLiterals(id, _values));
    addMObject(getBlackboard().getRecord(id).getFactory(), id, pipelet.getInputPath(), createLiterals(id,
      _moreValues));
    pipelet.process(getBlackboard(), new Id[] { id });

    final String expectedValue =
      _values[0] + pipelet.getSeparator() + _values[1] + pipelet.getSeparator() + _values[2]
        + pipelet.getSeparator() + _values[_values.length - 1] + pipelet.getSeparator() + _moreValues[0]
        + pipelet.getSeparator() + _moreValues[1] + pipelet.getSeparator() + _moreValues[2]
        + pipelet.getSeparator() + _moreValues[_moreValues.length - 1];
    final Literal textLiteral = getAttribute(id, pipelet.getOutputPath());
    assertEquals(expectedValue, textLiteral.getStringValue());
  }

  /**
   * Set the attribute value.
   * 
   * @param id
   *          the record id
   * @param path
   *          the attribute path
   * @param values
   *          a List of Literal values to set
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private void setAttribute(final Id id, final Path path, List<Literal> values) throws BlackboardAccessException {
    getBlackboard().setLiterals(id, path, values);
  }

  /**
   * Gets the attribute value.
   * 
   * @param id
   *          the record id
   * @param path
   *          the attribute path
   * @return a Literal containing the attribute value
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private Literal getAttribute(final Id id, final Path path) throws BlackboardAccessException {
    return getBlackboard().getLiteral(id, path);
  }

  /**
   * Creates a list of Literal values from the given String values.
   * 
   * @param id
   *          the record id
   * @param values
   *          the String values
   * @return a List of Literal objects
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private List<Literal> createLiterals(final Id id, final String[] values) throws BlackboardAccessException {
    final ArrayList<Literal> list = new ArrayList<Literal>();
    if (values != null) {
      for (final String value : values) {
        final Literal literal = getBlackboard().createLiteral(id);
        literal.setStringValue(value);
        list.add(literal);
      }
    }
    return list;
  }

  /**
   * Adds an MObject to the top attribute specified by path, using the given values as Literals for the attribute below.
   * 
   * @param factory
   *          a RecordFactory
   * @param id
   *          the record id
   * @param path
   *          the attribute path
   * @param values
   *          some Literal values
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private void addMObject(final RecordFactory factory, final Id id, final Path path, final List<Literal> values)
    throws BlackboardAccessException {
    final Attribute subAttribute = createSubAttribute(factory, path, 0, values);
    getBlackboard().getRecord(id).getMetadata().getAttribute(subAttribute.getName()).addObject(
      subAttribute.getObject());
  }

  /**
   * Creates sub attributes for the given path, using the given values as final Literal values.
   * 
   * @param factory
   *          a RecordFactory
   * @param path
   *          the attribute path
   * @param index
   *          the current index of the path
   * @param values
   *          some Literal values
   * @return an Attribute object
   */
  private Attribute createSubAttribute(final RecordFactory factory, final Path path, int index,
    final List<Literal> values) {
    final Attribute attribute = factory.createAttribute();
    attribute.setName(path.getName(index));
    if (index + 1 < path.length()) {
      // fill with MObject
      final MObject mobject = factory.createMetadataObject();
      attribute.setObject(mobject);
      final Attribute subAttribute = createSubAttribute(factory, path, ++index, values);
      mobject.setAttribute(subAttribute.getName(), subAttribute);
    } else {
      // fill Literals
      attribute.setLiterals(values);
    }
    return attribute;
  }
}
