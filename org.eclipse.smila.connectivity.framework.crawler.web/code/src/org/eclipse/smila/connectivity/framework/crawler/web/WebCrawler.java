/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Andrey Basalaev (brox IT Solutions GmbH) - initial creator, Ivan Churkin (brox IT Solutions GmbH)
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.AbstractCrawler;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.Attribute;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FieldAttributeType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.MetaReturnType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.MetaType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.Process;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParserManager;
import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceCounterHelper;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig.Attributes;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;
import org.eclipse.smila.connectivity.framework.util.DataReferenceFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.tools.MObjectHelper;
import org.eclipse.smila.datamodel.tools.NameValuePair;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * The WebCrawler class.
 */
public class WebCrawler extends AbstractCrawler {

  /**
   * The Constant POC_BYTES.
   */
  public static final String POC_BYTES = "bytes";

  /**
   * The Constant POC_PAGES.
   */
  public static final String POC_PAGES = "pages";

  /**
   * The Constant POC_PRODUCER_EXCEPTIONS.
   */
  public static final String POC_PRODUCER_EXCEPTIONS = "producerExceptions";

  /**
   * The Constant POC_AVEREGE_TIME_TO_FETCH.
   */
  public static final String POC_AVEREGE_TIME_TO_FETCH = "averageHttpFetchTime";

  /**
   * The Constant BUNDLE_NAME.
   */
  private static final String BUNDLE_NAME = "org.eclipse.smila.connectivity.framework.crawler.web";

  /**
   * The Constant UTF_8.
   */
  private static final String UTF_8 = "utf-8";

  /**
   * Separator between metadata name and value, eg. Server: example.com
   */
  private static final char METADATA_SEPARATOR = ':';

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
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(WebCrawler.class);

  /**
   * The queue.
   */
  private ArrayBlockingQueue<Record> _queue;

  private String _dataSourceID;

  /**
   * The attributes.
   */
  private Attribute[] _attributes;

  /**
   * The crawl thread.
   */
  private CrawlingProducerThread _crawlThread;

  /**
   * The web sites.
   */
  private Iterator<WebSite> _webSites;

  /**
   * The web site iterator.
   */
  private WebSiteIterator _webSiteIterator;

  /**
   * The opened flag.
   */
  private boolean _opened;

  /**
   * The force closing.
   */
  private boolean _forceClosing;

  /**
   * The producer running.
   */
  private boolean _producerRunning;

  /**
   * The opened monitor.
   */
  private final Object _openedMonitor = new Object();

  /**
   * The record factory.
   */
  private final RecordFactory _factory = RecordFactory.DEFAULT_INSTANCE;

  /**
   * The workspace path.
   */
  private String _workspace;

  /**
   * The _critical error.
   */
  private CrawlerCriticalException _criticalException;

  /**
   * The _performance counters.
   */
  private CrawlerPerformanceCounterHelper<WebCrawlerPerformanceAgent> _performanceCounters;

  /**
   * Regex pattern for extrating charset information from ContentType header.
   */
  private final Pattern _contentTypePattern =
    Pattern.compile("^CONTENT-TYPE\\s*:\\s*(?:.|\\s)*CHARSET\\s*=\\s*(.*)$", Pattern.CASE_INSENSITIVE);

  /**
   * Regex pattern for extrating mimetype information from ContentType header.
   */
  private final Pattern _mimeTypePattern =
    java.util.regex.Pattern.compile("^CONTENT-TYPE\\s*:\\s*([^\\s;]+)(\\s*;?.*)$",
      java.util.regex.Pattern.CASE_INSENSITIVE);

  /**
   * Webcrawler parsers manager.
   */
  private ParserManager _parserManager;

  /**
   * Map containing Records with properties that are only required for creating DataReference.
   */
  private HashMap<Id, Record> _dataReferenceRecords = new HashMap<Id, Record>();

  /**
   * Map containing records with all properties.
   */
  private HashMap<Id, Record> _records = new HashMap<Id, Record>();

