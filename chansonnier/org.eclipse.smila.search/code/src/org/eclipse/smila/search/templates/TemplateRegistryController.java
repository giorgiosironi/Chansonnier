/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.EIFActivator;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.plugin.Plugin;
import org.eclipse.smila.search.plugin.PluginFactory;
import org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplate;
import org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplates;
import org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplatesCodec;
import org.eclipse.smila.search.templates.messages.fieldtemplates.DFieldTemplatesException;
import org.eclipse.smila.search.templates.messages.searchtemplates.DIndexField;
import org.eclipse.smila.search.templates.messages.searchtemplates.DSearchTemplates;
import org.eclipse.smila.search.templates.messages.searchtemplates.DSearchTemplatesCodec;
import org.eclipse.smila.search.templates.messages.searchtemplates.DSearchTemplatesException;
import org.eclipse.smila.search.templates.messages.searchtemplates.DSelector;
import org.eclipse.smila.search.templates.messages.searchtemplates.DTemplate;
import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.eclipse.smila.search.utils.search.DField;
import org.eclipse.smila.search.utils.search.DQuery;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.eclipse.smila.utils.xml.XMLUtilsConfig;
import org.eclipse.smila.utils.xml.XMLUtilsException;
import org.w3c.dom.Document;

/**
 * A cache and manager for Search- and FieldTemplates. If a template is not defined then an empty dummy template is
 * created.
 * 
 * @author August Georg Schmidt (BROX)
 */
public abstract class TemplateRegistryController {

  /**
   * Search templates.
   */
  private static Hashtable<String, DSearchTemplates> s_searchTemplates = new Hashtable<String, DSearchTemplates>(0);

  /**
   * Field templates.
   */
  private static Hashtable<String, DFieldTemplates> s_fieldTemplates = new Hashtable<String, DFieldTemplates>(0);

  /**
   * 
   */
  private TemplateRegistryController() {
  }

  /**
   * Get template for simple search.
   * 
   * @param dQuery
   *          Simple search query.
   * @return Search template or null.
   * @throws TemplateException
   *           Unable to get template.
   */
  public static DTemplate getTemplate(DQuery dQuery) throws TemplateException {
    final DSearchTemplates templates = getSearchTemplates(dQuery.getIndexName());
    for (int i = 0; i < templates.getTemplateCount(); i++) {
      final DTemplate template = templates.getTemplate(i);
      if (doesQueryMatchTemplate(dQuery, template)) {
        return template;
      }
    }
    return null;
  }

  /**
   * Get field template for simple search field.
   * 
   * @param query
   *          Simple search query.
   * @param field
   *          Field from simple search.
   * @param indexName
   *          index name.
   * @return Field template or null.
   * @throws TemplateException
   *           Unable to get template.
   */
  public static DFieldTemplate getFieldTemplate(DQuery query, DField field, String indexName)
    throws TemplateException {
    final DFieldTemplates templates = getFieldTemplates(indexName);
    for (int i = 0; i < templates.getTemplateCount(); i++) {
      final DFieldTemplate template = templates.getTemplate(i);
      if (doesFieldMatchTemplate(query, field, template)) {
        try {
          return (DFieldTemplate) template.clone();
        } catch (final CloneNotSupportedException e) {
          ; // no error handling required.
        }
      }
    }
    return null;
  }

  /**
   * Check whether a template matches a search query.
   * 
   * @param query
   *          Simple search query.
   * @param template
   *          Template.
   * @return Whether the template matches the query.
   */
  static boolean doesQueryMatchTemplate(DQuery query, DTemplate template) {

    if (query == null) {
      throw new NullPointerException("query");
    }
    if (template == null) {
      throw new NullPointerException("template");
    }

    final DSelector dSelector = template.getSelector();
    String selector = "";
    if (dSelector.getName() != null) {
      selector = dSelector.getName().trim();
    }
    String templateSelectorName = "";
    if (query.getTemplateSelectorName() != null) {
      templateSelectorName = query.getTemplateSelectorName().trim();
    }

    if (query.getFieldsCount() != dSelector.getIndexFieldCount()) {
      // selector has additional fields
      return false;
    }

    // test selector name
    if (!templateSelectorName.equals(selector)) {
      return false;
    }

    // test selector structure
    final Iterator fields = dSelector.getIndexFields();
    while (fields.hasNext()) {
      final DIndexField field = (DIndexField) fields.next();
      if (!isFieldInQuery(field, query)) {
        // field does not exist in selector
        return false;
      }
    }

    return true;
  }

