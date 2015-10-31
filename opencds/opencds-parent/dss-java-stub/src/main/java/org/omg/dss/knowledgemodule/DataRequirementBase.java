
package org.omg.dss.knowledgemodule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.metadata.semanticrequirement.AllowedDataRequirement;
import org.omg.dss.metadata.semanticrequirement.RequiredDataRequirement;


/**
 * <p>Java class for DataRequirementBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataRequirementBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="informationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRequirementBase", propOrder = {
    "informationModelSSId"
})
@XmlSeeAlso({
    AllowedDataRequirement.class,
    RequiredDataRequirement.class
})
public abstract class DataRequirementBase {

    @XmlElement(required = true)
    protected EntityIdentifier informationModelSSId;

    /**
     * Gets the value of the informationModelSSId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getInformationModelSSId() {
        return informationModelSSId;
    }

    /**
     * Sets the value of the informationModelSSId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setInformationModelSSId(EntityIdentifier value) {
        this.informationModelSSId = value;
    }

}
