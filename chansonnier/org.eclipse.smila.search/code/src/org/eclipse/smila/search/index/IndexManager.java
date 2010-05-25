/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.index;

// tools

// data dictionary

// utils
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DConnection;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.plugin.Plugin;
import org.eclipse.smila.search.plugin.PluginFactory;

/**
 * The Class IndexManager.
 * 
 * @author August Georg Schmidt (BROX)
 */
public abstract class IndexManager {

  /**
   * Garbage collector for unused index connections.
   */
  private static IndexCleaner s_cleaner = new IndexCleaner();

  /**
   * The s_index usages.
   */
  private static Hashtable<String, IndexUsage> s_indexUsages = new Hashtable<String, IndexUsage>(0);

  /**
   * Instantiates a new index manager.
   */
  private IndexManager() {
  }

  /**
   * Returns the instance of a requested IndexConnection by the IndexName. This method implements a pooling mechanism
   * for these Object ensuring that: - onyl one IndexConnection is used at a time - there no more than the max. # of
   * IndexConections per Index alive.
   * 
   * @param indexName
   *          Index name.
   * 
   * @return Index connection.
   * 
   * @throws IndexException
   *           Unable to get instance of index connection.
   */
  public static IndexConnection getInstance(final String indexName) throws IndexException {
    final Log log = LogFactory.getLog(IndexManager.class);

    if (s_cleaner != null) {
      ; // remove compiler warning for cleaning thread.
    }
    IndexConnection indexConnection = null;
    DIndex dIndex = null;
    try {
      dIndex = DataDictionaryController.getIndex(indexName);
    } catch (final DataDictionaryException e) {
      throw new IndexException(e.getMessage());
    }
    if (dIndex == null) {
      throw new IndexException("index not in data dictionary [" + indexName + "]");
    }

    final DConnection dConnection = dIndex.getConnection();
    IndexConnectionUsage indexConUsage = null;
    IndexUsage iu = null;

    while (indexConnection == null) {

      iu = getIndexUsage(indexName);

      synchronized (iu) {
        // check if iu in SINGLE_USAGE
        if (iu._usage != Usage.Multi) {
          throw new IndexSingleUseException("index is not in multi use mode [" + indexName + "]");
        }

        // try to find iu that is not at work
        final Iterator<IndexConnectionUsage> it = iu._indexConnectionUsages.iterator();
        while (it.hasNext()) {
          indexConUsage = it.next();
          if (!indexConUsage._atWork) {
            indexConnection = indexConUsage._indexConnection;
            indexConUsage._atWork = true;
            break;
          }
        }

        // no available iu exist, create new if not exceeds max
        if (indexConnection == null) {
          if (dConnection.getMaxConnections() > iu._indexConnectionUsages.size()) {
            indexConUsage = new IndexConnectionUsage();
            indexConUsage._atWork = true;
            indexConUsage._idleSince = System.currentTimeMillis();
            final Plugin plugin = PluginFactory.getPlugin();
            indexConUsage._indexConnection = plugin.getIndexAccess().getIndexConnection(indexName);
            indexConnection = indexConUsage._indexConnection;
            iu._indexConnectionUsages.add(indexConUsage);
          }
        }
      } // sync iu

      if (indexConnection == null) {
        try {
          Thread.sleep(5);
        } catch (final Exception e) {
          log.error("SLEEP!", e);
        }
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("get index [" + indexConnection + "]");
    }
    return indexConnection;
  }

  /**
   * Release index connection. This method does not destroy a index connection.
   * {@see releaseInstance(IndexConnection, boolean}
   * 
   * @param indexConnection
   *          Index connection to release.
   */
  public static void releaseInstance(final IndexConnection indexConnection) {
    releaseInstance(indexConnection, false);
  }

  /**
   * Release index connection.
   * 
   * @param indexConnection
   *          Index connection to release.
   * @param destroy
   *          Whether to destroy the index connection.
   */
  public static void releaseInstance(final IndexConnection indexConnection, final boolean destroy) {
    final Log log = LogFactory.getLog(IndexManager.class);
    IndexConnectionUsage indexConUsage = null;
    final IndexUsage iu = getIndexUsage(indexConnection.getName());

    synchronized (iu) {
      final Iterator<IndexConnectionUsage> it = iu._indexConnectionUsages.iterator();
      while (it.hasNext()) {
        indexConUsage = it.next();
        if (indexConUsage._indexConnection == indexConnection) {
          indexConUsage._idleSince = System.currentTimeMillis();
          indexConUsage._atWork = false;
          if (log.isDebugEnabled()) {
            log.debug("index released [" + indexConnection + "]");
          }
          if (destroy) {
            it.remove();
            indexConUsage._indexConnection.close();
            if ((iu._indexConnectionUsages.size() == 0) && (iu._usage != Usage.None)) {
              s_indexUsages.remove(indexConnection.getName());
            }
            indexConUsage._indexConnection = null;
            indexConUsage = null;
            if (log.isDebugEnabled()) {
              log.debug("index destroyed [" + indexConnection + "]");
            }
          }
          break;
        }
      }
      iu._idleSince = System.currentTimeMillis();
    } // sync iu
  }

  /**
   * Do garbage collection on index connections.
   */
  protected static void doGarbageCollection() {
    synchronized (s_indexUsages) {
      final Iterator<IndexUsage> it = s_indexUsages.values().iterator();
      while (it.hasNext()) {
        final IndexUsage iu = it.next();
        final Iterator<IndexConnectionUsage> icus = iu._indexConnectionUsages.iterator();
        while (icus.hasNext()) {
          IndexConnectionUsage icu = icus.next();
          if (!icu._atWork) {
            if ((System.currentTimeMillis() - icu._idleSince) > icu._timeOut) {
              icus.remove();
              icu._indexConnection.close();
              icu = null;
            }
          }
        }
        if ((iu._indexConnectionUsages.size() == 0) && (iu._usage != Usage.None)) {
          if ((System.currentTimeMillis() - iu._idleSince) > IndexUsage.TIMEOUT) {
            it.remove();
          }
        }
      }
    }
  }

  /**
   * Causes the IndexUsage for the given index to be removed from indexUsages.
   * 
   * @param indexName
   *          Index name.
   * 
   * @throws IndexException
   *           Unable to delete index usage.
   */
  static void deleteIndexUsage(final String indexName) throws IndexException {

    synchronized (s_indexUsages) {
      final IndexUsage iu = getIndexUsage(indexName);

      if (iu == null) {
        throw new IndexException("index does not exist [" + indexName + "]");
      }
      // added iu synchronization
      synchronized (iu) {
        if (iu._usage != Usage.None) {
          throw new IndexException("index in use [" + indexName + "]");
        }
        s_indexUsages.remove(indexName);
      }
    }

  }

  /**
   * Deny all access to index.
   * 
   * @param indexName
   *          Index name.
   * 
   * @throws IndexException
   *           Unable to deny access to index.
   */
  static void noUse(final String indexName) throws IndexException {
    IndexUsage iu = null;

    iu = getIndexUsage(indexName);

    synchronized (iu) {
      // TODO: add correct solution of racing condition problem noUse, singleUse, etc
      if (Usage.None == iu._usage) {
        throw new IndexException(String.format("Index [%s] already in noUse state!", indexName));
      }
      iu._usage = Usage.None;
      final Iterator<IndexConnectionUsage> iter = iu._indexConnectionUsages.iterator();
      while (iter.hasNext()) {
        final IndexConnectionUsage icu = iter.next();
        final IndexConnection ic = icu._indexConnection;
        // releaseInstance(ic, true); WON'T WORK ==> fail-fast Iterator
        ic.close();
        iter.remove();
      } // while
    } // sync iu
  }

  /**
   * Get current index usage.
   * 
   * @param indexName
   *          Index name.
   * 
   * @return Index usage.
   */
  private static IndexUsage getIndexUsage(final String indexName) {
    synchronized (s_indexUsages) {
      IndexUsage iu = null;
      iu = s_indexUsages.get(indexName);
      if (iu == null) {
        iu = new IndexUsage();
        iu._indexName = indexName;
        s_indexUsages.put(indexName, iu);
      }
      return iu;
    }
  }

  /**
   * Put index in single use mode.
   * 
   * @param indexConnection
   *          Current index connection (only this connection may work.).
   * 
   * @throws IndexException
   *           Unable to set index to single use.
   */
  private static void singleUse(final IndexConnection indexConnection) throws IndexException {

    IndexUsage iu = null;

    iu = getIndexUsage(indexConnection.getName());

    synchronized (iu) {
      // TODO: add correct solution of racing condition problem noUse, singleUse, etc
      if (Usage.Multi != iu._usage) {
        throw new IndexException(String.format("Index [%s] in [%s] state!", indexConnection.getName(), iu._usage));
      }
      iu._usage = Usage.Single;
      final Iterator<IndexConnectionUsage> iter = iu._indexConnectionUsages.iterator();
      while (iter.hasNext()) {
        final IndexConnectionUsage icu = iter.next();
        final IndexConnection ic = icu._indexConnection;
        if (ic != indexConnection) {
          // releaseInstance(ic, true); WON'T WORK ==> fail-fast Iterator
          ic.close();
          iter.remove();
        }
      } // while
    } // sync iu
  }

  /**
   * Puts an index in multi-use mode. Indexes which are in multi-use mode can be opened by several search and/or
   * indexing processes at the same time. This method must be called whenever an index has been accessed in single-use
   * or no-use mode, since it is the only way to unlock an index, besides restarting the AnyFinder Engine.
   * 
   * @param indexName
   *          The name of the index to be put in multi-use mode
   * 
   * @throws IndexException
   *           Unable to set index in multi use mode.
   * 
   * @see noUse(String)
   * @see singleUse(String)
   */
  static void multiUse(final String indexName) throws IndexException {
    synchronized (s_indexUsages) {
      getIndexUsage(indexName)._usage = Usage.Multi;
    }
  }

  /**
   * Start burst mode for index connection.
   * 
   * @param indexConnection
   *          Current index connection.
   * 
   * @throws IndexException
   *           Unable to start burst mode.
   */
  public static void startBurstmode(final IndexConnection indexConnection) throws IndexException {
    try {
      singleUse(indexConnection);
      indexConnection.startBurstmode();
    } catch (final IndexException e) {
      throw e;
    } catch (final Exception e) {
      throw new IndexException("unable to start burstmode [" + indexConnection.getName() + "]", e);
    }
  }

  /**
   * Stop burst mode for index connection.
   * 
   * @param indexConnection
   *          Current index connection.
   * 
   * @throws IndexException
   *           Unable to stop burst mode.
   */
  public static void stopBurstmode(final IndexConnection indexConnection) throws IndexException {
    try {
      indexConnection.stopBurstmode();
    } catch (final Exception e) {
      throw new IndexException("unable to stop burstmode [" + indexConnection.getName() + "]", e);
    } finally {
      multiUse(indexConnection.getName());
    }
  }

  /**
   * Shutdown all connections.
   * 
   * @throws IndexException
   *           Unable to shutdown all connections.
   */
  public static void shutdown() throws IndexException {
    synchronized (s_indexUsages) {
      final Iterator<IndexUsage> it = s_indexUsages.values().iterator();
      while (it.hasNext()) {
        final IndexUsage iu = it.next();
        final Iterator<IndexConnectionUsage> icus = iu._indexConnectionUsages.iterator();
        while (icus.hasNext()) {
          IndexConnectionUsage icu = icus.next();
          icus.remove();
          icu._indexConnection.close();
          icu = null;
        }
      }
    }
  }

  /**
   * Checks if is index busy.
   * 
   * @param indexName
   *          the index name
   * 
   * @return true, if is index busy
   * 
   * @throws IndexException
   *           the index exception
   */
  public static boolean isIndexBusy(final String indexName) throws IndexException {
    // TODO: check this implementation. I do not understand the reason for "alreadyUsed"
    // reply: it's only a patch for isNew
    boolean alreadyUsed = true;
    synchronized (s_indexUsages) {
      alreadyUsed = s_indexUsages.containsKey(indexName);
    }
    final IndexUsage iu = getIndexUsage(indexName);
    synchronized (iu) {
      // TODO: valid isNew for IU
      if (alreadyUsed && ((System.currentTimeMillis() - iu._idleSince) < IndexUsage.TIMEOUT / 2)) {
        return true;
      }
      return false;
    }
  }

}
