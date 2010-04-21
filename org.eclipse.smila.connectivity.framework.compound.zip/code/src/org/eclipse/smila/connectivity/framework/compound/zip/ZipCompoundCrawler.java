/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.CrawlerCallback;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.compound.AbstractCompoundCrawler;
import org.eclipse.smila.connectivity.framework.schema.config.CompoundHandling;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.util.ConnectivityHashFactory;
import org.eclipse.smila.connectivity.framework.util.DataReferenceFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.tools.MObjectHelper;
import org.eclipse.smila.utils.file.EncodingHelper;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * The Interface CompoundHandler.
 */
public class ZipCompoundCrawler extends AbstractCompoundCrawler {

  /**
   * The Constant BUNDLE_ID.
   */
  private static final String BUNDLE_ID = "org.eclipse.smila.connectivity.framework.compound.zip";

  /**
   * The Constant QUEUE_POLL_WAITING.
   */
  private static final int QUEUE_POLL_WAITING = 300;

  /**
   * The Constant HAS_NEXT_WAITING.
   */
  private static final int HAS_NEXT_WAITING = 50;

  /**
   * The Constant CAPACITY.
   */
  private static final int CAPACITY = 100;

  /**
   * The Constant STEP.
   */
  private static final int STEP = 10;

  /**
   * Reference to the zip file.
   */
  private ZipFile _zipFile;

  /**
   * The _queue.
   */
  private ArrayBlockingQueue<DataReference> _queue;

  /**
   * The _crawl thread.
   */
  private ZipEntryProducerThread _producerThread;

  /**
   * The Id to ZipEntry mapping.
   */
  private Map<Id, ZipEntry> _entryMap;

  /**
   * The _attributes.
   */
  private CompoundHandling.CompoundAttribute[] _compoundAttributes;

  /**
   * The _attachment names.
   */
  private String[] _attachmentNames;

  /**
   * Flag if initialize() was called successfully.
   */
  private boolean _initialized;

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(this.getClass());

