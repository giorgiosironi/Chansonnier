/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator 
 * Sebastian Voigt (brox IT Solutions GmbH)
 * Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.processing.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.WorkflowProcessor;

/**
 * The PipeletConfiguration.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "_properties" }, namespace = WorkflowProcessor.NAMESPACE_PROCESSOR)
@XmlRootElement(name = "PipeletConfiguration", namespace = WorkflowProcessor.NAMESPACE_PROCESSOR)
public class PipeletConfiguration {

  /** The properties. */
  @XmlElement(name = "Property", required = false, namespace = WorkflowProcessor.NAMESPACE_PROCESSOR)
  private List<Property> _properties;

  /**
   * Gets the first property value.
   * 
   * @param name
   *          the name
   * 
   * @return the first property value
   */
  public Object getPropertyFirstValue(final String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    if (_properties != null) {
      for (final Property property : _properties) {
        if (name.equals(property.getName())) {
          if (property.getValue() != null) {
            return property.getValue();
          }
        }
      }
    }
    return null;
  }

  
  /**
   * Checks for value.
   * 
   * @param name
   *          property name
   * 
   * @return true, if successful
   */
  public boolean hasPropertyValue(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    if (_properties != null) {
      for (final Property property : _properties) {
        if (name.equals(property.getName())) {
          if (property.getValue() != null) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  /**
   * Gets the property values.
   * 
   * @param name
   *          the name
   * 
   * @return the property Object[] of values
   */
  public Object[] getPropertyValues(final String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    if (_properties != null) {
      for (final Property property : _properties) {
        if (name.equals(property.getName())) {
          if (property.getValues() != null) {
            return property.getValues().toArray(new Object[property.getValues().size()]);
          }
        }
      }
    }
    return new Object[] {};
  }

  /**
   * Gets the property values as Strings.
   * 
   * @param name
   *          the name
   * 
   * @return the property String[] of values
   */
  public String[] getPropertyStringValues(final String name) {
    if (name == null) {
      throw new IllegalArgumentException("name cannot be null");
    }
    final List<String> list = new ArrayList<String>();
    if (_properties != null) {
      for (final Property property : _properties) {
        if (name.equals(property.getName())) {
          if (property.getValues() != null) {
            for (final Object value : property.getValues()) {
              if (value != null) {
                list.add(value.toString());
              }
            }
          }
        }
      }
    }
    return list.toArray(new String[list.size()]);
  }

  /**
   * Gets the property value. The resolved property must have a not null value.
   * 
   * @param name
   *          the name
   * 
   * @return the property value
   * 
   * @throws ProcessingException
   *           Resolved value is null.
   */
  public Object getPropertyFirstValueNotNull(final String name) throws ProcessingException {
    final Object value = getPropertyFirstValue(name);
    if (value == null) {
      throw new ProcessingException("property requires a defined value [" + name + "]");
    }
    return value;
  }

  /**
   * The Class Property.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = { "_name", "_type", "_values" }, namespace = WorkflowProcessor.NAMESPACE_PROCESSOR)
  static class PropertySource {

    /** The _name. */
    @XmlAttribute(name = "name", required = true)
    private String _name;

    /** The _type. */
    @XmlAttribute(name = "type", required = true)
    private String _type;

    /** The properties. */
    @XmlElement(name = "Value", required = false, namespace = WorkflowProcessor.NAMESPACE_PROCESSOR)
    private List<String> _values;

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
      return _name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *          the new name
     */
    public void setName(final String name) {
      _name = name;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
      return _type;
    }

    /**
     * Sets the type.
     * 
     * @param type
     *          the new type
     */
    public void setType(final String type) {
      _type = type;
    }

    /**
     * Gets the values.
     * 
     * @return the values
     */
    public List<String> getValues() {
      return _values;
    }

  }

  /**
   * The Class Property.
   */
  @XmlJavaTypeAdapter(PropertyAdapter.class)
  public static class Property {

    /** The _name. */
    private final String _name;

    /** The _value. */
    private final List<Object> _values;

    /**
     * Instantiates a new property.
     * 
     * @param name
     *          the name
     * @param singleValue
     *          a single value
     */
    public Property(final String name, Object singleValue) {
      _name = name;
      _values = new ArrayList<Object>();
      _values.add(singleValue);
    }
    
    /**
     * Instantiates a new property.
     * 
     * @param name
     *          the name
     * @param values
     *          the values
     */
    public Property(final String name, List<Object> values) {
      _name = name;
      _values = values;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
      return _name;
    }

    /**
     * Gets the values.
     * 
     * @return the value
     */
    public List<Object> getValues() {
      return _values;
    }
    
    /**
     * Gets the first value.
     * 
     * @return the value
     */
    public Object getValue() {
      if (_values != null && !_values.isEmpty()) {
        return _values.get(0);        
      }
      return null;
    }

  }

  /**
   * Gets the properties.
   * 
   * @return the properties
   */
  public List<Property> getProperties() {
    return _properties;
  }

}
