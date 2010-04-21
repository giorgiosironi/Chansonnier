/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.ontology.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;choice>
 *             &lt;element ref="{http://www.eclipse.org/smila/ontology}MemoryStore"/>
 *             &lt;element ref="{http://www.eclipse.org/smila/ontology}NativeStore"/>
 *             &lt;element ref="{http://www.eclipse.org/smila/ontology}RdbmsStore"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.eclipse.org/smila/ontology}Stackable" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.eclipse.org/smila/ontology}HttpStore"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "memoryStore",
    "nativeStore",
    "rdbmsStore",
    "stackable",
    "httpStore"
})
@XmlRootElement(name = "RepositoryConfig")
public class RepositoryConfig {

    @XmlElement(name = "MemoryStore")
    protected MemoryStore memoryStore;
    @XmlElement(name = "NativeStore")
    protected NativeStore nativeStore;
    @XmlElement(name = "RdbmsStore")
    protected RdbmsStore rdbmsStore;
    @XmlElement(name = "Stackable")
    protected List<Stackable> stackable;
    @XmlElement(name = "HttpStore")
    protected HttpStore httpStore;
    @XmlAttribute(required = true)
    protected String name;

    /**
     * Gets the value of the memoryStore property.
     * 
     * @return
     *     possible object is
     *     {@link MemoryStore }
     *     
     */
    public MemoryStore getMemoryStore() {
        return memoryStore;
    }

    /**
     * Sets the value of the memoryStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemoryStore }
     *     
     */
    public void setMemoryStore(MemoryStore value) {
        this.memoryStore = value;
    }

    /**
     * Gets the value of the nativeStore property.
     * 
     * @return
     *     possible object is
     *     {@link NativeStore }
     *     
     */
    public NativeStore getNativeStore() {
        return nativeStore;
    }

    /**
     * Sets the value of the nativeStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link NativeStore }
     *     
     */
    public void setNativeStore(NativeStore value) {
        this.nativeStore = value;
    }

    /**
     * Gets the value of the rdbmsStore property.
     * 
     * @return
     *     possible object is
     *     {@link RdbmsStore }
     *     
     */
    public RdbmsStore getRdbmsStore() {
        return rdbmsStore;
    }

    /**
     * Sets the value of the rdbmsStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link RdbmsStore }
     *     
     */
    public void setRdbmsStore(RdbmsStore value) {
        this.rdbmsStore = value;
    }

    /**
     * Gets the value of the stackable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stackable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStackable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stackable }
     * 
     * 
     */
    public List<Stackable> getStackable() {
        if (stackable == null) {
            stackable = new ArrayList<Stackable>();
        }
        return this.stackable;
    }

    /**
     * Gets the value of the httpStore property.
     * 
     * @return
     *     possible object is
     *     {@link HttpStore }
     *     
     */
    public HttpStore getHttpStore() {
        return httpStore;
    }

    /**
     * Sets the value of the httpStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link HttpStore }
     *     
     */
    public void setHttpStore(HttpStore value) {
        this.httpStore = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}

// CHECKSTYLE:ON
