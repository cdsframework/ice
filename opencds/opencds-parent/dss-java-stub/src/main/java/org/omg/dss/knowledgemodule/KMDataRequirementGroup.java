
package org.omg.dss.knowledgemodule;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A KM Data Requirement Group consists of one or more data requirement items.  
 * 
 * <p>Java class for KMDataRequirementGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMDataRequirementGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMItem">
 *       &lt;sequence>
 *         &lt;element name="dataRequirementItem" type="{http://www.omg.org/spec/CDSS/201105/dss}KMDataRequirementItem" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMDataRequirementGroup", propOrder = {
    "dataRequirementItem"
})
public class KMDataRequirementGroup
    extends KMItem
{

    @XmlElement(required = true)
    protected List<KMDataRequirementItem> dataRequirementItem;

    /**
     * Gets the value of the dataRequirementItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataRequirementItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataRequirementItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMDataRequirementItem }
     * 
     * 
     */
    public List<KMDataRequirementItem> getDataRequirementItem() {
        if (dataRequirementItem == null) {
            dataRequirementItem = new ArrayList<KMDataRequirementItem>();
        }
        return this.dataRequirementItem;
    }

}