  /**
   * 
   * @param crawlerId
   */
  public ZipCompoundCrawler() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundCrawler#initialize(DataSourceConnectionConfig)
   */
  public void initialize(final DataSourceConnectionConfig config) throws CrawlerException, CrawlerCriticalException {
    if (config == null) {
      throw new CrawlerCriticalException("parameter config is null");
    }

    final Record record = getCompoundRecord();
    if (record == null) {
      throw new CrawlerCriticalException("the compound record was not set");
    }

    try {
      // get configured compound attributes and attachments
      final CompoundHandling.CompoundAttributes attributes = config.getCompoundHandling().getCompoundAttributes();
      final List<CompoundHandling.CompoundAttribute> attrs = attributes.getCompoundAttributes();
      _compoundAttributes = attrs.toArray(new CompoundHandling.CompoundAttribute[attrs.size()]);
      final List<String> attachmentsNames = new ArrayList<String>();
      for (final CompoundHandling.CompoundAttribute a : _compoundAttributes) {
        if (a.isAttachment()) {
          attachmentsNames.add(a.getName());
        }
      }
      _attachmentNames = attachmentsNames.toArray(new String[attachmentsNames.size()]);

      // get the content of the compound object
      final String contentAttachmentName = config.getCompoundHandling().getContentAttachment();
      byte[] content = record.getAttachment(contentAttachmentName);
      if (content == null) {
        content = new byte[0];
      }
      final File workingDir = WorkspaceHelper.createWorkingDir(BUNDLE_ID);
      final File file = new File(workingDir, record.getId().getIdHash());
      IOUtils.copy(new ByteArrayInputStream(content), new FileOutputStream(file));
      _zipFile = new ZipFile(file);

      _queue = new ArrayBlockingQueue<DataReference>(CAPACITY);
      _entryMap = new HashMap<Id, ZipEntry>();
      _producerThread = new ZipEntryProducerThread(this);
      _producerThread.start();
      _initialized = true;
    } catch (Throwable e) {
      final String msg = "Error during initialization";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      try {
        close();
      } catch (Exception ex) {
        if (_log.isErrorEnabled()) {
          _log.error("Error during close in initialization", ex);
        }
      }
      throw new CrawlerCriticalException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundCrawler#getNext()
   */
  public DataReference[] getNext() throws CrawlerException, CrawlerCriticalException {
    if (!_initialized) {
      throw new CrawlerCriticalException("ZipCompoundCrawler was not initialized");
    }
    while (hasNext()) {
      final List<DataReference> refList = new ArrayList<DataReference>();
      try {
        final DataReference ref = _queue.poll(QUEUE_POLL_WAITING, TimeUnit.MILLISECONDS);
        if (ref != null) {
          refList.add(ref);
          final int size = _queue.drainTo(refList, STEP - 1);
          return refList.toArray(new DataReference[size + 1]);
        }
      } catch (final InterruptedException e) {
        if (_log.isTraceEnabled()) {
          _log.trace("InterruptedException in getNext(): ", e);
        }
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundCrawler#close()
   */
  public void close() throws CrawlerException {
    _initialized = false;
    if (_zipFile != null) {
      try {
        _zipFile.close();
      } catch (IOException e) {
        final String msg = "Could not close temporary zip file " + _zipFile.getName();
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
      }

      final File file = new File(_zipFile.getName());
      file.delete();
      _zipFile = null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlerCallback#getMObject(Id)
   */
  public MObject getMObject(final Id id) throws CrawlerException, CrawlerCriticalException {
    if (!_initialized) {
      throw new CrawlerCriticalException("ZipCompoundCrawler was not initialized");
    }

    final ZipEntry zipEntry = _entryMap.get(id);
    if (zipEntry == null) {
      throw new CrawlerException("Could not find ZipEntry for id " + id);
    }

    final MObject mObject = getCompoundRecord().getFactory().createMetadataObject();
    for (final CompoundHandling.CompoundAttribute attribute : _compoundAttributes) {
      if (!attribute.isAttachment()) {
        final Object value = readAttribute(zipEntry, attribute, true);
        if (value != null) {
          try {
            MObjectHelper.addSimpleLiteralAttribute(getCompoundRecord().getFactory(), mObject, attribute.getName(),
              value);
          } catch (final Throwable e) {
            throw new CrawlerException(e);
          }
        } // if
      } // if
    } // for
    return mObject;
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlerCallback#getAttachmentNames(Id)
   */
  public String[] getAttachmentNames(final Id id) throws CrawlerException, CrawlerCriticalException {
    if (!_initialized) {
      throw new CrawlerCriticalException("ZipCompoundCrawler was not initialized");
    }

    return _attachmentNames;
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlerCallback#getAttachment(Id, String)
   */
  public byte[] getAttachment(final Id id, final String name) throws CrawlerException, CrawlerCriticalException {
    if (!_initialized) {
      throw new CrawlerCriticalException("ZipCompoundCrawler was not initialized");
    }

    final ZipEntry zipEntry = _entryMap.get(id);
    if (zipEntry == null) {
      throw new CrawlerException("Could not find ZipEntry for id " + id);
    }

    // find attribute
    for (final CompoundHandling.CompoundAttribute attribute : _compoundAttributes) {
      if (attribute.getName().equals(name)) {
        return readAttachment(zipEntry, attribute);
      }
    }
    throw new CrawlerException(String.format("Unable to find attachment definition for [%s]", name));
  }

  /**
   * {@inheritDoc}
   * 
   * @see CrawlerCallback#dispose(Id)
   */
  public void dispose(final Id id) {
    _entryMap.remove(id);
  }

  /**
   * Checks for next.
   * 
   * @return true, if successful
   */
  private boolean hasNext() {
    while (_producerThread != null && _queue.isEmpty()) {
      try {
        Thread.sleep(HAS_NEXT_WAITING);
      } catch (final InterruptedException e) {
        if (_log.isTraceEnabled()) {
          _log.trace("InterruptedException in hasNext(): ", e);
        }
      }
    }
    return !_queue.isEmpty();
  }

  /**
   * Reads the content of the given ZipEntry.
   * 
   * @param zipEntry
   *          the ZipEntry
   * @return a byte[]
   * @throws CrawlerException
   *           if any error occurs
   */
  private byte[] readZipEntryContent(final ZipEntry zipEntry) throws CrawlerException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream in = null;
    try {
      in = _zipFile.getInputStream(zipEntry);
      IOUtils.copy(in, out);
    } catch (IOException e) {
      final String msg =
        "Error reading content of ZipEntry '" + zipEntry.getName() + "' of record id "
          + getCompoundRecord().getId();
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new CrawlerException(msg, e);
    } catch (Throwable e) {
      // this may occur if a ZipEntry containing Umlauts in the filename is read. Depending on the zip tool, the
      // characters may not be encoded in utf-8, but ZipEntry expects names to be encoded in utf-8
      final String msg =
        "Error reading content of ZipEntry '" + zipEntry.getName() + "' of record id "
          + getCompoundRecord().getId();
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new CrawlerException(msg, e);
    } finally {
      IOUtils.closeQuietly(in);
      IOUtils.closeQuietly(out);
    }
    return out.toByteArray();
  }

  /**
   * Read attribute value.
   * 
   * @param zipEntry
   *          the ZipEntry
   * @param attribute
   *          the attribute
   * @param forceByteToString
   *          the force byte to string
   * 
   * @return the object
   * 
   * @throws CrawlerException
   *           the crawler exception
   */
  private Serializable readAttribute(final ZipEntry zipEntry, final CompoundHandling.CompoundAttribute attribute,
    final boolean forceByteToString) throws CrawlerException {
    switch (attribute.getElementAttribute()) {
      case NAME:
        return FilenameUtils.getName(zipEntry.getName());
      case FILE_EXTENSION:
        return FilenameUtils.getExtension(zipEntry.getName());
      case PATH:
        return zipEntry.getName();
      case LAST_MODIFIED_DATE:
        return new Date(zipEntry.getTime());
      case SIZE:
        return new Long(zipEntry.getSize());
      case CONTENT:
        try {
          final byte[] bytes = readZipEntryContent(zipEntry);
          if (forceByteToString) {
            final String encoding = EncodingHelper.getEncoding(bytes);
            if (encoding != null) {
              return IOUtils.toString(new ByteArrayInputStream(bytes), encoding);
            } else {
              return IOUtils.toString(new ByteArrayInputStream(bytes));
            }
          } else {
            return bytes;
          }
        } catch (final IOException e) {
          throw new CrawlerException(e);
        }
      default:
        throw new RuntimeException("Unknown compound element attributes type " + attribute.getElementAttribute());
    }
  }

  /**
   * Read attachment.
   * 
   * @param zipEntry
   *          the ZipEntry
   * @param attribute
   *          the attribute
   * 
   * @return the byte[]
   * 
   * @throws CrawlerException
   *           the crawler exception
   */
  private byte[] readAttachment(final ZipEntry zipEntry, final CompoundHandling.CompoundAttribute attribute)
    throws CrawlerException {
    final Serializable value = readAttribute(zipEntry, attribute, false);
    if (value != null) {
      if (value instanceof String) {
        try {
          return ((String) value).getBytes("utf-8");
        } catch (final UnsupportedEncodingException e) {
          throw new CrawlerException(e);
        }
      } else if (value instanceof byte[]) {
        return (byte[]) value;
      } // TODO serialization to byte[] for other types of attachments.
    }
    return null;
  }

  /**
   * Worker thread that fills the internal Queue with DataReference objects. It iterates over the _zipFile and creates a
   * DataReference for each ZipEntry, ignoring directory entries.
   */
  private class ZipEntryProducerThread extends Thread {

    /**
     * The _crawlerCallback.
     */
    private final CrawlerCallback _crawlerCallback;

    /**
     * Instantiates a new crawling producer thread.
     * 
     * @param crawlerCallback
     *          the CrawlerCallback
     */
    public ZipEntryProducerThread(final CrawlerCallback crawlerCallback) {
      super();
      _crawlerCallback = crawlerCallback;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      try {
        if (_zipFile != null) {
          final Enumeration<? extends ZipEntry> entries = _zipFile.entries();
          if (entries != null) {
            while (entries.hasMoreElements()) {
              final ZipEntry entry = entries.nextElement();
              if (entry != null && !entry.isDirectory()) {
                // creation of Id and Hash are NOT configurable
                final Id id = getCompoundRecord().getId().createElementId(entry.getName());
                final String hash =
                  ConnectivityHashFactory.getInstance().createHash(Long.toString(entry.getTime()));
                final DataReference dataRef =
                  DataReferenceFactory.getInstance().createDataReference(_crawlerCallback, id, hash);
                boolean added = false;
                while (!added) {
                  added = _queue.add(dataRef);
                  _entryMap.put(id, entry);
                }
              } // if
            } // while
          } // if
        } // if
      } catch (final Throwable ex) {
        if (_log.isErrorEnabled()) {
          _log.error("Producer error", ex);
        }
      } finally {
        _producerThread = null;
        if (_log.isInfoEnabled()) {
          _log.info("ZipEntry producer thread finished!");
        }
      }
    }
  }

}
