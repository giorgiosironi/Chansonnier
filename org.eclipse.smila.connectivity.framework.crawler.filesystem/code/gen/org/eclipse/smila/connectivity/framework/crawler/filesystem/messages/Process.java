/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.filesystem.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * Process Specification
 * 
 * <p>Java class for Process complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Process">
 *   &lt;complexContent>
 *     &lt;extension base="{}Process">
 *       &lt;redefine>
 *         &lt;complexType name="Process">
 *           &lt;complexContent>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *             &lt;/restriction>
 *           &lt;/complexContent>
 *         &lt;/complexType>
 *       &lt;/redefine>
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="BaseDir" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Filter">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Include" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="DateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                           &lt;attribute name="DateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Exclude" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="CaseSensitive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                 &lt;attribute name="Recursive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Process", propOrder = {
    "baseDirAndFilter"
})
public class Process
    extends OriginalProcess
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElements({
        @XmlElement(name = "Filter", required = true, type = Process.Filter.class),
        @XmlElement(name = "BaseDir", required = true, type = String.class)
    })
    protected List<Object> baseDirAndFilter;

    /**
     * Gets the value of the baseDirAndFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the baseDirAndFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBaseDirAndFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Process.Filter }
     * {@link String }
     * 
     * 
     */
    public List<Object> getBaseDirAndFilter() {
        if (baseDirAndFilter == null) {
            baseDirAndFilter = new ArrayList<Object>();
        }
        return this.baseDirAndFilter;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Include" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="DateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                 &lt;attribute name="DateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Exclude" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="CaseSensitive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *       &lt;attribute name="Recursive" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "include",
        "exclude"
    })
    public static class Filter implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Include")
        protected List<Process.Filter.Include> include;
        @XmlElement(name = "Exclude")
        protected List<Process.Filter.Exclude> exclude;
        @XmlAttribute(name = "CaseSensitive")
        protected Boolean caseSensitive;
        @XmlAttribute(name = "Recursive")
        protected Boolean recursive;

        /**
         * Gets the value of the include property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the include property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInclude().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Process.Filter.Include }
         * 
         * 
         */
        public List<Process.Filter.Include> getInclude() {
            if (include == null) {
                include = new ArrayList<Process.Filter.Include>();
            }
            return this.include;
        }

        /**
         * Gets the value of the exclude property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the exclude property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExclude().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Process.Filter.Exclude }
         * 
         * 
         */
        public List<Process.Filter.Exclude> getExclude() {
            if (exclude == null) {
                exclude = new ArrayList<Process.Filter.Exclude>();
            }
            return this.exclude;
        }

        /**
         * Gets the value of the caseSensitive property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isCaseSensitive() {
            if (caseSensitive == null) {
                return false;
            } else {
                return caseSensitive;
            }
        }

        /**
         * Sets the value of the caseSensitive property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setCaseSensitive(Boolean value) {
            this.caseSensitive = value;
        }

        /**
         * Gets the value of the recursive property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isRecursive() {
            if (recursive == null) {
                return true;
            } else {
                return recursive;
            }
        }

        /**
         * Sets the value of the recursive property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setRecursive(Boolean value) {
            this.recursive = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Exclude
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Name", required = true)
            protected String name;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="DateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *       &lt;attribute name="DateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Include
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Name", required = true)
            protected String name;
            @XmlAttribute(name = "DateFrom")
            @XmlJavaTypeAdapter(Adapter1 .class)
            @XmlSchemaType(name = "dateTime")
            protected Date dateFrom;
            @XmlAttribute(name = "DateTo")
            @XmlJavaTypeAdapter(Adapter1 .class)
            @XmlSchemaType(name = "dateTime")
            protected Date dateTo;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the dateFrom property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public Date getDateFrom() {
                return dateFrom;
            }

            /**
             * Sets the value of the dateFrom property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDateFrom(Date value) {
                this.dateFrom = value;
            }

            /**
             * Gets the value of the dateTo property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public Date getDateTo() {
                return dateTo;
            }

            /**
             * Sets the value of the dateTo property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDateTo(Date value) {
                this.dateTo = value;
            }

        }

    }

}

// CHECKSTYLE:ON
