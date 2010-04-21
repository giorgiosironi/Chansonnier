/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.AbstractCrawler;
import org.eclipse.smila.connectivity.framework.CrawlerCallback;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Attribute;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process.Filter;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process.Filter.Exclude;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process.Filter.Include;
import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceCounterHelper;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig.Attributes;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IProcess;
import org.eclipse.smila.connectivity.framework.util.DataReferenceFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.tools.MObjectHelper;
import org.eclipse.smila.utils.file.EncodingHelper;

/**
 * The Class FileSystemCrawler.
 */
public class FileSystemCrawler extends AbstractCrawler {

  /**
   * The Constant POC_FOLDERS.
   */
  private static final String POC_FOLDERS = "folders";

  /**
   * The Constant POC_FILES.
   */
  private static final String POC_FILES = "files";

  /**
   * The Constant POC_PRODUCER_EXCEPTIONS.
   */
  private static final String POC_PRODUCER_EXCEPTIONS = "producerExceptions";

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
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(FileSystemCrawler.class);

  /**
   * The _queue.
   */
  private ArrayBlockingQueue<DataReference> _queue;

  /**
   * The _crawl thread.
   */
  private CrawlingProducerThread _crawlThread;

  /**
   * The running.
   */
  private boolean _isProducerRunning = true;

  /**
   * The opened flag.
   */
  private boolean _opened;

  /**
   * The _opened monitor.
   */
  private final Object _openedMonitor = new Object();

  /**
   * The force close flag.
   */
  private boolean _forceClosing;

  /**
   * The _factory.
   */
  private final RecordFactory _factory = RecordFactory.DEFAULT_INSTANCE;

  /**
   * The _attributes.
   */
  private Attribute[] _attributes;

  /**
   * The _attachment names.
   */
  private String[] _attachmentNames;

  /**
   * The _id to path.
   */
  private Map<String, File> _idToPath;

  /**
   * The _counter helper.
   */
  private CrawlerPerformanceCounterHelper<FileSystemCrawlerPerformanceAgent> _performanceCounters;

