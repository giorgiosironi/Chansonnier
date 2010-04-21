/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.utils.param.def.DEnumeration;
import org.eclipse.smila.search.utils.param.def.DParameterDefinition;
import org.eclipse.smila.search.utils.param.def.DParameterDefinitionCodec;
import org.eclipse.smila.search.utils.param.set.DBoolean;
import org.eclipse.smila.search.utils.param.set.DDate;
import org.eclipse.smila.search.utils.param.set.DDateList;
import org.eclipse.smila.search.utils.param.set.DFloat;
import org.eclipse.smila.search.utils.param.set.DFloatList;
import org.eclipse.smila.search.utils.param.set.DInteger;
import org.eclipse.smila.search.utils.param.set.DIntegerList;
import org.eclipse.smila.search.utils.param.set.DParameter;
import org.eclipse.smila.search.utils.param.set.DParameterSet;
import org.eclipse.smila.search.utils.param.set.DParameterSetCodec;
import org.eclipse.smila.search.utils.param.set.DString;
import org.eclipse.smila.search.utils.param.set.DStringList;
import org.eclipse.smila.utils.xml.XMLUtils;

/*
 * To add new types, the following steps are necessary: 1. add new subtypes for Parameter to ParameterSet.xsd and
 * ParameterDefinition.xsd 2. add corresponding D classes and codec classes to packages
 * org.eclipse.smila.utils.param.(set|def) 3. modify encode/decode methods in
 * org.eclipse.smila.utils.param.(set|def).DParameterCodec 4. add getter and setter methods to ParameterSet.java 5.
 * modify ParameterSet.initializeFromDParamSet() 6. modify ParameterSet.checkParameter() 7. Hope this list is
 * complete... (not verified yet)
 */

/**
 * @author vluedeling
 * 
 * This class acts as a wrapper for the XML definition of ParameterSets. A ParameterSet can either be generated
 * standalone from a DParameterSet, or with the help of an additional DParameterDefinition. In the latter case,
 * ParameterSet will enforce consistency of the parameter set with its definition.
 * <p>
 * <code>getXxxParameter()</code> methods may return null only if a parameter of that name does not exist.
 * 
 */
@SuppressWarnings("unchecked")
public class ParameterSet {

  /**
   * 
   */
  private final Hashtable _params = new Hashtable();

  /**
   * 
   */
  private final Hashtable _types = new Hashtable();

  /**
   * Constructs a parameter set from a DParameterSet object and validates it against a DParameterDefinition structure.
   * <p>
   * This method will enforce the following conditions:
   * <ul>
   * <li>All parameters have been specified in the DParameterDefinition object
   * <li>All required parameters are present
   * <li>Parameter types match those put forth in the definition
   * <li>Enumeration types contain only values that are specified in the definition
   * </ul>
   * <p>
   * If an optional parameter for which a default value has been given is omitted, it will be created and filled with
   * its default value(s).
   * <p>
   * If any of the above checks fail, a ParameterException will be thrown which states the cause of the error. The first
   * error encountered will trigger an exception and abort further processing of parameter set construction.
   * 
   * 
   * @param paramSet
   *          The DParameterSet object from which the parameters will be extracted
   * @param paramDef
   *          The DParameterDefinition object where the parameter definitions are stored
   * @throws ParameterException
   *           if an error occurs while reading of validating the parameters.
   */
  public ParameterSet(DParameterSet paramSet, DParameterDefinition paramDef) throws ParameterException {
    initializeFromDParamSet(paramSet, paramDef);
  }

  /**
   * Constructs a parameter set from a DParameterSet object without an accompanying definition. No consistency checks
   * will be performed.
   * 
   * @param paramSet
   *          The DParamSet object from which to exctract the parameters
   * @throws ParameterException
   *           if an error occurs while reading the parameters.
   */
  public ParameterSet(DParameterSet paramSet) throws ParameterException {
    initializeFromDParamSet(paramSet, null);
  }

