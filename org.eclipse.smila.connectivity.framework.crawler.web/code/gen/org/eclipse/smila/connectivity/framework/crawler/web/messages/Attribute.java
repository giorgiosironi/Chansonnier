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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Attribute Specification
 * 
 * <p>Java class for Attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attribute">
 *   &lt;complexContent>
 *     &lt;extension base="{}Attribute">
 *       &lt;redefine>
 *         &lt;complexType name="Attribute">
 *           &lt;complexContent>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;attribute name="KeyAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;attribute name="HashAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;attribute name="MimeTypeAttribute" type="{}MimeTypeAttributeType" />
 *               &lt;attribute name="Attachment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *             &lt;/restriction>
 *           &lt;/complexContent>
 *         &lt;/complexType>
 *       &lt;/redefine>
 *       &lt;choice>
 *         &lt;element name="FieldAttribute" type="{}FieldAttributeType"/>
 *         &lt;element name="MetaAttribute" type="{}MetaAttributeType"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", propOrder = {
    "fieldAttribute",
    "metaAttribute"
})
public class Attribute
    extends OriginalAttribute
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "FieldAttribute")
    protected FieldAttributeType fieldAttribute;
    @XmlElement(name = "MetaAttribute")
    protected MetaAttributeType metaAttribute;

    /**
     * Gets the value of the fieldAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link FieldAttributeType }
     *     
     */
    public FieldAttributeType getFieldAttribute() {
        return fieldAttribute;
    }

    /**
     * Sets the value of the fieldAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldAttributeType }
     *     
     */
    public void setFieldAttribute(FieldAttributeType value) {
        this.fieldAttribute = value;
    }

    /**
     * Gets the value of the metaAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link MetaAttributeType }
     *     
     */
    public MetaAttributeType getMetaAttribute() {
        return metaAttribute;
    }

    /**
     * Sets the value of the metaAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetaAttributeType }
     *     
     */
    public void setMetaAttribute(MetaAttributeType value) {
        this.metaAttribute = value;
    }

}

// CHECKSTYLE:ON
