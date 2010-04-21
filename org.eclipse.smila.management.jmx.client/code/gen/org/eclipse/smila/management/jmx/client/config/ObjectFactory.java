
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.eclipse.smila.management.jmx.client.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Jmxclient_QNAME = new QName("http://www.eclipse.org/smila/management/jmx/client", "jmxclient");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.smila.management.jmx.client.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AttributeType }
     * 
     */
    public AttributeType createAttributeType() {
        return new AttributeType();
    }

    /**
     * Create an instance of {@link CustomType }
     * 
     */
    public CustomType createCustomType() {
        return new CustomType();
    }

    /**
     * Create an instance of {@link EqualsOpType }
     * 
     */
    public EqualsOpType createEqualsOpType() {
        return new EqualsOpType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link ConnectionConfigType }
     * 
     */
    public ConnectionConfigType createConnectionConfigType() {
        return new ConnectionConfigType();
    }

    /**
     * Create an instance of {@link WaitType }
     * 
     */
    public WaitType createWaitType() {
        return new WaitType();
    }

    /**
     * Create an instance of {@link InOpType }
     * 
     */
    public InOpType createInOpType() {
        return new InOpType();
    }

    /**
     * Create an instance of {@link ConstantType }
     * 
     */
    public ConstantType createConstantType() {
        return new ConstantType();
    }

    /**
     * Create an instance of {@link CmdConfigType }
     * 
     */
    public CmdConfigType createCmdConfigType() {
        return new CmdConfigType();
    }

    /**
     * Create an instance of {@link SampleConfigType }
     * 
     */
    public SampleConfigType createSampleConfigType() {
        return new SampleConfigType();
    }

    /**
     * Create an instance of {@link ItemType }
     * 
     */
    public ItemType createItemType() {
        return new ItemType();
    }

    /**
     * Create an instance of {@link RegexpType }
     * 
     */
    public RegexpType createRegexpType() {
        return new RegexpType();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link JmxClientConfigType }
     * 
     */
    public JmxClientConfigType createJmxClientConfigType() {
        return new JmxClientConfigType();
    }

    /**
     * Create an instance of {@link OperationType }
     * 
     */
    public OperationType createOperationType() {
        return new OperationType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JmxClientConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.eclipse.org/smila/management/jmx/client", name = "jmxclient")
    public JAXBElement<JmxClientConfigType> createJmxclient(JmxClientConfigType value) {
        return new JAXBElement<JmxClientConfigType>(_Jmxclient_QNAME, JmxClientConfigType.class, null, value);
    }

}

// CHECKSTYLE:ON