  public static void main(String[] arg) {
    final Log log = LogFactory.getLog(ParameterSet.class);
    try {

      // DParameterSet einlesen
      org.w3c.dom.Document d =
        XMLUtils.parse(new java.io.File("d:/anyfinder/af-engine-sdk/xml/param-testcase.xml"), true);
      final DParameterSet pset = DParameterSetCodec.decode(d.getDocumentElement());

      org.w3c.dom.Document d2 = XMLUtils.getDocument("top");
      org.w3c.dom.Element e = d2.getDocumentElement();
      org.w3c.dom.Element pelement = DParameterSetCodec.encode(pset, e);

      XMLUtils.stream(pelement, false, "UTF-8", System.err);

      // DParameterDefinition einlesen
      d = XMLUtils.parse(new java.io.File("d:/anyfinder/af-engine-sdk/xml/paramdef-testcase.xml"), true);
      final DParameterDefinition pdef = DParameterDefinitionCodec.decode(d.getDocumentElement());

      d2 = XMLUtils.getDocument("top");
      e = d2.getDocumentElement();
      pelement = DParameterDefinitionCodec.encode(pdef, e);

      XMLUtils.stream(pelement, false, "UTF-8", System.err);

      // ParameterSet erzeugen
      final ParameterSet ps = new ParameterSet(pset, pdef);

      final Enumeration en = ps.getParameterNames();
      for (; en.hasMoreElements();) {
        final String pname = (String) en.nextElement();
        final String ptype = ps.getParameterType(pname);
        Object value = ps.getParameter(pname);
        if (ptype.indexOf("List") >= 0) {
          String newValue = "";

          for (int i = 0; true; i++) {
            try {
              newValue += Array.get(value, i);
              Array.get(value, i + 1);
              newValue += ", ";
            } catch (final ArrayIndexOutOfBoundsException iae) {
              break;
            }
          }
          value = newValue;
        }
        if (log.isInfoEnabled()) {
          log.info(pname + "(" + ptype + "):\t" + value);
        }
      }
    } catch (final Exception e) {
      if (log.isErrorEnabled()) {
        log.error(e);
      }
    }
  }

  private void addParam(String name, Object value) {
    if (value == null) {
      _params.remove(name);
    } else {
      _params.put(name, value);
    }

  }

  private boolean checkConsistency(DParameterSet paramSet, DParameterDefinition paramDef) throws ParameterException {
    final org.eclipse.smila.search.utils.param.def.DParameter[] defs = paramDef.getParameters();
    for (int i = 0; i < defs.length; i++) {
      final String pname = defs[i].getName();
      checkParameter(paramSet, paramSet.getParameter(pname), defs[i]);
    }
    // are there parameters defined that are not specified in the definition?
    final org.eclipse.smila.search.utils.param.set.DParameter[] params = paramSet.getParameters();
    for (int i = 0; i < params.length; i++) {
      if (paramDef.getParameter(params[i].getName()) == null) {
        throw new ParameterException("Unknown parameter '" + params[i].getName() + "'");
      }
    }
    return true;
  }

