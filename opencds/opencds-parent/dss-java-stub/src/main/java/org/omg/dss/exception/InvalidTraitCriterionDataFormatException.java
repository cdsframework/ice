
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * An InvalidTraitCriterionDataFormatException is thrown when a trait criterion is inconsistent with the information model specified for the trait criterion through the use of a semantic signifier.
 * 
 * <p>Java class for InvalidTraitCriterionDataFormatException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvalidTraitCriterionDataFormatException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}InvalidDataFormatException">
 *       &lt;sequence>
 *         &lt;element name="traitCriterionId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidTraitCriterionDataFormatException", propOrder = {
    "traitCriterionId"
})
public class InvalidTraitCriterionDataFormatException
    extends InvalidDataFormatException
{

    @XmlElement(required = true)
    protected ItemIdentifier traitCriterionId;

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

}
