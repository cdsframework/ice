
package org.omg.dss.metadata.semanticrequirement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ScopedDO;


/**
 * The superclass of all DSS semantic requirement sub-types.
 * 
 * <p>Java class for SemanticRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SemanticRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ScopedDO">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirementType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticRequirement", propOrder = {
    "type"
})
@XmlSeeAlso({
    TraitSetRequirement.class,
    InformationModelRequirement.class,
    OtherSemanticRequirement.class,
    LanguageSupportRequirement.class
})
public abstract class SemanticRequirement
    extends ScopedDO
{

    @XmlElement(required = true)
    protected SemanticRequirementType type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticRequirementType }
     *     
     */
    public SemanticRequirementType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticRequirementType }
     *     
     */
    public void setType(SemanticRequirementType value) {
        this.type = value;
    }

}
