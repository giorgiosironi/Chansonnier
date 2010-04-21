/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.util;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.tools.MObjectHelper;
import org.eclipse.smila.utils.digest.DigestHelper;

/**
 * A factory to create Hash objects in Crawlers and Agents.
 */
public final class ConnectivityHashFactory {

  /**
   * singleton instance.
   */
  private static ConnectivityHashFactory s_instance;

  /**
   * Default Constructor.
   */
  private ConnectivityHashFactory() {
  }

  /**
   * Returns the singleton instance of the HashFactory.
   * 
   * @return the HashFactory
   */
  public static ConnectivityHashFactory getInstance() {
    if (s_instance == null) {
      s_instance = new ConnectivityHashFactory();
    }
    return s_instance;
  }

  /**
   * Create a hash object based on the given Attributes.
   * 
   * @param hashAttributes
   *          an array of Attribute objects whose values are used to create the hash. Must not be null or empty.
   * @return a String containing the hash
   */
  public String createHash(final Attribute[] hashAttributes) {
    if (hashAttributes == null || hashAttributes.length == 0) {
      throw new IllegalArgumentException("Parameter hashAttributes must not be null or empty");
    }
    return createHash(hashAttributes, null);
  }

  /**
   * Create a hash object based on the given Attachments.
   * 
   * @param hashAttachments
   *          a Map of attachment names and attachment String values, that are used to create the hash. Must not be null
   *          or empty.
   * @return a String containing the hash
   */
  public String createHash(final Map<String, ?> hashAttachments) {
    if (hashAttachments == null || hashAttachments.isEmpty()) {
      throw new IllegalArgumentException("Parameter hashAttachments must not be null or empty");
    }
    return createHash(null, hashAttachments);
  }

  /**
   * Create a hash object based on the given Attributes and Attachments. One of the parameters may be null or empty.
   * 
   * @param hashAttributes
   *          an array of Attribute objects whose values are used to create the hash
   * @param hashAttachments
   *          a Map of attachment names and attachment String values, that are used to create the hash.
   * @return a String containing the hash
   */
  public String createHash(final Attribute[] hashAttributes, final Map<String, ?> hashAttachments) {
    if ((hashAttributes == null || hashAttributes.length == 0)
      && (hashAttachments == null || hashAttachments.isEmpty())) {
      throw new IllegalArgumentException(
        "Parameters hashAttributes and hashAttachments must not both be null or empty");
    }
    final StringBuilder buffer = new StringBuilder();

    // append attribute values to buffer
    if (hashAttributes != null) {
      for (final Attribute attribute : hashAttributes) {
        final String value = MObjectHelper.glueLiterals(attribute);
        buffer.append(attribute.getName()).append('=').append(value).append(';');
      }
    }

    // append attachment values to buffer
    if (hashAttachments != null) {
      final Iterator<String> it = hashAttachments.keySet().iterator();
      while (it.hasNext()) {
        final String attachmentName = it.next();
        final Object attachmentValue = hashAttachments.get(attachmentName);
        if (attachmentValue instanceof String) {
          buffer.append(attachmentName).append('=').append(attachmentValue).append(';');
        } else if (attachmentValue instanceof byte[]) {
          buffer.append(attachmentName).append('=').append(DigestHelper.calculateDigest((byte[]) attachmentValue))
            .append(';');
        } else {
          throw new IllegalArgumentException("Attachments must be of type String or byte[]");
        }
      }
    }
    return DigestHelper.calculateDigest(buffer.toString());
  }

  public String createHash(final String value) {
    return DigestHelper.calculateDigest(value);
  }
}
