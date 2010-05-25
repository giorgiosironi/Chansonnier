/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for JAXB CompoundHandling.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "_mimeTypeAttribute", "_extensionAttribute", "_contentAttachment",
    "_compoundAttributes" })
@XmlRootElement(name = "CompoundHandling")
public class CompoundHandling {

  /** The MimeTypeAttribute. */
  @XmlElement(name = "MimeTypeAttribute", required = false)
  protected String _mimeTypeAttribute;

  /** The ExtensionAttribute. */
  @XmlElement(name = "ExtensionAttribute", required = false)
  protected String _extensionAttribute;

  /** The ContentAttachment. */
  @XmlElement(name = "ContentAttachment", required = true)
  protected String _contentAttachment;

  /** The CompoundAttributes. */
  @XmlElement(name = "CompoundAttributes", required = true)
  protected CompoundHandling.CompoundAttributes _compoundAttributes;

  /**
   * Gets the value of the _mimeTypeAttribute.
   * 
   * @return possible object is {@link String }
   */
  public String getMimeTypeAttribute() {
    return _mimeTypeAttribute;
  }

  /**
   * Sets the value of the MimeTypeAttribute property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  public void setMimeTypeAttribute(final String value) {
    _mimeTypeAttribute = value;
  }

  /**
   * Gets the value of the _extensionAttribute.
   * 
   * @return possible object is {@link String }
   */
  public String getExtensionAttribute() {
    return _extensionAttribute;
  }

  /**
   * Sets the value of the ExtensionAttribute property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  public void setExtensionAttribute(final String value) {
    _extensionAttribute = value;
  }

  /**
   * Gets the value of the _contentAttachment.
   * 
   * @return possible object is {@link String }
   */
  public String getContentAttachment() {
    return _contentAttachment;
  }

  /**
   * Sets the value of the ContentAttachment property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  public void setContentAttachment(final String value) {
    _contentAttachment = value;
  }

  /**
   * Gets the value of the _compoundAttributes.
   * 
   * @return possible object is {@link CompoundHandling.CompoundAttributes }
   */
  public CompoundHandling.CompoundAttributes getCompoundAttributes() {
    return _compoundAttributes;
  }

  /**
   * Sets the value of the CompoundAttributes property.
   * 
   * @param attributes
   *          allowed object is {@link CompoundHandling.CompoundAttributes }
   */
  public void setCompoundAttributes(CompoundHandling.CompoundAttributes attributes) {
    _compoundAttributes = attributes;
  }

  /**
   * Java class for JAXB CompoundAttributes.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = { "_compoundAttributes" })
  public static class CompoundAttributes {

    /** The CompoundAttribute. */
    @XmlElement(name = "CompoundAttribute", required = true)
    protected List<CompoundHandling.CompoundAttribute> _compoundAttributes;

    /**
     * Gets the value of the _compoundAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link CompoundAttribute }
     * 
     * @return the attribute
     */
    public List<CompoundHandling.CompoundAttribute> getCompoundAttributes() {
      if (_compoundAttributes == null) {
        _compoundAttributes = new ArrayList<CompoundHandling.CompoundAttribute>();
      }
      return _compoundAttributes;
    }
  }

  /**
   * Java class for JAXB CompoundAttribute.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "CompoundAttribute", propOrder = { "_elementAttribute" })
  public static class CompoundAttribute implements Serializable {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ElementAttribute.
     */
    @XmlElement(name = "ElementAttribute")
    protected ElementAttributeType _elementAttribute;

    /**
     * The mapped name.
     */
    @XmlAttribute(name = "Name", required = true)
    protected String _name;

    /**
     * The attribute type.
     */
    @XmlAttribute(name = "Type", required = true)
    protected String _type;

    /**
     * Attachment flag.
     */
    @XmlAttribute(name = "Attachment")
    protected Boolean _attachment;

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
      return _name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *          allowed object is {@link String }
     * 
     */
    public void setName(String value) {
      _name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getType() {
      return _type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *          allowed object is {@link String }
     * 
     */
    public void setType(String value) {
      this._type = value;
    }

    /**
     * Gets the value of the attachment property.
     * 
     * @return possible object is {@link Boolean }
     * 
     */
    public boolean isAttachment() {
      if (_attachment == null) {
        return false;
      } else {
        return _attachment;
      }
    }

    /**
     * Sets the value of the attachment property.
     * 
     * @param value
     *          allowed object is {@link Boolean }
     * 
     */
    public void setAttachment(Boolean value) {
      this._attachment = value;
    }

    /**
     * Gets the value of the _elementAttribute property.
     * 
     * @return possible object is {@link ElementAttributeType }
     * 
     */
    public ElementAttributeType getElementAttribute() {
      return _elementAttribute;
    }

    /**
     * Sets the value of the _elementAttribute property.
     * 
     * @param value
     *          allowed object is {@link ElementAttributeType }
     * 
     */
    public void setElementAttribute(ElementAttributeType value) {
      _elementAttribute = value;
    }

  }
}
