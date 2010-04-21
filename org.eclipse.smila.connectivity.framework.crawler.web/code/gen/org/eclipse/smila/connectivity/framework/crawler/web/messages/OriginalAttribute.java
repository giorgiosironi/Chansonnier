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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.smila.connectivity.framework.schema.config.MimeTypeAttributeType;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;


/**
 * <p>Java class for Attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="KeyAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="HashAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MimeTypeAttribute" type="{}MimeTypeAttributeType" />
 *       &lt;attribute name="Attachment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlSeeAlso({
    Attribute.class
})
public class OriginalAttribute
    implements Serializable, IAttribute
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "KeyAttribute")
    protected Boolean keyAttribute;
    @XmlAttribute(name = "HashAttribute")
    protected Boolean hashAttribute;
    @XmlAttribute(name = "Name", required = true)
    protected String name;
    @XmlAttribute(name = "Type", required = true)
    protected String type;
    @XmlAttribute(name = "MimeTypeAttribute")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected MimeTypeAttributeType mimeTypeAttribute;
    @XmlAttribute(name = "Attachment")
    protected Boolean attachment;

    /**
     * Gets the value of the keyAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isKeyAttribute() {
        if (keyAttribute == null) {
            return false;
        } else {
            return keyAttribute;
        }
    }

    /**
     * Sets the value of the keyAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKeyAttribute(Boolean value) {
        this.keyAttribute = value;
    }

    /**
     * Gets the value of the hashAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isHashAttribute() {
        if (hashAttribute == null) {
            return false;
        } else {
            return hashAttribute;
        }
    }

    /**
     * Sets the value of the hashAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHashAttribute(Boolean value) {
        this.hashAttribute = value;
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

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the mimeTypeAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public MimeTypeAttributeType getMimeTypeAttribute() {
        return mimeTypeAttribute;
    }

    /**
     * Sets the value of the mimeTypeAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeTypeAttribute(MimeTypeAttributeType value) {
        this.mimeTypeAttribute = value;
    }

    /**
     * Gets the value of the attachment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isAttachment() {
        if (attachment == null) {
            return false;
        } else {
            return attachment;
        }
    }

    /**
     * Sets the value of the attachment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAttachment(Boolean value) {
        this.attachment = value;
    }

}

// CHECKSTYLE:ON
