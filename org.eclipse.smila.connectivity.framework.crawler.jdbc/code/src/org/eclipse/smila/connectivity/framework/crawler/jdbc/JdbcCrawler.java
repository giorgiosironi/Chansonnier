/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.crawler.jdbc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.AbstractCrawler;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.messages.Attribute;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.messages.Process;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.messages.Process.Database;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.messages.Process.Selections;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.messages.Process.Selections.Grouping;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.util.GroupingRange;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.util.PreparedStatementTypedParameter;
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

/**
 * The Class JdbcCrawler. Instances of this class can be used to crawl JDBC datasources. Clients can access the
 * retrieved data via the methods defined in the {@link Crawler}-Interface.
 * 
 * The {@link DataSourceConnectionConfig} passed in the initialize()-Method must comply to the Schema defined in
 * JdbcDataSourceConnectionConfigSchema.xsd.
 * 
 * 
 */
public class JdbcCrawler extends AbstractCrawler {

  /**
   * Name used for the {@link CrawlerPerformanceCounters} property logging the number of retrieved database rows.
   */
  public static final String POC_DATA_ROWS_RETRIEVED = "databaseRows";

  /**
   * Name used for the {@link CrawlerPerformanceCounters} property logging the number of occured
   * {@link CrawlerException}s.
   */
  private static final String POC_CRAWLING_EXCEPTIONS = "producerExceptions";

  /**
   * Name used for the {@link CrawlerPerformanceCounters} property logging the number of occured
   * {@link CrawlerCriticalException}s.
   */
  private static final String POC_CRITICAL_CRAWLING_EXCEPTIONS = "producerCriticalExceptions";

  /**
   * Name used for the {@link CrawlerPerformanceCounters} property logging the number of created {@link DataReference}s.
   */
  private static final String POC_DATA_REFS_CREATED = "dataRefsCreated";

  /**
   * Name used for the {@link CrawlerPerformanceCounters} property logging the number of created {@link Record}s.
   */
  private static final String POC_RECORDS_CREATED = "recordsCreated";

  /**
   * Name used for the {@link CrawlerPerformanceCounters} property logging the number of {@link DataReference}s
   * retrieved from the Crawler by clients.
   */
  private static final String POC_DATA_REFS_RETRIEVED_BY_CLIENT = "dataRefsRetrievedByClient";

  /** Capacity of the internal queue. */
  private static final int INTERNAL_QUEUE_CAPACITY = 12000;

  /** If the internal record cache grows above this limit warnings are issued to the logger * */
  private static final int RECORD_CACHE_WARNING_THRESHOLD = 4 * INTERNAL_QUEUE_CAPACITY;

  /** Timeout in ms used when polling the queue for MObjects in {@link #getNextDeltaIndexingData()}. */
  private static final long QUEUE_POLL_WAITING = 300;

  /**
   * Time in ms to wait for the {@link #_producerThread} to terminate when the {@link #close()}-method is called.
   */
  private static final long WAIT_FOR_CRAWLING_THREAD_TERMINATION_WHEN_FORCECLOSING = 5000;

  /** Max number of {@link MObject}s returned by {@link #getNextDeltaIndexingData()}. */
  private static final int MAX_QUEUE_SIZE = 20;

  /** Thread-Timeout in ms used in {@link #hasNextItemInQueue()}. */
  private static final int HAS_NEXT_ITEM_THREAD_WAIT = 50;

  /** The Log object for logging exceptions and messages. */
  private final Log _log = LogFactory.getLog(JdbcCrawler.class);

  /** The Monitor object for synchronizing when opening and closing the Crawler. */
  private final Object _openedMonitor = new Object();

  /** This flag reflects whether the Crawler is in "opened"-State. */
  private boolean _opened;

  /** The {@link Process} object of the {@link DataSourceConnectionConfig}. */
  private Process _process;

  private String _dataSourceID;

  // /**
  // * This {@link List} is temporarily filled with {@link MObject}s from the {@link #_internalQueue} when
  // * {@link #getNextDeltaIndexingData()} is called.
  // */
  // private final ArrayList<MObject> _tempList = new ArrayList<MObject>();

  /**
   * Pre-Caches the Record-object for each MObject in the current DeltaIndexing block. Is filled when
   * {@link #getNextDeltaIndexingData()} is called. The key ist the offset of the corresponding {@link MObject} in the
   * {@link #_tempList}. The {@link List} gets cleared after {@link #getNextDeltaIndexingData()} is called, so it should
   * never have a size larger than {@link #MAX_QUEUE_SIZE}.
   */
  private HashMap<Id, Record> _recordCache;

  // /**
  // * Pre-caches the data retrieved from the JDBC-{@link ResultSet} for later creation of corresponding
  // * {@link Record}-Objects. Associates the {@link MObject} created by the {@link CrawlingProducerThread} with
  // * the data of the current row in the JDBC-{@link ResultSet}. The {@link Map} gets cleared after
  // * {@link #getNextDeltaIndexingData()} is called, so it should never have a size larger than
  // * {@link #DELTA_INDEXING_SIZE}.
  // */
  // private HashMap<MObject, Object[]> _dataCache;

