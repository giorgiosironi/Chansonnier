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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RouterRuleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RouterRuleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/queue}BaseRuleType">
 *       &lt;sequence>
 *         &lt;element name="Condition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Task" type="{http://www.eclipse.org/smila/queue}RouterTaskListType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RouterRuleType", propOrder = {
    "condition",
    "task"
})
public class RouterRuleType
    extends BaseRuleType
{

    @XmlElement(name = "Condition", required = true)
    protected String condition;
    @XmlElement(name = "Task", required = true)
    protected RouterTaskListType task;

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondition(String value) {
        this.condition = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link RouterTaskListType }
     *     
     */
    public RouterTaskListType getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouterTaskListType }
     *     
     */
    public void setTask(RouterTaskListType value) {
        this.task = value;
    }

}

// CHECKSTYLE:ON
