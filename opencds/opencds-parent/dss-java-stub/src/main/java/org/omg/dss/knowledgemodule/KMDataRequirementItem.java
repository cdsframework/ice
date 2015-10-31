
package org.omg.dss.knowledgemodule;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A KMDataRequirementItem represents the specification of knowledge module required data from the external world.  A data requirement item is specified in terms of one or more alternative information models.
 * 
 * <p>Java class for KMDataRequirementItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMDataRequirementItem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMItem">
 *       &lt;sequence>
 *         &lt;element name="informationModelAlternative" type="{http://www.omg.org/spec/CDSS/201105/dss}InformationModelAlternative" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMDataRequirementItem", propOrder = {
    "informationModelAlternative"
})
public class KMDataRequirementItem
    extends KMItem
{

    @XmlElement(required = true)
    protected List<InformationModelAlternative> informationModelAlternative;

    /**
     * Gets the value of the informationModelAlternative property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the informationModelAlternative property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInformationModelAlternative().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InformationModelAlternative }
     * 
     * 
     */
    public List<InformationModelAlternative> getInformationModelAlternative() {
        if (informationModelAlternative == null) {
            informationModelAlternative = new ArrayList<InformationModelAlternative>();
        }
        return this.informationModelAlternative;
    }

}
