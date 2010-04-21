/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 * 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.ConnectivityManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager.LockState;
import org.eclipse.smila.connectivity.framework.CrawlState;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.compound.CompoundException;
import org.eclipse.smila.connectivity.framework.compound.CompoundManager;
import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerControllerPerformanceCounterHelper;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;
import org.eclipse.smila.connectivity.framework.util.CrawlerControllerCallback;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * The Class CrawlThread.
 */
public class CrawlThread extends Thread {

  /**
   * The Constant MAX_NON_CRICICAL_ATTEMPTS_TO_GET_INDEX.
   */
  private static final int MAX_NON_CRICICAL_ATTEMPTS_TO_GET_INDEX = 1000;

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(CrawlThread.class);

  /**
   * Callback to the CrawlerController.
   */
  private final CrawlerControllerCallback _controllerCallback;

  /**
   * A reference to this CrawlThread's CrawlState.
   */
  private final CrawlState _crawlState;

  /**
   * The ConnectivityManager.
   */
  private final ConnectivityManager _connectivityManager;

  /**
   * The DeltaIndexingManager.
   */
  private final DeltaIndexingManager _diManager;

  /**
   * The CompoundManager.
   */
  private final CompoundManager _compoundManager;

  /**
   * The Crawler used for crawling in this CrawlThread.
   */
  private final Crawler _crawler;

  /**
   * The DataSourceConnectionConfig for the Crawler.
   */
  private final DataSourceConnectionConfig _configuration;

  /**
   * Flag if this Thread was stopped by calling method stopCrawl().
   */
  private boolean _stopped;

  /**
   * The _performance counter helper.
   */
  private CrawlerControllerPerformanceCounterHelper _performanceCounterHelper;

  /**
   * The delta indexing session id.
   */
  private String _sessionId;

  /**
   * A buffer of records to be flushed in one block to the ConnectivityManager.
   */
  private ArrayList<Record> _recordBuffer = new ArrayList<Record>();

  /**
   * Flag to store the last time a flush was performed.
   */
  private long _lastFlush;

