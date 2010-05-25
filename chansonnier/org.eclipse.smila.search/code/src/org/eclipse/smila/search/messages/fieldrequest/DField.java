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
 */
public class DField {

  /**
   * Field.
   */
  private org.eclipse.smila.search.utils.search.DField _field;

  /**
   * Reference ID.
   */
  private String _referenceID;

  /**
   * 
   */
  public DField() {
    super();
  }

  /**
   * @param referenceID
   *          Reference ID.
   */
  public DField(String referenceID) {
    super();
    setReferenceID(referenceID);
  }

  /**
   * @param referenceID
   *          Reference ID.
   * @param field
   *          Field.
   */
  public DField(String referenceID, org.eclipse.smila.search.utils.search.DField field) {
    super();
    setReferenceID(referenceID);
    setField(field);
  }

  /**
   * @return Returns the field.
   */
  public org.eclipse.smila.search.utils.search.DField getField() {
    return _field;
  }

  /**
   * @param field
   *          The field to set.
   */
  public void setField(org.eclipse.smila.search.utils.search.DField field) {
    _field = field;
  }

  /**
   * @return Returns the referenceID.
   */
  public String getReferenceID() {
    return _referenceID;
  }

  /**
   * @param referenceID
   *          The referenceID to set.
   */
  public void setReferenceID(String referenceID) {
    this._referenceID = referenceID;
  }

}
