package org.eclipse.smila.management.jmx.client.osgi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.smila.management.jmx.client.cmd.CmdConsole;
import org.eclipse.smila.management.jmx.client.cmd.CmdConsoleImpl;
import org.eclipse.smila.management.jmx.client.config.CmdConfigType;
import org.eclipse.smila.management.jmx.client.config.ConnectionConfigType;
import org.eclipse.smila.management.jmx.client.config.JmxClientConfigType;
import org.eclipse.smila.management.jmx.client.helpers.ConfigHelper;
import org.eclipse.smila.management.jmx.client.helpers.ConsoleArguments;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * Equinox console command provider for SMILA JMX management console.
 * 
 * @author jschumacher
 * 
 */
public class SMILACommandProvider implements CommandProvider {
  /**
   * Bundle ID.
   */
  public static final String BUNDLE_ID = "org.eclipse.smila.management.jmx.client";

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * produce a help string.
   * 
   * @return help string
   */
  public String getHelp() {
    final StringBuilder help = new StringBuilder("--- SMILA commands ---\n");
    help.append("\tsmila - invoke SMILA JMX management operation\n");
    help.append("Usage:\tsmila <operation> <parameter 1> <parameter N> ").append(
      "[-c:<alternate connection>] [-f:<alternate configuration file name>}\n");

    try {
      final InputStream configStream = ConfigUtils.getConfigStream(BUNDLE_ID, ConsoleArguments.DEFAULT_CONFIG);
      final JmxClientConfigType config = OSGiConfigLoader.load(configStream);
      help.append(ConfigHelper.createHelpString(config, "smila"));
    } catch (final Exception ex) {
      final String msg = "Failed to load default config file, cannot produce command samples.";
      if (_log.isErrorEnabled()) {
        _log.error(msg, ex);
      }
      help.append("\t" + msg);
    }
    return help.toString();
  }

  /**
   * execute a SMILA JMX command.
   * 
   * @param intp
   *          command line interpreter
   */
  public void _smila(final CommandInterpreter intp) {
    final List<String> args = new ArrayList<String>();
    String arg = null;
    while ((arg = intp.nextArgument()) != null) {
      args.add(arg);
    }
    ConsoleArguments arguments = null;
    // parsing parameters
    try {
      arguments = ConsoleArguments.parse(args.toArray(new String[args.size()]));
    } catch (final Throwable e) {
      // wrong parameters - print help and exit
      intp.println(e.toString());
      return;
    }
    final String validateMessage = arguments.validate();
    if (validateMessage != null) {
      // not complete parameters - print help and exit
      intp.println(validateMessage);
      return;
    }
    // parameters are ok
    try {
      final InputStream configStream = ConfigUtils.getConfigStream(BUNDLE_ID, arguments.getConfiguration());
      final JmxClientConfigType config = OSGiConfigLoader.load(configStream);
      // find connection
      final ConnectionConfigType connectionConfig = ConfigHelper.findConnectionConfig(arguments, config);
      // find cmd
      final CmdConfigType cmdConfig = ConfigHelper.findCmdConfig(arguments, config);
      try {
        final CmdConsole console = new CmdConsoleImpl();
        // TODO: make this use intp for output printing, too
        console.execute(cmdConfig, connectionConfig, arguments.getParameters());
      } catch (final Exception e) {
        return; // this is logged in console.execute() already.
      }
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error executing smila osgi command", e);
      }
      intp.println(e.toString());
    }
  }
}