  /**
   * Checks whether a field is contained in a search query.
   * 
   * @param indexField
   *          Field.
   * @param query
   *          Search query.
   * @return Whether field is in query.
   */
  private static boolean isFieldInQuery(DIndexField indexField, DQuery query) {
    final Enumeration enm = query.getFields();
    while (enm.hasMoreElements()) {
      final DField field = (DField) enm.nextElement();
      if (field.getFieldNo() == indexField.getFieldNo()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether a field does match a template.
   * 
   * @param query
   *          Query.
   * @param field
   *          Field.
   * @param template
   *          Template.
   * @return Whether a field matches a template.
   */
  public static boolean doesFieldMatchTemplate(DQuery query, DField field, DFieldTemplate template) {

    if (query == null) {
      throw new NullPointerException("query");
    }
    if (field == null) {
      throw new NullPointerException("field");
    }
    if (template == null) {
      throw new NullPointerException("template");
    }

    final Log log = LogFactory.getLog(TemplateRegistryController.class);

    final org.eclipse.smila.search.templates.messages.fieldtemplates.DSelector dSelector = template.getSelector();
    if (dSelector == null) {
      throw new NullPointerException("template.selector");
    }

    String selector = "";
    if (dSelector.getName() != null) {
      selector = dSelector.getName().trim();
    }
    String fieldTemplate = "";
    if (field.getFieldTemplate() != null) {
      fieldTemplate = field.getFieldTemplate().trim();
    }

    // test selector name and field no
    if (fieldTemplate.equals(selector) && field.getFieldNo() == dSelector.getFieldNo()) {

      if ((dSelector.getFilterExpression() != null) && (!"".equals(dSelector.getFilterExpression().trim()))) {
        final boolean matchesFilterExpression;
        try {
          matchesFilterExpression = matchesFilterExpression(dSelector.getFilterExpression().trim(), query);
        } catch (final TemplateException exception) {
          log.error("unable to check template expression filter [" + template.getName() + "]", exception);
          return false;
        }
        return matchesFilterExpression;
      } else {
        return true;
      }

    }
    return false;
  }

  /**
   * Checks whether a query matches the filter expression.
   * 
   * @param filterExpression
   *          Filter expression.
   * @param query
   *          Query.
   * @return Whether query matches.
   * @throws TemplateException -
   */
  public static boolean matchesFilterExpression(String filterExpression, DQuery query) throws TemplateException {

    if (filterExpression == null) {
      throw new NullPointerException("filterExpression");
    }
    if (query == null) {
      throw new NullPointerException("query");
    }

    if ("".equals(filterExpression.trim())) {
      return true;
    }

    final String[] tokens = filterExpression.trim().split(";");

    final Set<String> queryFields = new HashSet<String>();
    for (final Enumeration fields = query.getFields(); fields.hasMoreElements();) {
      queryFields.add(((DField) fields.nextElement()).getFieldNo() + "");
    }

    final Set<String> requiredFields = new HashSet<String>();
    final Set<String> prohibitedFields = new HashSet<String>();

    for (int i = 0; i < tokens.length; i++) {
      final String token = tokens[i].trim();
      if (token.length() > 0) {
        String number = token;
        if (token.startsWith("!")) {
          number = token.substring(1);
          prohibitedFields.add(number);
        } else {
          requiredFields.add(number);
        }

        try {
          final int fieldNo = Integer.parseInt(number);
        } catch (final NumberFormatException exception) {
          throw new TemplateException("unable to check for filter expression", exception);
        }
      }
    }

    // check all required fields
    for (final String field : requiredFields) {
      if (!queryFields.contains(field)) {
        return false;
      }
    }

    // check all prohibited fields
    for (final String field : prohibitedFields) {
      if (queryFields.contains(field)) {
        return false;
      }
    }

    return true;
  }

  /**
   * @param dQuery
   *          Search query.
   * @param dTemplate
   *          Search template.
   * @param ic
   *          Index connection.
   * @return Query expression.
   * @throws TemplateException
   *           Unable to apply search template.
   * @throws NodeTransformerException
   *           Unable to apply node transformers.
   * @throws IndexException
   *           Unable to apply search template.
   */
  public static IQueryExpression applyTemplate(DQuery dQuery, DTemplate dTemplate, IndexConnection ic)
    throws TemplateException, NodeTransformerException, IndexException {

    final Plugin plugin = PluginFactory.getPlugin();
    return plugin.getTemplateAccess().applyTemplate(dQuery, dTemplate, ic);
  }

  /**
   * Apply field template.
   * 
   * @param dField
   *          Query field.
   * @param dTemplate
   *          Template.
   * @param ic
   *          Index connection.
   * @return Subterm containing applied field template.
   * @throws TemplateException
   *           Unable to apply field template.
   * @throws NodeTransformerException
   *           Unable to apply node transformers.
   * @throws IndexException
   *           Error applying field templates.
   */
  public static ITerm applyFieldTemplate(DField dField, DFieldTemplate dTemplate, IndexConnection ic)
    throws TemplateException, NodeTransformerException, IndexException {

    final Plugin plugin = PluginFactory.getPlugin();
    return plugin.getTemplateAccess().applyFieldTemplate(dField, dTemplate, ic);
  }

  /**
   * Takes the given SearchTemplate (a) caches it in .searchTemplates and saves it to the harddrive.
   * 
   * @param templates
   *          Search templates for a index.
   * @throws TemplateException
   *           Unable to set search templates.
   */
  public static void setSearchTemplates(DSearchTemplates templates) throws TemplateException {
    synchronized (s_searchTemplates) {

      // cache template
      if (s_searchTemplates.containsKey(templates.getIndexName())) {
        s_searchTemplates.put(templates.getIndexName(), templates);
      }

      // write template to disk
      saveTemplateToDisk(templates);

    }
  }

  /**
   * Store templates persistent on disk.
   * 
   * @param templates
   *          Search templates to store.
   * @throws TemplateException
   *           Unable to store search templates.
   */
  private static void saveTemplateToDisk(DSearchTemplates templates) throws TemplateException {
    final Log log = LogFactory.getLog(TemplateRegistryController.class);
    final File file = getSearchTemplateFile(templates.getIndexName());
    try {
      final Document doc = DSearchTemplatesCodec.encode(templates);
      XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", file);
    } catch (final Exception e) {
      s_searchTemplates.remove(templates.getIndexName());
      log.error("unable to store templates [" + templates.getIndexName() + "] in file [" + file.getName() + "]", e);
      throw new TemplateException("unable to store templates [" + templates.getIndexName() + "]", e);
    }
  }

  /**
   * Returns the SearchTemplate-File-object for the given index. Just gets the object but it doesnt create or open the
   * file for you.
   * 
   * @param indexName
   *          Index name.
   * @return File containing search templates.
   */
  private static File getSearchTemplateFile(String indexName) {
    final File folder = ConfigUtils.getConfigFolder(EIFActivator.BUNDLE_NAME, "xml");
    final File file = new File(folder, "SearchTemplates-" + indexName + ".xml");
    return file;
  }

  /**
   * Returns the SearchTemplate-File-object for the given index. Just gets the object but it doesnt create or open the
   * file for you.
   * 
   * @param indexName
   *          Index name.
   * @return File containing field templates.
   */
  private static File getFieldTemplateFile(String indexName) {
    final File folder = ConfigUtils.getConfigFolder(EIFActivator.BUNDLE_NAME, "xml");
    final File file = new File(folder, "FieldTemplates-" + indexName + ".xml");
    return file;
  }

  /**
   * Resolves all search templates for a given index.
   * 
   * @param indexName
   *          Index name.
   * @return Search templates.
   * @throws TemplateException
   *           Unable to get search templates.
   */
  public static DSearchTemplates getSearchTemplates(String indexName) throws TemplateException {
    final Log log = LogFactory.getLog(TemplateRegistryController.class);

    checkIndexExistance(indexName);

    synchronized (s_searchTemplates) {
      if (s_searchTemplates.containsKey(indexName)) {
        return s_searchTemplates.get(indexName);
      } else {
        DSearchTemplates templates = null;
        final File file = getSearchTemplateFile(indexName);

        // create dummy template
        if (!file.exists()) {
          templates = new DSearchTemplates();
          templates.setIndexName(indexName);

          saveTemplateToDisk(templates);
        } else {
          // load template file

          try {
            final Document doc = XMLUtils.parse(file, new XMLUtilsConfig());
            templates = DSearchTemplatesCodec.decode(doc.getDocumentElement());
          } catch (final XMLUtilsException e) {
            log.error(e.getMessage());
            throw new TemplateException("unable parse templates file [" + file.getName() + "]", e);
          } catch (final DSearchTemplatesException e) {
            log.error("unable to load templates file [" + file.getName() + "]", e);
            throw new TemplateException("unable decode templates file [" + file.getName() + "]", e);
          }
        }
        // register file
        s_searchTemplates.put(indexName, templates);
        return templates;
      }
    }
  }

  /**
   * Takes the given FieldTemplate (a) caches it in .fieldTemplates and saves it to the harddrive.
   * 
   * @param templates
   *          Field templates for a index.
   * @throws TemplateException
   *           Unable to set field templates.
   */
  public static void setFieldTemplates(DFieldTemplates templates) throws TemplateException {
    synchronized (s_fieldTemplates) {

      // cache
      if (s_fieldTemplates.containsKey(templates.getIndexName())) {
        s_fieldTemplates.put(templates.getIndexName(), templates);
      }

      saveTemplateToDisk(templates);

    }
  }

  /**
   * Store templates persistent on disk.
   * 
   * @param templates
   *          Field templates to store.
   * @throws TemplateException
   *           Unable to store field templates.
   */
  private static void saveTemplateToDisk(DFieldTemplates templates) throws TemplateException {
    final Log log = LogFactory.getLog(TemplateRegistryController.class);
    // write template to disk
    final File file = getFieldTemplateFile(templates.getIndexName());
    try {
      final Document doc = DFieldTemplatesCodec.encode(templates);
      XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", file);
    } catch (final Exception e) {
      s_fieldTemplates.remove(templates.getIndexName());
      log.error("unable to store templates [" + templates.getIndexName() + "] in file [" + file.getName() + "]", e);
      throw new TemplateException("unable to store templates [" + templates.getIndexName() + "]", e);
    }
  }

  /**
   * Returns the template for the given index. These attempts are made in this order: 1. cache; 2. file 3. dummy
   * 
   * @param indexName
   *          Index name.
   * @return Field templates.
   * @throws TemplateException
   *           Unable to resolve field templates.
   */
  public static DFieldTemplates getFieldTemplates(String indexName) throws TemplateException {
    final Log log = LogFactory.getLog(TemplateRegistryController.class);

    checkIndexExistance(indexName);

    synchronized (s_fieldTemplates) {
      // try to get from cache
      if (s_fieldTemplates.containsKey(indexName)) {
        return s_fieldTemplates.get(indexName);
      } else {
        DFieldTemplates templates = null;
        final File file = getFieldTemplateFile(indexName);

        // create dummy (empty) template
        if (!file.exists()) {
          templates = new DFieldTemplates();
          templates.setIndexName(indexName);

          // create templatefile
          saveTemplateToDisk(templates);
        } else {
          // load template file
          try {
            final Document doc = XMLUtils.parse(file, new XMLUtilsConfig());
            templates = DFieldTemplatesCodec.decode(doc.getDocumentElement());
          } catch (final XMLUtilsException e) {
            log.error(e.getMessage());
            throw new TemplateException("unable to parse templates file [" + file.getName() + "]", e);
          } catch (final DFieldTemplatesException e) {
            log.error("unable to load templates file [" + file.getName() + "]: " + e.getMessage(), e);
            throw new TemplateException("unable to decode templates file [" + file.getName() + "]", e);
          }
        }
        // register file
        s_fieldTemplates.put(indexName, templates);
        return templates;
      }
    }
  }

  /**
   * Checks existance of given index and just returns if it does, otherwise throes exception.
   * 
   * @param indexName
   *          Index name.
   * @throws TemplateException
   *           Index does not exist.
   */
  private static void checkIndexExistance(String indexName) throws TemplateException {
    boolean indexExists = false;

    try {
      indexExists = DataDictionaryController.hasIndex(indexName);
    } catch (final Throwable e) {
      throw new TemplateException("index does not exist [" + indexName + "]", e);
    }

    if (!indexExists) {
      throw new TemplateException("index does not exist [" + indexName + "]");
    }
  }

  /**
   * Removes for the given index the given template(s) from cache and disk. The method is optimistic, ie it doesnt check
   * for existance of the given index nor the successful removal from cache and disk and consequently doesnt throw any
   * exception.
   * 
   * @param indexName
   *          Index name.
   * @param doSearchTemplates
   *          Whether search templates should be deleted.
   * @param doFieldTemplates
   *          Whether field templates should be deleted.
   */
  public static void removeTemplates(String indexName, boolean doSearchTemplates, boolean doFieldTemplates) {

    if (doSearchTemplates) {
      final File file = getSearchTemplateFile(indexName);
      file.delete();

      synchronized (s_searchTemplates) {
        s_searchTemplates.remove(indexName);
      }
    }

    if (doFieldTemplates) {
      final File file = getFieldTemplateFile(indexName);
      file.delete();

      synchronized (s_fieldTemplates) {
        s_fieldTemplates.remove(indexName);
      }
    }

  }

}
