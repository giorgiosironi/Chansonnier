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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MetaAttributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MetaAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MetaName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" use="required" type="{}MetaType" />
 *       &lt;attribute name="ReturnType" type="{}MetaReturnType" default="MetaDataString" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetaAttributeType", propOrder = {
    "metaName"
})
public class MetaAttributeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MetaName")
    protected List<String> metaName;
    @XmlAttribute(name = "Type", required = true)
    protected MetaType type;
    @XmlAttribute(name = "ReturnType")
    protected MetaReturnType returnType;

    /**
     * Gets the value of the metaName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metaName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetaName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMetaName() {
        if (metaName == null) {
            metaName = new ArrayList<String>();
        }
        return this.metaName;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link MetaType }
     *     
     */
    public MetaType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetaType }
     *     
     */
    public void setType(MetaType value) {
        this.type = value;
    }

    /**
     * Gets the value of the returnType property.
     * 
     * @return
     *     possible object is
     *     {@link MetaReturnType }
     *     
     */
    public MetaReturnType getReturnType() {
        if (returnType == null) {
            return MetaReturnType.META_DATA_STRING;
        } else {
            return returnType;
        }
    }

    /**
     * Sets the value of the returnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetaReturnType }
     *     
     */
    public void setReturnType(MetaReturnType value) {
        this.returnType = value;
    }

}

// CHECKSTYLE:ON
