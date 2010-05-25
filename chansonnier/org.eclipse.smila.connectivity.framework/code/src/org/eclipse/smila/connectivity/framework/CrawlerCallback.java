/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.MObject;

/**
 * A callback interface to access metadata and attachments of crawled data.
 */
public interface CrawlerCallback {
  /**
   * Returns the MObject for the given id.
   * 
   * @param id
   *          the record id
   * @return the MObject
   * @throws CrawlerException
   *           if any non critical error occurs
   * @throws CrawlerCriticalException
   *           if any critical error occurs
   */
  MObject getMObject(final Id id) throws CrawlerException, CrawlerCriticalException;

  /**
   * Returns an array of String[] containing the names of the available attachments for the given id.
   * 
   * @param id
   *          the record id
   * @return an array of String[] containing the names of the available attachments
   * @throws CrawlerException
   *           if any non critical error occurs
   * @throws CrawlerCriticalException
   *           if any critical error occurs
   */
  String[] getAttachmentNames(final Id id) throws CrawlerException, CrawlerCriticalException;

  /**
   * Returns the attachment for the given Id and name pair.
   * 
   * @param id
   *          the record id
   * @param name
   *          the name of the attachment
   * @return a byte[] containing the attachment
   * @throws CrawlerException
   *           if any non critical error occurs
   * @throws CrawlerCriticalException
   *           if any critical error occurs
   */
  byte[] getAttachment(final Id id, final String name) throws CrawlerException, CrawlerCriticalException;

  /**
   * Disposes the record with the given Id.
   * 
   * @param id
   *          the record id
   */
  void dispose(final Id id);
}
