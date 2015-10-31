
package org.omg.dss.evaluation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.evaluation.requestresponse.EvaluationResponse;


/**
 * <p>Java class for evaluateAtSpecifiedTimeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evaluateAtSpecifiedTimeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="evaluationResponse" type="{http://www.omg.org/spec/CDSS/201105/dss}EvaluationResponse"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluateAtSpecifiedTimeResponse", propOrder = {
    "evaluationResponse"
})
public class EvaluateAtSpecifiedTimeResponse {

    @XmlElement(required = true)
    protected EvaluationResponse evaluationResponse;

    /**
     * Gets the value of the evaluationResponse property.
     * 
     * @return
     *     possible object is
     *     {@link EvaluationResponse }
     *     
     */
    public EvaluationResponse getEvaluationResponse() {
        return evaluationResponse;
    }

    /**
     * Sets the value of the evaluationResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link EvaluationResponse }
     *     
     */
    public void setEvaluationResponse(EvaluationResponse value) {
        this.evaluationResponse = value;
    }

}
