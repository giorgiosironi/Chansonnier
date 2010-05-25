/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.smila.search.utils.search.DDateField;
import org.eclipse.smila.search.utils.search.DField;
import org.eclipse.smila.search.utils.search.DNumberField;
import org.eclipse.smila.search.utils.search.DTextField;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

public class DIndexStructure extends org.eclipse.smila.search.utils.indexstructure.DIndexStructure {
  private final Hashtable<Long, org.eclipse.smila.search.utils.indexstructure.DIndexField> _fields =
    new Hashtable<Long, org.eclipse.smila.search.utils.indexstructure.DIndexField>(0);

  /** @link dependency */
  /* #DIndexField lnkDIndexField; */
  private DAnalyzer _analyzer;

  @Override
  public org.eclipse.smila.search.utils.indexstructure.DIndexField addField(
    org.eclipse.smila.search.utils.indexstructure.DIndexField dIndexField) {
    _fields.put(new Long(dIndexField.getFieldNo()), dIndexField);
    return dIndexField;
  }

  public void removeField(DIndexField dIndexField) {
    _fields.remove(new Long(dIndexField.getFieldNo()));
  }

  @Override
  public org.eclipse.smila.search.utils.indexstructure.DIndexField removeField(int fieldNo) {
    return this._fields.remove(new Long(fieldNo));
  }

  @Override
  public Iterator<org.eclipse.smila.search.utils.indexstructure.DIndexField> getFields() {
    return this._fields.values().iterator();
  }

  @Override
  public org.eclipse.smila.search.utils.indexstructure.DIndexField getField(int fieldNo) {
    return _fields.get(new Long(fieldNo));
  }

  @Override
  public int getFieldCount() {
    return this._fields.size();
  }

  public DAnalyzer getAnalyzer() {
    return this._analyzer;
  }

  public void setAnalyzer(DAnalyzer analyzer) {
    this._analyzer = analyzer;
  }

  @Override
  public String toString() {
    try {
      final Element el = DIndexStructureCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

  /**
   * Checks equality of two IndexStructure objects, including their names. This will return <code>true</code> only if
   * all attributes and child elements are equal, including the <code>name</code> attribute.
   * <p>
   * <b>WARNING:</b>This method will return <code>false</code> when comparing two indexes with different names, even
   * if they have the same structure. In such cases, use <code>equalsStructure(Object)</code>.
   * </p>
   * 
   * @param obj -
   * @return boolean
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DIndexStructure)) {
      return false;
    }

    final DIndexStructure o = (DIndexStructure) obj;
    if (!o.getName().equals(getName())) {
      return false;
    }
    final DAnalyzer analA = getAnalyzer();
    final DAnalyzer analB = o.getAnalyzer();
    if ((analA == null && analB != null) || (analA != null && analB == null)
      || (analA != null && !analB.equals(analA))) {
      return false;
    }
    if (o.getFieldCount() != getFieldCount()) {
      return false;
    }
    for (int i = 0; i < getFieldCount(); i++) {
      if (!o.getField(i).equals(getField(i))) {
        return false;
      }
    }
    return true;
  } // End Method equals

  /**
   * Checks structural equality of two IndexStructure objects. This will return <code>true</code> if its child
   * elements (Analyzer and IndexFields) are equal. This method does not consider the <code>name</code> attribute.
   * Thus, comparison of two indexes with the same structure will return true, even if their names differ. If exact
   * checking, including index name, is reuired, use <code>equals(Object)</code> instead. {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.utils.indexstructure.DIndexStructure#equalsStructure(org.eclipse.smila.search.utils.indexstructure.DIndexStructure,
   *      boolean)
   * @param indexStructure -
   */
  @Override
  public boolean equalsStructure(org.eclipse.smila.search.utils.indexstructure.DIndexStructure indexStructure,
    boolean throwException) {
    if (!(indexStructure instanceof DIndexStructure)) {
      return false;
    }
    final DIndexStructure dIS = (DIndexStructure) indexStructure;
    final DAnalyzer analA = getAnalyzer();
    final DAnalyzer analB = dIS.getAnalyzer();
    if ((analA == null && analB != null) || (analA != null && analB == null)
      || (analA != null && !analB.equals(analA))) {
      return false;
    }
    if (dIS.getFieldCount() != getFieldCount()) {
      return false;
    }
    for (int i = 0; i < getFieldCount(); i++) {
      if (!dIS.getField(i).equals(getField(i))) {
        return false;
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.utils.indexstructure.DIndexStructure#hasField(int)
   */
  @Override
  public boolean hasField(int fieldNo) {
    return _fields.containsKey(new Long(fieldNo));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.utils.indexstructure.DIndexStructure#doesFieldTypeMatch(org.eclipse.smila.search.utils.search.DField)
   */
  @Override
  public boolean doesFieldTypeMatch(DField field) {
    final int fieldNo = field.getFieldNo();

    if (hasField(fieldNo)) {
      final DIndexField dIF = (DIndexField) getField(fieldNo);
      if (field instanceof DTextField) {
        if (dIF.getType().equals("Text")) {
          return true;
        }
      } else if (field instanceof DNumberField) {
        if (dIF.getType().equals("Number")) {
          return true;
        }
      } else if (field instanceof DDateField) {
        if (dIF.getType().equals("Date")) {
          return true;
        }
      }
    } else {
      return false;
    }

    return false;
  } // End Method equals

} // End class def.
