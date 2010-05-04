
// CHECKSTYLE:OFF

package it.polimi.chansonnier.agent.messages;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attribute">
 *   &lt;complexContent>
 *     &lt;extension base="{}Attribute">
 *       &lt;redefine>
 *         &lt;complexType name="Attribute">
 *           &lt;complexContent>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;attribute name="KeyAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;attribute name="HashAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;attribute name="MimeTypeAttribute" type="{}MimeTypeAttributeType" />
 *               &lt;attribute name="Attachment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *             &lt;/restriction>
 *           &lt;/complexContent>
 *         &lt;/complexType>
 *       &lt;/redefine>
 *       &lt;choice>
 *         &lt;element name="MockAttributes" type="{}MockAttributesType"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", propOrder = {
    "mockAttributes"
})
public class Attribute
    extends OriginalAttribute
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MockAttributes")
    protected MockAttributesType mockAttributes;

    /**
     * Gets the value of the mockAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link MockAttributesType }
     *     
     */
    public MockAttributesType getMockAttributes() {
        return mockAttributes;
    }

    /**
     * Sets the value of the mockAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link MockAttributesType }
     *     
     */
    public void setMockAttributes(MockAttributesType value) {
        this.mockAttributes = value;
    }

}

// CHECKSTYLE:ON
