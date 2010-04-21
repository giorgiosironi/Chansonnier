/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Contributors: Andrey Basalaev (brox IT Solutions GmbH) - initial creator, Ivan Churkin (brox IT Solutions GmbH)
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.web.messages;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Filter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Filter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="WorkType" use="required" type="{}FilterWorkType" />
 *       &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Type" use="required" type="{}FilterType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Filter")
@XmlSeeAlso({
    org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.CrawlScope.Filters.Filter.class,
    org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.Filters.Filter.class
})
public class Filter
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "WorkType", required = true)
    protected FilterWorkType workType;
    @XmlAttribute(name = "Value", required = true)
    protected String value;
    @XmlAttribute(name = "Type", required = true)
    protected FilterType type;

    /**
     * Gets the value of the workType property.
     * 
     * @return
     *     possible object is
     *     {@link FilterWorkType }
     *     
     */
    public FilterWorkType getWorkType() {
        return workType;
    }

    /**
     * Sets the value of the workType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterWorkType }
     *     
     */
    public void setWorkType(FilterWorkType value) {
        this.workType = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FilterType }
     *     
     */
    public FilterType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterType }
     *     
     */
    public void setType(FilterType value) {
        this.type = value;
    }

}

// CHECKSTYLE:ON
