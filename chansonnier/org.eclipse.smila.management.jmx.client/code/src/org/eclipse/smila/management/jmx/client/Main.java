/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.smila.management.jmx.client.cmd.CmdConsole;
import org.eclipse.smila.management.jmx.client.cmd.CmdConsoleImpl;
import org.eclipse.smila.management.jmx.client.config.CmdConfigType;
import org.eclipse.smila.management.jmx.client.config.ConnectionConfigType;
import org.eclipse.smila.management.jmx.client.config.JmxClientConfigType;
import org.eclipse.smila.management.jmx.client.helpers.ConfigHelper;
import org.eclipse.smila.management.jmx.client.helpers.ConfigLoader;
import org.eclipse.smila.management.jmx.client.helpers.ConsoleArguments;
import org.eclipse.smila.management.jmx.client.helpers.OutWriter;

/**
 * The Class Main.
 */
public final class Main {
  /**
   * no constructor for utility class.
   */
  private Main() {
  }

  /**
   * The main method.
   * 
   * @param args
   *          the args
   * 
   * @throws Exception
   *           the exception
   */
  public static void main(final String[] args) throws Exception {
    // no parameters - print help and exit
    if (args.length == 0) {
      printHelp(null);
      return;
    }
    final ConsoleArguments arguments;
    // parsing parameters
    try {
      arguments = ConsoleArguments.parse(args);
    } catch (final Throwable e) {
      // wrong parameters - print help and exit
      OutWriter.writeOut(e.getMessage());
      printHelp(null);
      return;
    }
    final String validateMessage = arguments.validate();
    if (validateMessage != null) {
      // not complete parameters - print help and exit
      OutWriter.writeOut(validateMessage);
      printHelp(arguments);
      return;
    }
    // parameters are ok
    try {
      final File file = new File(arguments.getConfiguration());
      final JmxClientConfigType config = ConfigLoader.load(new FileInputStream(file));
      // find connection
      final ConnectionConfigType connectionConfig = ConfigHelper.findConnectionConfig(arguments, config);
      // find cmd
      final CmdConfigType cmdConfig = ConfigHelper.findCmdConfig(arguments, config);
      final CmdConsole console = new CmdConsoleImpl();
      console.execute(cmdConfig, connectionConfig, arguments.getParameters());
    } catch (final Exception e) {
      throw e;
    }
  }

  /**
   * Prints the help.
   * 
   * @param arguments
   *          the arguments
   * 
   * @throws Exception
   *           the exception
   */
  private static void printHelp(final ConsoleArguments arguments) throws Exception {
    OutWriter
      .writeOut("Usage:\n  run <operation> <parameter 1> <parameter N> -c:<altrnate connection> -f:<altrnate configuration path>\n");

    final String configPath;
    if (arguments != null) {
      configPath = arguments.getConfiguration();
    } else {
      configPath = ConsoleArguments.DEFAULT_CONFIG;
    }

    JmxClientConfigType config;
    try {
      final File file = new File(configPath);
      config = ConfigLoader.load(new FileInputStream(file));
      OutWriter.writeOut(ConfigHelper.createHelpString(config, "run"));
    } catch (final Exception e) {
      throw e;
    }
  }
}
