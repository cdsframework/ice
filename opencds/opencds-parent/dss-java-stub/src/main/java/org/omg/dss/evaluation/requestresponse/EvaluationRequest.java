
package org.omg.dss.evaluation.requestresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The EvaluationRequest is an object containing evaluation request of one or more knowledge modules in a non-iterative fashion. 
 * 
 * 
 * <p>Java class for EvaluationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EvaluationRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}EvaluationRequestBase">
 *       &lt;sequence>
 *         &lt;element name="kmEvaluationRequest" type="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationRequest" maxOccurs="unbounded"/>
 *         &lt;element name="dataRequirementItemData" type="{http://www.omg.org/spec/CDSS/201105/dss}DataRequirementItemData" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvaluationRequest", propOrder = {
    "kmEvaluationRequest",
    "dataRequirementItemData"
})
public class EvaluationRequest
    extends EvaluationRequestBase
{

    @XmlElement(required = true)
    protected List<KMEvaluationRequest> kmEvaluationRequest;
    @XmlElement(required = true)
    protected List<DataRequirementItemData> dataRequirementItemData;

    /**
     * Gets the value of the kmEvaluationRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kmEvaluationRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKmEvaluationRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMEvaluationRequest }
     * 
     * 
     */
    public List<KMEvaluationRequest> getKmEvaluationRequest() {
        if (kmEvaluationRequest == null) {
            kmEvaluationRequest = new ArrayList<KMEvaluationRequest>();
        }
        return this.kmEvaluationRequest;
    }

    /**
     * Gets the value of the dataRequirementItemData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataRequirementItemData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataRequirementItemData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataRequirementItemData }
     * 
     * 
     */
    public List<DataRequirementItemData> getDataRequirementItemData() {
        if (dataRequirementItemData == null) {
            dataRequirementItemData = new ArrayList<DataRequirementItemData>();
        }
        return this.dataRequirementItemData;
    }

}