  /**
   * The internal queue, which is filled with {@link MObject}s by the {@link CrawlingProducerThread}. When it grows to a
   * size of {@link #INTERNAL_QUEUE_CAPACITY} its put()-Method blocks, so the ProcuerThread blocks until the queue is
   * drained a bit by the client calling {@link #getNextDeltaIndexingData()}.
   * 
   * @see ArrayBlockingQueue
   */
  private ArrayBlockingQueue<DataReference> _internalQueue;

  /**
   * This Map contains the mapping between the attribute names in the {@link DataSourceConnectionConfig} and the
   * corresponding column indexes of the {@link #_retrievalResultSet}.
   */
  private HashMap<String, Integer> _attributeMapping;

  /**
   * The JDBC connection to the database.
   * 
   * @see Connection
   */
  private Connection _connection;

  /** The {@link PreparedStatement} used for querying the JDBC datasource. */
  private PreparedStatement _retrievalStatement;

  /** The {@link ResultSet} returned by the {@link #_retrievalStatement}. */
  private ResultSet _retrievalResultSet;

  /** The {@link ResultSetMetaData} associated with the {@link #_retrievalResultSet}. */
  private ResultSetMetaData _retrievalResultSetMetaData;

  /**
   * The {@link RecordFactory}-instance used for creating the {@link Record}s. In the case of {@link JdbcCrawler}
   * obtained by {@link RecordFactory#DEFAULT_INSTANCE}.
   */
  private final RecordFactory _recordFactory = RecordFactory.DEFAULT_INSTANCE;

  /** The attributes to be retrieved from the datasource as defined in the {@link DataSourceConnectionConfig}. */
  private Attribute[] _attributes;

  /** Flag indicating if the {@link CrawlingProducerThread} is currently active. */
  private boolean _isProducerRunning;

  /** The {@link CrawlingProducerThread}-instance which populates the {@link #_internalQueue}. */
  private CrawlingProducerThread _producerThread;

  /**
   * Class member storing the last {@link CrawlerCriticalException} encountered in the {@link CrawlingProducerThread}.
   */
  private CrawlerCriticalException _producerException;

  /** Flag indicating whether the {@link #close()}-Method has been called by the client. */
  private boolean _forceClosing;

  /** An {@link ArrayList} containing the {@link GroupingRange}s which were determined. */
  private ArrayList<GroupingRange> _groupingRanges;

  /** The {@link Iterator} associated with the {@link #_groupingRanges}-List. */
  private Iterator<GroupingRange> _groupingRangesIterator;

  /** The {@link GroupingRange} currently used in the {@link #_retrievalStatement}. */
  private GroupingRange _currentGroupingRange;

  /** the {@link CrawlerPerformanceCounters} used by the {@link JdbcCrawler}. */
  private CrawlerPerformanceCounterHelper<JdbcCrawlerPerformanceAgent> _performanceCounters;

  /**
   * Standard constructor of {@link JdbcCrawler}.
   */
  public JdbcCrawler() {
    super();
    if (_log.isDebugEnabled()) {
      _log.debug("Creating new JdbcCrawler instance");
    }
  }

  /**
   * This method should be called by clients after completing their work with the {@link JdbcCrawler} can release its
   * JDBC- and other resources. If the method is called before the {@link CrawlingProducerThread} terminates, the thread
   * will terminate after evaluating the {@link #_forceClosing}-flag the next time which is set by the {@link #close()}
   * -Method.
   * 
   */
  public void close() {

    synchronized (_openedMonitor) {

      _forceClosing = true;
      _opened = false;
      _log.info("Closing JdbcCrawler...");
      try {
        _producerThread.join(WAIT_FOR_CRAWLING_THREAD_TERMINATION_WHEN_FORCECLOSING);
      } catch (final InterruptedException exception) {
        if (_log.isTraceEnabled()) {
          _log.trace("Encounterd InterruptedException while waiting for the ProducerThread to die.", exception);
        }
      }
      _isProducerRunning = false;
      _producerThread = null;
    }

    try {
      if (_retrievalResultSet != null) {
        _retrievalResultSet.close();
        _retrievalResultSet = null;
      }

    } catch (final SQLException e) {
      if (_log.isErrorEnabled()) {
        _log.error(e.getMessage(), e);
      }
    }

    try {
      if (_retrievalStatement != null) {
        _retrievalStatement.close();
        _retrievalStatement = null;
      }
    } catch (final SQLException e) {
      if (_log.isErrorEnabled()) {
        _log.error(e.getMessage(), e);
      }
    }
    try {
      if (_connection != null) {
        _connection.close();
        _connection = null;
      }
    } catch (final SQLException e) {
      if (_log.isErrorEnabled()) {
        _log.error(e.getMessage(), e);
      }
    }
    _dataSourceID = null;

  }

