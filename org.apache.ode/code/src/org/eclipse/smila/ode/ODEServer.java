/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactory;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.iapi.BpelEngineException;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.apache.ode.bpel.iapi.BpelServer;
import org.apache.ode.bpel.iapi.InvocationStyle;
import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.bpel.rtrep.common.extension.AbstractExtensionBundle;
import org.apache.ode.il.EmbeddedGeronimoFactory;
import org.apache.ode.il.config.OdeConfigProperties;
import org.apache.ode.il.dbutil.Database;
import org.apache.ode.il.dbutil.DatabaseConfigException;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.store.ProcessStoreImpl;
import org.apache.ode.utils.GUID;
import org.w3c.dom.Element;

/**
 * very simple ODE integration.
 *
 * @author jschumacher
 *
 */
public class ODEServer {

  /**
   * config property for transaction timeout = "pipeline.timeout".
   */
  public static final String PROP_PIPELINE_TIMEOUT = "pipeline.timeout";

  /**
   * default transaction timeout for transaction manager in seconds = 5 minutes.
   */
  public static final String DEFAULT_PIPELINE_TIMEOUT = "300";

  /**
   * for conversion of timeout seconds to millis ... prevent a magic number.
   */
  public static final int MILLIS_PER_SECOND = 1000;

  /**
   * path to SQL script that prepares the in-memory HSQLDB instance for the scheduler.
   */
  private static final String RESOURCE_SCHEDULER_HSQLDB_SQL = "/sql/scheduler-hsqldb.sql";

  /**
   * path to SQL script that prepares the in-memory Derby instance for the scheduler.
   */
  private static final String RESOURCE_SCHEDULER_DERBY_SQL = "/sql/scheduler-derby.sql";

  /**
   * logger for this class.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * configuration of ODE.
   */
  private ODEConfigProperties _odeConfig;

  /**
   * the BPEL server.
   */
  private BpelServerImpl _server;

  /**
   * store for deployed processes.
   */
  private ProcessStoreImpl _store;

  /**
   * transaction manager.
   */
  private TransactionManager _txManager;

  /**
   * factory for database related objects (datasources, DAO connection factories, etc.).
   */
  private Database _database;

  /**
   * . data source to use by BPEL engine
   */
  private DataSource _dataSource;

  /**
   * BPEL job scheduler.
   */
  private Scheduler _scheduler;

  /**
   * DAO connection factory for BPEL objects.
   */
  private BpelDAOConnectionFactory _daoCF;

  /**
   * timeout for BPEL pipelines in seconds.
   */
  private int _txTimeoutMillis;

  /**
   * initialize ODE BPEL engine with settings in specified config properties.
   *
   * @param odeConfig
   *          config properties for engine.
   * @param contextFactory
   *          context factory to create the needed context objects.
   * @throws ODEServerException
   *           error in initialization
   */
  public ODEServer(final ODEConfigProperties odeConfig, final ODEServerContextFactory contextFactory)
    throws ODEServerException {
    final ClassLoader tcclBackup = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(ODEServer.class.getClassLoader());
    try {
      _odeConfig = odeConfig;
      _server = new BpelServerImpl();
      createTransactionManager();
      createDataSource();
      createScheduler();
      createProcessStore(contextFactory);
      initBPELServer(contextFactory);
      _server.start();
    } catch (final Exception ex) {
      // _log.error("error in ODE initialization", ex);
      throw new ODEServerException("error in ODE initialization" + ex.getMessage(), ex);
    } finally {
      Thread.currentThread().setContextClassLoader(tcclBackup);
    }
  }

  /**
   * deploy BPEL processes in given directory.
   *
   * @param deploymentUnitDirectory
   *          directory to search for BPEL processes.
   * @return names of deployed processes
   */
  public Collection<QName> deploy(final File deploymentUnitDirectory) {
    final ClassLoader tcclBackup = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(ODEServer.class.getClassLoader());
    try {
      final Collection<QName> pids = _store.deploy(deploymentUnitDirectory);
      final Collection<QName> names = new ArrayList<QName>(pids.size());
      for (final QName pid : pids) {
        final ProcessConf processConf = _store.getProcessConfiguration(pid);
        names.add(processConf.getType());
        _server.register(processConf);
      }
      return names;
    } finally {
      Thread.currentThread().setContextClassLoader(tcclBackup);
    }
  }