  /**
   * Constructor.
   * 
   * @param controllerCallback
   *          the callback to the CrawlerController
   * @param crawlState
   *          the CrawlState
   * @param connectivityManager
   *          the ConnectivityManager
   * @param diManager
   *          the DeltaIndexingManager
   * @param compoundManager
   *          the CompoundManager
   * @param crawler
   *          the Crawler
   * @param configuration
   *          the DataSourceConnectionConfig
   * @throws DeltaIndexingException
   *           if DeltaIndexing is enabled and no DeltaIndexing session can be initialized
   */
  public CrawlThread(final CrawlerControllerCallback controllerCallback, final CrawlState crawlState,
    final ConnectivityManager connectivityManager, final DeltaIndexingManager diManager,
    final CompoundManager compoundManager, final Crawler crawler, final DataSourceConnectionConfig configuration)
    throws DeltaIndexingException {
    if (controllerCallback == null) {
      final String msg = "Parameter controllerCallback is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException(msg);
    }
    if (crawlState == null) {
      final String msg = "Parameter crawlState is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException(msg);
    }
    if (connectivityManager == null) {
      final String msg = "Parameter connectivityManager is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException("parameter connectivityManager is null");
    }
    if (compoundManager == null) {
      final String msg = "Parameter compoundManager is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException("parameter connectivityManager is null");
    }
    if (crawler == null) {
      final String msg = "Parameter crawler is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException("parameter crawler is null");
    }

    // check if data source is locked in DeltaIndexing
    if (controllerCallback.doDeltaIndexing(configuration.getDeltaIndexing())) {
      final LockState state = diManager.getLockStates().get(configuration.getDataSourceID());
      if (state != null && state == LockState.LOCKED) {
        throw new DeltaIndexingException("data source " + configuration.getDataSourceID()
          + " is already locked by another session");
      }
    }

    if (_log.isTraceEnabled()) {
      final String msg = "Creating CrawlThread for dataSourceId " + configuration.getDataSourceID();
      _log.trace(msg);
    }
    _controllerCallback = controllerCallback;
    _crawlState = crawlState;
    _connectivityManager = connectivityManager;
    _diManager = diManager;
    _compoundManager = compoundManager;
    _crawler = crawler;
    _configuration = configuration;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    try {
      if (_controllerCallback.doDeltaIndexing(_configuration.getDeltaIndexing())) {
        _sessionId = _diManager.init(_configuration.getDataSourceID());
      }
      try {
        _crawler.initialize(_configuration);
        getPerformanceCounterHelper().setJobId(_crawlState.getJobId());
        getPerformanceCounterHelper().setCrawlerStartDate(new Date(_crawlState.getStartTime()));
      } catch (final CrawlerException e) {
        getPerformanceCounterHelper().addException(e);
        _log.error("Non critical exception during crawler initialization", e);
      }
      _lastFlush = System.currentTimeMillis();
      DataReference[] dataReferences;
      while (!_stopped) {
        dataReferences = null;
        int i = 0;
        for (i = 0; i < MAX_NON_CRICICAL_ATTEMPTS_TO_GET_INDEX; i++) {
          try {
            dataReferences = _crawler.getNext();
            break; // for
          } catch (final CrawlerException e) {
            getPerformanceCounterHelper().addException(e);
            _log.error("Non critical exception during crawler getNext() call", e);
          }
        }
        if (dataReferences == null) {
          if (i >= MAX_NON_CRICICAL_ATTEMPTS_TO_GET_INDEX - 1) {
            _log.error("Maximum noncritical errors reached");
          }
          break; // while
        }
        processDataReferences(dataReferences, null);
      } // while
      if (!_stopped) {
        flushRecords();

        // execute delta delete only if thread was not stopped and no errors occured
        if (_crawlState.getLastError() == null) {
          deleteDelta(_configuration.getDataSourceID());
        }
        // crawl completed successfully
        _crawlState.setState(CrawlThreadState.Finished);
      } else {
        _crawlState.setState(CrawlThreadState.Stopped);
      }
    } catch (final CrawlerCriticalException e) {
      getPerformanceCounterHelper().addCriticalException(e);
      _log.error(e);
      _crawlState.setLastError(e);
      _crawlState.setState(CrawlThreadState.Aborted);
    } catch (final Throwable e) {
      getPerformanceCounterHelper().addCriticalException(e);
      _log.error(e);
      _crawlState.setLastError(e);
      _crawlState.setState(CrawlThreadState.Aborted);
    } finally {
      // close the Crawler conversation
      try {
        _crawler.close();
      } catch (final CrawlerException e) {
        getPerformanceCounterHelper().addException(e);
        if (_log.isErrorEnabled()) {
          final String msg =
            "Error while closing Crawler after crawling dataSourceId " + _configuration.getDataSourceID();
          _log.error(msg, e);
        }
      }

      if (_controllerCallback.doDeltaIndexing(_configuration.getDeltaIndexing())) {
        // finish DeltaIndexing (close session and remove lock)
        _log.info("Removing deltaindexing lock on datasource " + _configuration.getDataSourceID());
        try {
          _diManager.finish(_sessionId);
          _log.info("Finished session " + _sessionId + " and removed Deltaindexing lock on datasource "
            + _configuration.getDataSourceID());
        } catch (final Exception e) {
          getPerformanceCounterHelper().addCriticalException(e);
          if (_log.isErrorEnabled()) {
            final String msg =
              "Error while finishing DeltaIndexing after crawling dataSourceId " + _configuration.getDataSourceID();
            _log.error(msg, e);
          }
        }
      }
      // unregister CrawlThread from CrawlerController
      _log.info("Unregistering crawling thread " + _configuration.getDataSourceID());
      try {
        _controllerCallback.unregister(_configuration.getDataSourceID());
        _log.info("Crawling thread " + _configuration.getDataSourceID() + " unregistered");
      } catch (final Exception e) {
        getPerformanceCounterHelper().addException(e);
        if (_log.isErrorEnabled()) {
          final String msg =
            "Error while unregistering CrawlThread after crawling dataSourceId " + _configuration.getDataSourceID();
          _log.error(msg, e);
        }
      }
      // set the end time of the CrawlThread
      try {
        _crawlState.setEndTime(System.currentTimeMillis());
        getPerformanceCounterHelper().setCrawlerEndDate(new Date(_crawlState.getEndTime()));
      } catch (final Exception e) {
        getPerformanceCounterHelper().addException(e);
        if (_log.isErrorEnabled()) {
          final String msg =
            "Error while settingend end time in CrawlState after crawling dataSourceId "
              + _configuration.getDataSourceID();
          _log.error(msg, e);
        }
      }
    }
    _log.info("Crawling thread " + _configuration.getDataSourceID() + " stopped.");
  }

