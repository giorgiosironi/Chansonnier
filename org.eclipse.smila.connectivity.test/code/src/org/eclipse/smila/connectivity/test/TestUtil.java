/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.test;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * The Class TestUtil.
 */
public final class TestUtil {

  /**
   * The default RECORD_FACTORY.
   */
  public static final RecordFactory RECORD_FACTORY = RecordFactory.DEFAULT_INSTANCE;

  /**
   * Instantiates a new test util.
   */
  private TestUtil() {
  }

  /**
   * Creates am array of 10 Record objects.
   * 
   * @param dataSourceId
   *          the dataSourceId
   * 
   * @return a Record[]
   * 
   * @throws InvalidTypeException
   *           a InvalidTypeException
   */
  public static Record[] createDiRecord(final String dataSourceId) throws InvalidTypeException {
    final Record[] diData = new Record[AllTests.NUMBER_10];
    for (int i = 0; i < AllTests.NUMBER_10; i++) {
      diData[i] = RECORD_FACTORY.createRecord();
      diData[i].setId(IdFactory.DEFAULT_INSTANCE.createId(dataSourceId, "myNameVale" + i));
      diData[i].setMetadata(RECORD_FACTORY.createMetadataObject());
    }
    return diData;
  }

  /**
   * Creates am array of 8 Record objects, first 4 are identical to the ones created with createDiRecord(), the last 4
   * have a different HASH.
   * 
   * @param dataSourceId
   *          the dataSourceId
   * 
   * @return a Record[]
   * 
   * @throws InvalidTypeException
   *           a InvalidTypeException
   */
  public static Record[] createDeltaDiRecord(final String dataSourceId) throws InvalidTypeException {
    final Record[] diData = new Record[AllTests.NUMBER_8];

    // create 4 identical Records (no update)
    for (int i = 0; i < AllTests.NUMBER_4; i++) {
      diData[i] = RECORD_FACTORY.createRecord();
      diData[i].setId(IdFactory.DEFAULT_INSTANCE.createId(dataSourceId, "myNameVale" + i));
      diData[i].setMetadata(RECORD_FACTORY.createMetadataObject());
    }
    // create 4 changed Records (update)
    for (int i = AllTests.NUMBER_4; i < AllTests.NUMBER_8; i++) {
      diData[i] = RECORD_FACTORY.createRecord();
      diData[i].setId(IdFactory.DEFAULT_INSTANCE.createId(dataSourceId, "myNameVale" + i));
      diData[i].setMetadata(RECORD_FACTORY.createMetadataObject());
    }
    // leave 2 records for deltaIndexingDelete
    return diData;
  }

  /**
   * Creates an array of 10 Id objects.
   * 
   * @param dataSourceId
   *          the dataSourceId
   * 
   * @return a Record[]
   * 
   * @throws InvalidTypeException
   *           a InvalidTypeException
   */
  public static Id[] createIds(final String dataSourceId) throws InvalidTypeException {
    final Id[] ids = new Id[AllTests.NUMBER_10];
    for (int i = 0; i < AllTests.NUMBER_10; i++) {
      ids[i] = IdFactory.DEFAULT_INSTANCE.createId(dataSourceId, "myNameVale" + i);
    }
    return ids;
  }
}
