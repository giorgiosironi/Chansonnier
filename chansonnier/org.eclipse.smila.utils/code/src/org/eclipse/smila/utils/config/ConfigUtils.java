/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Helper class to load configs from these locations:
 * <ul>
 * <li>a central config folder</li>
 * <li>a default config delivered and deployed with the bundle</li>
 * </ul>
 * The configs are attempted to be loaded in the order of the list above.
 * <p>
 * 
 * The central config folder is determined in this order from the first non-blank location (the last will always
 * succeed!)
 * <ol>
 * <li>a System.property set to <code>&#x2011;Dorg.eclipse.smila.utils.config.root=&lt;path> </code></li>
 * <li>an environment variable set to <code>org.eclipse.smila.utils.config.root=&lt;path> </code></li>
 * <li><code>&lt;org.eclipse.core.runtime.Platform.getInstallLocation()>/configuration.</code></li>
 * </ol>
 * 
 */

public final class ConfigUtils {

  /** The Constant PROPERTY_CONFIG_ROOT. */
  public static final String PROPERTY_CONFIG_ROOT = "org.eclipse.smila.utils.config.root";

  /** The Constant CONFIGURATION_FOLDER_NAME. */
  private static final String CONFIGURATION_FOLDER_NAME = "configuration/";

  /** PRIOTIZED_PATHS. */
  private static final String[] PRIOTIZED_PATHS = new String[] { System.getProperty(PROPERTY_CONFIG_ROOT) //
    , System.getenv(PROPERTY_CONFIG_ROOT) //
    // the use Platform can cause problems ---> org.eclipse.core.runtime should be started (level1 !?, default doesn't
    // help sometimes)
    , Platform.getInstallLocation().getURL().getFile() + CONFIGURATION_FOLDER_NAME };

  /** The Constant CONFIGURATION_FOLDER. */
  private static final File CONFIGURATION_FOLDER = determineFolder();

  /**
   * Prevents instantiating a config utils.
   */
  private ConfigUtils() {
  }

  /**
   * Determine folder.
   * 
   * @return the file
   */
  private static File determineFolder() {
    final Log log = LogFactory.getLog(ConfigUtils.class);
    File file = null;
    for (final String path : PRIOTIZED_PATHS) {
      if (StringUtils.isNotBlank(path)) {
        file = new File(path);
        break;
      }
    }

    if (file != null) {
      if (file.exists()) {
        if (log.isInfoEnabled()) {
          log.info("CONFIGURATION_FOLDER = " + file.getPath());
        } else {
          System.out.println("CONFIGURATION_FOLDER = " + file.getPath());
        }
      } else {
        if (log.isInfoEnabled()) {
          log.info("CONFIGURATION_FOLDER ( " + file.getPath() + " ) is not found");
        } else {
          System.out.println("CONFIGURATION_FOLDER ( " + file.getPath() + " ) is not found");
        }
        file = null;
      }
    } else {
      if (log.isInfoEnabled()) {
        log.info("No CONFIGURATION_FOLDER is found");
      } else {
        System.out.println("No CONFIGURATION_FOLDER is found");
      }
    }
    return file;
  }

  /**
   * Gets the configuration stream.
   * 
   * @param bundleName
   *          the bundle name
   * @param configPath
   *          the config name
   * @param defaultConfigPath
   *          the default config name
   * 
   * @return the configuration stream
   */
  public static InputStream getConfigStream(final String bundleName, final String configPath,
    final String defaultConfigPath) {
    if (getConfigurationFolder() != null) {
      File file = new File(getConfigurationFolder(), bundleName);
      if (file.exists()) {
        if (configPath.contains(":")) {
          file = new File(configPath);
        } else {
          file = new File(file, configPath);
        }

        if (file.exists()) {
          try {
            return new FileInputStream(file);
          } catch (final FileNotFoundException e) {
            ;// nothing
          }
        }
      }
    }
    if (defaultConfigPath == null) {
      throw new ConfigurationLoadException(String.format(
        "Configuration resource %s is for the bundle %s not found", configPath, bundleName));
    }
    final Bundle bundle = Platform.getBundle(bundleName);
    if (bundle == null) {
      throw new ConfigurationLoadException("Unable to fing bundle " + bundleName);
    }
    final URL url = bundle.getEntry(defaultConfigPath);
    if (url == null) {
      throw new ConfigurationLoadException(String.format(
        "Unable to find configuration resource %s in the bundle %s", defaultConfigPath, bundleName));
    }
    try {
      return url.openStream();
    } catch (final IOException e) {
      throw new ConfigurationLoadException(e);
    }
  }

  /**
   * Gets the configuration folder.
   * 
   * @param bundleName
   *          the bundle name
   * @param configPath
   *          the configuration name
   * 
   * @return the configuration folder
   */
  public static File getConfigFolder(final String bundleName, final String configPath) {
    if (CONFIGURATION_FOLDER != null) {
      File file = new File(CONFIGURATION_FOLDER, bundleName);
      if (file.exists()) {
        file = new File(file, configPath);
        if (file.exists()) {
          return file;
        }
      }
    }
    return null;
  }

  /**
   * Gets the configuration stream.
   * 
   * @param bundleName
   *          the bundle name
   * @param configPath
   *          the configuration path
   * 
   * @return the configuration stream
   */
  public static InputStream getConfigStream(final String bundleName, final String configPath) {
    return getConfigStream(bundleName, configPath, configPath);
  }

  /**
   * Gets the configuration stream.
   * 
   * @param bundleName
   *          the bundle name
   * @param configPath
   *          the config name
   * 
   * @return the configuration stream
   */
  @SuppressWarnings("unchecked")
  public static List<String> getConfigEntries(final String bundleName, final String configPath) {
    if (getConfigurationFolder() != null) {
      File file = new File(getConfigurationFolder(), bundleName);
      if (file.exists()) {
        file = new File(file, configPath);
        if (file.exists()) {
          final String[] files = file.list();
          return Arrays.asList(files);
        }
      }
    }
    final Bundle bundle = Platform.getBundle(bundleName);
    if (bundle == null) {
      throw new ConfigurationLoadException("Unable to fing bundle " + bundleName);
    }
    try {
      final ArrayList<String> entries = new ArrayList<String>();
      final Enumeration paths = bundle.getEntryPaths(configPath);
      if (entries != null) {
        while (paths.hasMoreElements()) {
          entries.add((String) paths.nextElement());
        }
      }
      return entries;
    } catch (final Exception e) {
      throw new ConfigurationLoadException(String.format(
        "Unable to find configuration resource %s in the bundle %s", configPath, bundleName));
    }
  }

  /**
   * Gets the configuration folder.
   * 
   * @return the cONFIGURATION_FOLDER
   */
  public static File getConfigurationFolder() {
    return CONFIGURATION_FOLDER;
  }

}
