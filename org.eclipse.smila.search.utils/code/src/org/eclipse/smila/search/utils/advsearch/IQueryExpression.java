/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.advsearch;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IQueryExpression extends Cloneable {

  public String getIndexName();

  public int getMaxHits();

  public int getMinSimilarity();

  public boolean getShowHitDistribution();

  public Integer getStartHits();

  public ITerm getTerm();

  public void setIndexName(String indexName);

  public void setMaxHits(int maxHits);

  public void setMinSimilarity(int minSimilarity);

  public void setShowHitDistribution(boolean showHitDistribution);

  public void setStartHits(Integer startHits);

  public ITerm setTerm(ITerm term);

}