  /**
   * Instantiates a new file system crawler.
   */
  public FileSystemCrawler() {
    super();
    if (_log.isDebugEnabled()) {
      _log.debug("Creating FileSystemCrawler instance");
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.Crawler#
   *      initialize(org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig)
   */
  public void initialize(final DataSourceConnectionConfig config) throws CrawlerException, CrawlerCriticalException {
    _log.info("Initializing FileSystemCrawler...");
    synchronized (_openedMonitor) {
      if (_opened) {
        throw new CrawlerCriticalException(
          "Crawler is busy (it should not happen because new instances are created by ComponentFactories)");
      }
      checkFolders(config);
      _opened = true;
    }
    _forceClosing = false;
    _isProducerRunning = true;
    _queue = new ArrayBlockingQueue<DataReference>(CAPACITY);
    _idToPath = new HashMap<String, File>();
    final Attributes attributes = config.getAttributes();
    final List<IAttribute> attrs = attributes.getAttribute();
    _performanceCounters =
      new CrawlerPerformanceCounterHelper<FileSystemCrawlerPerformanceAgent>(config, hashCode(),
        FileSystemCrawlerPerformanceAgent.class);
    _attributes = attrs.toArray(new Attribute[attrs.size()]);

    final List<String> attachmentsNames = new ArrayList<String>();
    for (final Attribute a : _attributes) {
      if (a.isAttachment()) {
        attachmentsNames.add(a.getName());
      }
    }
    _attachmentNames = attachmentsNames.toArray(new String[attachmentsNames.size()]);
    _crawlThread = new CrawlingProducerThread(this, config);
    _crawlThread.start();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.Crawler#getNext()
   */
  public DataReference[] getNext() throws CrawlerException, CrawlerCriticalException {
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
        ; // nothing
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#dispose(org.eclipse.smila.datamodel.id.Id)
   */
  public void dispose(final Id id) {
    synchronized (_idToPath) {
      _idToPath.remove(id.getIdHash());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#getAttachment(org.eclipse.smila.datamodel.id.Id,
   *      java.lang.String)
   */
  public byte[] getAttachment(final Id id, final String name) throws CrawlerException, CrawlerCriticalException {
    final File file = getFileById(id);
    // find attribute
    for (final Attribute attribute : _attributes) {
      if (attribute.getName().equals(name)) {
        return readAttachment(file, attribute);
      }
    }
    throw new CrawlerCriticalException(String.format("Unable to find attachment definition for [%s]", name));
  }

  /**
   * Gets the file by id.
   *
   * @param id
   *          the id
   *
   * @return the file by id
   *
   * @throws CrawlerException
   *           the crawler exception
   */
  private File getFileById(final Id id) throws CrawlerException {
    File file;
    synchronized (_idToPath) {
      file = _idToPath.get(id.getIdHash());
    }
    ensureFileExists(id, file);
    return file;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#getAttachmentNames(org.eclipse.smila.datamodel.id.Id)
   */
  public String[] getAttachmentNames(final Id id) throws CrawlerException, CrawlerCriticalException {
    return _attachmentNames;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#getMObject(org.eclipse.smila.datamodel.id.Id)
   */
  public MObject getMObject(final Id id) throws CrawlerException, CrawlerCriticalException {
    final File file = getFileById(id);
    final MObject mObject = _factory.createMetadataObject();
    for (final Attribute attribute : _attributes) {
      if (!attribute.isAttachment()) {
        final Object value = readAttribute(file, attribute, true);
        if (value != null) {
          try {
            MObjectHelper.addSimpleLiteralAttribute(_factory, mObject, attribute.getName(), value);
          } catch (final Throwable e) {
            throw new CrawlerException(e);
          }
        }
      }
    }
    return mObject;
  }

  /**
   * Ensure file exists.
   *
   * @param id
   *          the id
   * @param file
   *          the file
   *
   * @throws CrawlerException
   *           the crawler exception
   */
  private void ensureFileExists(final Id id, final File file) throws CrawlerException {
    if (file == null) {
      throw new CrawlerException(String.format("Unable to find file for hash [%s]", id.getIdHash()));
    }
    if (file == null || !file.exists()) {
      throw new CrawlerException(String.format("Unable to find file [%s]", file.getPath()));
    }
  }

  /**
   * check are folders exists, if not throw critical exception.
   *
   * @param config
   *          the config
   *
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  private void checkFolders(final DataSourceConnectionConfig config) throws CrawlerCriticalException {
    final Process process = (Process) config.getProcess();
    final int processingLength = process.getBaseDirAndFilter().size();
    int i = 0;
    while (i < processingLength) {
      final String path = (String) process.getBaseDirAndFilter().get(i++);
      // filter
      i++;
      final File file = new File(path);
      if (!file.exists() || !file.isDirectory()) {
        throw new CrawlerCriticalException(String.format("Folder \"%s\" is not found", path));
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.framework.Crawler#close()
   */
  public void close() throws CrawlerException {
    synchronized (_openedMonitor) {
      _opened = false;
      _log.info("Closing FileSystemCrawler...");
      _forceClosing = true;
      _isProducerRunning = false;
      _crawlThread = null;
      _queue = null;
      _idToPath = null;
      _attachmentNames = null;
      _performanceCounters = null;
    }
  }

  /**
   * Checks for next.
   *
   * @return true, if successful
   */
  private boolean hasNext() {
    while (_isProducerRunning && _queue.isEmpty()) {
      try {
        Thread.sleep(HAS_NEXT_WAITING);
      } catch (final InterruptedException e) {
        ; // nothing
      }
    }
    return !_queue.isEmpty();
  }

  /**
   * Read attribute value.
   *
   * @param file
   *          the file
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
  private Serializable readAttribute(final File file, final Attribute attribute, final boolean forceByteToString)
    throws CrawlerException {
    switch (attribute.getFileAttributes()) {
      case NAME:
        return file.getName();
      case FILE_EXTENSION:
        return FilenameUtils.getExtension(file.getName());
      case PATH:
        return file.getAbsolutePath();
      case LAST_MODIFIED_DATE:
        return new Date(file.lastModified());
      case SIZE:
        return new Long(file.length());
      case CONTENT:
        try {
          final byte[] bytes = FileUtils.readFileToByteArray(file);
          if (forceByteToString) {
            try {
              return EncodingHelper.convertToString(bytes);
            } catch (final Exception e) {
              throw new CrawlerException("Error decoding content from file " + file.getAbsolutePath(), e);
            }
          } else {
            return bytes;
          }
        } catch (final IOException e) {
          throw new CrawlerException("Error reading attribute from file " + file.getAbsolutePath(), e);
        }
      default:
        throw new RuntimeException("Unknown file attributes type " + attribute.getFileAttributes());
    }
  }

  /**
   * Read attachment.
   *
   * @param file
   *          the file
   * @param attribute
   *          the attribute
   *
   * @return the byte[]
   *
   * @throws CrawlerException
   *           the crawler exception
   */
  private byte[] readAttachment(final File file, final Attribute attribute) throws CrawlerException {
    final Serializable value = readAttribute(file, attribute, false);
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
   * The Class CrawlThread.
   */
  private class CrawlingProducerThread extends Thread {

    /**
     * The _crawler callback.
     */
    private final CrawlerCallback _crawlerCallback;

    /**
     * The _data source id.
     */
    private final String _dataSourceID;

    /**
     * The _process.
     */
    private final Process _process;

    /**
     * The _processing length.
     */
    private final int _processingLength;

    /**
     * Instantiates a new crawling producer thread.
     *
     * @param configuration
     *          the configuration
     * @param crawlerCallback
     *          the crawler callback
     */
    public CrawlingProducerThread(final CrawlerCallback crawlerCallback,
      final DataSourceConnectionConfig configuration) {
      super();
      final IProcess process = configuration.getProcess();
      _crawlerCallback = crawlerCallback;
      _dataSourceID = configuration.getDataSourceID();
      _process = (Process) process;
      _processingLength = _process.getBaseDirAndFilter().size();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      try {
        int i = 0;
        while (i < _processingLength) {
          final String path = (String) _process.getBaseDirAndFilter().get(i++);
          final Filter filter = (Filter) _process.getBaseDirAndFilter().get(i++);
          final File file = new File(path);
          if (!file.exists() || !file.isDirectory()) {
            _log.error("Folder " + path + " is not found");
            continue;
          }
          processFolder(file, filter);
        }
      } catch (final Throwable ex) {
        _performanceCounters.addException(ex);
        _log.error("Producer error", ex);
      } finally {
        _isProducerRunning = false;
        if (_forceClosing) {
          _log.info("Producer finished by forcing close procedure");
        } else {
          _log.info("Producer finished!");
        }

      }
    }

    /**
     * Process folder.
     *
     * @param dir
     *          the dir
     * @param filter
     *          the filter
     *
     * @throws InvalidTypeException
     *           the invalid type exception
     * @throws CrawlerException
     *           the crawler exception
     */
    private void processFolder(final File dir, final Filter filter) throws InvalidTypeException, CrawlerException {
      if (_forceClosing) {
        return;
      }
      final CrawlerFileFilter fileFilter = new CrawlerFileFilter(filter);
      treeWalk(dir, fileFilter, filter.isRecursive());
    }

    /**
     * Tree walk.
     *
     * @param dir
     *          the dir
     * @param fileFilter
     *          the file filter
     * @param isRecursive
     *          the is recursive
     *
     * @throws InvalidTypeException
     *           the invalid type exception
     * @throws CrawlerException
     *           the crawler exception
     */
    private void treeWalk(final File dir, final CrawlerFileFilter fileFilter, final boolean isRecursive)
      throws InvalidTypeException, CrawlerException {
      if (_forceClosing) {
        return;
      }
      final File[] entries = dir.listFiles(fileFilter);
      if (entries == null) {
        _log.warn("Unknown IO error while listing directory " + dir + ", skipping.");
      } else {
        for (int i = 0; i < entries.length; i++) {
          final File file = entries[i];
          if (file.isFile()) {
            boolean waiting = true;
            DataReference reference = null;
            while (waiting) {
              try {
                if (reference == null) {
                  reference = initializeDataReference(entries[i]);
                }
                synchronized (_idToPath) {
                  _idToPath.put(reference.getId().getIdHash(), file);
                }
                _queue.put(reference);
                waiting = false;
                _performanceCounters.increment(POC_FILES);
              } catch (final Throwable e) {
                _performanceCounters.increment(POC_PRODUCER_EXCEPTIONS);
                _performanceCounters.addException(e);
                _log.error(e);
              }
            }
          } else if (isRecursive && file.isDirectory()) {
            treeWalk(file, fileFilter, true);
          } else {
            _log.warn("Path " + file + " is neither file nor directory, skipping.");
          }
        }
      }
      _performanceCounters.increment(POC_FOLDERS);
    }

    /**
     * Initialize data reference.
     *
     * @param file
     *          the file
     *
     * @return the data reference
     *
     * @throws CrawlerException
     *           the crawler exception
     * @throws InvalidTypeException
     *           the invalid type exception
     */
    private DataReference initializeDataReference(final File file) throws CrawlerException, InvalidTypeException {
      final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes =
        new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
      final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes =
        new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
      final Map<String, byte[]> hashAttachments = new HashMap<String, byte[]>();
      readIdAndHashAttributesAndAttachments(file, idAttributes, hashAttributes, hashAttachments);
      return DataReferenceFactory.getInstance().createDataReference(_crawlerCallback, _dataSourceID,
        idAttributes.toArray(new org.eclipse.smila.datamodel.record.Attribute[idAttributes.size()]),
        hashAttributes.toArray(new org.eclipse.smila.datamodel.record.Attribute[hashAttributes.size()]),
        hashAttachments);
    }

    /**
     * Read id and hash attributes and attachments.
     *
     * @param file
     *          the file
     * @param idAttributes
     *          the id attributes
     * @param hashAttributes
     *          the hash attributes
     * @param hashAttachments
     *          the hash attachments
     *
     * @throws CrawlerException
     *           the crawler exception
     * @throws InvalidTypeException
     *           the invalid type exception
     */
    private void readIdAndHashAttributesAndAttachments(final File file,
      final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes,
      final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes,
      final Map<String, byte[]> hashAttachments) throws CrawlerException, InvalidTypeException {
      for (final Attribute attributeDef : _attributes) {
        if (attributeDef.isKeyAttribute() || attributeDef.isHashAttribute()) {
          if (attributeDef.isAttachment()) {
            final byte[] value = readAttachment(file, attributeDef);
            hashAttachments.put(attributeDef.getName(), value);
          } else {
            final Object value = readAttribute(file, attributeDef, true);
            if (value != null) {
              final org.eclipse.smila.datamodel.record.Attribute attribute = _factory.createAttribute();
              attribute.setName(attributeDef.getName());
              final Literal literal = _factory.createLiteral();
              literal.setValue(value);
              attribute.addLiteral(literal);
              if (attributeDef.isKeyAttribute()) {
                idAttributes.add(attribute);
              }
              if (attributeDef.isHashAttribute()) {
                hashAttributes.add(attribute);
              }

            }
          }
        }
      }
    }

    /**
     * The Class CrawlerFileFilter.
     */
    private class CrawlerFileFilter implements FileFilter {

      /**
       * The _filter.
       */
      private final Filter _filter;

      /**
       * The _case.
       */
      private final IOCase _case;

      /**
       * Instantiates a new crawler filter.
       *
       * @param filter
       *          the filter
       */
      public CrawlerFileFilter(final Filter filter) {
        _filter = filter;
        if (filter.isCaseSensitive()) {
          _case = IOCase.SENSITIVE;
        } else {
          _case = IOCase.INSENSITIVE;

        }
      }

      /**
       * {@inheritDoc}
       *
       * @see java.io.FileFilter#accept(java.io.File)
       */
      public boolean accept(final File file) {
        if (file.isDirectory()) {
          return true;
        }
        // process includes , if there is no includes defined, then accept file
        if (_filter.getInclude() != null && _filter.getInclude().size() > 0) {
          final long dateLong = file.lastModified();
          boolean acceptedByInclude = false;
          for (final Include include : _filter.getInclude()) {
            if (include.getDateFrom() != null) {
              if (dateLong < include.getDateFrom().getTime()) {
                continue;
              }
            }
            if (include.getDateTo() != null) {
              if (dateLong > include.getDateTo().getTime()) {
                continue;
              }
            }
            if (FilenameUtils.wildcardMatch(file.getName(), include.getName(), _case)) {
              acceptedByInclude = true;
              break;
            }
          }
          if (!acceptedByInclude) {
            return false;
          }
        }
        // process excludes
        for (final Exclude exclude : _filter.getExclude()) {
          if (FilenameUtils.wildcardMatch(file.getName(), exclude.getName(), _case)) {
            return false;
          }
        }
        return true;
      }
    }
  }

}
