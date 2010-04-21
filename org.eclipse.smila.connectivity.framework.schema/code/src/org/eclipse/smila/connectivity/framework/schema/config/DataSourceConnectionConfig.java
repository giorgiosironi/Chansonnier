/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IProcess;

/**
 * Java class for JAXB DataSourceConnectionConfig.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "_dataSourceID", "_schemaID", "_dataConnectionID", "_recordBuffer",
  "_deltaIndexing", "_compoundHandling", "_attributes", "_process" })
@XmlRootElement(name = "DataSourceConnectionConfig")
public class DataSourceConnectionConfig {

  /** The _data source id. */
  @XmlElement(name = "DataSourceID", required = true)
  protected String _dataSourceID;

  /** The _schema id. */
  @XmlJavaTypeAdapter(SchemaIdAdapter.class)
  @XmlElement(name = "SchemaID", required = true)
  protected String _schemaID;

  /** The _data connection id. */
  @XmlElement(name = "DataConnectionID", required = true)
  protected DataConnectionID _dataConnectionID;

  /** The RecordBuffer. */
  @XmlElement(name = "RecordBuffer", required = false)
  protected DataSourceConnectionConfig.RecordBuffer _recordBuffer;

  /** The _deltaIndexing. */
  @XmlElement(name = "DeltaIndexing", required = true)
  protected DeltaIndexingType _deltaIndexing;

  /** The _compound handling. */
  @XmlElement(name = "CompoundHandling", required = false)
  protected CompoundHandling _compoundHandling;

  /** The _attributes. */
  @XmlElement(name = "Attributes", required = true)
  protected DataSourceConnectionConfig.Attributes _attributes;

  /** The _process. */
  @XmlElement(name = "Process", required = true)
  protected IProcess _process;

  /**
   * Gets the value of the dataSourceID.
   * 
   * @return possible object is {@link String }
   */
  public String getDataSourceID() {
    return _dataSourceID;
  }

  /**
   * Sets the value of the dataSourceID property.
   * 
   * @param value
   *          allowed object is {@link String }
   */
  public void setDataSourceID(final String value) {
    this._dataSourceID = value;
  }

  /**
   * Gets the schema id.
   * 
   * @return schema ID
   */
  public String getSchemaID() {
    return this._schemaID;
  }

  /**
   * sets schema ID.
   * 
   * @param value
   *          the value
   */
  public void setSchemaID(final String value) {
    this._schemaID = value;
  }

  /**
   * Gets the value of the dataConnectionID property.
   * 
   * @return possible object is {@link DataSourceConnectionConfig.DataConnectionID }
   */
  public DataConnectionID getDataConnectionID() {
    return _dataConnectionID;
  }

  /**
   * Sets the value of the dataConnectionID property.
   * 
   * @param value
   *          allowed object is {@link DataSourceConnectionConfig.DataConnectionID }
   */
  public void setDataConnectionID(final DataConnectionID value) {
    this._dataConnectionID = value;
  }

  /**
   * Gets the value of the RecordBuffer property.
   * 
   * @return possible object is {@link DataSourceConnectionConfig.RecordBuffer }
   */
  public DataSourceConnectionConfig.RecordBuffer getRecordBuffer() {
    if (_recordBuffer != null) {
      return _recordBuffer;
    } else {
      return RecordBuffer.getDefault();
    }
  }

  /**
   * Sets the value of the RecordBuffer property.
   * 
   * @param value
   *          allowed object is {@link DataSourceConnectionConfig.RecordBuffer }
   */
  public void setRecordBuffer(final DataSourceConnectionConfig.RecordBuffer value) {
    this._recordBuffer = value;
  }

  /**
   * Gets the value of the delta indexing property.
   * 
   * @return possible object is {@link DeltaIndexing }
   */
  public DeltaIndexingType getDeltaIndexing() {
    return _deltaIndexing;
  }

  /**
   * Sets the value of the delta indexing property.
   * 
   * @param value
   *          allowed object is {@link DeltaIndexing }
   */
  public void setDeltaIndexing(final DeltaIndexingType value) {
    this._deltaIndexing = value;
  }

  /**
   * Gets the value of the compoundHandling property.
   * 
   * @return possible object is {@link CompoundHandling }
   */
  public CompoundHandling getCompoundHandling() {
    return _compoundHandling;
  }

  /**
   * Sets the value of the compoundHandling property.
   * 
   * @param value
   *          allowed object is {@link CompoundHandling }
   */
  public void setCompoundHandling(final CompoundHandling value) {
    this._compoundHandling = value;
  }

  /**
   * Gets the value of the attributes property.
   * 
   * @return possible object is {@link DataSourceConnectionConfig.Attributes }
   */
  public DataSourceConnectionConfig.Attributes getAttributes() {
    return _attributes;
  }

  /**
   * Sets the value of the attributes property.
   * 
   * @param value
   *          allowed object is {@link DataSourceConnectionConfig.Attributes }
   */
  public void setAttributes(final DataSourceConnectionConfig.Attributes value) {
    this._attributes = value;
  }

  /**
   * Gets the value of the process property.
   * 
   * @return possible object is {@link IProcess }
   */
  public IProcess getProcess() {
    return _process;
  }

  /**
   * Sets the value of the process property.
   * 
   * @param value
   *          allowed object is {@link IProcess }
   */
  public void setProcess(final IProcess value) {
    this._process = value;
  }

  /**
   * The following schema fragment specifies the expected content contained within this class.
   * 
   * <pre>
   * &lt;complexType&gt;
   * &lt;complexContent&gt;
   * &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
   * &lt;sequence maxOccurs=&quot;unbounded&quot;&gt;
   * &lt;element name=&quot;Attribute&quot; type=&quot;{}Attribute&quot;/&gt;
   * &lt;/sequence&gt;
   * &lt;/restriction&gt;
   * &lt;/complexContent&gt;
   * &lt;/complexType&gt;
   * </pre>
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = { "_attribute" })
  public static class Attributes {

    /** The attribute. */
    @XmlElement(name = "Attribute", required = true)
    protected List<IAttribute> _attribute;

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Attribute }
     * 
     * @return the attribute
     */
    public List<IAttribute> getAttribute() {
      if (_attribute == null) {
        _attribute = new ArrayList<IAttribute>();
      }
      return this._attribute;
    }

  }

  /**
   * The Class DataConnectionIDOriginal.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder = { "_agent", "_crawler" })
  static final class DataConnectionIDOriginal {

    /** The agent. */
    @XmlElement(name = "Agent")
    private String _agent;

    /** The crawler. */
    @XmlElement(name = "Crawler")
    private String _crawler;

    /**
     * Gets the value of the agent property.
     * 
     * @return possible object is {@link String }
     */
    public String getAgent() {
      return _agent;
    }

    /**
     * Sets the value of the agent property.
     * 
     * @param value
     *          allowed object is {@link String }
     */
    public void setAgent(final String value) {
      this._agent = value;
    }

    /**
     * Gets the value of the crawler property.
     * 
     * @return possible object is {@link String }
     */
    public String getCrawler() {
      return _crawler;
    }

    /**
     * Sets the value of the crawler property.
     * 
     * @param value
     *          allowed object is {@link String }
     */
    public void setCrawler(final String value) {
      this._crawler = value;
    }
  }

  /**
   * Java class for JAXB RecordBuffer.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "RecordBuffer", propOrder = { "_size", "_flushInterval" })
  public static class RecordBuffer implements Serializable {

    /**
     * Constant for the default buffer size (20).
     */
    private static final int DEFAULT_SIZE = 20;

    /**
     * Constant for the default flush interval (1000 ms).
     */
    private static final long DEFAULT_FLUSH_INTRVAL = 1000;

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default instance.
     */
    private static RecordBuffer s_defaultInstance;

    /**
     * The buffer size.
     */
    @XmlAttribute(name = "Size", required = false)
    protected int _size;

    /**
     * The flush interval in milli seconds.
     */
    @XmlAttribute(name = "FlushInterval", required = false)
    protected long _flushInterval;

    /**
     * Returns a RecordBuffer instance with default values.
     * 
     * @return a RecordBuffer
     */
    public static RecordBuffer getDefault() {
      if (s_defaultInstance == null) {
        s_defaultInstance = new RecordBuffer();
        s_defaultInstance.setSize(DEFAULT_SIZE);
        s_defaultInstance.setFlushInterval(DEFAULT_FLUSH_INTRVAL);
      }
      return s_defaultInstance;
    }

    /**
     * Gets the value of the Size property.
     * 
     * @return possible object is {@link int }
     * 
     */
    public int getSize() {
      return _size;
    }

    /**
     * Sets the value of the Size property.
     * 
     * @param value
     *          allowed object is {@link int }
     * 
     */
    public void setSize(int value) {
      _size = value;
    }

    /**
     * Gets the value of the FlushInterval property.
     * 
     * @return possible object is {@link int }
     * 
     */
    public long getFlushInterval() {
      return _flushInterval;
    }

    /**
     * Sets the value of the FlushInterval property.
     * 
     * @param value
     *          allowed object is {@link long }
     * 
     */
    public void setFlushInterval(long value) {
      _flushInterval = value;
    }
  }

}
