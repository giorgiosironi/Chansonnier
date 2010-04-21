/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc.test;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.JdbcCrawler;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Abstract base-class for Tests of the {@link JdbcCrawler}-class. Provides a database with a well defined state which
 * can be used by extending Classes.<br>
 * <br>
 * Extends {@link DeclarativeServiceTestCase}.
 * 
 */
public abstract class AbstractDataEnabledJdbcCrawlerTestCase extends TestCase {

  /** Timeout for service detection. */
  protected static final long WAIT_FOR_SERVICE_DELAY = 30000;

  /** The name of the JDBC-driver to use. */
  protected static final String DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";

  /** The name of the database to use. */
  protected static final String DB_NAME = "crawlerTestDerbyDB";

  /** Time to wait in ms for the crawler to initialize (used by subclasses). */
  protected static final int WAIT_FOR_CRAWLER_INIT = 1000;

  /**
   * The Connection URL to use. Notice that this includes the "create=true"-flag which causes the Derby engine to create
   * the database files for us.
   */
  protected static final String CONNECTION_URL = "jdbc:derby:" + DB_NAME + ";create=true";

  /** The URL used for shutting down the entire embedded Derby engine. */
  protected static final String SHUTDOWN_URL = "jdbc:derby:;shutdown=true";

  /** Path to the resources-folder containing the file resources needed for creation of the database. */
  protected static final String RES_FOLDER_PATH = "configuration/res/";

  /** Name of the image file used as input for BLOB fields. */
  protected static final String PHOTO_FILE_NAME = "photo.png";

  /** Name of the text file used as input for CLOB fields. */
  protected static final String CV_FILE_NAME = "cv.txt";

  /** Constant specifying how many records to insert into the database fixture. */
  protected static final int RECORDS_TO_INSERT = 100;

  /** Column-Index constant. */
  private static final int COLUMN_CV = 14;

  /** Column-Index constant. */
  private static final int COLUMN_DOWNSIZED = 12;

  /** Column-Index constant. */
  private static final int COLUMN_SCHEDULED_FOR_DOWNSIZING = 11;

  /** Column-Index constant. */
  private static final int COLUMN_BIRTHDAY = 10;

  /** Column-Index constant. */
  private static final int COLUMN_VACATIONDAYS = 9;

  /** Column-Index constant. */
  private static final int COLUMN_BMI = 8;

  /** Column-Index constant. */
  private static final int COLUMN_PHONE = 7;

  /** Column-Index constant. */
  private static final int COLUMN_CITY = 6;

  /** Column-Index constant. */
  private static final int COLUMN_PLZ = 5;

  /** Column-Index constant. */
  private static final int COLUMN_STREET = 4;

  /** Column-Index constant. */
  private static final int COLUMN_SURNAME = 3;

  /** Column-Index constant. */
  private static final int COLUMN_FIRSTNAME = 2;

  /** Column-Index constant. */
  private static final int COLUMN_ID = 1;

  /** Column-Index constant. */
  private static final int COLUMN_PHOTO = 13;

  /** Constant. */
  private static final int MAX_VACATIONDAYS = 2;

  /** Constant. */
  private static final int DIGITS_IN_EXTENSION = 8;

  /** Constant. */
  private static final int DIGITS_IN_AREA_CODE = 4;

  /** Constant. */
  private static final int DIGITS_IN_PLZ = 5;

  /** The {@link JdbcCrawler}-instance to be tested. */
  protected JdbcCrawler _crawler;

