/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.utils.test.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.eclipse.smila.utils.test.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TestElement2_QNAME = new QName("", "TestElement2");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.smila.utils.test.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TestElement }
     * 
     */
    public TestElement createTestElement() {
        return new TestElement();
    }

    /**
     * Create an instance of {@link TestComplexType }
     * 
     */
    public TestComplexType createTestComplexType() {
        return new TestComplexType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestComplexType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TestElement2")
    public JAXBElement<TestComplexType> createTestElement2(TestComplexType value) {
        return new JAXBElement<TestComplexType>(_TestElement2_QNAME, TestComplexType.class, null, value);
    }

}

// CHECKSTYLE:ON
