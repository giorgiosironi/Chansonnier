
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.jdbc.messages;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *           Process Specification
 *         
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
 *       &lt;sequence>
 *         &lt;element name="Selections">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Grouping" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Stepping" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *                             &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Database">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="Connection" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;pattern value="jdbc:oracle:thin:@[\w\.\-]+:\d+:\w+"/>
 *                       &lt;pattern value="jdbc:microsoft:sqlserver://[\w\.\-]+:\d+(;(DatabaseName|HostProcess|NetAddress|Password|PortNumber|ProgramName|SelectMethod|SendStringParametersAsUnicode|ServerName|User)=[\w\i]+)*"/>
 *                       &lt;pattern value="jdbc:sqlserver://[\w\.\-]+:\d+(;(DatabaseName|HostProcess|NetAddress|Password|PortNumber|ProgramName|SelectMethod|SendStringParametersAsUnicode|ServerName|User)=[\w\i]+)*"/>
 *                       &lt;pattern value="jdbc:odbc:[\w\.\-]+"/>
 *                       &lt;pattern value="jdbc:derby:[\w\.\-\\:/]+"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="User" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="FetchSize" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="JdbcDriver" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "selections",
    "database"
})
public class Process
    extends OriginalProcess
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Selections", required = true)
    protected Process.Selections selections;
    @XmlElement(name = "Database", required = true)
    protected Process.Database database;

    /**
     * Gets the value of the selections property.
     * 
     * @return
     *     possible object is
     *     {@link Process.Selections }
     *     
     */
    public Process.Selections getSelections() {
        return selections;
    }

    /**
     * Sets the value of the selections property.
     * 
     * @param value
     *     allowed object is
     *     {@link Process.Selections }
     *     
     */
    public void setSelections(Process.Selections value) {
        this.selections = value;
    }

    /**
     * Gets the value of the database property.
     * 
     * @return
     *     possible object is
     *     {@link Process.Database }
     *     
     */
    public Process.Database getDatabase() {
        return database;
    }

    /**
     * Sets the value of the database property.
     * 
     * @param value
     *     allowed object is
     *     {@link Process.Database }
     *     
     */
    public void setDatabase(Process.Database value) {
        this.database = value;
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
     *       &lt;attribute name="Connection" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;pattern value="jdbc:oracle:thin:@[\w\.\-]+:\d+:\w+"/>
     *             &lt;pattern value="jdbc:microsoft:sqlserver://[\w\.\-]+:\d+(;(DatabaseName|HostProcess|NetAddress|Password|PortNumber|ProgramName|SelectMethod|SendStringParametersAsUnicode|ServerName|User)=[\w\i]+)*"/>
     *             &lt;pattern value="jdbc:sqlserver://[\w\.\-]+:\d+(;(DatabaseName|HostProcess|NetAddress|Password|PortNumber|ProgramName|SelectMethod|SendStringParametersAsUnicode|ServerName|User)=[\w\i]+)*"/>
     *             &lt;pattern value="jdbc:odbc:[\w\.\-]+"/>
     *             &lt;pattern value="jdbc:derby:[\w\.\-\\:/]+"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="User" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="FetchSize" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="JdbcDriver" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Database
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlAttribute(name = "Connection", required = true)
        protected String connection;
        @XmlAttribute(name = "User", required = true)
        protected String user;
        @XmlAttribute(name = "Password", required = true)
        protected String password;
        @XmlAttribute(name = "FetchSize", required = true)
        protected int fetchSize;
        @XmlAttribute(name = "JdbcDriver")
        protected String jdbcDriver;

        /**
         * Gets the value of the connection property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getConnection() {
            return connection;
        }

        /**
         * Sets the value of the connection property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setConnection(String value) {
            this.connection = value;
        }

        /**
         * Gets the value of the user property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUser() {
            return user;
        }

        /**
         * Sets the value of the user property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUser(String value) {
            this.user = value;
        }

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * Gets the value of the fetchSize property.
         * 
         */
        public int getFetchSize() {
            return fetchSize;
        }

        /**
         * Sets the value of the fetchSize property.
         * 
         */
        public void setFetchSize(int value) {
            this.fetchSize = value;
        }

        /**
         * Gets the value of the jdbcDriver property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJdbcDriver() {
            return jdbcDriver;
        }

        /**
         * Sets the value of the jdbcDriver property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJdbcDriver(String value) {
            this.jdbcDriver = value;
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
     *       &lt;sequence>
     *         &lt;element name="Grouping" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Stepping" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
     *                   &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "grouping",
        "sql"
    })
    public static class Selections
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Grouping")
        protected Process.Selections.Grouping grouping;
        @XmlElement(name = "SQL", required = true)
        protected String sql;

        /**
         * Gets the value of the grouping property.
         * 
         * @return
         *     possible object is
         *     {@link Process.Selections.Grouping }
         *     
         */
        public Process.Selections.Grouping getGrouping() {
            return grouping;
        }

        /**
         * Sets the value of the grouping property.
         * 
         * @param value
         *     allowed object is
         *     {@link Process.Selections.Grouping }
         *     
         */
        public void setGrouping(Process.Selections.Grouping value) {
            this.grouping = value;
        }

        /**
         * Gets the value of the sql property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSQL() {
            return sql;
        }

        /**
         * Sets the value of the sql property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSQL(String value) {
            this.sql = value;
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
         *         &lt;element name="Stepping" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
         *         &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "stepping",
            "sql"
        })
        public static class Grouping
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElement(name = "Stepping", required = true)
            @XmlSchemaType(name = "positiveInteger")
            protected BigInteger stepping;
            @XmlElement(name = "SQL", required = true)
            protected String sql;

            /**
             * Gets the value of the stepping property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getStepping() {
                return stepping;
            }

            /**
             * Sets the value of the stepping property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setStepping(BigInteger value) {
                this.stepping = value;
            }

            /**
             * Gets the value of the sql property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSQL() {
                return sql;
            }

            /**
             * Sets the value of the sql property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSQL(String value) {
                this.sql = value;
            }

        }

    }

}

// CHECKSTYLE:ON
