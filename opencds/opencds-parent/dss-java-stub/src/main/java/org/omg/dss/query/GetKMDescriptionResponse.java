
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.knowledgemodule.ExtendedKMDescription;


/**
 * <p>Java class for getKMDescriptionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getKMDescriptionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extendedKMDescription" type="{http://www.omg.org/spec/CDSS/201105/dss}ExtendedKMDescription"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getKMDescriptionResponse", propOrder = {
    "extendedKMDescription"
})
public class GetKMDescriptionResponse {

    @XmlElement(required = true)
    protected ExtendedKMDescription extendedKMDescription;

    /**
     * Gets the value of the extendedKMDescription property.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedKMDescription }
     *     
     */
    public ExtendedKMDescription getExtendedKMDescription() {
        return extendedKMDescription;
    }

    /**
     * Sets the value of the extendedKMDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedKMDescription }
     *     
     */
    public void setExtendedKMDescription(ExtendedKMDescription value) {
        this.extendedKMDescription = value;
    }

}