  private boolean checkParameter(org.eclipse.smila.search.utils.param.set.DParameterSet paramSet,
    org.eclipse.smila.search.utils.param.set.DParameter param, org.eclipse.smila.search.utils.param.def.DParameter def)
    throws ParameterException {

    String defType = def.getType();
    // check occurrence of required parameters, create if optional and default
    // exists
    if (param == null) {
      if ("optional".equals(def.getConstraint())) {
        // add optional parameter
        if (def.hasDefault()) { // create new and fill in default values

          String type = defType;

          if ("Boolean".equals(defType)) {
            param = new DBoolean();
            ((DBoolean) param).setValue(((org.eclipse.smila.search.utils.param.def.DBoolean) def).getDefault());
          } else if ("String".equals(defType)) {
            param = new DString();
            ((DString) param).setValue(((org.eclipse.smila.search.utils.param.def.DString) def).getDefault());
          } else if ("Float".equals(defType)) {
            param = new DFloat();
            ((DFloat) param).setValue(((org.eclipse.smila.search.utils.param.def.DFloat) def).getDefault());
          } else if ("Integer".equals(defType)) {
            param = new DInteger();
            ((DInteger) param).setValue(((org.eclipse.smila.search.utils.param.def.DInteger) def).getDefault());
          } else if ("Date".equals(defType)) {
            param = new DDate();
            ((DDate) param).setValue(((org.eclipse.smila.search.utils.param.def.DDate) def).getDefault());
          } else if ("StringList".equals(defType)) {
            param = new DStringList();
            ((DStringList) param).addValues(((org.eclipse.smila.search.utils.param.def.DStringList) def).getDefaults());
          } else if ("FloatList".equals(defType)) {
            param = new DFloatList();
            ((DFloatList) param).addValues(((org.eclipse.smila.search.utils.param.def.DFloatList) def).getDefaults());
          } else if ("IntegerList".equals(defType)) {
            param = new DIntegerList();
            ((DIntegerList) param).addValues(((org.eclipse.smila.search.utils.param.def.DIntegerList) def).getDefaults());
          } else if ("DateList".equals(defType)) {
            param = new DDateList();
            ((DDateList) param).addValues(((org.eclipse.smila.search.utils.param.def.DDateList) def).getDefaults());
          } else if ("Enumeration".equals(defType)) {
            final String[] defaults = ((org.eclipse.smila.search.utils.param.def.DEnumeration) def).getDefaults();
            if (((org.eclipse.smila.search.utils.param.def.DEnumeration) def).isAllowMultiple()) {
              param = new DStringList();
              ((DStringList) param).addValues(defaults);
              type = "StringList";
            } else {
              param = new DString();
              if (defaults.length > 1) {
                throw new ParameterException("Invalid default values for parameter '" + def.getName()
                  + "': Enumeration does not allow multiple values");
              }
              ((DString) param).setValue(defaults[0]);
              type = "String";
            }
          } else {
            return true;
          }
          param.setName(def.getName());
          param.setType(type);
          paramSet.addParameter(param);
        } else { // if optional and has no default => return true
          return true;
        }
      } else {
        throw new ParameterException("Required parameter '" + def.getName() + "' not found");
      }
    }

    final String paramType = param.getType();

    // check types
    boolean typeMatch = false;
    if (paramType.equals(defType)) {
      typeMatch = true;
    } else if (defType.equals("Enumeration")) {
      // If isAllowMultiple, then either String or StringList are allowed,
      // otherwise only String
      final boolean multiple = ((DEnumeration) def).isAllowMultiple();

      if ((!multiple) && ("String".equals(paramType))) {
        typeMatch = true;
      } else if ((multiple) && ("StringList".equals(paramType))) {
        typeMatch = true;
      }

      if (!typeMatch) {
        if (multiple) {
          defType = "StringList";
        } else {
          defType = "String";
        }
      }
    }
    if (!typeMatch) {
      throw new ParameterException("Type mismatch for parameter '" + def.getName() + "': expected " + defType
        + ", but found " + paramType);
    }

    // check Enumeration consistency
    if (def.getType().equals("Enumeration")) {
      String[] values;
      if ("StringList".equals(param.getType())) {
        values = ((DStringList) param).getValues();
      } else {
        values = new String[] { ((DString) param).getValue() };
      }

      for (int i = 0; i < values.length; i++) {

        boolean enumMatch = false;
        final String[] defValues = ((DEnumeration) def).getValues();

        for (int j = 0; j < defValues.length; j++) {
          if (values[i] != null && values[i].equals(defValues[j])) {
            enumMatch = true;
            break;
          }
        }

        if (!enumMatch) {
          String allowedValues = "";
          for (int j = 0; j < defValues.length; j++) {
            if (!"".equals(allowedValues)) {
              allowedValues += ",";
            }

            allowedValues += defValues[j];
          }
          throw new ParameterException("Unknown value for parameter " + def.getName() + ": expected ["
            + allowedValues + "], but found '" + values[i] + "'");
        }

      }

    }

    return true;
  }

  public Boolean getBooleanParameter(String name) {
    final Object o = _params.get(name);
    return (Boolean) o;
  }

  public Date[] getDateListParameter(String name) {
    final Object o = _params.get(name);
    return (Date[]) o;
  }

  public Date getDateParameter(String name) {
    final Object o = _params.get(name);
    return (Date) o;
  }

  public float[] getFloatListParameter(String name) {
    final Object o = _params.get(name);
    return (float[]) o;
  }

  public Float getFloatParameter(String name) {
    final Object o = _params.get(name);
    return (Float) o;
  }

  public int[] getIntegerListParameter(String name) {
    final Object o = _params.get(name);
    return (int[]) o;
  }

  public Integer getIntegerParameter(String name) {
    final Object o = _params.get(name);
    return (Integer) o;
  }

