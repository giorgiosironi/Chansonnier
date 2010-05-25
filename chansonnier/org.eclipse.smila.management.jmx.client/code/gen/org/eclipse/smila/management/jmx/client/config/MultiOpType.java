
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MultiOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/management/jmx/client}BooleanOpType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="2">
 *         &lt;choice>
 *           &lt;element name="cmd" type="{http://www.eclipse.org/smila/management/jmx/client}CmdConfigType"/>
 *           &lt;element name="const" type="{http://www.eclipse.org/smila/management/jmx/client}ConstantType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiOpType", propOrder = {
    "cmdOrConst"
})
@XmlSeeAlso({
    InOpType.class
})
public abstract class MultiOpType
    extends BooleanOpType
{

    @XmlElements({
        @XmlElement(name = "const", type = ConstantType.class),
        @XmlElement(name = "cmd", type = CmdConfigType.class)
    })
    protected List<ItemType> cmdOrConst;

    /**
     * Gets the value of the cmdOrConst property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cmdOrConst property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCmdOrConst().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstantType }
     * {@link CmdConfigType }
     * 
     * 
     */
    public List<ItemType> getCmdOrConst() {
        if (cmdOrConst == null) {
            cmdOrConst = new ArrayList<ItemType>();
        }
        return this.cmdOrConst;
    }

}

// CHECKSTYLE:ON