  /**
   * Process DataReference objects.
   * 
   * @param dataReferences
   *          the DataReference to process
   * @param parentId
   *          the id of the parent record or null if none exists
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  private void processDataReferences(final DataReference[] dataReferences, final Id parentId)
    throws CrawlerCriticalException {
    if (dataReferences != null) {
      try {
        getPerformanceCounterHelper().incrementDeltaIndicesBy(dataReferences.length);

        // iterate over data references, check if to update and add to recordBuffer
        for (DataReference dataReference : dataReferences) {
          // check if CrawlThread was stopped in the meantime
          if (_stopped) {
            return;
          }

          if (dataReference != null) {
            try {
              // check if to flush record buffer
              checkForFlush();

              boolean isUpdate = true;
              if (_controllerCallback.doCheckForUpdate(_configuration.getDeltaIndexing())) {
                isUpdate = _diManager.checkForUpdate(_sessionId, dataReference.getId(), dataReference.getHash());
              }
              if (!_stopped && isUpdate) {
                // load complete Record via callback
                Record record = dataReference.getRecord();
                if (record != null) {
                  // set jobId as annotation on record
                  JobIdHelper.setJobIdAnnotation(record, _crawlState);

                  final long recordAttachmentLength = getAttachmentsByteLength(record);
                  getPerformanceCounterHelper().incrementAttachmentBytes(recordAttachmentLength);

                  // check if the record is a compound
                  final boolean isCompound = isCompound(record, _configuration);
                  if (isCompound) {
                    // process compound objects
                    record = processCompounds(record, _configuration, parentId);
                  } // if

                  // record may be set to null by handleCompounds
                  if (record != null) {
                    // add record to recordBuffer to be sent to ConnectivityManager
                    _recordBuffer.add(record);

                    getPerformanceCounterHelper().incrementRecords();
                    if (_controllerCallback.doDeltaIndexing(_configuration.getDeltaIndexing())) {
                      _diManager.visit(_sessionId, dataReference.getId(), dataReference.getHash(), isCompound);
                    }
                  } // if
                } // if
              } // if
            } catch (final InvalidTypeException e) {
              getPerformanceCounterHelper().addCriticalException(e);
              _crawlState.setLastError(e);
              if (_log.isErrorEnabled()) {
                final String msg = "Error while crawling dataSourceId " + _configuration.getDataSourceID();
                _log.error(msg, e);
              }
            } catch (final DeltaIndexingSessionException e) {
              getPerformanceCounterHelper().addCriticalException(e);
              _crawlState.setLastError(e);
              if (_log.isErrorEnabled()) {
                final String msg = "Error while crawling dataSourceId " + _configuration.getDataSourceID();
                _log.error(msg, e);
              }
            } catch (final DeltaIndexingException e) {
              getPerformanceCounterHelper().addException(e);
              _crawlState.setLastError(e);
              if (_log.isErrorEnabled()) {
                final String msg = "Error while crawling dataSourceId " + _configuration.getDataSourceID();
                _log.error(msg, e);
              }
            } catch (final ConnectivityException e) {
              getPerformanceCounterHelper().addCriticalException(e);
              _crawlState.setLastError(e);
              if (_log.isErrorEnabled()) {
                final String msg = "Error while crawling dataSourceId " + _configuration.getDataSourceID();
                _log.error(msg, e);
              }
            } catch (final CrawlerException e) {
              getPerformanceCounterHelper().addException(e);
              if (_log.isErrorEnabled()) {
                final String msg =
                  "Error while processing record with Id " + _configuration.getDataSourceID() + " of dataSourceId ";
                _log.error(msg, e);
              }
            } finally {
              dataReference.dispose();
            }
          } // if
        } // for
      } catch (final RuntimeException e) {
        getPerformanceCounterHelper().addCriticalException(e);
        _crawlState.setLastError(e);
        throw e;
      }
    } else {
      if (_log.isWarnEnabled()) {
        final String msg = "processDataReferences was called with dataReferences=null";
        _log.warn(msg);
      }
    }
  }

  /**
   * Stop the execution of this CrawlThread.
   */
  public void stopCrawl() {
    _stopped = true;
  }

  /**
   * Gets the performance counter helper.
   * 
   * @return the performance counter helper
   */
  private CrawlerControllerPerformanceCounterHelper getPerformanceCounterHelper() {
    if (_performanceCounterHelper == null) {
      _performanceCounterHelper =
        new CrawlerControllerPerformanceCounterHelper(_configuration, _crawler.hashCode());
    }
    return _performanceCounterHelper;
  }

  /**
   * Gets the attachments bytes length.
   * 
   * @param record
   *          the record
   * 
   * @return the attachments size
   */
  private long getAttachmentsByteLength(Record record) {
    if (!record.hasAttachments()) {
      return 0;
    }
    long size = 0;
    final Iterator<String> attachmentNames = record.getAttachmentNames();
    while (attachmentNames.hasNext()) {
      final String attachmentName = attachmentNames.next();
      final byte[] attachment = record.getAttachment(attachmentName);
      if (attachment != null) {
        size += attachment.length;
      }
    }
    return size;
  }