  /**
   * Instantiates a new web crawler.
   */
  public WebCrawler() {
    super();
    if (_log.isDebugEnabled()) {
      _log.debug("Creating WebCrawler instance");
    }
  }

  /* ******************** implementation of interface Crawler ******************** */

  /**
   * {@inheritDoc}
   */
  public void initialize(final DataSourceConnectionConfig config) throws CrawlerException, CrawlerCriticalException {
    _log.info("Initializing WebCrawler...");
    synchronized (_openedMonitor) {
      if (_opened) {
        throw new CrawlerCriticalException(
          "Crawler is busy (it should not happen because new instances are created by ComponentFactories)");
      }
      _opened = true;
    }
    _performanceCounters =
      new CrawlerPerformanceCounterHelper<WebCrawlerPerformanceAgent>(config, hashCode(),
        WebCrawlerPerformanceAgent.class);
    _queue = new ArrayBlockingQueue<Record>(CAPACITY);
    _forceClosing = false;
    _producerRunning = true;

    _dataSourceID = config.getDataSourceID();
    final Attributes attributes = config.getAttributes();
    final List<IAttribute> attrs = attributes.getAttribute();
    _attributes = attrs.toArray(new Attribute[attrs.size()]);

    final Process process = (Process) config.getProcess();
    _webSites = process.getWebSite().iterator();

    // _webSiteIterator = new WebSiteIterator(_webSites.next());
    initDataFolder();
    initializeNextSite();

    _crawlThread = new CrawlingProducerThread();
    _crawlThread.start();

    _log.debug("WebCrawler indexer started");
  }

