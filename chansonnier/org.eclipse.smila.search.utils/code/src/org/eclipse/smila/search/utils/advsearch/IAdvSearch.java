/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.advsearch;

import java.util.Iterator;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IAdvSearch extends Cloneable {

  public IQueryExpression addQueryExpression(IQueryExpression dQueryExpression);

  public IQueryExpression getQueryExpression(int index);

  public int getQueryExpressionCount();

  public Iterator getQueryExpressions();

  public IQueryExpression removeQueryExpression(int index);

  public IQueryExpression removeQueryExpression(IQueryExpression dQueryExpression);

  public IQueryExpression setQueryExpression(int index, IQueryExpression dQueryExpression);

}
