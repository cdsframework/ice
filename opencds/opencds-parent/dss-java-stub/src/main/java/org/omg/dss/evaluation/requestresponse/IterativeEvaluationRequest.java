
package org.omg.dss.evaluation.requestresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The IterativeEvaluationRequest is an object containing evaluation request of one or more knowledge modules in an iterative fashion.
 * 
 * <p>Java class for IterativeEvaluationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IterativeEvaluationRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}EvaluationRequestBase">
 *       &lt;sequence>
 *         &lt;element name="iterativeKMEvaluationRequest" type="{http://www.omg.org/spec/CDSS/201105/dss}IterativeKMEvaluationRequest" maxOccurs="unbounded"/>
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
@XmlType(name = "IterativeEvaluationRequest", propOrder = {
    "iterativeKMEvaluationRequest",
    "dataRequirementItemData"
})
public class IterativeEvaluationRequest
    extends EvaluationRequestBase
{

    @XmlElement(required = true)
    protected List<IterativeKMEvaluationRequest> iterativeKMEvaluationRequest;
    @XmlElement(required = true)
    protected List<DataRequirementItemData> dataRequirementItemData;

    /**
     * Gets the value of the iterativeKMEvaluationRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the iterativeKMEvaluationRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIterativeKMEvaluationRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IterativeKMEvaluationRequest }
     * 
     * 
     */
    public List<IterativeKMEvaluationRequest> getIterativeKMEvaluationRequest() {
        if (iterativeKMEvaluationRequest == null) {
            iterativeKMEvaluationRequest = new ArrayList<IterativeKMEvaluationRequest>();
        }
        return this.iterativeKMEvaluationRequest;
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