  /** The Log - guess what: we need it for logging messages and errors. */
  protected final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc} Called by the JUnit-Runner before execution of a testMethod of inheriting classes. Sets up a Database
   * fixture by performing the following steps:
   * <ol>
   * <li>Shutdown a (potentially) running Derby engine by calling {@link DriverManager#getConnection(String)} with the
   * Shutdown-URL (see {@link #SHUTDOWN_URL}).</li>
   * <li>Delete all database files (potentially) remaining from prior test cases.</li>
   * <li>Configure Derby engine to log all executed SQL in the log file and to rather append to an existing logfile
   * than to overwrite it.</li>
   * <li>Get a {@link Connection} to the Derby DB and insert 100 rows of data (see source code for details). This
   * includes BLOB (from image file) and CLOB (from text file) fields. </li>
   * <li>Release allocated JDBC-resources (Statement, Connection).</li>
   * <li>Shutdown Derby Engine via Shutdown-URL (so the Crawler can start it up as it would normally)</li>
   * <li>Instantiates a {@link JdbcCrawler} and registers it by calling the register-Method of the parent class (see
   * {@link DeclarativeServiceTestCase}). </li>
   * </ol>
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {

    super.setUp();

    Class.forName(DRIVER_NAME).newInstance();
    // shutdown embedded Derby engine (if running)
    // using SHUTDOWN_URL *always* results in SQLException, so we catch and ignore ...
    try {
      DriverManager.getConnection(SHUTDOWN_URL);
    } catch (final SQLException e) {
      _log.info("Testcase Setup: Shutting down Derby Engine");

    }

    // delete existing db files
    final File dbDirectory = new File(DB_NAME);
    if (FileUtils.deleteQuietly(dbDirectory)) {
      _log.info("Deleted DB files of [" + DB_NAME + "] database");
    } else {
      _log.warn("Could not delete DB files of [" + DB_NAME + "] database");
    }

    Class.forName(DRIVER_NAME).newInstance();
    final Properties p = System.getProperties();

    // we want to see all sql in the db log file
    p.put("derby.language.logStatementText", "true");
    // we don't want the logfile to be recreated each time the engine starts ...
    p.put("derby.infolog.append", "true");
    Connection connection = DriverManager.getConnection(CONNECTION_URL);

    final ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements,
    // PreparedStatements
    PreparedStatement psInsert = null;
    Statement createStatement = null;

    createStatement = connection.createStatement();
    statements.add(createStatement);

    // create a person table...
    createStatement
      .execute("CREATE TABLE person(id int, vorname varchar(40), name varchar(40), strasse varchar(40), "
        + "plz varchar(5), ort varchar(40), festnetz varchar(20), body_mass_index double, vacationdays "
        + "integer, birthday date, scheduled_for_downsizing smallint, downsized timestamp, photo blob, cv clob)");
    _log.info("Created TABLE [person]");

    // insert 100 records ...
    psInsert = connection.prepareStatement("INSERT INTO person VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
    statements.add(psInsert);

    // prepare resource files for blob / clob insertion...
    final File resDir = new File(RES_FOLDER_PATH);
    final File photoFile = new File(resDir, PHOTO_FILE_NAME);
    final File cvFile = new File(resDir, CV_FILE_NAME);

    for (int i = 1; i <= RECORDS_TO_INSERT; i++) {

      psInsert.setInt(COLUMN_ID, i);
      psInsert.setString(COLUMN_FIRSTNAME, "Mustervorname" + i);
      psInsert.setString(COLUMN_SURNAME, "Mustername" + i);
      psInsert.setString(COLUMN_STREET, "Musterstrasse " + i);
      psInsert.setString(COLUMN_PLZ, String.valueOf(getRandomInteger(DIGITS_IN_PLZ)));
      psInsert.setString(COLUMN_CITY, "Musterstadt" + i);
      psInsert.setString(COLUMN_PHONE, "0" + getRandomInteger(DIGITS_IN_AREA_CODE) + "-"
        + getRandomInteger(DIGITS_IN_EXTENSION));
      psInsert.setDouble(COLUMN_BMI, (Math.random() / Math.random()));
      psInsert.setLong(COLUMN_VACATIONDAYS, getRandomInteger(MAX_VACATIONDAYS));
      psInsert.setDate(COLUMN_BIRTHDAY, new Date(new java.util.Date().getTime()));
      psInsert.setBoolean(COLUMN_SCHEDULED_FOR_DOWNSIZING, ((getRandomInteger(1) % 2) == 0));
      psInsert.setDate(COLUMN_DOWNSIZED, new Date(new java.util.Date().getTime()));

      psInsert.setBytes(COLUMN_PHOTO, FileUtils.readFileToByteArray(photoFile));

      psInsert.setString(COLUMN_CV, FileUtils.readFileToString(cvFile));

      psInsert.execute();

    }

    // release all open resources to avoid unnecessary memory usage

    for (final Statement st : statements) {
      try {
        st.close();
      } catch (final SQLException sqle) {
        _log.error("Could not release Statement", sqle);
      }
    }
    statements.clear();

    // Connection
    try {
      if (connection != null) {
        connection.close();
        connection = null;
      }
    } catch (final SQLException sqle) {
      _log.error("Could not release Connection", sqle);
    }

    // shutdown Derby engine AGAIN, so the Crawler can start it up as it would normally
    try {
      DriverManager.getConnection(SHUTDOWN_URL);
    } catch (final SQLException e) {
      _log.info("Testcase Setup: Shutting down Derby Engine");
    }

    _crawler = new JdbcCrawler();

  }

  /**
   * tearDown()-Implementation. Waits for the Producing-Thread of the Crawler to die in order to prevent other test
   * cases from starting (as they would causes issues when trying to re-setup the database fixture themselves) and
   * finally closes the crawler. {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {

    _log.debug("Waiting for Crawling Thread to terminate ...");
    final Thread thread = _crawler.getProducerThread();
    if (thread != null && thread.isAlive()) {
      thread.join();
    }
    _log.debug("Crawling Thread terminated");
    _crawler.close();
    _log.debug("Crawler closed");
    _crawler = null;
    _log.debug("Crawler object dereferenced");

  }

  /**
   * Used for generating random numbers for the data to insert into the db fixture.
   * 
   * @param numberOfDigits
   *          Number of digits the generated {@code int} is supposed to have.
   * @return A random {@code int}-value with the specified number of (decimal) digits.
   */
  private int getRandomInteger(final int numberOfDigits) {

    float f = (float) Math.random();

    for (int i = 0; i < numberOfDigits; i++) {
      // CHECKSTYLE:OFF
      // Seriously, I dont't want to create a constant for this literal of 10 ...
      f = f * 10;
      // CHECKSTYLE:ON
    }

    final int i = Math.round(f);
    return i;

  }

}
