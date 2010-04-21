/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH) 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config.interfaces;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.smila.connectivity.framework.schema.config.AttributeAdapter;
import org.eclipse.smila.connectivity.framework.schema.config.MimeTypeAttributeType;

/**
 * The Interface Attribute.
 */
@XmlJavaTypeAdapter(AttributeAdapter.class)
public interface IAttribute {

  /**
   * Gets the value of the keyAttribute property.
   * 
   * @return possible object is {@link Boolean }
   */
  boolean isKeyAttribute();

  /**
   * Sets the value of the keyAttribute property.
   * 
   * @param value
   *          allowed object is {@link Boolean }
   */
  void setKeyAttribute(Boolean value);

  /**
   * Gets the value of the hashAttribute property.
   * 
   * @return possible object is {@link Boolean }
   */
  boolean isHashAttribute();

  /**
   * Sets the value of the hashAttribute property.
   * 
   * @param value
   *          allowed object is {@link Boolean }
   */
  void setHashAttribute(Boolean value);

  /**
   * Gets the value of the name property.
   * 
   * @return possible object is {@link String }
   */
  String getName();

  /**
   * Sets the value of the name property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  void setName(String value);

  /**
   * Gets the value of the type property.
   * 
   * @return possible object is {@link String }
   */
  String getType();

  /**
   * Sets the value of the type property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  void setType(String value);

  /**
   * Gets the value of the mimeTypeAttribute property.
   * 
   * @return possible object is {@link MimeTypeAttributeType }
   */
  MimeTypeAttributeType getMimeTypeAttribute();

  /**
   * Sets the value of the mimeTypeAttribute property.
   * 
   * @param value
   *          allowed object is {@link MimeTypeAttributeType }
   */
  void setMimeTypeAttribute(MimeTypeAttributeType value);

  /**
   * Checks if is attachment.
   * 
   * @return true, if is attachment
   */
  boolean isAttachment();

  /**
   * Sets the attachment.
   * 
   * @param value
   *          the new attachment
   */
  void setAttachment(final Boolean value);

}