  /**
   * get the configuration of the named process.
   *
   * @param processId
   *          qname of proces
   * @return definition of process.
   */
  public ProcessConf getProcessConfiguration(final QName processId) {
    return _store.getProcessConfiguration(processId);
  }

  /**
   * register an extension bundle with the BPEL server.
   *
   * @param bundle
   *          an extension bundle.
   */
  public void registerExtensionBundle(final AbstractExtensionBundle bundle) {
    _server.registerExtensionBundle(bundle);
    _store.setExtensionValidators(bundle.getExtensionValidators());
  }

  /**
   * register an extension bundle with the BPEL server.
   *
   * @param bundle
   *          an extension bundle.
   */
  public void unregisterExtensionBundle(final AbstractExtensionBundle bundle) {
    _server.unregisterExtensionBundle(bundle.getNamespaceURI());
  }

  /**
   * register an listener to {@link org.apache.ode.bpel.evt.BpelEvent} issued by the ODE engine during execution of
   * processes.
   *
   * @param listener
   *          BPEL event listener
   */
  public void registerEventListener(final BpelEventListener listener) {
    _server.registerBpelEventListener(listener);
  }

  /**
   * unregister an listener to {@link org.apache.ode.bpel.evt.BpelEvent} issued by the ODE engine during execution of
   * processes.
   *
   * @param listener
   *          BPEL event listener
   */
  public void unregisterEventListener(final BpelEventListener listener) {
    _server.unregisterBpelEventListener(listener);
  }

  /**
   * invoke a BPEL process.
   *
   * @param serviceName
   *          name of BPEL process as returned by deploy()
   * @param opName
   *          name of the operation to execute
   * @param message
   *          message to send to process
   * @return result message
   * @throws ODEServerException
   *           error in invocation
   */
  public Element invoke(final QName serviceName, final String opName, final Element message)
    throws ODEServerException {
    final ClassLoader tcclBackup = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(ODEServer.class.getClassLoader());
    try {
      MyRoleMessageExchange mex = null;
      try {
        mex = invokeProcess(serviceName, opName, message);
        return processResponse(mex);
      } catch (final ODEServerException ex) {
        throw ex;
      } catch (final RuntimeException ex) {
        ex.printStackTrace();
        throw new ODEServerException("Runtime exception when invoking BPEL process", ex);
      } finally {
        if (mex != null) {
          mex.complete();
          mex.release();
        }
      }
    } finally {
      Thread.currentThread().setContextClassLoader(tcclBackup);
    }
  }

  /**
   * invoke process.
   *
   * @param serviceName
   *          service name
   * @param opName
   *          operation name
   * @param message
   *          message content
   * @return invocation message exchange
   * @throws ODEServerException
   *           processing error
   */
  private MyRoleMessageExchange invokeProcess(final QName serviceName, final String opName, final Element message)
    throws ODEServerException {
    try {
      final String messageId = new GUID().toString();
      if (_log.isDebugEnabled()) {
        _log.debug("request messageID = " + messageId);
      }
      final MyRoleMessageExchange mex =
        _server.createMessageExchange(InvocationStyle.UNRELIABLE, serviceName, opName, messageId);
      if (mex.getOperation() == null) {
        throw new ODEServerException("Did not find operation " + opName + " on service " + serviceName);
      }
      final Message request = mex.createMessage(mex.getOperation().getInput().getMessage().getQName());
      request.setMessage(message);
      mex.setRequest(request);
      mex.setTimeout(_txTimeoutMillis);
      mex.invokeBlocking();
      return mex;
    } catch (final TimeoutException ex) {
      throw new ODEServerException("Timeout in execution of pipeline " + serviceName, ex);
    } catch (final BpelEngineException ex) {
      throw new ODEServerException("BPEL error in execution of pipeline " + serviceName, ex);
    }
  }

