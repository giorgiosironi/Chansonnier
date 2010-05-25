/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.templates.transformer;

import java.util.Date;

import org.eclipse.smila.search.lucene.messages.advsearch.DDateField;
import org.eclipse.smila.search.templates.NodeTransformer;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.search.DField;


/**
 * The date node transformer returns a search on a date range. One of the end points of this query is the current date
 * time value.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class DateNodeTransformer extends NodeTransformer {

  /**
   * Creates a new instance of the date node transformer.
   * 
   * @throws NodeTransformerException
   *           Unable to create node transformer.
   */
  public DateNodeTransformer() throws NodeTransformerException {
    super();
  }

  /**
   * Transform Node.
   * 
   * @param dField
   *          Simple search field.
   * @return Representing advanced search structure.
   * @throws NodeTransformerException
   *           Unable to perform node transformation.
   * @see org.eclipse.smila.search.templates.NodeTransformer#transformNode(org.eclipse.smila.search.utils.search.DField)
   */
  @Override
  public ITermContent transformNode(final DField dField) throws NodeTransformerException {

    final int fieldNo = getParameterSet().getIntegerParameter("FieldNo").intValue();
    final boolean lowerValue = getParameterSet().getBooleanParameter("LowerValue").booleanValue();
    final Date compareValue = getParameterSet().getDateParameter("CompareValue");

    Date min = null;
    Date max = null;

    if (!lowerValue) {
      min = new Date();
      max = compareValue;
    } else {
      max = new Date();
      min = compareValue;
    }

    return new DDateField(fieldNo, min, max);
  }

}
