/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.management.jmx.client.helpers;

import org.eclipse.smila.management.jmx.client.config.CmdConfigType;
import org.eclipse.smila.management.jmx.client.config.ConnectionConfigType;
import org.eclipse.smila.management.jmx.client.config.JmxClientConfigType;
import org.eclipse.smila.management.jmx.client.config.SampleConfigType;

/**
 * @author jschumacher
 * 
 */
public final class ConfigHelper {

  /**
   * Private constructor to avoid instatiation.
   */
  private ConfigHelper() {

  }

  /**
   * Find connection config.
   * 
   * @param arguments
   *          the arguments
   * @param config
   *          the config
   * 
   * @return the connection config type
   */
  public static ConnectionConfigType findConnectionConfig(final ConsoleArguments arguments,
    final JmxClientConfigType config) {
    ConnectionConfigType connectionConfig = null;
    if (arguments.getConnection() == null) {
      connectionConfig = config.getConnection().get(0);
    } else {
      for (final ConnectionConfigType iConnectionConfig : config.getConnection()) {
        if (arguments.getConnection().equals(iConnectionConfig.getId())) {
          connectionConfig = iConnectionConfig;
          break;
        }
      }
    }
    if (connectionConfig == null) {
      throw new IllegalArgumentException(String.format("Unable to find connection with id=[%s]", arguments
        .getConnection()));
    }
    return connectionConfig;
  }

  /**
   * Find cmd config.
   * 
   * @param arguments
   *          the arguments
   * @param config
   *          the config
   * 
   * @return the cmd config type
   */
  public static CmdConfigType findCmdConfig(final ConsoleArguments arguments, final JmxClientConfigType config) {
    CmdConfigType cmdConfig = null;
    if (arguments.getCmd() == null) {
      throw new IllegalArgumentException("Operation argument cannot be null");
    }
    for (final CmdConfigType iCmdConfig : config.getCmd()) {
      if (arguments.getCmd().equals(iCmdConfig.getId())) {
        cmdConfig = iCmdConfig;
        break;
      }
    }

    if (cmdConfig == null) {
      throw new IllegalArgumentException(String.format("Unable to find command with id=[%s]", arguments.getCmd()));
    }
    return cmdConfig;
  }

  /**
   * create a help string describing the contents of this config.
   * 
   * @param config
   *          the config
   * @param commandName
   *          name of command to prefix to the sample strings.
   * 
   * @return help string
   */
  public static String createHelpString(final JmxClientConfigType config, final String commandName) {
    final StringBuilder builder = new StringBuilder("<Connections> - optional\n");
    boolean first = true;
    for (final ConnectionConfigType connectionConfig : config.getConnection()) {
      // print connection info;
      builder.append(String.format("  %-20s\t%s:%s", connectionConfig.getId(), connectionConfig.getHost(),
        connectionConfig.getPort()));
      if (first) {
        builder.append("  - default\n");
        first = false;
      } else {
        builder.append("\n");
      }
    }
    builder.append("\n<Operations>  - required\n");
    for (final CmdConfigType cmdConfig : config.getCmd()) {
      builder.append(String.format("  %-20s\t%s\n", cmdConfig.getId(), cmdConfig.getEcho()));
    }
    if (!config.getSample().isEmpty()) {
      builder.append("\nSamples:\n");
      for (final SampleConfigType sampleConfigType : config.getSample()) {
        builder.append("  ").append(commandName);
        builder.append(" ").append(sampleConfigType.getValue()).append("\n");
        builder.append("            ").append(sampleConfigType.getEcho()).append("\n");
      }
    }
    return builder.toString();
  }
}
