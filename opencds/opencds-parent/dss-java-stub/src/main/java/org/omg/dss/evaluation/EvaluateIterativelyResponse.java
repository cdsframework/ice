
package org.omg.dss.evaluation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.evaluation.requestresponse.IterativeEvaluationResponse;


/**
 * <p>Java class for evaluateIterativelyResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evaluateIterativelyResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iterativeEvaluationResponse" type="{http://www.omg.org/spec/CDSS/201105/dss}IterativeEvaluationResponse"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluateIterativelyResponse", propOrder = {
    "iterativeEvaluationResponse"
})
public class EvaluateIterativelyResponse {

    @XmlElement(required = true)
    protected IterativeEvaluationResponse iterativeEvaluationResponse;

    /**
     * Gets the value of the iterativeEvaluationResponse property.
     * 
     * @return
     *     possible object is
     *     {@link IterativeEvaluationResponse }
     *     
     */
    public IterativeEvaluationResponse getIterativeEvaluationResponse() {
        return iterativeEvaluationResponse;
    }

    /**
     * Sets the value of the iterativeEvaluationResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link IterativeEvaluationResponse }
     *     
     */
    public void setIterativeEvaluationResponse(IterativeEvaluationResponse value) {
        this.iterativeEvaluationResponse = value;
    }

}
