
package org.omg.dss.metadata.semanticrequirement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A DSS claiming conformance to this requirement must fulfill the semantic restrictions defined in the narrative specification.
 * 
 * <p>Java class for OtherSemanticRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OtherSemanticRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement">
 *       &lt;sequence>
 *         &lt;element name="requirementSpecification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OtherSemanticRequirement", propOrder = {
    "requirementSpecification"
})
public class OtherSemanticRequirement
    extends SemanticRequirement
{

    @XmlElement(required = true)
    protected String requirementSpecification;

    /**
     * Gets the value of the requirementSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequirementSpecification() {
        return requirementSpecification;
    }

    /**
     * Sets the value of the requirementSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequirementSpecification(String value) {
        this.requirementSpecification = value;
    }

}
