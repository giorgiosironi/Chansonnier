/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.templates.TemplateException;
import org.eclipse.smila.search.templates.messages.searchtemplates.DTemplate;
import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.eclipse.smila.search.utils.search.DField;
import org.eclipse.smila.search.utils.search.DQuery;

/**
 * Interface for applying template logic.
 * 
 * @author August Georg Schmidt (BROX)
 */
public interface ITemplateAccess {

  /**
   * @param dQuery
   *          Simple search query expression.
   * @param dTemplate
   *          Template.
   * @param ic
   *          Index connection.
   * @return Advanced search.
   * @throws NodeTransformerException
   *           Unable to apply node transformers.
   * @throws TemplateException
   *           Invalid search template.
   * @throws IndexException
   *           Unable to apply transformers.
   */
  IQueryExpression applyTemplate(DQuery dQuery, DTemplate dTemplate, IndexConnection ic) throws TemplateException,
    NodeTransformerException, IndexException;

  /**
   * Apply field template.
   * 
   * @param dField
   *          Field from simple search.
   * @param dTemplate
   *          Template.
   * @param ic
   *          Index connection.
   * @return Subquery expression as ITerm.
   * @throws NodeTransformerException
   *           Unable to apply node transformers.
   * @throws TemplateException
   *           Invalid search template.
   * @throws IndexException
   *           Unable to apply transformers.
   */
  ITerm applyFieldTemplate(DField dField,
    org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplate dTemplate, IndexConnection ic)
    throws TemplateException, NodeTransformerException, IndexException;

}
