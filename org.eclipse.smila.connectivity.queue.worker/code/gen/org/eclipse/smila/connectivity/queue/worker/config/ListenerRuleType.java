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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListenerRuleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListenerRuleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/queue}BaseRuleType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.eclipse.org/smila/queue}QueueConnectionType"/>
 *         &lt;element name="Condition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Task" type="{http://www.eclipse.org/smila/queue}ListenerTaskListType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Threads" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="WaitMessageTimeout" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="MaxMessageBlockSize" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListenerRuleType", propOrder = {
    "source",
    "condition",
    "task"
})
public class ListenerRuleType
    extends BaseRuleType
{

    @XmlElement(name = "Source", required = true)
    protected QueueConnectionType source;
    @XmlElement(name = "Condition", required = true)
    protected String condition;
    @XmlElement(name = "Task", required = true)
    protected ListenerTaskListType task;
    @XmlAttribute(name = "Threads")
    protected Integer threads;
    @XmlAttribute(name = "WaitMessageTimeout")
    protected Integer waitMessageTimeout;
    @XmlAttribute(name = "MaxMessageBlockSize")
    protected Integer maxMessageBlockSize;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link QueueConnectionType }
     *     
     */
    public QueueConnectionType getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueueConnectionType }
     *     
     */
    public void setSource(QueueConnectionType value) {
        this.source = value;
    }

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
     *     {@link ListenerTaskListType }
     *     
     */
    public ListenerTaskListType getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListenerTaskListType }
     *     
     */
    public void setTask(ListenerTaskListType value) {
        this.task = value;
    }

    /**
     * Gets the value of the threads property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getThreads() {
        if (threads == null) {
            return  1;
        } else {
            return threads;
        }
    }

    /**
     * Sets the value of the threads property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setThreads(Integer value) {
        this.threads = value;
    }

    /**
     * Gets the value of the waitMessageTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getWaitMessageTimeout() {
        if (waitMessageTimeout == null) {
            return  1;
        } else {
            return waitMessageTimeout;
        }
    }

    /**
     * Sets the value of the waitMessageTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWaitMessageTimeout(Integer value) {
        this.waitMessageTimeout = value;
    }

    /**
     * Gets the value of the maxMessageBlockSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMaxMessageBlockSize() {
        if (maxMessageBlockSize == null) {
            return  1;
        } else {
            return maxMessageBlockSize;
        }
    }

    /**
     * Sets the value of the maxMessageBlockSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxMessageBlockSize(Integer value) {
        this.maxMessageBlockSize = value;
    }

}

// CHECKSTYLE:ON
