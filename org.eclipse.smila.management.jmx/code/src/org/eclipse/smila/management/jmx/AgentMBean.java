/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator, Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.Coefficient;
import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.error.ErrorsBuffer;
import org.eclipse.smila.management.performance.PerformanceCounter;

/**
 * The Class LocalMBean.
 */
@SuppressWarnings("unchecked")
public class AgentMBean implements DynamicMBean {

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(AgentMBean.class);

  /**
   * The _bean info.
   */
  private final ModelMBeanInfo _beanInfo;

  /**
   * The _agent.
   */
  private final ManagementAgent _agent;

  /**
   * Instantiates a new local m bean.
   * 
   * @param beanInfo
   *          the bean info
   * @param agent
   *          the agent
   */
  public AgentMBean(final ModelMBeanInfo beanInfo, final ManagementAgent agent) {
    _beanInfo = beanInfo;
    _agent = agent;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.management.DynamicMBean#getMBeanInfo()
   */
  public MBeanInfo getMBeanInfo() {
    return ((MBeanInfo) (_beanInfo).clone());
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
   */
  public Object getAttribute(final String attribute) throws AttributeNotFoundException, MBeanException,
    ReflectionException {
    final ModelMBeanAttributeInfo info = _beanInfo.getAttribute(attribute);
    final Descriptor descriptor = info.getDescriptor();
    final String getterMethod = (String) (descriptor.getFieldValue("getMethod"));
    return invoke(getterMethod, ClassHelper.EMPTY_PARAMS, ClassHelper.EMPTY_SIGNATURE);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
   */
  public Object invoke(final String actionName, final Object[] params, final String[] signature)
    throws MBeanException, ReflectionException {
    final Class[] classesSig = new Class[signature.length];
    for (int i = 0; i < signature.length; i++) {
      classesSig[i] = ClassHelper.searchClass(signature[i]);
    }
    Method method;
    try {
      method = _agent.getClass().getMethod(actionName, classesSig);
    } catch (final Exception e) {
      _log.error(e);
      throw new ReflectionException(e);
    }
    final Object result;
    try {
      result = method.invoke(_agent, params);
    } catch (final Exception e) {
      _log.error(e);
      throw new ReflectionException(e);
    }
    return processValue(result, method);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
   */
  public void setAttribute(final Attribute attribute) throws AttributeNotFoundException,
    InvalidAttributeValueException, MBeanException, ReflectionException {
    final ModelMBeanAttributeInfo info = _beanInfo.getAttribute(attribute.getName());
    if (info == null) {
      String msg =
        "MBean " + _beanInfo.getClassName() + " doesnt contain attribute Information for method "
          + attribute.getName() + ", please check if you have defined a gettter and setter for this Attriubute!";
      _log.error(msg);
      throw new AttributeNotFoundException(msg);
    }
    final Descriptor descriptor = info.getDescriptor();
    final String setterMethod = (String) (descriptor.getFieldValue("setMethod"));
    final String setterSignatureClass = (String) (descriptor.getFieldValue("setterSignatureClass"));
    invoke(setterMethod, new Object[] { attribute.getValue() }, new String[] { setterSignatureClass });
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
   */
  public AttributeList getAttributes(final String[] attributes) {
    if (attributes == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"),
        "Exception occured trying to get attributes of a " + "LocalMBean");
    }
    final AttributeList attributeList = new AttributeList();
    String attributeName;
    for (int i = 0; i < attributes.length; i++) {
      attributeName = attributes[i];
      try {
        attributeList.add(new Attribute(attributeName, getAttribute(attributeName)));
      } catch (final Throwable e) {
        _log.error("getAttributes(String[]), failed to get \"" + attributeName + "\"", e);
      }
    }
    return attributeList;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
   */
  public AttributeList setAttributes(final AttributeList attributes) {
    if (attributes == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"),
        "Exception occured trying to set attributes of a " + "LocalMBean");
    }
    final AttributeList attributeList = new AttributeList();
    for (final Iterator i = attributes.iterator(); i.hasNext();) {
      final Attribute attribute = (Attribute) i.next();
      try {
        setAttribute(attribute);
        attributeList.add(attribute);
      } catch (final Exception e) {
        i.remove();
      }
    }
    return attributeList;
  }

  /**
   * Process value.
   * 
   * @param result
   *          the result
   * @param method
   *          the method
   * 
   * @return the object
   */
  private Object processValue(final Object result, final Method method) {
    if (result == null) {
      return null;
    }
    if (PerformanceCounter.class.isAssignableFrom(result.getClass())) {
      final PerformanceCounter counter = (PerformanceCounter) result;
      double value = counter.getNextSampleValue();
      if (method.isAnnotationPresent(Coefficient.class)) {
        final Coefficient coefficient = method.getAnnotation(Coefficient.class);
        final double coefficientToApply = coefficient.value();
        value = value * coefficientToApply;
      }
      return value;
    }
    if (ErrorsBuffer.class.isAssignableFrom(result.getClass())) {
      final ErrorsBuffer buffer = (ErrorsBuffer) result;
      return buffer.getErrors();
    }
    return result;
  }

}