  /**
   * Starts the compound processing. The input record may be left unmodified, modified or even set to null depending on
   * the given configuration.
   * 
   * @param record
   *          the record to check and eventually process
   * @param config
   *          the DataSourceConnectionConfig
   * @param parentId
   *          the id of the parent record or null
   * @return the input record (changed or unchanged) or null
   * @throws CrawlerCriticalException
   *           if any critical exception occurs
   * @throws InvalidTypeException
   *           if any error occurs
   */
  private Record processCompounds(Record record, final DataSourceConnectionConfig config, final Id parentId)
    throws CrawlerCriticalException, InvalidTypeException {
    Crawler compoundCrawler = null;
    try {
      // execute compound extraction
      compoundCrawler = _compoundManager.extract(record, config);
      if (compoundCrawler != null) {
        DataReference[] dataReferences;
        while (!_stopped) {
          dataReferences = null;
          int i = 0;
          for (i = 0; i < MAX_NON_CRICICAL_ATTEMPTS_TO_GET_INDEX; i++) {
            try {
              dataReferences = compoundCrawler.getNext();
              break;
            } catch (final CrawlerException e) {
              getPerformanceCounterHelper().addException(e);
              _log.error("Non critical exception during compound crawler getNext() call", e);
            }
          }
          if (dataReferences == null) {
            if (i >= MAX_NON_CRICICAL_ATTEMPTS_TO_GET_INDEX - 1) {
              _log.error("Maximum noncritical errors reached");
            }
            break;
          }
          processDataReferences(dataReferences, record.getId());
        } // while
      } // if

      // adapt original record according to config
      record = _compoundManager.adaptCompoundRecord(record, config);

    } catch (CompoundException e) {
      final String msg = "Error during compound processing of record " + record.getId();
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
    } finally {
      if (compoundCrawler != null) {
        // close the CompoundCrawler conversation if any exists
        try {
          compoundCrawler.close();
        } catch (final CrawlerException e) {
          getPerformanceCounterHelper().addException(e);
          if (_log.isErrorEnabled()) {
            final String msg =
              "Error while closing CompoundCrawler while crawling dataSourceId " + _configuration.getDataSourceID();
            _log.error(msg, e);
          }
        }
      } // if
    } // finally

    // return record
    return record;
  }

  /**
   * Checks if CompoundHandling is enabled (a CompoundHandling config exists) and the record is a compound.
   * 
   * @param record
   *          the record to check
   * @param config
   *          the DataSourceConnectionConfig
   * @return true if compound handling is enabled and the record is a compound, false otherwise
   */
  private boolean isCompound(final Record record, final DataSourceConnectionConfig config) {
    if (config.getCompoundHandling() != null) {
      try {
        return _compoundManager.isCompound(record, config);
      } catch (CompoundException e) {
        final String msg = "Error while checking if record " + record.getId() + " is a compound";
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
      }
    }
    return false;
  }

  /**
   * Deletes all records of a delta indexing run for the given dataSourceId that were not visited.
   * 
   * @param dataSourceId
   *          the id of the data source
   * @return the number of deleted Ids
   */
  private int deleteDelta(final String dataSourceId) {
    int count = 0;
    if (_controllerCallback.doDeltaDelete(_configuration.getDeltaIndexing())) {
      try {
        final RecordFactory recordFactory = RecordFactory.DEFAULT_INSTANCE;
        final Iterator<Id> it = _diManager.obsoleteIdIterator(_sessionId, dataSourceId);
        if (it != null) {
          while (it.hasNext()) {
            final Id id = it.next();
            if (id != null) {
              final Record record = recordFactory.createRecord();
              JobIdHelper.setJobIdAnnotation(record, _crawlState);
              _connectivityManager.delete(new Record[] { record });
              _diManager.delete(_sessionId, id);
              count++;
            }
          }
        }
      } catch (final Exception e) {
        final String msg = "Error during execution of deleteDelta for dataSourceId " + dataSourceId;
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
      }
    }
    return count;
  }

  /**
   * Checks if the _recordBuffer needs to be flushed. Flushing is done if either the minimum number of records is
   * reached or the last flush is older than a specified flushing interval.
   * 
   * @throws ConnectivityException
   *           if any error occurs
   */
  private void checkForFlush() throws ConnectivityException {
    if (_recordBuffer.size() >= _configuration.getRecordBuffer().getSize()
      || ((System.currentTimeMillis() - _lastFlush) > _configuration.getRecordBuffer().getFlushInterval())) {
      flushRecords();
    }
  }

  /**
   * Flushes the non empty _recordBuffer by sending all records in the list to ConnectivityManager.
   * 
   * @throws ConnectivityException
   *           if any error occurs
   */
  private void flushRecords() throws ConnectivityException {
    if (!_recordBuffer.isEmpty()) {
      try {
        _lastFlush = System.currentTimeMillis();
        _connectivityManager.add(_recordBuffer.toArray(new Record[_recordBuffer.size()]));
      } finally {
        _recordBuffer.clear();
      }
    } // if
  }
}
