/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.utils.workspace;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Helper class to create bundle working directories in a single location. Use system property
 * "org.eclipse.smila.utils.workspace.root" to specify location of SMILA workspace. Default is SMILA.work in eclipse
 * install location.
 * 
 * @author Juergen Schumacher (empolis GmbH)
 */
public final class WorkspaceHelper {

  /**
   * Name of system property of environment variable specifying a custom workspace root directory.
   */
  private static final String PROPERTY_WORKSPACE_ROOT = "org.eclipse.smila.utils.workspace.root";

  /**
   * File for general workspace folder specified by the property or env variable.
   */
  private static final File WORKSPACE_FOLDER;

  /**
   * Utility class, should not be instantiated.
   */
  private WorkspaceHelper() {
    // prevent creation of instances.
  }

  static {
    final Log log = LogFactory.getLog(WorkspaceHelper.class);
    String path = System.getProperty(PROPERTY_WORKSPACE_ROOT);
    if (path == null) {
      path = System.getenv(PROPERTY_WORKSPACE_ROOT);
    }
    File file;
    if (StringUtils.isBlank(path)) {
      WORKSPACE_FOLDER = null;
      log.info("Using default workspace.");
    } else {
      file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
      }
      if (file.exists() && file.isDirectory()) {
        WORKSPACE_FOLDER = file;
        log.info("Configured Workspace is " + WORKSPACE_FOLDER.getPath());
      } else {
        WORKSPACE_FOLDER = null;
        log.info("Configured workspace cannot be created, using default workspace.");
      }
    }
  }

  /**
   * Creates the working dir.
   * 
   * @param bundle
   *          the bundle
   * 
   * @return the file
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static File createWorkingDirByBundle(final Bundle bundle) throws IOException {
    final String bundleName = bundle.getSymbolicName();
    final Log log = LogFactory.getLog(WorkspaceHelper.class);
    File workingDir = createWorkingDirInternal(bundleName, log);
    if (workingDir == null) {
      workingDir = createDefaultBundleWorkspace(bundleName, log, workingDir, bundle);
    }
    ensureWorkingDir(bundleName, workingDir);
    return workingDir;
  }

  /**
   * Ensure working dir.
   * 
   * @param bundleName
   *          the bundle name
   * @param workingDir
   *          the working dir
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void ensureWorkingDir(final String bundleName, final File workingDir) throws IOException {
    if (workingDir == null) {
      throw new IOException("Failed to create working directory for bundle " + bundleName
        + ", see log file for possible reasons.");
    }
  }

  /**
   * Create a working directory for the given bundle in the SMILA workspace.
   * 
   * @param bundleName
   *          bundle name
   * 
   * @return working direcory.
   * 
   * @throws IOException
   *           creation error.
   */
  public static File createWorkingDir(final String bundleName) throws IOException {
    final Log log = LogFactory.getLog(WorkspaceHelper.class);
    final File workingDir = createWorkingDirInternal(bundleName, log);
    ensureWorkingDir(bundleName, workingDir);
    return workingDir;
  }

  /**
   * Creates the working dir internal.
   * 
   * @param bundleName
   *          the bundle name
   * @param log
   *          the log
   * 
   * @return the file
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static File createWorkingDirInternal(final String bundleName, final Log log) throws IOException {
    File workingDir = createCustomBundleWorkspace(bundleName, log);
    if (workingDir == null) {
      if (WORKSPACE_FOLDER == null) {
        workingDir = createDefaultBundleWorkspace(bundleName, log);
      } else {
        workingDir = ensureDirectory(WORKSPACE_FOLDER, bundleName);
      }
    }
    return workingDir;
  }

  /**
   * create default eclipse workspace directory for given bundle.
   * 
   * @param bundleName
   *          bundle anme.
   * @param log
   *          error log
   * 
   * @return working dir in eclipse workspace, if all goes well (bundleName is name of an installed bundle), else null.
   */
  private static File createDefaultBundleWorkspace(final String bundleName, final Log log) {
    File workingDir = null;
    final Bundle bundle = Platform.getBundle(bundleName);
    workingDir = createDefaultBundleWorkspace(bundleName, log, workingDir, bundle);
    return workingDir;
  }

  /**
   * Creates the default bundle workspace.
   * 
   * @param bundleName
   *          the bundle name
   * @param log
   *          the log
   * @param workingDir
   *          the working dir
   * @param bundle
   *          the bundle
   * 
   * @return the file
   */
  private static File createDefaultBundleWorkspace(final String bundleName, final Log log, File workingDir,
    final Bundle bundle) {
    if (bundle == null) {
      log.error("Bundle " + bundleName + " seems not to be installed, cannot create default working dir.");
    } else {
      final IPath location = Platform.getStateLocation(bundle);
      if (location == null) {
        log.error("Platform did not return a state location for bundle " + bundleName + ".");
      } else {
        workingDir = location.toFile();
      }
    }
    return workingDir;
  }

  /**
   * read property $bundleName.workspace to determine and ensure a custom workspace for the given bundle.
   * 
   * @param bundleName
   *          name of bundle
   * @param log
   *          error log
   * 
   * @return working dir, if custom bundle workspace has been specified, else null.
   */
  private static File createCustomBundleWorkspace(final String bundleName, final Log log) {
    File workingDir = null;
    final String bundleWorkspaceProperty = bundleName + ".workspace";
    String customBundleWorkspace = System.getProperty(bundleWorkspaceProperty);
    if (customBundleWorkspace == null) {
      customBundleWorkspace = System.getenv(bundleWorkspaceProperty);
    }
    if (!StringUtils.isBlank(customBundleWorkspace)) {
      if (log.isInfoEnabled()) {
        log.info("Custom workspace for bundle " + bundleName + " is " + customBundleWorkspace);
      }
      final File file = new File(customBundleWorkspace);
      if (!file.exists()) {
        file.mkdirs();
      }
      if (file.exists() && file.isDirectory()) {
        workingDir = file;
      } else {
        log.error("Creating custom workspace for bundle " + bundleName + " failed, using fallback locations.");
      }
    }
    return workingDir;
  }

  /**
   * Create a working subdirectory for the given bundle in the SMILA workspace.
   * 
   * @param bundleName
   *          bundle name
   * @param dirName
   *          name of subdirectory.
   * 
   * @return working direcory.
   * 
   * @throws IOException
   *           creation error.
   */
  public static File createWorkingDir(final String bundleName, final String dirName) throws IOException {
    final File bundleWorkingDir = createWorkingDir(bundleName);
    return ensureDirectory(bundleWorkingDir, dirName);
  }

  /**
   * Checks if the working subdirectory exists for the given bundle in the SMILA workspace. The bundle workspace is
   * created if it does not exist, the subdirectory is NOT created!
   * 
   * @param bundleName
   *          bundle name
   * @param dirName
   *          name of subdirectory
   * 
   * @return true if the subdirectory exists, false otherwise
   * 
   * @throws IOException
   *           if any error occurs
   */
  public static boolean existsWorkingDir(final String bundleName, final String dirName) throws IOException {
    final File bundleWorkingDir = createWorkingDir(bundleName);
    final File subDirectory = new File(bundleWorkingDir, dirName);
    if ((subDirectory.exists()) && (subDirectory.isDirectory())) {
      return true;
    }
    return false;
  }

  /**
   * create new subdirectory in given directory. If it exists, it will not be deleted.
   * 
   * @param directory
   *          directory
   * @param child
   *          name of subdirectory
   * 
   * @return file of subdirectory.
   * 
   * @throws IOException
   *           directory cannot be created or a different kind of file exists in this location.
   */
  private static File ensureDirectory(final File directory, final String child) throws IOException {
    final File subDirectory = new File(directory, child);
    FileUtils.forceMkdir(subDirectory);
    return subDirectory;
  }
}
