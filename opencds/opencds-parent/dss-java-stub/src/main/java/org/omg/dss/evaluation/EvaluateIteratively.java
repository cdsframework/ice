
package org.omg.dss.evaluation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationRequest;


/**
 * <p>Java class for evaluateIteratively complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evaluateIteratively">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="iterativeEvaluationRequest" type="{http://www.omg.org/spec/CDSS/201105/dss}IterativeEvaluationRequest"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluateIteratively", propOrder = {
    "iterativeEvaluationRequest"
})
public class EvaluateIteratively
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected IterativeEvaluationRequest iterativeEvaluationRequest;

    /**
     * Gets the value of the iterativeEvaluationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link IterativeEvaluationRequest }
     *     
     */
    public IterativeEvaluationRequest getIterativeEvaluationRequest() {
        return iterativeEvaluationRequest;
    }

    /**
     * Sets the value of the iterativeEvaluationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link IterativeEvaluationRequest }
     *     
     */
    public void setIterativeEvaluationRequest(IterativeEvaluationRequest value) {
        this.iterativeEvaluationRequest = value;
    }

}