  /**
   * Creates an {@link MObject} for the passed data.
   * 
   * @param data
   *          An {@link Object[]} which constitutes a database row retrieved via the {@link #_retrievalResultSet}.
   * 
   * @return the {@link MObject} created.
   * @throws CrawlerCriticalException
   *           If {@code data} was {@code null} or one of the attributes defined in {@link #_attributes} could not be
   *           retrieved from {@code data}
   * 
   * @throws InvalidTypeException
   *           If one of the {@link Object}s in {@code data} is an instance of a Class which cannot be processed by
   *           {@link ConnectivityMObjectHelper}.
   */
  private DataReference createDataReference(final Object[] data) throws CrawlerCriticalException,
    InvalidTypeException {

    DataReference dataRef = null;

    final Record record = createRecord(data);
    final org.eclipse.smila.datamodel.record.Attribute[] idAttributes = getIdAttributes(record);
    final org.eclipse.smila.datamodel.record.Attribute[] hashAttributes = getHashAttributes(record);
    dataRef =
      DataReferenceFactory.getInstance().createDataReference(this, _dataSourceID, idAttributes, hashAttributes);
    _recordCache.put(dataRef.getId(), record);
    if (_recordCache.size() > RECORD_CACHE_WARNING_THRESHOLD) {
      _performanceCounters.increment(POC_RECORDS_CREATED);
    }
    return dataRef;

  }

  /**
   * @param record
   * @return
   */
  private org.eclipse.smila.datamodel.record.Attribute[] getHashAttributes(final Record record) {

    final ArrayList<org.eclipse.smila.datamodel.record.Attribute> hashList =
      new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
    final Iterator<String> attributeNames = record.getMetadata().getAttributeNames();

    while (attributeNames.hasNext()) {
      final String attributeName = attributeNames.next();
      for (final Attribute processingAttrib : _attributes) {
        if (processingAttrib.getName().equals(attributeName) && processingAttrib.isHashAttribute()) {
          hashList.add(record.getMetadata().getAttribute(attributeName));
        }
      }
    }
    return hashList.toArray(new org.eclipse.smila.datamodel.record.Attribute[] {});
  }

  /**
   * @param record
   * @return
   */
  private org.eclipse.smila.datamodel.record.Attribute[] getIdAttributes(final Record record) {
    final ArrayList<org.eclipse.smila.datamodel.record.Attribute> idList =
      new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
    final Iterator<String> attributeNames = record.getMetadata().getAttributeNames();

    while (attributeNames.hasNext()) {
      final String attributeName = attributeNames.next();
      for (final Attribute processingAttrib : _attributes) {
        if (processingAttrib.getName().equals(attributeName) && processingAttrib.isKeyAttribute()) {
          idList.add(record.getMetadata().getAttribute(attributeName));
        }
      }
    }
    return idList.toArray(new org.eclipse.smila.datamodel.record.Attribute[] {});

  }

  /**
   * 
   * Creates a {@link Record}-object for the passed data and metadata.
   * 
   * @param data
   *          The data to be used for the {@link Record} - an {@link Object[]} constituting a database row.
   * @param metaData
   *          The metadata-Object to be associated with the record.
   * @return The created {@link Record}-object.
   * @throws CrawlerCriticalException
   *           If any of the specified {@link #_attributes} could not be extracted from the {@code data}.
   * 
   */
  private Record createRecord(final Object[] data) throws CrawlerCriticalException {

    final Record record = _recordFactory.createRecord();
    final MObject metaData = record.getMetadata();

    for (final Attribute attribute : _attributes) {

      if (attribute.isAttachment()) {
        // set Attachment attributes as Attachments to the record
        final Object attachmentValue = readAttribute(data, attribute);
        if (attachmentValue != null) {
          if (attachmentValue instanceof String) {
            try {
              record.setAttachment(attribute.getName(), ((String) attachmentValue).getBytes("utf-8"));
            } catch (final UnsupportedEncodingException exception) {
              _log.warn("UTF-8 Encoding ist not supported by this VM. (Very unlikely...)", exception);
            }
          } else if (attachmentValue instanceof byte[]) {
            record.setAttachment(attribute.getName(), (byte[]) attachmentValue);
          } else if (attachmentValue instanceof Blob) {
            final Blob blob = (Blob) attachmentValue;
            byte[] byteValue = null;
            try {
              byteValue = IOUtils.toByteArray(blob.getBinaryStream());
            } catch (final IOException exception) {
              _log.error("Encountered IOException when getting byte[]-Value of BLOB-Stream for attribute ["
                + attribute.getName() + "]. Assigning null-Value.", exception);
              byteValue = null;
            } catch (final SQLException exception) {
              _log.error("Encountered SQLException when retrieving BLOB-Stream for attribute ["
                + attribute.getName() + "]. Assigning null-Value.", exception);
              byteValue = null;
            }
            record.setAttachment(attribute.getName(), byteValue);

          } else if (attachmentValue instanceof Clob) {
            final Clob clob = (Clob) attachmentValue;
            byte[] byteValue = null;
            try {
              byteValue = IOUtils.toByteArray(clob.getAsciiStream());
            } catch (final IOException exception) {
              _log.error("Encountered IOException when getting byte[]-Value of CLOB-Stream for attribute ["
                + attribute.getName() + "]. Assigning null-Value.", exception);
              byteValue = null;
            } catch (final SQLException exception) {
              _log.error("Encountered SQLException when retrieving CLOB-Stream for attribute ["
                + attribute.getName() + "]. Assigning null-Value.", exception);
              byteValue = null;
            }
            record.setAttachment(attribute.getName(), byteValue);
          } else {
            throw new IllegalArgumentException("Unsupported Attachment type ["
              + attachmentValue.getClass().getName() + "]");
          }
        }
        // else: attribute is NOT an attachment ...
      } else {
        final Object value = readAttribute(data, attribute);
        if (value != null) {
          if (value instanceof Object[]) {
            try {
              MObjectHelper.addLiteralArrayAttribute(_recordFactory, metaData, attribute.getName(),
                (Object[]) value);
            } catch (final InvalidTypeException exception) {
              _log.error(
                "Could not set value of attribute [" + attribute.getName() + "] as LiteralArrayAttribute.",
                exception);
            }
          } else {
            try {
              MObjectHelper.addSimpleLiteralAttribute(_recordFactory, metaData, attribute.getName(), value);
            } catch (final InvalidTypeException exception) {
              _log.error("Could not set value of attribute [" + attribute.getName()
                + "] as SimpleLiteralAttribute.", exception);
            }
          }
        }
      }

    }
    return record;
  }

