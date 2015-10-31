
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.semanticrequirement.SemanticRequirement;


/**
 * <p>Java class for describeSemanticRequirementResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeSemanticRequirementResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="semanticRequirement" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeSemanticRequirementResponse", propOrder = {
    "semanticRequirement"
})
public class DescribeSemanticRequirementResponse {

    @XmlElement(required = true)
    protected SemanticRequirement semanticRequirement;

    /**
     * Gets the value of the semanticRequirement property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticRequirement }
     *     
     */
    public SemanticRequirement getSemanticRequirement() {
        return semanticRequirement;
    }

    /**
     * Sets the value of the semanticRequirement property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticRequirement }
     *     
     */
    public void setSemanticRequirement(SemanticRequirement value) {
        this.semanticRequirement = value;
    }

}
