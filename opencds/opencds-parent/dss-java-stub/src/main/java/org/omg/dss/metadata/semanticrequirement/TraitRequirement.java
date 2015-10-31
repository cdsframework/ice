
package org.omg.dss.metadata.semanticrequirement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * It specifies the a specific knowledge module trait requirement. Trait is identified by a scoping entity, the trait identifier (unique within the scoping entity), and the trait version. The requirement also specifies if the trait is required or optional for knowledge modules claiming conformance to the requirement.
 * 
 * 
 * <p>Java class for TraitRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TraitRequirement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isMandatory" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="traitId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TraitRequirement", propOrder = {
    "isMandatory",
    "traitId"
})
public class TraitRequirement {

    protected boolean isMandatory;
    @XmlElement(required = true)
    protected EntityIdentifier traitId;

    /**
     * Gets the value of the isMandatory property.
     * 
     */
    public boolean isIsMandatory() {
        return isMandatory;
    }

    /**
     * Sets the value of the isMandatory property.
     * 
     */
    public void setIsMandatory(boolean value) {
        this.isMandatory = value;
    }

    /**
     * Gets the value of the traitId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getTraitId() {
        return traitId;
    }

    /**
     * Sets the value of the traitId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setTraitId(EntityIdentifier value) {
        this.traitId = value;
    }

}
