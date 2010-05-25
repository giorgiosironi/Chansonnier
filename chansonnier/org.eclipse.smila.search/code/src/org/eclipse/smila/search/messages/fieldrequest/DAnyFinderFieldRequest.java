/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.messages.fieldrequest;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class DAnyFinderFieldRequest {

  /**
   * Index name.
   */
  private String _indexName;

  /**
   * Fields.
   */
  private java.util.List<DField> _fields = new java.util.ArrayList<DField>();

  /**
   * 
   */
  public DAnyFinderFieldRequest() {
    super();
  }

  /**
   * @return Returns the indexName.
   */
  public String getIndexName() {
    return _indexName;
  }

  /**
   * @param indexName
   *          The indexName to set.
   */
  public void setIndexName(String indexName) {
    _indexName = indexName;
  }

  /**
   * Add field.
   * 
   * @param field
   *          Field to add.
   * @return Added field.
   */
  public DField addField(DField field) {
    _fields.add(field);
    return field;
  }

  /**
   * Add field at index position.
   * 
   * @param index
   *          Index.
   * @param field
   *          Field to add.
   * @return Added field.
   */
  public DField addField(int index, DField field) {
    _fields.add(index, field);
    return field;
  }

  /**
   * Remove field.
   * 
   * @param field
   *          Field to remove.
   * @return Removed field.
   */
  public DField removeField(DField field) {
    _fields.remove(field);
    return field;
  }

  /**
   * Remove field at index position.
   * 
   * @param index
   *          Index to remove field at.
   * @return Removed field.
   */
  public DField removeField(int index) {
    final DField field = _fields.get(index);
    _fields.remove(index);
    return field;
  }

  /**
   * Get field at index position.
   * 
   * @param index
   *          Index position.
   * @return Field.
   */
  public DField getFieldAt(int index) {
    return _fields.get(index);
  }

  /**
   * Set field at index position.
   * 
   * @param index
   *          Index position.
   * @param field
   *          Field to set.
   * @return Setted field.
   */
  public DField setFieldAt(int index, DField field) {
    _fields.set(index, field);
    return field;
  }

  /**
   * Return list as iterator.
   * 
   * @return Iterator.
   */
  public java.util.Iterator<DField> getFields() {
    return _fields.iterator();
  }

  /**
   * Get amount of field objects in list.
   * 
   * @return Amount of field objects.
   */
  public int getFieldCount() {
    return _fields.size();
  }

  /**
   * Remove all field objects from list.
   */
  public void clearFields() {
    _fields.clear();
  }

}