  /**
   * Returns the value for a named parameter. No type checks will be performed. Use in combination with
   * <code>getParameterType()</code> and <code>getParameterNames()</code> to dynamically extract parameters.
   * <p>
   * NOTICE: simple types will be returned as their Object counterparts. E. g., integer parameters will be represented
   * as Integer objects. Currently this applies to <code>boolean</code>, <code>float</code>, and <code>int</code>
   * parameters. List parameters will be returned as Array objects.
   * <p>
   * In order to print all parameters from a set, the following piece of code may be used as an example:
   * <p>
   * 
   * <pre>
   * Enumeration en = ps.getParameterNames();
   * for (; en.hasMoreElements();) {
   *   String pname = (String) en.nextElement();
   *   String ptype = ps.getParameterType(pname);
   *   Object value = ps.getParameter(pname);
   *   if (ptype.indexOf(&quot;List&quot;) &gt;= 0) {
   *     String newValue = &quot;&quot;;
   * 
   *     for (int i = 0; true; i++) {
   *       try {
   *         newValue += Array.get(value, i);
   *         Array.get(value, i + 1);
   *         newValue += &quot;, &quot;;
   *       } catch (ArrayIndexOutOfBoundsException iae) {
   *         break;
   *       }
   *     }
   *     value = newValue;
   *   }
   *   System.err.println(pname + &quot;(&quot; + ptype + &quot;):  &quot; + value);
   * }
   * </pre>
   * 
   * @param name -
   * @return Object -
   */
  public Object getParameter(String name) {
    return _params.get(name);
  }

  /**
   * Returns an enumeration of the parameter names in this set.
   * 
   * @return Enumeration
   */
  public Enumeration getParameterNames() {
    return _params.keys();
  }

  /**
   * Returns the type for a named parameter. The type names are the same as used in the <code>xsi:type</code> field of
   * the XML representation.
   * 
   * @param name -
   * @return String -
   */
  public String getParameterType(String name) {
    final String type = (String) _types.get(name);
    if (type == null) {
      throw new NullPointerException("Parameter '" + name + "' does not exist");
    }

    return type;
  }

  public String[] getStringListParameter(String name) {
    final Object o = _params.get(name);
    return (String[]) o;
  }

  public String getStringParameter(String name) {
    final Object o = _params.get(name);
    return (String) o;
  }

  public boolean hasParameter(String name) {
    return _params.containsKey(name);
  }

  protected void initializeFromDParamSet(DParameterSet paramSet, DParameterDefinition paramDef)
    throws ParameterException {
    if (paramDef != null) {
      checkConsistency(paramSet, paramDef);
    }
    final DParameter[] params = paramSet.getParameters();
    for (int i = 0; i < params.length; i++) {
      final DParameter p = params[i];
      if (p instanceof DBoolean) {
        setBooleanParameter(p.getName(), new Boolean(((DBoolean) p).getValue()));
      } else if (p instanceof DString) {
        setStringParameter(p.getName(), (((DString) p).getValue()));
      } else if (p instanceof DInteger) {
        setIntegerParameter(p.getName(), new Integer(((DInteger) p).getValue()));
      } else if (p instanceof DFloat) {
        setFloatParameter(p.getName(), new Float(((DFloat) p).getValue()));
      } else if (p instanceof DDate) {
        setDateParameter(p.getName(), ((DDate) p).getValue());
      } else if (p instanceof DStringList) {
        setStringListParameter(p.getName(), (((DStringList) p).getValues()));
      } else if (p instanceof DIntegerList) {
        setIntegerListParameter(p.getName(), (((DIntegerList) p).getValues()));
      } else if (p instanceof DFloatList) {
        setFloatListParameter(p.getName(), (((DFloatList) p).getValues()));
      } else if (p instanceof DDateList) {
        setDateListParameter(p.getName(), ((DDateList) p).getValues());
      } else {
        throw new ParameterException("Unknown parameter type [" + p.getClass() + "]");
      }
    }
  }

  public void setBooleanParameter(String name, Boolean value) {
    _types.put(name, "Boolean");
    addParam(name, value);
  }

  public void setDateListParameter(String name, Date[] value) {
    _types.put(name, "DateList");
    addParam(name, value);
  }

  public void setDateParameter(String name, Date value) {
    _types.put(name, "Date");
    addParam(name, value);
  }

  public void setFloatListParameter(String name, float[] value) {
    _types.put(name, "FloatList");
    addParam(name, value);
  }

  public void setFloatParameter(String name, float value) {
    _types.put(name, "Float");
    addParam(name, new Float(value));
  }

  public void setFloatParameter(String name, Float value) {
    _types.put(name, "Float");
    addParam(name, value);
  }

  public void setIntegerListParameter(String name, int[] value) {
    _types.put(name, "IntegerList");
    addParam(name, value);
  }

  public void setIntegerParameter(String name, int value) {
    _types.put(name, "Integer");
    addParam(name, new Integer(value));
  }

  public void setIntegerParameter(String name, Integer value) {
    _types.put(name, "Integer");
    addParam(name, value);
  }

  public void setStringListParameter(String name, String[] value) {
    _types.put(name, "StringList");
    addParam(name, value);
  }

  public void setStringParameter(String name, String value) {
    _types.put(name, "String");
    addParam(name, value);
  }

}
