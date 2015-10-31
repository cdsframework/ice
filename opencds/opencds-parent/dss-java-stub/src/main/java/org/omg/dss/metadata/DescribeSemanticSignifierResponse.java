
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.semanticsignifier.SemanticSignifier;


/**
 * <p>Java class for describeSemanticSignifierResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeSemanticSignifierResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="semanticSignifier" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticSignifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeSemanticSignifierResponse", propOrder = {
    "semanticSignifier"
})
public class DescribeSemanticSignifierResponse {

    @XmlElement(required = true)
    protected SemanticSignifier semanticSignifier;

    /**
     * Gets the value of the semanticSignifier property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticSignifier }
     *     
     */
    public SemanticSignifier getSemanticSignifier() {
        return semanticSignifier;
    }

    /**
     * Sets the value of the semanticSignifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticSignifier }
     *     
     */
    public void setSemanticSignifier(SemanticSignifier value) {
        this.semanticSignifier = value;
    }

}
