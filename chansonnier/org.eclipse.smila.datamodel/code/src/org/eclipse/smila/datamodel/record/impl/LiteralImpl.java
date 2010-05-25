/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.LiteralFormatHelper;

/**
 * Default implementation of SMILA Literals.
 * 
 * @author jschumacher
 * 
 */
public class LiteralImpl extends AttributeValueImpl implements Literal {
  /**
   * guess what ...
   */
  private static final long serialVersionUID = 1L;

  /**
   * the literal value object.
   */
  private Object _value;

  /**
   * datatype of this object.
   */
  private DataType _dataType;

  /**
   * create new empty literal.
   */
  public LiteralImpl() {
    // nothing to do.
  }

  /**
   * create new literal from value.
   * 
   * @param value
   *          value object
   * @throws InvalidTypeException
   *           if value object cannot be used by LiteralImpl
   */
  public LiteralImpl(Object value) throws InvalidTypeException {
    setValue(value);
  }

  /**
   * convert value to LiteralImpl if it is of another implementation.
   * 
   * @param someLiteral
   *          literal in any implementation
   * @return same literal in LiteralImpl.
   */
  public static LiteralImpl ensureImpl(Literal someLiteral) {
    if (someLiteral instanceof LiteralImpl) {
      return (LiteralImpl) someLiteral;
    } else {
      final LiteralImpl literalImpl = new LiteralImpl();
      final Object value = someLiteral.getValue();
      if (value != null) {
        try {
          literalImpl.setValue(value);
        } catch (InvalidTypeException e) {
          throw new IllegalArgumentException("Literal of class " + someLiteral.getClass().getName()
            + " returned illegal value object of class " + value.getClass(), e);
        }
      }
      return literalImpl;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getDataType()
   */
  public DataType getDataType() {
    return _dataType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getBoolValue()
   */
  public Boolean getBoolValue() {
    if (_dataType == DataType.BOOL) {
      return (Boolean) _value;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getDateTimeValue()
   */
  public Date getDateTimeValue() {
    if (_dataType == DataType.DATETIME) {
      return (Date) _value;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getDateValue()
   */
  public Date getDateValue() {
    if (_dataType == DataType.DATE) {
      return (Date) _value;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getFpValue()
   */
  public Double getFpValue() {
    if (_dataType == DataType.FP) {
      return (Double) _value;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getIntValue()
   */
  public Long getIntValue() {
    if (_dataType == DataType.INT) {
      return (Long) _value;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getStringValue()
   */
  public String getStringValue() {
    if (_value == null) {
      return null;
    } else {
      switch (_dataType) {
        case STRING:
          return (String) _value;
        case DATE:
          return LiteralFormatHelper.INSTANCE.formatDate((Date) _value);
        case TIME:
          return LiteralFormatHelper.INSTANCE.formatTime((Date) _value);
        case DATETIME:
          return LiteralFormatHelper.INSTANCE.formatDateTime((Date) _value);
        default:
          return _value.toString();
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getTimeValue()
   */
  public Date getTimeValue() {
    if (_dataType == DataType.TIME) {
      return (Date) _value;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#getValue()
   */
  public Object getValue() {
    return _value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setBoolValue(java.lang.Boolean)
   */
  public void setBoolValue(Boolean value) {
    _value = value;
    _dataType = DataType.BOOL;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setDateTimeValue(java.util.Date)
   */
  public void setDateTimeValue(Date value) {
    _value = value;
    _dataType = DataType.DATETIME;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setDateValue(java.util.Date)
   */
  public void setDateValue(Date value) {
    _value = value;
    _dataType = DataType.DATE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setFpValue(java.lang.Double)
   */
  public void setFpValue(Double value) {
    _value = value;
    _dataType = DataType.FP;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setIntValue(java.lang.Long)
   */
  public void setIntValue(Long value) {
    _value = value;
    _dataType = DataType.INT;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setIntValue(java.lang.Integer)
   */
  public void setIntValue(Integer value) {
    _value = value.longValue();
    _dataType = DataType.INT;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setStringValue(java.lang.String)
   */
  public void setStringValue(String value) {
    _value = value;
    _dataType = DataType.STRING;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setTimeValue(java.util.Date)
   */
  public void setTimeValue(Date value) {
    _value = value;
    _dataType = DataType.TIME;
  }

  /**
   * {@inheritDoc}
   * 
   * Apart from the supported classes this implementation also automatically converts Floats and BigDecimals to Doubles
   * and all other subclasses of Number to Long.
   * 
   * @see org.eclipse.smila.datamodel.record.Literal#setValue(java.lang.Object)
   */
  public void setValue(Object value) throws InvalidTypeException {
    if (value instanceof String) {
      setStringValue((String) value);
    } else if (value instanceof Double) {
      setFpValue((Double) value);
    } else if (value instanceof Float | value instanceof BigDecimal) {
      setFpValue(((Number) value).doubleValue());
    } else if (value instanceof Long) {
      setIntValue((Long) value);
    } else if (value instanceof Number) {
      setIntValue(((Number) value).longValue());
    } else if (value instanceof Boolean) {
      setBoolValue((Boolean) value);
    } else if (value instanceof Date) {
      setDateTimeValue((Date) value);
    } else {
      throw new InvalidTypeException("Cannot use instance of " + value.getClass() + " as literal value.");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (_value == null) {
      return "(void)";
    }
    return _value.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * Literals are equals when their values and datatypes are equal.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   * 
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof LiteralImpl) {
      final LiteralImpl otherLit = (LiteralImpl) obj;
      if (_value == null) {
        return otherLit._value == null;
      } else {
        return _dataType == otherLit._dataType && _value.equals(otherLit._value);
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    if (_value == null) {
      return 0;
    } else {
      return _dataType.hashCode() + _value.hashCode();
    }
  }
}
