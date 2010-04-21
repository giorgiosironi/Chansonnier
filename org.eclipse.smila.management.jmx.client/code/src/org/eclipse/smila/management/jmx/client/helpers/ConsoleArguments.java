/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ConsoleArguments.
 */
public final class ConsoleArguments {

  /**
   * The Constant DEFAULT_CONFIG.
   */
  public static final String DEFAULT_CONFIG = "config.xml";

  /**
   * The _cmd.
   */
  private String _cmd;

  /**
   * The _parameters.
   */
  private String[] _parameters;

  /**
   * The _connection.
   */
  private String _connection;

  /**
   * The _configuration.
   */
  private String _configuration = DEFAULT_CONFIG;

  /**
   * Instantiates a new console arguments.
   */
  private ConsoleArguments() {
  }

  /**
   * Sets the cmd.
   * 
   * @param cmd
   *          the new cmd
   */
  private void setCmd(final String cmd) {
    _cmd = cmd;
  }

  /**
   * Gets the cmd.
   * 
   * @return the cmd
   */
  public String getCmd() {
    return _cmd;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the new parameters
   */
  private void setParameters(final String[] parameters) {
    _parameters = parameters;
  }

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  public String[] getParameters() {
    return _parameters;
  }

  /**
   * Sets the connection.
   * 
   * @param connection
   *          the new connection
   */
  private void setConnection(final String connection) {
    _connection = connection;
  }

  /**
   * Gets the connection.
   * 
   * @return the connection
   */
  public String getConnection() {
    return _connection;
  }

  /**
   * Sets the configuration.
   * 
   * @param configuration
   *          the new configuration
   */
  private void setConfiguration(final String configuration) {
    _configuration = configuration;
  }

  /**
   * Gets the configuration.
   * 
   * @return the configuration
   */
  public String getConfiguration() {
    return _configuration;
  }

  /**
   * Parses the.
   * 
   * @param args
   *          the args
   * 
   * @return the console arguments
   */
  public static ConsoleArguments parse(final String[] args) {
    final ConsoleArguments a = new ConsoleArguments();
    boolean hasOperation = false;
    final List<String> parameters = new ArrayList<String>();
    for (final String arg : args) {
      // connection
      if (arg != null && arg.length() > 3 && arg.charAt(0) == '-' && arg.charAt(2) == ':') {
        // special options
        final String value = arg.substring(3);
        switch (arg.charAt(1)) {
          case 'c':
            a.setConnection(value);
            break;
          case 'f':
            a.setConfiguration(value);
            break;
          default:
            throw new IllegalArgumentException(String.format("Unknown special argument [%s]", arg));
        }
      } else {
        // operation
        if (!hasOperation) {
          hasOperation = true;
          a.setCmd(arg);
        } else {
          // operation parameters
          parameters.add(arg);
        }
      }
    }
    a.setParameters(parameters.toArray(new String[parameters.size()]));
    return a;
  }

  /**
   * Validate.
   * 
   * @return the string
   */
  public String validate() {
    if (this.getCmd() == null) {
      return "No operation specified";
    }
    return null;
  }
}