  /**
   * process response of invocation.
   *
   * @param mex
   *          invocation mex
   * @return result content.
   * @throws ODEServerException
   *           error in processing.
   */
  private Element processResponse(final MyRoleMessageExchange mex) throws ODEServerException {
    try {
      final QName serviceName = mex.getServiceName();
      final String messageId = mex.getMessageExchangeId();
      if (_log.isDebugEnabled()) {
        _log.debug("response messageID = " + messageId);
      }
      final MyRoleMessageExchange responseMex = (MyRoleMessageExchange) _server.getMessageExchange(messageId);
      switch (responseMex.getAckType()) {
        case FAILURE:
          throw new ODEServerException("BPEL process " + serviceName.getLocalPart()
            + " completed with failure type " + responseMex.getFailureType() + ", explanation: "
            + responseMex.getFaultExplanation());
        case FAULT:
          throw new ODEServerException("BPEL process  " + serviceName.getLocalPart() + " completed with fault "
            + responseMex.getFault() + ", explanation: " + responseMex.getFaultExplanation());
        case RESPONSE:
        default:
          final Message response = responseMex.getResponse();
          return response.getMessage();
      }
    } finally {
      if (mex != null) {
        mex.complete();
        mex.release();
      }
    }
  }

  /**
   * shutdown the BPEL engine and all used resources.
   *
   */
  public void shutdown() {
    try {
      _server.stop();
    } catch (final Exception ex) {
      _server = null;
    }
    try {
      _scheduler.stop();
      _scheduler.shutdown();
    } catch (final Exception ex) {
      _scheduler = null;
    }
    try {
      _daoCF.shutdown();
    } catch (final Exception ex) {
      _daoCF = null;
    }
    _dataSource = null;
    try {
      _database.shutdown();
    } catch (final Exception ex) {
      _database = null;
    }
    _txManager = null;
  }

  /**
   * @return integrated BPEL engine
   */
  protected BpelServer getBpelServer() {
    return _server;
  }

  /**
   * intialize BPEL server.
   *
   * @param contextFactory
   *          context factory creating necessary context objects.
   */
  private void initBPELServer(final ODEServerContextFactory contextFactory) {
    if (_scheduler == null) {
      throw new RuntimeException("No scheduler");
    }
    if (_daoCF == null) {
      throw new RuntimeException("No DAO");
    }
    _server.setDaoConnectionFactory(_daoCF);
    _server.setScheduler(_scheduler);
    _server.setTransactionManager(_txManager);
    _server.setMessageExchangeContext(contextFactory.createMessageExchangeContext());
    _server.setBindingContext(contextFactory.createBindingContext(this));
    _server.setEndpointReferenceContext(contextFactory.createEPRContext());
    _server.setConfigProperties(_odeConfig);
    _server.init();

    final String txTimeoutValue =
      _odeConfig.getProperties().getProperty(PROP_PIPELINE_TIMEOUT, DEFAULT_PIPELINE_TIMEOUT);
    final int txTimeout = Integer.parseInt(txTimeoutValue);
    _log.info("BPEL process execution timeout: " + txTimeout + " seconds.");
    _txTimeoutMillis = txTimeout * MILLIS_PER_SECOND;
  }

  /**
   * create a store for BPEL process management.
   *
   * @param contextFactory
   *          context factory to create the needed context objects.
   */
  private void createProcessStore(final ODEServerContextFactory contextFactory) {
    _store =
      new ProcessStoreImpl(contextFactory.createEPRContext(), _dataSource, _odeConfig.getDAOConnectionFactory(),
        _odeConfig, true);
  }

  /**
   * create a TransactionManager for the BPEL engine. Currently hardcoded to use the Geronimo implementation of
   * transactions.
   *
   * @return a new transaction manager
   * @throws SystemException
   *           error in initialisation.
   */
  private TransactionManager createTransactionManager() throws SystemException {
    final EmbeddedGeronimoFactory factory = new EmbeddedGeronimoFactory();
    _txManager = factory.getTransactionManager();
    return _txManager;
  }

