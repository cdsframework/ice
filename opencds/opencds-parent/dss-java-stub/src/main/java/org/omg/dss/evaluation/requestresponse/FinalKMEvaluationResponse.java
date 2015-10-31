
package org.omg.dss.evaluation.requestresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * The FinalKMEvaluationResponse is an object containing final conclusions of a single knowledge module evaluation response. 
 * 
 * <p>Java class for FinalKMEvaluationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinalKMEvaluationResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResponse">
 *       &lt;sequence>
 *         &lt;element name="kmEvaluationResultData" type="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResultData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinalKMEvaluationResponse", propOrder = {
    "kmEvaluationResultData"
})
public class FinalKMEvaluationResponse
    extends KMEvaluationResponse
{

    protected List<KMEvaluationResultData> kmEvaluationResultData;

    /**
     * Gets the value of the kmEvaluationResultData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kmEvaluationResultData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKmEvaluationResultData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMEvaluationResultData }
     * 
     * 
     */
    public List<KMEvaluationResultData> getKmEvaluationResultData() {
        if (kmEvaluationResultData == null) {
            kmEvaluationResultData = new ArrayList<KMEvaluationResultData>();
        }
        return this.kmEvaluationResultData;
    }

}
