
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.query.responses.KMEvaluationResultSemanticsList;


/**
 * <p>Java class for getKMEvaluationResultSemanticsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getKMEvaluationResultSemanticsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmEvaluationResultSemanticsList" type="{http://www.omg.org/spec/CDSS/201105/dss}KMEvaluationResultSemanticsList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getKMEvaluationResultSemanticsResponse", propOrder = {
    "kmEvaluationResultSemanticsList"
})
public class GetKMEvaluationResultSemanticsResponse {

    @XmlElement(required = true)
    protected KMEvaluationResultSemanticsList kmEvaluationResultSemanticsList;

    /**
     * Gets the value of the kmEvaluationResultSemanticsList property.
     * 
     * @return
     *     possible object is
     *     {@link KMEvaluationResultSemanticsList }
     *     
     */
    public KMEvaluationResultSemanticsList getKmEvaluationResultSemanticsList() {
        return kmEvaluationResultSemanticsList;
    }

    /**
     * Sets the value of the kmEvaluationResultSemanticsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMEvaluationResultSemanticsList }
     *     
     */
    public void setKmEvaluationResultSemanticsList(KMEvaluationResultSemanticsList value) {
        this.kmEvaluationResultSemanticsList = value;
    }

}
