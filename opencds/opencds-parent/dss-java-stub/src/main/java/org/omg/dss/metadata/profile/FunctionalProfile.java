
package org.omg.dss.metadata.profile;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A FunctionalProfile represents a list of supported service operations.
 * 
 * <p>Java class for FunctionalProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FunctionalProfile">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceProfile">
 *       &lt;sequence>
 *         &lt;element name="supportedOperation" type="{http://www.omg.org/spec/CDSS/201105/dss}OperationType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunctionalProfile", propOrder = {
    "supportedOperation"
})
public class FunctionalProfile
    extends ServiceProfile
{

    @XmlElement(required = true)
    protected List<OperationType> supportedOperation;

    /**
     * Gets the value of the supportedOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OperationType }
     * 
     * 
     */
    public List<OperationType> getSupportedOperation() {
        if (supportedOperation == null) {
            supportedOperation = new ArrayList<OperationType>();
        }
        return this.supportedOperation;
    }

}
