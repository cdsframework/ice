
package org.omg.dss.evaluation.requestresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * The IntermediateKMEvaluationResponse is an object that identifies what further data requirement groups were needed for reaching a final conclusion through the knowledge module evaluation.
 * 
 * <p>Java class for IntermediateKMEvaluationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntermediateKMEvaluationResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResponse">
 *       &lt;sequence>
 *         &lt;element name="requiredDRGId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntermediateKMEvaluationResponse", propOrder = {
    "requiredDRGId"
})
@XmlSeeAlso({
    IterativeKMEvaluationResponse.class
})
public class IntermediateKMEvaluationResponse
    extends KMEvaluationResponse
{

    @XmlElement(required = true)
    protected List<ItemIdentifier> requiredDRGId;

    /**
     * Gets the value of the requiredDRGId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredDRGId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredDRGId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemIdentifier }
     * 
     * 
     */
    public List<ItemIdentifier> getRequiredDRGId() {
        if (requiredDRGId == null) {
            requiredDRGId = new ArrayList<ItemIdentifier>();
        }
        return this.requiredDRGId;
    }

}
