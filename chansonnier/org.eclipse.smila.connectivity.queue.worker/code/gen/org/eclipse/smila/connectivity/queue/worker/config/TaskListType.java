/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.queue.worker.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaskListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;choice>
 *           &lt;element name="Process" type="{http://www.eclipse.org/smila/queue}ProcessType"/>
 *           &lt;element name="Send" type="{http://www.eclipse.org/smila/queue}SendType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="BlackboardSync" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskListType", propOrder = {
    "processOrSend"
})
@XmlSeeAlso({
    RouterTaskListType.class,
    RecordRecyclerTaskListType.class,
    ListenerTaskListType.class
})
public abstract class TaskListType {

    @XmlElements({
        @XmlElement(name = "Send", type = SendType.class),
        @XmlElement(name = "Process", type = ProcessType.class)
    })
    protected List<Object> processOrSend;
    @XmlAttribute(name = "BlackboardSync")
    protected Boolean blackboardSync;

    /**
     * Gets the value of the processOrSend property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processOrSend property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessOrSend().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SendType }
     * {@link ProcessType }
     * 
     * 
     */
    public List<Object> getProcessOrSend() {
        if (processOrSend == null) {
            processOrSend = new ArrayList<Object>();
        }
        return this.processOrSend;
    }

    /**
     * Gets the value of the blackboardSync property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isBlackboardSync() {
        if (blackboardSync == null) {
            return true;
        } else {
            return blackboardSync;
        }
    }

    /**
     * Sets the value of the blackboardSync property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBlackboardSync(Boolean value) {
        this.blackboardSync = value;
    }

}

// CHECKSTYLE:ON
