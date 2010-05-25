/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.queue.worker.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListenerTaskListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListenerTaskListType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/queue}TaskListType">
 *       &lt;attribute name="InitiallySet" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListenerTaskListType")
public class ListenerTaskListType
    extends TaskListType
{

    @XmlAttribute(name = "InitiallySet")
    protected Boolean initiallySet;

    /**
     * Gets the value of the initiallySet property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isInitiallySet() {
        if (initiallySet == null) {
            return false;
        } else {
            return initiallySet;
        }
    }

    /**
     * Sets the value of the initiallySet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInitiallySet(Boolean value) {
        this.initiallySet = value;
    }

}

// CHECKSTYLE:ON