  /**
   * @return The Crawlers ProducerThread.
   * @see CrawlingProducerThread
   */
  public Thread getProducerThread() {
    return _producerThread;
  }

  /**
   * This method evaluates if there is any more data to be fetched from the data source.
   * <p>
   * This is the case if any of the following is true
   * </p>
   * <ul>
   * <li>the {@link #_retrievalResultSet} has at least one more row to fetch</li>
   * <li>there ist at least one more {@link GroupingRange} in {@link #_groupingRanges} to be processed
   * </ul>
   * 
   * @return True if there is at least one more database row to be processed, false otherwise.
   * @throws CrawlerCriticalException
   *           If an {@link SQLException} is encountered when checking the {@link #_retrievalResultSet} for more rows.
   */
  private boolean hasNext() throws CrawlerCriticalException {

    try {

      if (_retrievalResultSet == null) {
        populateRetrievalResultSet();
      }
      if (_retrievalResultSet.next()) {
        _retrievalResultSet.previous();
        return true;

      } else if (_groupingRangesIterator != null && _groupingRangesIterator.hasNext()) {

        _currentGroupingRange = _groupingRangesIterator.next();
        populateRetrievalResultSet();
        while (_retrievalResultSet.next()) {
          _retrievalResultSet.previous();
          return true;
        }

      }
    } catch (final SQLException e) {
      throw new CrawlerCriticalException("Encounterd SQLException in hasNext()-Procedure", e);
    }
    return false;
  }

