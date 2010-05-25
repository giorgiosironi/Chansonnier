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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SendType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SendType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/queue}QueueConnectionType">
 *       &lt;sequence>
 *         &lt;element name="SetProperty" type="{http://www.eclipse.org/smila/queue}PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="RecordFilter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PersistentDelivery" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="WithAttachments" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendType", propOrder = {
    "setProperty"
})
public class SendType
    extends QueueConnectionType
{

    @XmlElement(name = "SetProperty")
    protected List<PropertyType> setProperty;
    @XmlAttribute(name = "RecordFilter")
    protected String recordFilter;
    @XmlAttribute(name = "PersistentDelivery")
    protected Boolean persistentDelivery;
    @XmlAttribute(name = "WithAttachments")
    protected Boolean withAttachments;

    /**
     * Gets the value of the setProperty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setProperty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyType }
     * 
     * 
     */
    public List<PropertyType> getSetProperty() {
        if (setProperty == null) {
            setProperty = new ArrayList<PropertyType>();
        }
        return this.setProperty;
    }

    /**
     * Gets the value of the recordFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordFilter() {
        return recordFilter;
    }

    /**
     * Sets the value of the recordFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordFilter(String value) {
        this.recordFilter = value;
    }

    /**
     * Gets the value of the persistentDelivery property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPersistentDelivery() {
        if (persistentDelivery == null) {
            return true;
        } else {
            return persistentDelivery;
        }
    }

    /**
     * Sets the value of the persistentDelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPersistentDelivery(Boolean value) {
        this.persistentDelivery = value;
    }

    /**
     * Gets the value of the withAttachments property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isWithAttachments() {
        if (withAttachments == null) {
            return false;
        } else {
            return withAttachments;
        }
    }

    /**
     * Sets the value of the withAttachments property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWithAttachments(Boolean value) {
        this.withAttachments = value;
    }

}

// CHECKSTYLE:ON
