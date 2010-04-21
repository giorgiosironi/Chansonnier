/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.Descriptor;
import javax.management.DynamicMBean;
import javax.management.IntrospectionException;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.MeasureUnit;
import org.eclipse.smila.management.RegistrationException;
import org.eclipse.smila.management.performance.PerformanceCounter;

/**
 * The Class DynamicMBeanBuilder.
 */
class DynamicMBeanBuilder {

  /**
   * Constant for number 3.
   */
  private static final int NUMBER_3 = 3;

  /**
   * Log for this class.
   */
  private final Log _log = LogFactory.getLog(DynamicMBeanBuilder.class);

  /**
   * The _object name.
   */
  private final ObjectName _objectName;

  /**
   * The _bean name.
   */
  private final String _beanName;

  /**
   * The _bean.
   */
  private final ManagementAgent _bean;

  /**
   * The _methods.
   */
  private final Method[] _methods;

  /**
   * Methods that won't be exposed to JMX.
   */
  private final String[] _skippedMethodNames =
    new String[] { "hashCode", "getClass", "wait", "equals", "notify", "notifyAll", "toString", "getLocation",
      "setLocation", "getCategory", "getName" };

  /**
   * Skipped methods set.
   */
  private final Set<String> _skippedMethodsSet = new HashSet<String>();

  /**
   * Instantiates a new dynamic m bean builder.
   * 
   * @param objectName
   *          the object name
   * @param bean
   *          the bean
   * 
   * @throws RegistrationException
   *           the registration exception
   */
  public DynamicMBeanBuilder(final ObjectName objectName, final ManagementAgentLocation location,
    final ManagementAgent bean) throws RegistrationException {
    if (bean == null) {
      throw new RegistrationException("Bean is null!");
    }
    _objectName = objectName;
    _beanName = location.getName();
    _bean = bean;
    _methods = bean.getClass().getMethods();
    if (_methods.length == 0) {
      throw new RegistrationException("Unable to find managed methods in class " + bean.getClass().getName());
    }
    for (final String methodName : _skippedMethodNames) {
      _skippedMethodsSet.add(methodName);
    }
  }

  /**
   * Builds the.
   * 
   * @return the object
   * 
   * @throws RegistrationException
   *           the registration exception
   */
  public Object build() throws RegistrationException {
    try {
      final ModelMBeanInfo dMBeanInfo = buildModelMBeanInfo();
      final DynamicMBean mBean = new AgentMBean(dMBeanInfo, _bean);
      // final RequiredModelMBean mBean = new ExtendedRequiredModelMBean(dMBeanInfo);
      // mBean.setManagedResource(_bean, "objectReference");

      return mBean;
    } catch (final Throwable e) {
      throw new RegistrationException(e);
    }
  }

  /**
   * Builds the model m bean info.
   * 
   * @return the model m bean info
   * 
   * @throws IllegalAccessException
   *           the illegal access exception
   * @throws InvocationTargetException
   *           the invocation target exception
   */
  @SuppressWarnings("unchecked")
  private ModelMBeanInfo buildModelMBeanInfo() throws IllegalAccessException, InvocationTargetException {
    // lists
    final List<ModelMBeanOperationInfo> operationsList = new ArrayList<ModelMBeanOperationInfo>();
    final List<ModelMBeanAttributeInfo> attributesList = new ArrayList<ModelMBeanAttributeInfo>();
    // arrays
    final ModelMBeanConstructorInfo[] dConstructors = new ModelMBeanConstructorInfo[0];
    final ModelMBeanNotificationInfo[] dNotifications = new ModelMBeanNotificationInfo[0];
    for (final Method method : _methods) {
      if (!_skippedMethodsSet.contains(method.getName())) {
        // process getter
        if (method.getParameterTypes().length == 0 && method.getName().startsWith("get")
          && !method.getName().equals("get") && method.getReturnType() != void.class) {
          String attributeName = method.getName().substring(3);
          final Descriptor attributeDescriptor;
          final boolean isPoc = PerformanceCounter.class.isAssignableFrom(method.getReturnType());
          
          String measureUnitStr = null;
          if (method.isAnnotationPresent(MeasureUnit.class)) {
            measureUnitStr = method.getAnnotation(MeasureUnit.class).value();
          }
          
          Method setter = null;
          Class setterSignatureClass = null;
          for (final Method method1 : _methods) {
            if (method1.getParameterTypes().length == 1 && (method1.getName().startsWith("set"))
              && (method1.getName().substring(NUMBER_3).equals(attributeName))) {
              setter = method1;
              setterSignatureClass = method1.getParameterTypes()[0];
              break;
            }
          }
          
          if (measureUnitStr != null) {
            attributeName += (" (" + measureUnitStr + ") ");
          }
          
          if (setter != null) {
            attributeDescriptor =
              new DescriptorSupport(new String[] { "name=" + attributeName, "descriptorType=attribute",
                "getMethod=" + method.getName(), "setMethod=" + setter.getName(),
                "setterSignatureClass=" + setterSignatureClass.getName() });
          } else {
            if (isPoc) {
              attributeDescriptor =
                new DescriptorSupport(new String[] { "name=" + attributeName, "descriptorType=attribute",
                  "getMethod=" + method.getName() });
            } else {
              attributeDescriptor =
                new DescriptorSupport(new String[] { "name=" + attributeName, "descriptorType=attribute",
                  "getMethod=" + method.getName() });
            }
          }
          try {
            final ModelMBeanAttributeInfo info =
              new ModelMBeanAttributeInfo(attributeName, attributeName, method, setter, attributeDescriptor);
            attributesList.add(info);
          } catch (final IntrospectionException exception) {
            _log.error("Error creating MBeanAttributeInfo [" + attributeName + "]", exception);
          }
        }
        // }
        final ModelMBeanOperationInfo info = new ModelMBeanOperationInfo("", method);
        operationsList.add(info);
      }
    }
    final ModelMBeanOperationInfo[] dOperations =
      operationsList.toArray(new ModelMBeanOperationInfo[operationsList.size()]);

    final ModelMBeanAttributeInfo[] dAttributes =
      attributesList.toArray(new ModelMBeanAttributeInfo[attributesList.size()]);

    final Descriptor beanDesc =
      new DescriptorSupport(new String[] { ("name=" + _objectName), "descriptorType=mbean",
        ("displayName=" + _beanName) });

    final ModelMBeanInfoSupport dMBeanInfo =
      new ModelMBeanInfoSupport(_beanName, "", dAttributes, dConstructors, dOperations, dNotifications, beanDesc);
    return dMBeanInfo;

  }
}
