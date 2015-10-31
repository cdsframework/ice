
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The IterativeKMEvaluationResponse is an object containing intermediate state of a single knowledge module iterative evaluation response. It contains contextual information of the evaluation process so far. 
 * @see IntermediateKMEvaluationResponse
 * 
 * <p>Java class for IterativeKMEvaluationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IterativeKMEvaluationResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}IntermediateKMEvaluationResponse">
 *       &lt;sequence>
 *         &lt;element name="intermediateState" type="{http://www.omg.org/spec/CDSS/201105/dss}IntermediateState"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IterativeKMEvaluationResponse", propOrder = {
    "intermediateState"
})
public class IterativeKMEvaluationResponse
    extends IntermediateKMEvaluationResponse
{

    @XmlElement(required = true)
    protected IntermediateState intermediateState;

    /**
     * Gets the value of the intermediateState property.
     * 
     * @return
     *     possible object is
     *     {@link IntermediateState }
     *     
     */
    public IntermediateState getIntermediateState() {
        return intermediateState;
    }

    /**
     * Sets the value of the intermediateState property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntermediateState }
     *     
     */
    public void setIntermediateState(IntermediateState value) {
        this.intermediateState = value;
    }

}
