/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.agent.feed.messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Process Specification
 * 
 * <p>Java class for Process complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Process">
 *   &lt;complexContent>
 *     &lt;extension base="{}Process">
 *       &lt;redefine>
 *         &lt;complexType name="Process">
 *           &lt;complexContent>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *             &lt;/restriction>
 *           &lt;/complexContent>
 *         &lt;/complexType>
 *       &lt;/redefine>
 *       &lt;sequence>
 *         &lt;element name="UpdateInterval" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="FeedUrl" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Process", propOrder = {
    "updateInterval",
    "feedUrl"
})
public class Process
    extends OriginalProcess
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UpdateInterval", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger updateInterval;
    @XmlElement(name = "FeedUrl", required = true)
    protected List<String> feedUrl;

    /**
     * Gets the value of the updateInterval property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Sets the value of the updateInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUpdateInterval(BigInteger value) {
        this.updateInterval = value;
    }

    /**
     * Gets the value of the feedUrl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the feedUrl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeedUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFeedUrl() {
        if (feedUrl == null) {
            feedUrl = new ArrayList<String>();
        }
        return this.feedUrl;
    }

}

// CHECKSTYLE:ON
