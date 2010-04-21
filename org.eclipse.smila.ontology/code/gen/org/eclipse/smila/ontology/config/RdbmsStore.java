/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.ontology.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.eclipse.org/smila/ontology}UrlConnection"/>
 *       &lt;attribute name="driver" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxTripleTables" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="indexed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="sequenced" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "url",
    "user",
    "password"
})
@XmlRootElement(name = "RdbmsStore")
public class RdbmsStore {

    @XmlElement(name = "Url", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String url;
    @XmlElement(name = "User")
    protected String user;
    @XmlElement(name = "Password")
    protected String password;
    @XmlAttribute(required = true)
    protected String driver;
    @XmlAttribute
    protected Integer maxTripleTables;
    @XmlAttribute
    protected Boolean indexed;
    @XmlAttribute
    protected Boolean sequenced;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the driver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets the value of the driver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriver(String value) {
        this.driver = value;
    }

    /**
     * Gets the value of the maxTripleTables property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMaxTripleTables() {
        if (maxTripleTables == null) {
            return  1;
        } else {
            return maxTripleTables;
        }
    }

    /**
     * Sets the value of the maxTripleTables property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxTripleTables(Integer value) {
        this.maxTripleTables = value;
    }

    /**
     * Gets the value of the indexed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIndexed() {
        if (indexed == null) {
            return true;
        } else {
            return indexed;
        }
    }

    /**
     * Sets the value of the indexed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndexed(Boolean value) {
        this.indexed = value;
    }

    /**
     * Gets the value of the sequenced property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSequenced() {
        if (sequenced == null) {
            return true;
        } else {
            return sequenced;
        }
    }

    /**
     * Sets the value of the sequenced property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSequenced(Boolean value) {
        this.sequenced = value;
    }

}

// CHECKSTYLE:ON
