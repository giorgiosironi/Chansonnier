/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.util;

import java.util.Map;

import org.eclipse.smila.connectivity.framework.CrawlerCallback;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.util.internal.DataReferenceImpl;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Attribute;

/**
 * A factory to create DataReference objects in Crawlers.
 */
public final class DataReferenceFactory {

  /**
   * singleton instance.
   */
  private static DataReferenceFactory s_instance;

  /**
   * Default Constructor.
   */
  private DataReferenceFactory() {
  }

  /**
   * Returns the singleton instance of the DataReferenceFactory.
   * 
   * @return the DataReferenceFactory
   */
  public static DataReferenceFactory getInstance() {
    if (s_instance == null) {
      s_instance = new DataReferenceFactory();
    }
    return s_instance;
  }

  /**
   * Creates a DataReference object with the given CrawlerCallback, the dataSourceId, list of Id Attributes and list of
   * Hash Attributes.
   * 
   * @param ccb
   *          reference to the CrawlerCallback
   * @param dataSourceId
   *          the dataSourceId. Must not be null or empty.
   * @param idAttributes
   *          an array of Attributes to create the Id from
   * @param hashAttributes
   *          an array of Attributes to create the Hash from
   * @return the DataReference
   */
  public DataReference createDataReference(final CrawlerCallback ccb, final String dataSourceId,
    final Attribute[] idAttributes, final Attribute[] hashAttributes) {
    return createDataReference(ccb, dataSourceId, idAttributes, hashAttributes, null);
  }

  /**
   * Creates a DataReference object with the given CrawlerCallback, the dataSourceId, list of Id Attributes and list of
   * Hash Attachments.
   * 
   * @param ccb
   *          reference to the CrawlerCallback
   * @param dataSourceId
   *          the dataSourceId. Must not be null or empty.
   * @param idAttributes
   *          an array of Attributes to create the Id from
   * @param hashAttachments
   *         a Map of attachment names and attachment String values to create the hash from
   * @return the DataReference
   */
  public DataReference createDataReference(final CrawlerCallback ccb, final String dataSourceId,
    final Attribute[] idAttributes, final Map<String, ?> hashAttachments) {
    return createDataReference(ccb, dataSourceId, idAttributes, null, hashAttachments);
  }

  /**
   * Creates a DataReference object with the given CrawlerCallback, the dataSourceId, list of Id Attributes, list of
   * Hash Attributes and list of Hash Attachments.
   * 
   * @param ccb
   *          reference to the CrawlerCallback
   * @param dataSourceId
   *          the dataSourceId. Must not be null or empty.
   * @param idAttributes
   *          an array of Attributes to create the Id from
   * @param hashAttributes
   *          an array of Attributes to create the Hash from
   * @param hashAttachments
   *          a Map of attachment names and attachment String values to create the hash from
   * @return the DataReference
   */
  public DataReference createDataReference(final CrawlerCallback ccb, final String dataSourceId,
    final Attribute[] idAttributes, final Attribute[] hashAttributes, final Map<String, ?> hashAttachments) {
    final Id id = ConnectivityIdFactory.getInstance().createId(dataSourceId, idAttributes);
    final String hash = ConnectivityHashFactory.getInstance().createHash(hashAttributes, hashAttachments);
    return createDataReference(ccb, id, hash);
  }

  /**
   * Creates a DataReference object with the given CrawlerCallback, Id and hash.
   * 
   * @param ccb
   *          reference to the CrawlerCallback
   * @param id
   *          the Id
   * @param hash
   *          the hash
   * @return the DataReference
   */
  public DataReference createDataReference(final CrawlerCallback ccb, final Id id, final String hash) {
    return new DataReferenceImpl(ccb, id, hash);
  }
}
