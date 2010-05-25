
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JmxClientConfigType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JmxClientConfigType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="connection" type="{http://www.eclipse.org/smila/management/jmx/client}ConnectionConfigType" maxOccurs="unbounded"/>
 *         &lt;element name="cmd" type="{http://www.eclipse.org/smila/management/jmx/client}CmdConfigType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sample" type="{http://www.eclipse.org/smila/management/jmx/client}SampleConfigType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JmxClientConfigType", propOrder = {
    "connection",
    "cmd",
    "sample"
})
public class JmxClientConfigType {

    @XmlElement(required = true)
    protected List<ConnectionConfigType> connection;
    protected List<CmdConfigType> cmd;
    protected List<SampleConfigType> sample;

    /**
     * Gets the value of the connection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConnectionConfigType }
     * 
     * 
     */
    public List<ConnectionConfigType> getConnection() {
        if (connection == null) {
            connection = new ArrayList<ConnectionConfigType>();
        }
        return this.connection;
    }

    /**
     * Gets the value of the cmd property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cmd property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCmd().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CmdConfigType }
     * 
     * 
     */
    public List<CmdConfigType> getCmd() {
        if (cmd == null) {
            cmd = new ArrayList<CmdConfigType>();
        }
        return this.cmd;
    }

    /**
     * Gets the value of the sample property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sample property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSample().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SampleConfigType }
     * 
     * 
     */
    public List<SampleConfigType> getSample() {
        if (sample == null) {
            sample = new ArrayList<SampleConfigType>();
        }
        return this.sample;
    }

}

// CHECKSTYLE:ON
