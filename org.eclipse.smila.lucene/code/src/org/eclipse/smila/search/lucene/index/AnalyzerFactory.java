/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.lucene.messages.indexstructure.DAnalyzer;
import org.eclipse.smila.search.lucene.messages.indexstructure.DIndexField;
import org.eclipse.smila.search.lucene.messages.indexstructure.DIndexStructure;
import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.set.DBoolean;
import org.eclipse.smila.search.utils.param.set.DDate;
import org.eclipse.smila.search.utils.param.set.DDateList;
import org.eclipse.smila.search.utils.param.set.DFloat;
import org.eclipse.smila.search.utils.param.set.DFloatList;
import org.eclipse.smila.search.utils.param.set.DInteger;
import org.eclipse.smila.search.utils.param.set.DIntegerList;
import org.eclipse.smila.search.utils.param.set.DParameter;
import org.eclipse.smila.search.utils.param.set.DParameterSet;
import org.eclipse.smila.search.utils.param.set.DString;
import org.eclipse.smila.search.utils.param.set.DStringList;

public abstract class AnalyzerFactory {

  private AnalyzerFactory() {
  }

  public static Analyzer getAnalyzer(DIndex dIndex) throws IndexException {
    return getAnalyzer(((DIndexStructure) dIndex.getIndexStructure()).getAnalyzer(), (DIndexStructure) dIndex
      .getIndexStructure());
  }

  public static Analyzer getAnalyzer(org.eclipse.smila.search.utils.indexstructure.DIndexStructure dIndexStructure)
    throws IndexException {
    return getAnalyzer(((DIndexStructure) dIndexStructure).getAnalyzer(), (DIndexStructure) dIndexStructure);
  }

  public static Analyzer getAnalyzer(DAnalyzer dAnalyzer, DIndexStructure dIndexStructure) throws IndexException {
    Analyzer analyzer = null;

    // lookup fields fith analyzer
    final List<DIndexField> fieldsWithAnalyzer = new ArrayList<DIndexField>();
    for (final Iterator it = dIndexStructure.getFields(); it.hasNext();) {
      final DIndexField field = (DIndexField) it.next();
      if (field.getAnalyzer() != null) {
        fieldsWithAnalyzer.add(field);
      }
    }

    if (fieldsWithAnalyzer.size() > 0) {
      // use per field analyzer when index fields have a analyzer configured
      final PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(getAnalyzer(dAnalyzer));

      for (final Iterator<DIndexField> it = fieldsWithAnalyzer.iterator(); it.hasNext();) {
        final DIndexField field = it.next();
        wrapper.addAnalyzer(field.getName(), getAnalyzer(field.getAnalyzer()));
      }

      analyzer = wrapper;
    } else {
      return getAnalyzer(dAnalyzer);
    }

    return analyzer;
  }

  private static Analyzer getAnalyzer(DAnalyzer dAnalyzer) throws IndexException {

    Class cAnalyzer;
    try {
      // TODO: does this work via OSGi?
      cAnalyzer = Class.forName(dAnalyzer.getClassName());
    } catch (final ClassNotFoundException e) {
      throw new IndexException(
        "unable to load analyzer class. please ensure this class could be found on class path ["
          + dAnalyzer.getClassName() + "]");
    }

    List<List<Object>> values;
    try {
      values = getParameterSetValues(dAnalyzer.getParameterSet());
    } catch (final ParameterException e) {
      throw new IndexException("unable to resolve analyzer parameter", e);
    }

    // get analyzer constructor
    Constructor analyzerConstructor = null;
    final List<Object> constructorParameterClasses = values.get(0);
    try {
      analyzerConstructor = cAnalyzer.getConstructor(constructorParameterClasses.toArray(new Class[0]));
    } catch (final Exception e) {
      throw new IndexException("unable to receive constructor for analyzer [" + dAnalyzer.getClassName() + "]", e);
    }

    // create analyzer
    final List<Object> constructorParameterValues = values.get(1);
    Analyzer analyzer = null;
    try {
      analyzer = (Analyzer) analyzerConstructor.newInstance(constructorParameterValues.toArray());
    } catch (final Exception e) {
      throw new IndexException("unable to create analyzer [" + dAnalyzer.getClassName() + "]", e);
    }

    return analyzer;
  }

  private static List<List<Object>> getParameterSetValues(DParameterSet parameterSet) throws ParameterException {
    final List<List<Object>> values = new ArrayList<List<Object>>();

    final List<Object> classes = new ArrayList<Object>();
    final List<Object> objects = new ArrayList<Object>();

    values.add(classes);
    values.add(objects);

    if (parameterSet != null) {
      final DParameter[] parameters = parameterSet.getParameters();

      for (final DParameter parameter : parameters) {
        if (parameter instanceof DBoolean) {
          classes.add(boolean.class);
          objects.add(((DBoolean) parameter).getValue());
        } else if (parameter instanceof DString) {
          classes.add(String.class);
          objects.add(((DString) parameter).getValue());
        } else if (parameter instanceof DInteger) {
          classes.add(int.class);
          objects.add(((DInteger) parameter).getValue());
        } else if (parameter instanceof DFloat) {
          classes.add(float.class);
          objects.add(((DFloat) parameter).getValue());
        } else if (parameter instanceof DDate) {
          classes.add(Date.class);
          objects.add(((DDate) parameter).getValue());
        } else if (parameter instanceof DStringList) {
          classes.add(String[].class);
          objects.add(((DStringList) parameter).getValues());
        } else if (parameter instanceof DIntegerList) {
          classes.add(int[].class);
          objects.add(((DIntegerList) parameter).getValues());
        } else if (parameter instanceof DFloatList) {
          classes.add(float[].class);
          objects.add(((DFloatList) parameter).getValues());
        } else if (parameter instanceof DDateList) {
          classes.add(Date[].class);
          objects.add(((DDateList) parameter).getValues());
        } else {
          throw new ParameterException("Unknown parameter type [" + parameter.getClass() + "]");
        }
      }
    }

    return values;
  }

}
