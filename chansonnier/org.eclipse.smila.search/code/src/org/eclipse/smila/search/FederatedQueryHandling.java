/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.eclipse.smila.search.utils.searchresult.DHit;
import org.eclipse.smila.search.utils.searchresult.DHitDistribution;

/**
 * @author August Georg Schmidt (BROX)
 * 
 * This class is a utility class for federated query handling.
 */
public final class FederatedQueryHandling {

  /**
   * Hide constructor. Class is used in a static way.
   */
  private FederatedQueryHandling() {
  }

  /**
   * This method creates a list of queries to be executed during search process.
   * 
   * @param indexNames
   *          Index names from search query.
   * @param startHits
   *          Start of hits in logical result structure.
   * @param maxHits
   *          Maximum number of hits to be returned.
   * @param hitDistributions
   *          Hit Distributions of all indices.
   * @return Array of QueryScope objects to be processed.
   */
  public static QueryScope[] calculateQueries(String[] indexNames, int startHits, int maxHits,
    HashMap<String, DHitDistribution> hitDistributions) {

    // prepare data structure containing all hits grouped/sorted by score and index name.
    final SortedMap<Integer, List<HitsPerIndex>> indicesPerHitLevel =
      calculateIndicesPerHitLevel(indexNames, hitDistributions);

    final HashMap<String, QueryScope> queryScopes = calculateQueryScopes(startHits, maxHits, indicesPerHitLevel);

    final HashMap<String, Integer> resolvedRecords =
      calculateStartPositionForQueryScopes(startHits, maxHits, indicesPerHitLevel);

    // assign selection start
    for (QueryScope queryScope : queryScopes.values()) {
      if (resolvedRecords.containsKey(queryScope.getIndexName())) {
        queryScope.setStartSelection(resolvedRecords.get(queryScope.getIndexName()));
      }
    }

    // prepare query scope return in correct order and do query scope extension
    final ArrayList<QueryScope> queryScopeOrderedResult = new ArrayList<QueryScope>();
    for (String indexName : indexNames) {
      if (queryScopes.containsKey(indexName)) {

        QueryScope queryScope = queryScopes.get(indexName);
        queryScopeOrderedResult.add(queryScope);

        final int alreadySpentRecords = queryScope.getStartSelection() - queryScope.getStart() - 1;
        final int spendAndSelectedRecords = (alreadySpentRecords + queryScope.getRecordsToSelect());
        if (spendAndSelectedRecords > queryScope.getHits()) {
          final int oldRecordsToSelect = queryScope.getRecordsToSelect();
          queryScope.setRecordsToSelect(queryScope.getHits() - alreadySpentRecords);

          final int newRecordsToSelect = oldRecordsToSelect - queryScope.getRecordsToSelect();
          queryScope =
            new QueryScope(queryScope.getIndexName(), queryScope.getStart() + queryScope.getHits(), queryScope
              .getHits(), newRecordsToSelect, queryScope.getStart() + queryScope.getHits() + 1);
          queryScopeOrderedResult.add(queryScope);
        }
      }
    }

    return queryScopeOrderedResult.toArray(new QueryScope[0]);
  }

  /**
   * This method calculates the start position for query scopes.
   * 
   * @param startHits
   *          Start of hits in search result.
   * @param maxHits
   *          Maximum number of hits in search result.
   * @param indicesPerHitLevel
   *          Structure with hit distribution gouped by score.
   * @return Start positions of a query scope by index name.
   */
  private static HashMap<String, Integer> calculateStartPositionForQueryScopes(int startHits, int maxHits,
    final SortedMap<Integer, List<HitsPerIndex>> indicesPerHitLevel) {

    final HashMap<String, Integer> positionPerIndex = new HashMap<String, Integer>();
    final HashMap<String, Integer> startPositionPerIndex = new HashMap<String, Integer>();
    int hitsFetched = 0;
    int position = 0;
    for (List<HitsPerIndex> hitsPerIndexList : indicesPerHitLevel.values()) {
      for (final HitsPerIndex hitsPerIndex : hitsPerIndexList) {

        final String indexName = hitsPerIndex.getIndexName();
        if (!positionPerIndex.containsKey(indexName)) {
          positionPerIndex.put(indexName, 0);
        }

        int positionInIndex = 0;
        positionInIndex = positionPerIndex.get(indexName);

        for (int i = 0; i < hitsPerIndex.getHits(); i++) {
          position++;
          positionInIndex++;
          positionPerIndex.put(indexName, positionInIndex);

          final boolean hitsShouldBeFetched = hitsFetched < maxHits;

          if ((hitsShouldBeFetched) && (position > startHits)) {

            hitsFetched++;
            if (!startPositionPerIndex.containsKey(indexName)) {
              startPositionPerIndex.put(indexName, positionInIndex);
            }
          } else {
            if (!hitsShouldBeFetched) {
              return startPositionPerIndex;
            }
          }
        }
      }
    }

    return startPositionPerIndex;
  }

