
package org.omg.dss.exception;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * A RequiredDataNotProvidedException is thrown when trying to evaluate a KM but one of the required data requirement groups is not provided.
 * 
 * <p>Java class for RequiredDataNotProvidedException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequiredDataNotProvidedException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="drgId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequiredDataNotProvidedException", propOrder = {
    "drgId"
})
public class RequiredDataNotProvidedException
    extends DSSException
{

    @XmlElement(required = true)
    protected List<ItemIdentifier> drgId;

    /**
     * Gets the value of the drgId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the drgId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDrgId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemIdentifier }
     * 
     * 
     */
    public List<ItemIdentifier> getDrgId() {
        if (drgId == null) {
            drgId = new ArrayList<ItemIdentifier>();
        }
        return this.drgId;
    }

}
