
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The IterativeKMEvaluationRequest is an object containing information of a single knowledge module evaluation request in an iterative fashion. It contains information from a previous evaluation iteration.
 * 
 * <p>Java class for IterativeKMEvaluationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IterativeKMEvaluationRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationRequestBase">
 *       &lt;sequence>
 *         &lt;element name="previousState" type="{http://www.omg.org/spec/CDSS/201105/dss}IntermediateState"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IterativeKMEvaluationRequest", propOrder = {
    "previousState"
})
public class IterativeKMEvaluationRequest
    extends KMEvaluationRequestBase
{

    @XmlElement(required = true)
    protected IntermediateState previousState;

    /**
     * Gets the value of the previousState property.
     * 
     * @return
     *     possible object is
     *     {@link IntermediateState }
     *     
     */
    public IntermediateState getPreviousState() {
        return previousState;
    }

    /**
     * Sets the value of the previousState property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntermediateState }
     *     
     */
    public void setPreviousState(IntermediateState value) {
        this.previousState = value;
    }

}
