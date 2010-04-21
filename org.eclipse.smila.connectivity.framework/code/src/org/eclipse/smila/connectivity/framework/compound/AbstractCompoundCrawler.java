/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound;

import org.eclipse.smila.connectivity.framework.AbstractCrawler;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The abstract class AbstractCompoundCrawler.
 */
public abstract class AbstractCompoundCrawler extends AbstractCrawler implements CompoundCrawler {

  /**
   * The compound record to extract data from.
   */
  private Record _compoundRecord;

  /**
   * Sets the compound record to extract data from.
   * 
   * @param record
   *          the compound Record
   * @throws CrawlerException
   *           if parameter record is null
   */
  public final void setCompoundRecord(final Record record) throws CrawlerException {
    if (record == null) {
      throw new CrawlerException("parameter record is null");
    }
    _compoundRecord = record;
  }

  /**
   * Gets the compound record.
   * 
   * @return the compound record.
   */
  public final Record getCompoundRecord() {
    return _compoundRecord;
  }
}
