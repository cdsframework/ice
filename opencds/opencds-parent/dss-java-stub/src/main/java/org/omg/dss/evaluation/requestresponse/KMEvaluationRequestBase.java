
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * The KMEvaluationRequestBase is the superclass of a single knowledge module evaluation request. It contains information about the knowledge module identifier to evaluate.  
 * 
 * The following are valid ways of specifying the version number of a knowledge module to run: (i) the specific version number (e.g., 2.1.0): this will result in the evaluation of version 2.1.0; (ii) the specific major and minor version, with * as the revision number (e.g., 2.1.*): this will result in the evaluation of the highest revision with the specified major and minor version number (e.g., 2.1.0, 2.1.1, or 2.1.2, depending on the latest available revision); and (iii) the specific major version, with * as the minor version and * as the revision number (e.g., 2.*.*): this will result in the evaluation of the highest minor version, and the highest revision within that minor version (e.g., 2.3.1, if the highest minor version within major version 2 is 3, and the highest revision within version 2.3 is revision 1, then 2.3.1 would be used).
 * 
 * <p>Java class for KMEvaluationRequestBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMEvaluationRequestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMEvaluationRequestBase", propOrder = {
    "kmId"
})
@XmlSeeAlso({
    IterativeKMEvaluationRequest.class,
    KMEvaluationRequest.class
})
public abstract class KMEvaluationRequestBase {

    @XmlElement(required = true)
    protected EntityIdentifier kmId;

    /**
     * Gets the value of the kmId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getKmId() {
        return kmId;
    }

    /**
     * Sets the value of the kmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setKmId(EntityIdentifier value) {
        this.kmId = value;
    }

}