  /**
   * Checks whether there is more data to be returned for a call to {@link #getNextDeltaIndexingData()}.
   * 
   * @return Boolean flag: {@code true} if the {@link #_internalQueue} is not empty, {@code false} otherwise.
   */
  private boolean hasNextItemInQueue() {
    while (_isProducerRunning && _internalQueue.isEmpty()) {

      try {
        Thread.sleep(HAS_NEXT_ITEM_THREAD_WAIT);

      } catch (final InterruptedException e) {
        _log.trace("Got interrupted while waiting for queue to fill up in hasNextItemInQueue()-Procedure");
      }

    }
    return !_internalQueue.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   */
  public void initialize(final DataSourceConnectionConfig config) throws CrawlerException, CrawlerCriticalException {

    if (_log.isDebugEnabled()) {
      _log.debug("Initializing JdbcCrawler...");
    }
    synchronized (_openedMonitor) {
      if (_opened) {
        throw new CrawlerCriticalException(
          "Crawler is already busy. This should not be the case when initializing.");

      }
      _opened = true;
      _forceClosing = false;
    }

    _performanceCounters =
      new CrawlerPerformanceCounterHelper<JdbcCrawlerPerformanceAgent>(config, hashCode(),
        JdbcCrawlerPerformanceAgent.class);

    _isProducerRunning = true;
    _internalQueue = new ArrayBlockingQueue<DataReference>(INTERNAL_QUEUE_CAPACITY);
    _dataSourceID = config.getDataSourceID();
    final Attributes attributes = config.getAttributes();
    final List<IAttribute> attributeList = attributes.getAttribute();
    _attributes = attributeList.toArray(new Attribute[attributeList.size()]);
    _process = (Process) config.getProcess();
    _recordCache = new HashMap<Id, Record>();

    _producerThread = new CrawlingProducerThread();
    _producerThread.start();

  }

  /**
   * This method populates the {@link #_attributeMapping} {@link HashMap}, mapping attribute names to column indexes in
   * the {@link #_retrievalResultSet}. It uses the columnNames provided by the {@link ResultSetMetaData}-object
   * {@link #_retrievalResultSetMetaData}
   * 
   * @throws SQLException
   *           If any of the operations on {@link #_retrievalResultSetMetaData} failed for whatever reason.
   */
  private void populateAttributeMapping() throws SQLException {

    _attributeMapping = new HashMap<String, Integer>();
    for (final Attribute attribute : _attributes) {

      for (int i = 1; i <= _retrievalResultSetMetaData.getColumnCount(); i++) {
        if (_retrievalResultSetMetaData.getColumnName(i).trim().equalsIgnoreCase(attribute.getColumnName().trim())) {
          _attributeMapping.put(attribute.getName(), i);
          _log.debug("Mapping dataset column with name [" + _retrievalResultSetMetaData.getColumnName(i)
            + "] to Attribute with name [" + attribute.getName() + "] which declares SQL-Type ["
            + attribute.getSqlType() + "] and selects column [" + attribute.getColumnName() + "]");
          break;
        }
      }
    }

    if (_attributeMapping.size() != _retrievalResultSetMetaData.getColumnCount()
      || _attributeMapping.size() != _attributes.length) {
      _log.warn("Only " + _attributeMapping.size() + " Attribute-Mappings could be found. "
        + _retrievalResultSetMetaData.getColumnCount() + " Resultset-Columns and "
        + (_attributes.length - _attributeMapping.size())
        + " Schema-Attributes remain unmapped. Check name and type conformance");

    }

  }

  /**
   * This method populates the {@link #_retrievalResultSet} using the {@link PreparedStatement}
   * {@link #_retrievalStatement}. If groupigs are enabled the respective parameters of the statement are set to the
   * values of {@link #_currentGroupingRange} before statement execution. After executing the statement the member
   * {@link #_retrievalResultSetMetaData} is set.
   * 
   * @throws SQLException
   *           If any of the JDBC operations fail for whatever reason.
   */
  private void populateRetrievalResultSet() throws SQLException {

    // free resources to avoid oom-exceptions
    if (_retrievalResultSet != null) {
      _log.info("Closing Retrieval Resultset for re-population");
      _retrievalResultSet.close();
      _retrievalResultSet = null;
    }

    if (_groupingRanges != null && _groupingRanges.size() > 0) {
      // if groupings are enabled we need to set the retrieval parameters in the statement

      // insert "start"-values
      final PreparedStatementTypedParameter[] startValues = _currentGroupingRange.getStartValues();
      for (int i = 0; i < startValues.length; i++) {
        _log.trace("Setting Start-Value [" + startValues[i].toString() + "] as statement parameter");
        startValues[i].applyToPreparedStatement(_retrievalStatement);
      }

      // insert "end"-values
      final PreparedStatementTypedParameter[] endValues = _currentGroupingRange.getEndValues();
      for (int i = 0; i < endValues.length; i++) {
        _log.trace("Setting End-Value [" + endValues[i].toString() + "] as statement parameter");
        endValues[i].applyToPreparedStatement(_retrievalStatement);
      }
    }

    // execute statement and assign resultset to member variable
    _log.trace("Executing retrieval statement");
    final ResultSet resultSet = _retrievalStatement.executeQuery();
    _retrievalResultSet = resultSet;
    _retrievalResultSetMetaData = _retrievalResultSet.getMetaData();
    if (_attributeMapping == null) {
      populateAttributeMapping();
    }

  }

  /**
   * Sets up the JDBC-{@link Connection} to the datasource as specified in the {@link Database}-attribute in the
   * {@link DataSourceConnectionConfig} and sets the {@link #_connection} property accordingly.
   * 
   * @throws CrawlerCriticalException
   *           If any of the following conditions arises:
   *           <ul>
   *           <li>The Driver class cannot be found</li>
   *           <li>The Driver class cannont be instatiated</li>
   *           <li>A {@link SQLException} occurs when creating the {@link Connection}</li>
   *           </ul>
   */
  private void prepareConnection() throws CrawlerCriticalException {
    final Database database = _process.getDatabase();
    final String driverName = database.getJdbcDriver();

    try {
      Class.forName(database.getJdbcDriver()).newInstance();
      if (_log.isDebugEnabled()) {
        _log.debug("Loaded JDBC driver [" + driverName + "]");
      }

    } catch (final ClassNotFoundException e) {
      final String errorMessage = "Unable to load jdbc driver [" + driverName + "]";
      throw new CrawlerCriticalException(errorMessage, e);
    } catch (final InstantiationException e) {
      final String errorMessage = "Unable to load jdbc driver [" + driverName + "]";
      throw new CrawlerCriticalException(errorMessage, e);
    } catch (final IllegalAccessException e) {
      final String errorMessage = "Unable to load jdbc driver [" + driverName + "]";
      throw new CrawlerCriticalException(errorMessage, e);
    }

    try {
      if (_log.isInfoEnabled()) {
        _log.info("Connecting to database [" + database.getConnection() + "]");
      }
      _connection =
        DriverManager.getConnection(database.getConnection(), database.getUser(), database.getPassword());
    } catch (final SQLException e) {
      final String errorMessage = "Failed to connect to database [" + database.getConnection() + "]";
      throw new CrawlerCriticalException(errorMessage, e);
    }

  }

  /**
   * Populates the {@link #_groupingRanges}-{@link ArrayList} according to the configuration specified in the
   * {@link Grouping}-attribute of the {@link DataSourceConnectionConfig}. The SQL-statements needed for this are
   * executed via a local {@link Statement}-object, just as the data is retrieved via a local {@link ResultSet}-object.
   * 
   * @throws CrawlerCriticalException
   *           If any of the following conditions occur:
   *           <ul>
   *           <li>Any of the columns used for grouping has a data type which is not supported: !(Number||String)</li>
   *           <li>A SQLException is raised while retrieving the grouping data from the database</li>
   *           </ul>
   */
  private void prepareGrouping() throws CrawlerCriticalException {
    final Grouping grouping = _process.getSelections().getGrouping();
    BigInteger stepping = BigInteger.ONE;
    ResultSet groupingResultSet = null;
    ResultSetMetaData groupingMetaData = null;
    if (grouping != null) {
      _groupingRanges = new ArrayList<GroupingRange>();
      final String groupingSQL = grouping.getSQL();
      stepping = grouping.getStepping();
      Statement groupingStatement = null;
      try {
        groupingStatement =
          _connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        _log.debug("Executing SQL for grouping preparation: [" + groupingSQL + "]");
        groupingResultSet = groupingStatement.executeQuery(groupingSQL);
        groupingMetaData = groupingResultSet.getMetaData();
        _log.debug("Retrieved groupingResultSet with [" + groupingMetaData.getColumnCount() + "] columns");
        for (int i = 1; i <= groupingMetaData.getColumnCount(); i++) {
          Class columnClass = null;
          try {
            columnClass = Class.forName(groupingMetaData.getColumnClassName(i));

          } catch (final ClassNotFoundException e) {
            _log.error("This should never happen: the class[" + groupingMetaData.getColumnClassName(i)
              + "] for the column " + i + " in the grouping result set could not be resolved");
          }
          if (Number.class.isAssignableFrom(columnClass)) {
            _log.debug("RowNr " + i + " of the grouping result set is of type [" + columnClass.getName()
              + "], which is derived from [Number]: fine for use in a grouping");
            continue;
          } else if (String.class.equals(columnClass)) {
            _log
              .debug("RowNr " + i + " of the grouping result set is of type [String]: fine for use in a grouping");
          } else {
            throw new CrawlerCriticalException("RowNr " + i + " of the grouping result set is of type ["
              + columnClass.getName() + "]: NOT supported as a grouping field");
          }
        }
        int groupingRecords = 0;
        PreparedStatementTypedParameter[] startValues = null;
        PreparedStatementTypedParameter[] endValues = null;
        final PreparedStatementTypedParameter[] finalValues =
          new PreparedStatementTypedParameter[groupingMetaData.getColumnCount()];

        while (groupingResultSet.next()) {

          if (groupingRecords == 0) {

            startValues = new PreparedStatementTypedParameter[groupingMetaData.getColumnCount()];
            for (int i = 1; i <= groupingMetaData.getColumnCount(); i++) {

              startValues[i - 1] =
                new PreparedStatementTypedParameter(groupingResultSet.getObject(i), (i * 2) - 1, groupingMetaData
                  .getColumnType(i));
            }

          }
          groupingRecords++;

          if (groupingRecords == stepping.intValue()) {
            endValues = new PreparedStatementTypedParameter[groupingMetaData.getColumnCount()];
            for (int i = 1; i <= groupingMetaData.getColumnCount(); i++) {
              endValues[i - 1] =
                new PreparedStatementTypedParameter(groupingResultSet.getObject(i), i * 2, groupingMetaData
                  .getColumnType(i));
            }
            final GroupingRange groupingRange = new GroupingRange(startValues, endValues);
            _groupingRanges.add(groupingRange);
            if (_log.isTraceEnabled()) {
              _log.trace("Added GroupingRange: [" + groupingRange.toString() + "] to _groupingRanges");
            }
            groupingRecords = 0;
            continue;
          }

          for (int i = 1; i <= groupingMetaData.getColumnCount(); i++) {
            finalValues[i - 1] =
              new PreparedStatementTypedParameter(groupingResultSet.getObject(i), i * 2, groupingMetaData
                .getColumnType(i));

          }

        }
        if (groupingRecords != 0 && stepping.intValue() != 1) {
          final GroupingRange finalgroupingRange = new GroupingRange(startValues, finalValues);
          _groupingRanges.add(finalgroupingRange);
          _log.debug("Added final GroupingRange [" + finalgroupingRange.toString() + "] to _groupingRanges");
        }
      } catch (final SQLException e1) {
        throw new CrawlerCriticalException("Encountered SQLException while preparing Groupings");
      } finally {
        try {
          if (groupingStatement != null) {
            groupingStatement.close();
          }
        } catch (final SQLException e) {
          _log.error("Could not closeGrouping statement");
        }
        try {
          groupingResultSet.close();
          _log.debug("Closed Grouping Resultset");
        } catch (final SQLException e) {
          _log.error("Could not close Resultset for Grouping statement");
        }
      }

    }
    // set current grouping to first grouping in list (if list is not empty)
    _groupingRangesIterator = _groupingRanges.iterator();
    if (_groupingRangesIterator.hasNext()) {
      _currentGroupingRange = _groupingRangesIterator.next();
    }

    _log.debug(String.format("Prepared %d grouping ranges based on specified stepping of %d", _groupingRanges
      .size(), stepping.intValue()));
  }

  /**
   * This method is called during initialization and assembles the {@link PreparedStatement}-member
   * {@link #_retrievalStatement} used for data retrieval according to the configuration in the {@link Selections}
   * -attribute of the {@link DataSourceConnectionConfig}. If grouping is enabled in the
   * {@link DataSourceConnectionConfig} the {@link #prepareGrouping()}-method is called.
   * 
   * @throws CrawlerCriticalException
   *           If the {@link PreparedStatement} could not be created on the {@link #_connection}
   * 
   */
  private void prepareRetrievalStatement() throws CrawlerCriticalException {

    String retrievalSql = _process.getSelections().getSQL();
    retrievalSql = retrievalSql.trim();

    if (_process.getSelections().getGrouping() != null) {
      prepareGrouping();
      _log.debug("Transforming SQL passed from index: [" + retrievalSql + "]");
      final Pattern groupingPlaceholderPattern = Pattern.compile("%\\d\\d(min|max)");
      final Matcher matcher = groupingPlaceholderPattern.matcher(retrievalSql);
      final String transformedSQL = matcher.replaceAll("?");
      _log.debug("Using transformed SQL for PreparedStatement: [" + transformedSQL + "]");
      retrievalSql = transformedSQL;
    }

    try {
      _retrievalStatement =
        _connection.prepareStatement(retrievalSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

    } catch (final SQLException e) {
      throw new CrawlerCriticalException("Failed to create statement on database connection", e);
    }

  }

  /**
   * Reads the specified attribute's value from the database row passed as {@link Object[]}. Uses the
   * {@link #_attributeMapping} {@link Map} to select the right index in {@code data}.
   * 
   * @param data
   *          A database row.
   * @param attribute
   *          The attribute whose value is to be determined.
   * @return the attribute value as {@link Object}.
   * @throws CrawlerCriticalException
   *           If any of the following conditions occur:
   *           <ul>
   *           <li>The parameter {@code data} was {@code null}.</li>
   *           <li>No mapping for the attribute could be found</li>
   *           </ul>
   */
  private Object readAttribute(final Object[] data, final Attribute attribute) throws CrawlerCriticalException {
    if (data == null) {
      throw new CrawlerCriticalException("Could not extract required attribute [" + attribute.getName()
        + "]. The data Object to read it from was null");
    }

    int index = -1;
    try {
      index = _attributeMapping.get(attribute.getName());
      return data[index - 1];
    } catch (final ArrayIndexOutOfBoundsException e) {
      throw new CrawlerCriticalException("Could not extract required attribute [" + attribute.getName() + "]");
    }

  }

  /**
   * This method is called by the {@code public}-methods of {@link JdbcCrawler} prior to any other activity to ensure
   * that critical exceptions that were caused in the Producer-Thread and stored in the class member
   * {@link #_producerException} are delegated to the client.
   * 
   * @throws CrawlerCriticalException
   *           If a critical exception was stored in {@link #_producerException} it gets thrown here.
   */
  private void rethrowProducerExceptions() throws CrawlerCriticalException {
    if (_producerException != null) {
      if (_log.isDebugEnabled()) {
        _log.debug("Rethrowing Producer Exceptions");
      }
      throw _producerException;
    }

  }

  /**
   * Inner class of {@link JdbcCrawler} subclassing the {@link Thread}-class. It is instantiated in
   * {@link JdbcCrawler#initialize(DataSourceConnectionConfig)} and handles the actual crawling process so crawling can
   * happen asynchronously.
   * 
   * @author mbreidenband
   * 
   */
  private class CrawlingProducerThread extends Thread {

    /**
     * Crawls the JDBC-datasource. {@inheritDoc}
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

      try {

        try {
          prepareConnection();
        } catch (final CrawlerCriticalException e) {
          _producerException = e;
          _performanceCounters.increment(POC_CRITICAL_CRAWLING_EXCEPTIONS);
          _log.error("Encountered critical Exception in prepareConnection() procedure", e);
        }

        try {
          prepareRetrievalStatement();
        } catch (final CrawlerCriticalException e) {
          _producerException = e;
          _performanceCounters.increment(POC_CRITICAL_CRAWLING_EXCEPTIONS);
          _log.error("Encountered critical Exception in prepareRetievalStatement() procedure", e);
        }

        while (!_forceClosing && hasNext()) {

          final Object[] values = new Object[_retrievalResultSetMetaData.getColumnCount()];
          _retrievalResultSet.next();
          for (int i = 1; i <= values.length; i++) {
            values[i - 1] = _retrievalResultSet.getObject(i);
          }
          _performanceCounters.increment(POC_DATA_ROWS_RETRIEVED);
          boolean waiting = true;
          DataReference dataRef = null;
          while (waiting) {

            if (dataRef == null) {
              try {
                dataRef = createDataReference(values);
                _performanceCounters.increment(POC_DATA_REFS_CREATED);
              } catch (final InvalidTypeException e) {
                _performanceCounters.increment(POC_CRAWLING_EXCEPTIONS);
                _log.error(e);
              }

              try {
                if (_log.isTraceEnabled()) {
                  _log.trace("Putting DataReference [" + dataRef + "] in internal queue");
                }
                synchronized (_openedMonitor) {
                  _internalQueue.put(dataRef);
                }

              } catch (final InterruptedException e) {
                if (_log.isTraceEnabled()) {
                  _log.trace("Got interrupted...");
                }
              }
            }
            waiting = false;
          }
        }

      } catch (final CrawlerCriticalException e) {
        _producerException = e;

        _performanceCounters.increment(POC_CRITICAL_CRAWLING_EXCEPTIONS);
        _log.error("Encountered critical Exception in Producer-Thread", e);
      } catch (final SQLException e) {
        _producerException = new CrawlerCriticalException("Encountered SQLException in Producer-Thread", e);

        _performanceCounters.increment(POC_CRITICAL_CRAWLING_EXCEPTIONS);
        _log.error("Encountered SQLException in Producer-Thread", e);
      } catch (final RuntimeException e) {
        _producerException = new CrawlerCriticalException("Encountered RuntimeException in Producer-Thread", e);

        _performanceCounters.increment(POC_CRITICAL_CRAWLING_EXCEPTIONS);
        _log.error("Encountered RuntimeException in ProducerThread", e);
      } finally {
        _isProducerRunning = false;
        if (_forceClosing) {
          _log.info("DbCrawling was terminated by close()-Procedure");
        } else if (_producerException != null) {
          _log.info("DbCrawling terminated with Exception");
        } else {
          _log.info("DbCrawling terminated normally");

        }
      }
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.Crawler#getNext()
   */
  public DataReference[] getNext() throws CrawlerException, CrawlerCriticalException {
    rethrowProducerExceptions();
    while (hasNextItemInQueue()) {

      try {
        final DataReference dataRef = _internalQueue.poll(QUEUE_POLL_WAITING, TimeUnit.MILLISECONDS);
        if (dataRef != null) {
          synchronized (_openedMonitor) {
            final List<DataReference> tempList = new ArrayList<DataReference>();
            tempList.add(dataRef);
            final int size = _internalQueue.drainTo(tempList, MAX_QUEUE_SIZE - 1);

            _performanceCounters.incrementBy(POC_DATA_REFS_RETRIEVED_BY_CLIENT, size + 1);
            return tempList.toArray(new DataReference[size + 1]);
          }
        }
      } catch (final InterruptedException e) {
        ; // nothing
      } catch (final Throwable t) {
        _log.error("Error occurred in getNext()", t);
        throw new CrawlerCriticalException(t);
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
    _recordCache.remove(id);

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#getAttachment(org.eclipse.smila.datamodel.id.Id,
   *      java.lang.String)
   */
  public byte[] getAttachment(final Id id, final String name) throws CrawlerException, CrawlerCriticalException {
    final Record record = _recordCache.get(id);
    if (record == null) {
      throw new CrawlerException("The requested record with id [" + id + "] was not found in the Crawler's cache");
    }
    return record.getAttachment(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#getAttachmentNames(org.eclipse.smila.datamodel.id.Id)
   */
  public String[] getAttachmentNames(final Id id) throws CrawlerException, CrawlerCriticalException {

    final Record record = _recordCache.get(id);
    if (record == null) {
      throw new CrawlerException("The requested record with id [" + id + "] was not found in the Crawler's cache");
    }
    final ArrayList<String> names = new ArrayList<String>();
    final Iterator<String> it = record.getAttachmentNames();
    while (it.hasNext()) {
      names.add(it.next());
    }
    return names.toArray(new String[] {});
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerCallback#getMObject(org.eclipse.smila.datamodel.id.Id)
   */
  public MObject getMObject(final Id id) throws CrawlerException, CrawlerCriticalException {
    final Record record = _recordCache.get(id);
    if (record == null) {
      throw new CrawlerException("The requested record with id [" + id + "] was not found in the Crawler's cache");
    }
    return record.getMetadata();
  }

}
