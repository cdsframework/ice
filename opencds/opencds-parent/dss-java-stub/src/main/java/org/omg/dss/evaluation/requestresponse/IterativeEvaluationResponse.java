
package org.omg.dss.evaluation.requestresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * The IterativeEvaluationResponse represents the responses from knowledge module evaluations conducted in an iterative fashion. It contains a list of knowledge module evaluation responses reaching final conclusions, as well as a list of knowledge module evaluations responses reaching intermediate conclusions due to insufficient data.  This object differs from the EvaluationResponse object in that it also provides intermediate state data in the case that a final conclusion could not be reached for a knowledge module.
 * 
 * <p>Java class for IterativeEvaluationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IterativeEvaluationResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}EvaluationResponseBase">
 *       &lt;sequence>
 *         &lt;element name="iterativeKMEvaluationResponse" type="{http://www.omg.org/spec/CDSS/201105/dss}IterativeKMEvaluationResponse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IterativeEvaluationResponse", propOrder = {
    "iterativeKMEvaluationResponse"
})
public class IterativeEvaluationResponse
    extends EvaluationResponseBase
{

    protected List<IterativeKMEvaluationResponse> iterativeKMEvaluationResponse;

    /**
     * Gets the value of the iterativeKMEvaluationResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the iterativeKMEvaluationResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIterativeKMEvaluationResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IterativeKMEvaluationResponse }
     * 
     * 
     */
    public List<IterativeKMEvaluationResponse> getIterativeKMEvaluationResponse() {
        if (iterativeKMEvaluationResponse == null) {
            iterativeKMEvaluationResponse = new ArrayList<IterativeKMEvaluationResponse>();
        }
        return this.iterativeKMEvaluationResponse;
    }

}