  /**
   * {@inheritDoc}
   */
  public DataReference[] getNext() throws CrawlerException, CrawlerCriticalException {
    rethrowProducerExceptions();
    while (hasNext()) {
      rethrowProducerExceptions();
      try {
        final List<Record> list = new ArrayList<Record>();
        final Record topRecord = _queue.poll(QUEUE_POLL_WAITING, TimeUnit.MILLISECONDS);
        if (topRecord != null) {
          list.add(topRecord);
          _queue.drainTo(list, STEP - 1);
          final DataReference[] dataRefs = new DataReference[list.size()];
          for (int i = 0; i < list.size(); i++) {
            final Record record = list.get(i);
            final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes =
              new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
            final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes =
              new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
            getIdAndHashAttributes(record.getMetadata(), idAttributes, hashAttributes);
            dataRefs[i] =
              DataReferenceFactory.getInstance().createDataReference(this, _dataSourceID,
                idAttributes.toArray(new org.eclipse.smila.datamodel.record.Attribute[idAttributes.size()]),
                hashAttributes.toArray(new org.eclipse.smila.datamodel.record.Attribute[hashAttributes.size()]),
                getHashAttachments(record));
            _dataReferenceRecords.put(dataRefs[i].getId(), record);
          }
          return dataRefs;
        }
      } catch (final InterruptedException e) {
        ; // nothing
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void close() throws CrawlerException {
    synchronized (_openedMonitor) {
      _opened = false;
      _log.info("Closing WebCrawler...");
      _queue = null;
      _records.clear();
      _records = null;
      _dataReferenceRecords.clear();
      _dataReferenceRecords = null;
      _performanceCounters = null;
      _forceClosing = true;
      if (_crawlThread != null) {
        try {
          _crawlThread.join();
        } catch (final InterruptedException e) {
          ;// nothing
        } catch (final NullPointerException e) {
          ;// nothing
        }
        _crawlThread = null;
      }
      _dataSourceID = null;
      _attributes = null;
      _criticalException = null;
      if (_workspace != null) {
        FileUtils.deleteQuietly(new File(_workspace));
      }
    }
  }

  /* ******************** implementation of interface CrawlerCallback ******************** */

  /**
   * {@inheritDoc}
   */
  public MObject getMObject(final Id id) throws CrawlerException, CrawlerCriticalException {
    Record record = null;
    try {
      record = getRecord(id);
    } catch (final Exception exception) {
      throw new CrawlerException(exception);
    }
    return record.getMetadata();
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getAttachment(final Id id, final String name) throws CrawlerException, CrawlerCriticalException {
    Record record = null;
    try {
      record = getRecord(id);
    } catch (final Exception exception) {
      throw new CrawlerException(exception);
    }
    return record.getAttachment(name);
  }

  /**
   * {@inheritDoc}
   */
  public String[] getAttachmentNames(final Id id) throws CrawlerException, CrawlerCriticalException {
    Record record = null;
    try {
      record = getRecord(id);
    } catch (final Exception exception) {
      throw new CrawlerException(exception);
    }
    final ArrayList<String> names = new ArrayList<String>();
    final Iterator<String> it = record.getAttachmentNames();
    while (it.hasNext()) {
      names.add(it.next());
    }
    return names.toArray(new String[names.size()]);
  }

  /**
   * {@inheritDoc}
   */
  public void dispose(final Id id) {
    _dataReferenceRecords.remove(id);
    _records.remove(id);
  }

  /* ******************** private stuff ******************** */

  /**
   * Populates given lists with id and hash attributes of given MObject.
   *
   * @param metadata
   *          MObject
   * @param idAttributes
   *          list of id attributes
   * @param hashAttributes
   *          list of hash attributes
   */
  private void getIdAndHashAttributes(final MObject metadata,
    final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes,
    final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes) {
    for (final Attribute attribute : _attributes) {
      if (!attribute.isAttachment()) {
        if (attribute.isKeyAttribute()) {
          idAttributes.add(metadata.getAttribute(attribute.getName()));
        } else if (attribute.isHashAttribute()) {
          hashAttributes.add(metadata.getAttribute(attribute.getName()));
        }
      }
    }
  }

  /**
   * Returns map consisting of attachment names and values.
   *
   * @param record
   *          Record
   * @return map
   */
  private Map<String, byte[]> getHashAttachments(final Record record) {
    if (record.hasAttachments()) {
      final Map<String, byte[]> hashAttachments = new HashMap<String, byte[]>();
      for (final Iterator<String> it = record.getAttachmentNames(); it.hasNext();) {
        final String attachmentName = it.next();
        hashAttachments.put(attachmentName, record.getAttachment(attachmentName));
      }
      return hashAttachments;
    }
    return null;
  }

  /**
   * Sets given {@link Attribute} to the given record.
   *
   * @param record
   *          Record
   * @param indexDocument
   *          indexDocument
   * @param attribute
   *          attribute to set
   * @throws UnsupportedEncodingException
   *           UnsupportedEncodingException
   * @throws InvalidTypeException
   *           InvalidTypeException
   */
  private void setAttribute(final Record record, final IndexDocument indexDocument, final Attribute attribute)
    throws UnsupportedEncodingException, InvalidTypeException {
    final String name = attribute.getName();
    final MObject metadata = record.getMetadata();
    if (attribute.isAttachment()) {
      final Object value = readAttribute(indexDocument, attribute, false);
      if (value != null) {
        if (value instanceof String) {
          record.setAttachment(name, ((String) value).getBytes(UTF_8));
        } else if (value instanceof byte[]) {
          record.setAttachment(name, (byte[]) value);
        } else {
          throw new RuntimeException("Unknown attachment type!");
        }
      }
      // TODO serialization to byte[] for other types of attachments.
    } else {
      final Object value = readAttribute(indexDocument, attribute, true);
      if (value != null) {
        if (value instanceof NameValuePair[]) {
          MObjectHelper.addNameValuePairsAttribute(_factory, metadata, name, (NameValuePair[]) value);
        } else if (value instanceof Object[]) {
          MObjectHelper.addLiteralArrayAttribute(_factory, metadata, name, (Object[]) value);
        } else {
          MObjectHelper.addSimpleLiteralAttribute(_factory, metadata, name, value);
        }
      }
    }
  }

  /**
   * Initialize next site.
   *
   * @return true, if successful
   *
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  private boolean initializeNextSite() throws CrawlerCriticalException {
    _webSiteIterator = null;
    if (_webSites.hasNext()) {
      final WebSite site = _webSites.next();
      _webSiteIterator = new WebSiteIterator(site, _parserManager, _performanceCounters);
      final boolean hasNext = _webSiteIterator.hasNext();
      if (!hasNext) {
        _forceClosing = true;
        _criticalException =
          new CrawlerCriticalException("Unable to connect to web site specified in project "
            + site.getProjectName());
      }
      return hasNext;
    }
    return false;
  }

  /**
   * Rethrow producer exceptions.
   *
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  private void rethrowProducerExceptions() throws CrawlerCriticalException {
    if (_criticalException != null) {
      throw _criticalException;
    }
  }

  /**
   * Checks for next.
   *
   * @return true, if successful
   *
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  private boolean hasNext() throws CrawlerCriticalException {
    while (_producerRunning && _queue.isEmpty()) {
      try {
        Thread.sleep(HAS_NEXT_WAITING);
      } catch (final InterruptedException e) {
        ; // nothing
      }
    }
    rethrowProducerExceptions();
    return !_queue.isEmpty();
  }

  /**
   * Creates the record.
   *
   * @param id
   *          the record id
   *
   * @return the record
   *
   * @throws InvalidTypeException
   *           the invalid type exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws CrawlerException
   *           the crawler exception
   */
  private Record getRecord(final Id id) throws InvalidTypeException, IOException, CrawlerException {
    if (_records.containsKey(id)) {
      return _records.get(id);
    } else {
      final Record record = _dataReferenceRecords.get(id);
      final MObject metadata = record.getMetadata();
      IndexDocument indexDocument = null;
      for (final Attribute attribute : _attributes) {
        if (!(attribute.isHashAttribute() || attribute.isKeyAttribute())) {
          if (indexDocument == null) {
            final String url = metadata.getAttribute(FieldAttributeType.URL.value()).getLiteral().getStringValue();
            indexDocument = deserializeIndexDocument(DigestUtils.md5Hex(url));
          }
          setAttribute(record, indexDocument, attribute);
        }
      }

      if (_log.isDebugEnabled()) {
        _log.debug("Created record for url: "
          + metadata.getAttribute(FieldAttributeType.URL.value()).getLiteral().getValue());
      }

      _records.put(id, record);
      _dataReferenceRecords.remove(id);
      return record;
    }
  }

  /**
   * Creates the di data.
   *
   * @param indexDocument
   *          the index document
   *
   * @return the m object
   *
   * @throws InvalidTypeException
   *           the invalid type exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private Record createDataReferenceRecord(final IndexDocument indexDocument) throws InvalidTypeException,
    IOException {
    final Record record = _factory.createRecord();
    final MObject metadata = _factory.createMetadataObject();
    record.setMetadata(metadata);
    for (final Attribute attribute : _attributes) {
      // read key, hash and 'URL' attributes
      if (attribute.isKeyAttribute() || attribute.isHashAttribute() || attribute.getFieldAttribute() != null
        && attribute.getFieldAttribute().equals(FieldAttributeType.URL)) {
        setAttribute(record, indexDocument, attribute);
      }
    }

    return record;
  }

  /**
   * Read attribute.
   *
   * @param indexDocument
   *          the index document
   * @param attribute
   *          the attribute
   * @param forceByteToString
   *          the force byte to string
   *
   * @return the attribute value
   *
   * @throws UnsupportedEncodingException
   *           the unsupported encoding exception
   */
  private Serializable readAttribute(final IndexDocument indexDocument, final Attribute attribute,
    final boolean forceByteToString) throws UnsupportedEncodingException {
    if (attribute.getFieldAttribute() != null) {
      switch (attribute.getFieldAttribute()) {
        case URL:
          return indexDocument.getUrl();
        case CONTENT:
          // search encoding in headers
          String charsetName = indexDocument.extractFromResponseHeaders(_contentTypePattern, 1);
          if (charsetName == null) {
            charsetName = UTF_8;
          }
          if (forceByteToString) {
            return new String(indexDocument.getContent(), charsetName);
          } else {
            if (UTF_8.equalsIgnoreCase(charsetName)) {
              return indexDocument.getContent();
            }
            // decode to utf
            return (new String(indexDocument.getContent(), charsetName)).getBytes(UTF_8);
          }
        case TITLE:
          return indexDocument.getTitle();
        case MIME_TYPE:
          return indexDocument.extractFromResponseHeaders(_mimeTypePattern, 1);
        default:
          throw new IllegalArgumentException("Unknown field attribute type " + attribute.getFieldAttribute());
      }
    } else if (attribute.getMetaAttribute() != null) {
      final MetaType metaType = attribute.getMetaAttribute().getType();
      final List<String> metaNames = attribute.getMetaAttribute().getMetaName();
      List<String> metaData;
      switch (metaType) {
        case META_DATA:
          metaData = getFilteredMetadataList(indexDocument.getHtmlMetaData(), metaNames);
          break;
        case RESPONSE_HEADER:
          metaData = getFilteredMetadataList(indexDocument.getResponseHeaders(), metaNames);
          break;
        case META_DATA_WITH_RESPONSE_HEADER_FALL_BACK:
          metaData = getFilteredMetadataList(indexDocument.getMetaDataWithResponseHeaderFallBack(), metaNames);
          break;
        default:
          throw new IllegalArgumentException("Unknown meta attribute type " + attribute.getFieldAttribute());
      }

      final MetaReturnType returnType = attribute.getMetaAttribute().getReturnType();
      switch (returnType) {
        case META_DATA_STRING:
          return metaData.toArray();
        case META_DATA_VALUE:
          for (int i = 0; i < metaData.size(); i++) {
            final String metaDataString = metaData.get(i);
            metaData.set(i, metaDataString.substring(metaDataString.indexOf(METADATA_SEPARATOR) + 1).trim());
          }
          return metaData.toArray();
        case META_DATA_M_OBJECT:
          final NameValuePair[] metaDataNameValuePairs = new NameValuePair[metaData.size()];
          for (int i = 0; i < metaData.size(); i++) {
            final String metaDataString = metaData.get(i);
            final String metadataName =
              metaDataString.substring(0, metaDataString.indexOf(METADATA_SEPARATOR)).trim();
            final String metaDataValue =
              metaDataString.substring(metaDataString.indexOf(METADATA_SEPARATOR) + 1).trim();
            metaDataNameValuePairs[i] = new NameValuePair(metadataName, metaDataValue);
          }
          return metaDataNameValuePairs;
        default:
          throw new IllegalArgumentException("Unknown meta attribute return type " + returnType);
      }
    } else {
      throw new IllegalArgumentException("Unknown attribute " + attribute.getName());
    }
  }

  /**
   * Returns fileterd metadata list.
   *
   * @param list
   *          the list
   * @param filters
   *          the filters
   *
   * @return the filtered metadata list
   */
  private List<String> getFilteredMetadataList(final List<String> list, final List<String> filters) {
    if (filters.isEmpty()) {
      return list;
    }
    final List<String> filteredList = new ArrayList<String>();
    for (final String s : list) {
      if (s.indexOf(METADATA_SEPARATOR) > 0) {
        final String metadataName = s.substring(0, s.indexOf(METADATA_SEPARATOR)).trim();
        for (final String metaName : filters) {
          if (metadataName.equals(metaName)) {
            filteredList.add(s);
          }
        }
      }
    }
    return filteredList;
  }

  /**
   * Serialize index document to have possibility to fill record later.
   *
   * @param indexDocument
   *          the index document
   * @param filename
   *          the filename
   *
   * @throws CrawlerException
   *           the crawler exception
   */
  private void serializeIndexDocument(final String filename, final IndexDocument indexDocument)
    throws CrawlerException {
    if (_log.isDebugEnabled()) {
      _log.debug("Serializing document " + filename);
    }
    ObjectOutputStream objstream = null;
    try {
      objstream = new ObjectOutputStream(new FileOutputStream(new File(_workspace, filename)));
      objstream.writeObject(indexDocument);
    } catch (final Throwable e) {
      throw new CrawlerException("Unable to serialize index document", e);
    } finally {
      IOUtils.closeQuietly(objstream);
    }
  }

  /**
   * Deserialize index document.
   *
   * @param filename
   *          the filename
   *
   * @return the index document
   *
   * @throws CrawlerException
   *           the crawler exception
   */
  private IndexDocument deserializeIndexDocument(final String filename) throws CrawlerException {
    IndexDocument indexDocument = null;
    if (_log.isDebugEnabled()) {
      _log.debug("Deserializing document " + filename);
    }
    final File file = new File(_workspace, filename);
    if (!file.exists()) {
      throw new CrawlerException(String.format("Unable to find file %s for deserializing cached document", file
        .getPath()));
    }
    ObjectInputStream objstream = null;
    try {
      objstream = new ObjectInputStream(new FileInputStream(file));
      indexDocument = (IndexDocument) objstream.readObject();
    } catch (final Throwable e) {
      throw new CrawlerException(e);
    } finally {
      IOUtils.closeQuietly(objstream);
    }
    return indexDocument;
  }

  /**
   * Initializes the data folder.
   *
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  private void initDataFolder() throws CrawlerCriticalException {
    try {
      final File file = WorkspaceHelper.createWorkingDir(BUNDLE_NAME, String.valueOf(hashCode()));
      file.mkdir();
      _workspace = file.getCanonicalPath();
    } catch (final IOException e) {
      throw new CrawlerCriticalException("Unable to initialize workspace", e);
    }
  }

  /**
   * To be used by Declarative Services. Sets the ParserManager service.
   *
   * @param parserManager
   *          ParserManager Service.
   */
  public void setParserManager(final ParserManager parserManager) {
    _parserManager = parserManager;
    if (_log.isDebugEnabled()) {
      _log.debug("ParserManager is bound");
    }
  }

  /**
   * To be used by Declarative Services. Removes ParserManager service.
   *
   * @param parserManager
   *          ParserManager Service.
   */
  public void unsetParserManager(final ParserManager parserManager) {
    if (parserManager == _parserManager) {
      _parserManager = null;
    }
    if (_log.isDebugEnabled()) {
      _log.debug("ParserManager is unbound");
    }
  }

  /**
   * The Class CrawlThread.
   */
  private class CrawlingProducerThread extends Thread {

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      try {
        while (!_forceClosing && hasNextDoc()) {
          final IndexDocument document = _webSiteIterator.next();
          boolean waiting = true;
          Record record = null;
          while (waiting) {
            try {
              if (record == null) {
                record = createDataReferenceRecord(document);
              }

              // serialize index document to have possibility to get attachments later
              if (_forceClosing) {
                break;
              }
              serializeIndexDocument(DigestUtils.md5Hex(document.getUrl()), document);

              if (_forceClosing) {
                break;
              }
              final ArrayBlockingQueue<Record> queue = _queue;
              if (queue != null) {
                _queue.put(record);
              }
              waiting = false;
            } catch (final InterruptedException e) {
              ;// nothing
            } catch (final IOException e) {
              _log.error(e);
              _performanceCounters.increment(POC_PRODUCER_EXCEPTIONS);
              _performanceCounters.addException(e);
            }
          }
        }
      } catch (final Throwable t) {
        _log.error("Producer error", t);
        _performanceCounters.increment(POC_PRODUCER_EXCEPTIONS);
        _performanceCounters.addException(t);
      } finally {
        _producerRunning = false;
        if (_forceClosing) {
          _log.info("Producer finished by forcing close procedure");
        } else {
          _log.info("Producer finished!");
        }
      }
    }

    /**
     * Checks for next.
     *
     * @return true, if successful
     *
     * @throws CrawlerCriticalException
     *           the crawler critical exception
     */
    private boolean hasNextDoc() throws CrawlerCriticalException {
      if (_webSiteIterator.hasNext()) {
        return true;
      } else if (_webSites.hasNext()) {
        return initializeNextSite();
      }
      return false;
    }

  }

}
