/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.recycler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smila.connectivity.queue.worker.RecordRecycler;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerException;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueServicesAccessPoint;
import org.eclipse.smila.connectivity.queue.worker.internal.LocalUtils;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * The Class RecordRecyclerImpl.
 */
public class RecordRecyclerImpl extends AbstractQueueServicesAccessPoint implements RecordRecycler {

  /**
   * The _monitor.
   */
  private final Object _monitor = new Object();

  /**
   * The _threads.
   */
  private final Map<String, Thread> _threads = new HashMap<String, Thread>();

  /**
   * The _recyclers.
   */
  private final Map<String, Recycler> _recyclers = new HashMap<String, Recycler>();

  /**
   * Instantiates a new record recycler impl.
   */
  public RecordRecyclerImpl() {
    super("RecordRecycler");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.RecordRecycler#recycle(java.lang.String, java.lang.String)
   */
  public void recycle(final String configurationId, final String dataSourceId) throws RecordRecyclerException {
    final Recycler recycler = initRecycler(configurationId, dataSourceId);
    recycler.recycle();
    recycler.stop();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.RecordRecycler#recycleAsync(java.lang.String, java.lang.String,
   *      java.lang.String)
   */
  public void recycleAsync(final String configurationId, final String dataSourceId) throws RecordRecyclerException {
    synchronized (_monitor) {
      final Recycler recycler = initRecycler(configurationId, dataSourceId);
      final Thread thread = new Thread(recycler);
      _threads.put(dataSourceId, thread);
      thread.start();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.RecordRecycler#stopRecycle(java.lang.String)
   */
  public void stopRecycle(final String dataSourceId) throws RecordRecyclerException {
    synchronized (_monitor) {
      final Recycler recycler = _recyclers.get(dataSourceId);
      if (recycler == null) {
        throw new RecordRecyclerException(String.format("Recycler for data source [%s] is not found", dataSourceId));
      }
      recycler.stopRecycle();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.RecordRecycler#getRecordsRecycled(java.lang.String)
   */
  public long getRecordsRecycled(final String dataSourceId) {
    synchronized (_monitor) {
      final Recycler recycler = _recyclers.get(dataSourceId);
      if (recycler == null) {
        return 0;
      } else {
        return recycler.getRecordsRecycled();
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.RecordRecycler#getStatus(java.lang.String)
   */
  public RecordRecyclerStatus getStatus(final String dataSourceId) {
    synchronized (_monitor) {
      final Recycler recycler = _recyclers.get(dataSourceId);
      if (recycler == null) {
        return RecordRecyclerStatus.FINISHED;
      } else {
        return recycler.getStatus();
      }
    }
  }

  /**
   * Inits the recycler.
   * 
   * @param configurationId
   *          the configuration id
   * @param dataSourceId
   *          the data source id
   * 
   * @return the recycler
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  private Recycler initRecycler(final String configurationId, final String dataSourceId)
    throws RecordRecyclerException {
    synchronized (_monitor) {
      final Thread thread = _threads.get(dataSourceId);
      if (thread != null) {
        if (thread.isAlive()) {
          throw new RecordRecyclerException(String.format("DataSource %s is busy", dataSourceId));
        } else {
          _threads.remove(dataSourceId);
        }
      }
      Recycler recycler = _recyclers.get(dataSourceId);
      if (recycler != null) {
        if (recycler.getStatus() != RecordRecyclerStatus.FINISHED) {
          throw new RecordRecyclerException(String.format("DataSource %s is busy", dataSourceId));
        }
        _recyclers.remove(dataSourceId);
        recycler.stop();
      }
      recycler = new RecyclerImpl(configurationId, this, dataSourceId);
      recycler.start();
      _recyclers.put(dataSourceId, recycler);
      return recycler;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.RecordRecycler#getConfigurations()
   */
  public String[] getConfigurations() throws RecordRecyclerException {
    final List<String> entries = ConfigUtils.getConfigEntries(LocalUtils.BUNDLE_ID, "recyclers");
    final List<String> configs = new ArrayList<String>();
    for (final String entry : entries) {
      if (entry.toLowerCase().endsWith(".xml")) {
        configs.add(entry.substring(0, entry.length() - (2 + 2)));
      }
    }
    return configs.toArray(new String[configs.size()]);
  }
}