  /**
   * create a data source for persistence operations of the BPEL engine.
   *
   * @throws DatabaseConfigException
   *           invalid database configuration.
   */
  private void createDataSource() throws DatabaseConfigException {
    if (_txManager == null) {
      throw new RuntimeException("No transaction manager");
    }
    _database = new Database(_odeConfig);
    _database.setTransactionManager(_txManager);
    if (_odeConfig.getDbMode() == OdeConfigProperties.DatabaseMode.EMBEDDED) {
      _database.setWorkRoot(new File(_odeConfig.getWorkingDir()));
    }
    _database.start();
    _dataSource = _database.getDataSource();
    _daoCF = _database.createDaoCF();
    // create dummy connection to setup DB schema.
    // this way errors during schema creation (because tables exist alreay, for example) do not
    // disturb later operaion (they seem to do with EclipseLink, else).
    try {
      _txManager.begin();
      _daoCF.getConnection();
      _txManager.commit();
    } catch (final Exception e) {
      _log.error("error creating initial BPEL DAO connection", e);
    }
  }

  /**
   * create a scheduler for the BPEL engine.
   *
   * @return a new scheduler
   * @throws ODEServerException
   *           error initializing the scheduler database.
   */
  private Scheduler createScheduler() throws ODEServerException {
    if (_server == null) {
      throw new RuntimeException("No BPEL server");
    }
    if (_txManager == null) {
      throw new RuntimeException("No transaction manager");
    }
    if (_dataSource == null) {
      throw new RuntimeException("No data source");
    }

    prepareSchedulerDb();
    final SimpleScheduler simpleScheduler =
      new SimpleScheduler(new GUID().toString(), new JdbcDelegate(_dataSource), _odeConfig.getProperties());
    simpleScheduler.setTransactionManager(_txManager);
    simpleScheduler.setJobProcessor(_server);
    _scheduler = simpleScheduler;
    // _scheduler = new MockScheduler(_txManager);
    return _scheduler;
  }

  /**
   * create tables and aliases in in-memory HSQLDB.
   *
   * @throws ODEServerException
   *           error creating tables
   */
  private void prepareSchedulerDb() throws ODEServerException {
    Connection c = null;
    String sqlScriptName = null;
    if ("org.apache.derby.jdbc.EmbeddedDriver".equals(_odeConfig.getDbInternalJdbcDriverClass())) {
      sqlScriptName = RESOURCE_SCHEDULER_DERBY_SQL;
    } else if ("org.hsqldb.jdbcDriver".equals(_odeConfig.getDbInternalJdbcDriverClass())) {
      sqlScriptName = RESOURCE_SCHEDULER_HSQLDB_SQL;
    }
    // init some tables in DB
    if (sqlScriptName != null) {
      try {
        c = _dataSource.getConnection();
        _log.info("Reading SQL commands from " + sqlScriptName + " to prepare DB for scheduler.");
        final InputStream sqlStream = getClass().getResourceAsStream(sqlScriptName);
        if (sqlStream == null) {
          _log.error("Error reading SQL script " + sqlScriptName);
          throw new ODEServerException("Error reading SQL script " + sqlScriptName);
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(sqlStream));
        String line = null;
        StringBuilder sql = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          sql.append(line.trim());
          if (sql.length() > 0 && sql.charAt(sql.length() - 1) == ';') {
            // cut off ";", Derby doesn't like it.
            sql.setLength(sql.length() - 1);
            c.createStatement().execute(sql.toString());
            sql = new StringBuilder();
          }
        }
        reader.close();
      } catch (final IOException ex) {
        throw new ODEServerException("Error reading SQL script: " + ex.getMessage(), ex);
      } catch (final SQLException ex) {
        _log.info("Error creating tables in scheduler DB: " + ex.toString());
        _log.info("Usually this means that the DB has been initialized earlier already, "
          + "in this case everything should be fine.");
      } finally {
        try {
          if (c != null) {
            c.close();
          }
        } catch (SQLException ex) {
          ex = null; // ignorable.
        }
      }
    }
  }
}