  /**
   * This method calculates the query scopes for several results.
   * 
   * @param startHits
   *          Start of hits in search result.
   * @param maxHits
   *          Maximum number of hits in search result.
   * @param indicesPerHitLevel
   *          Structure with hit distribution gouped by score.
   * @return Query scopes.
   */
  private static HashMap<String, QueryScope> calculateQueryScopes(int startHits, int maxHits,
    final SortedMap<Integer, List<HitsPerIndex>> indicesPerHitLevel) {

    final HashMap<String, QueryScope> queryScopes = new HashMap<String, QueryScope>();
    final HashMap<String, Integer> positionPerIndex = new HashMap<String, Integer>();
    int recordsCollected = 0;
    int currentPosition = 0;
    for (List<HitsPerIndex> hitsPerIndexList : indicesPerHitLevel.values()) {

      for (final HitsPerIndex hitsPerIndex : hitsPerIndexList) {

        final String indexName = hitsPerIndex.getIndexName();
        if (!positionPerIndex.containsKey(indexName)) {
          positionPerIndex.put(indexName, 0);
        }

        int positionInIndex = 0;
        positionInIndex = positionPerIndex.get(indexName);

        for (int i = 0; i < hitsPerIndex.getHits(); i++) {

          currentPosition++;
          positionInIndex++;
          positionPerIndex.put(indexName, positionInIndex);
          if (currentPosition <= startHits) {
            continue;
          }

          if (recordsCollected < maxHits) {
            QueryScope queryScope = null;
            if (!queryScopes.containsKey(indexName)) {
              final int start = (((int) Math.floor((float) (positionInIndex - 1) / maxHits)) * maxHits);
              queryScope = new QueryScope(indexName, start, maxHits);
              queryScopes.put(indexName, queryScope);
            } else {
              queryScope = queryScopes.get(indexName);
            }
            recordsCollected++;
            queryScope.setRecordsToSelect(queryScope.getRecordsToSelect() + 1);
          } else {
            return queryScopes;
          }
        }
      }
    }
    return queryScopes;
  }

  /**
   * This method creates a sorted map containing all indices and hits grouped by score.
   * 
   * @param indexNames
   *          Name of indices.
   * @param hitDistributions
   *          Hit distributions.
   * @return Sorted set containing all hits grouped by score and index.
   */
  @SuppressWarnings("unchecked")
  private static SortedMap<Integer, List<HitsPerIndex>> calculateIndicesPerHitLevel(String[] indexNames,
    HashMap<String, DHitDistribution> hitDistributions) {
    final SortedMap<Integer, List<HitsPerIndex>> indicesPerHitLevel =
      new TreeMap<Integer, List<HitsPerIndex>>(new ReverseComparator());

    for (String indexName : indexNames) {

      if (!hitDistributions.containsKey(indexName)) {
        continue;
      }

      final DHitDistribution hitDistribution = hitDistributions.get(indexName);

      for (final Enumeration hits = hitDistribution.getHits(); hits.hasMoreElements();) {
        final DHit hit = (DHit) hits.nextElement();

        if (!indicesPerHitLevel.containsKey(hit.getScore())) {
          indicesPerHitLevel.put(hit.getScore(), new ArrayList<HitsPerIndex>());
        }
        indicesPerHitLevel.get(hit.getScore()).add(new HitsPerIndex(indexName, hit.getScore(), hit.getHits()));
      }
    }
    return indicesPerHitLevel;
  }

}
