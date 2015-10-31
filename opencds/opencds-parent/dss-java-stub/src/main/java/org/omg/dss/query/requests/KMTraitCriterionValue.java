
package org.omg.dss.query.requests;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;
import org.omg.dss.common.SemanticPayload;


/**
 * <p>Java class for KMTraitCriterionValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMTraitCriterionValue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="traitCriterionId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *         &lt;element name="value" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticPayload"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMTraitCriterionValue", propOrder = {
    "traitCriterionId",
    "value"
})
public class KMTraitCriterionValue {

    @XmlElement(required = true)
    protected ItemIdentifier traitCriterionId;
    @XmlElement(required = true)
    protected SemanticPayload value;

    /**
     * Gets the value of the traitCriterionId property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getTraitCriterionId() {
        return traitCriterionId;
    }

    /**
     * Sets the value of the traitCriterionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setTraitCriterionId(ItemIdentifier value) {
        this.traitCriterionId = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticPayload }
     *     
     */
    public SemanticPayload getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPayload }
     *     
     */
    public void setValue(SemanticPayload value) {
        this.value = value;
    }

}
