
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;
import org.omg.dss.common.SemanticPayload;


/**
 * The KMEvaluationResultData is an object containing a knowledge module's evaluation result data.
 * 
 * <p>Java class for KMEvaluationResultData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMEvaluationResultData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="evaluationResultId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *         &lt;element name="data" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticPayload"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMEvaluationResultData", propOrder = {
    "evaluationResultId",
    "data"
})
public class KMEvaluationResultData {

    @XmlElement(required = true)
    protected ItemIdentifier evaluationResultId;
    @XmlElement(required = true)
    protected SemanticPayload data;

    /**
     * Gets the value of the evaluationResultId property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getEvaluationResultId() {
        return evaluationResultId;
    }

    /**
     * Sets the value of the evaluationResultId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setEvaluationResultId(ItemIdentifier value) {
        this.evaluationResultId = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticPayload }
     *     
     */
    public SemanticPayload getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPayload }
     *     
     */
    public void setData(SemanticPayload value) {
        this.data = value;
    }

}
