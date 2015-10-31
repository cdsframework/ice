
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.knowledgemodule.KMDataRequirements;


/**
 * <p>Java class for getKMDataRequirementsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getKMDataRequirementsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmDataRequirements" type="{http://www.omg.org/spec/CDSS/201105/dss}KMDataRequirements"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getKMDataRequirementsResponse", propOrder = {
    "kmDataRequirements"
})
public class GetKMDataRequirementsResponse {

    @XmlElement(required = true)
    protected KMDataRequirements kmDataRequirements;

    /**
     * Gets the value of the kmDataRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link KMDataRequirements }
     *     
     */
    public KMDataRequirements getKmDataRequirements() {
        return kmDataRequirements;
    }

    /**
     * Sets the value of the kmDataRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMDataRequirements }
     *     
     */
    public void setKmDataRequirements(KMDataRequirements value) {
        this.kmDataRequirements = value;
    }

}
