
package org.omg.dss.metadata.trait;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.DescribedDO;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ItemIdentifier;


/**
 * The TraitCriterion object represents a criterion that can be applied to a knowledge module trait value.  The identifier of a trait criterion consists of the EntityIdentifier of the parent trait plus an itemId that is unique within the scope of the EntityIdentifier of the parent trait.
 * 
 * 
 * <p>Java class for TraitCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TraitCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DescribedDO">
 *       &lt;sequence>
 *         &lt;element name="traitCriterionId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *         &lt;element name="criterionModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TraitCriterion", propOrder = {
    "traitCriterionId",
    "criterionModelSSId"
})
public class TraitCriterion
    extends DescribedDO
{

    @XmlElement(required = true)
    protected ItemIdentifier traitCriterionId;
    @XmlElement(required = true)
    protected EntityIdentifier criterionModelSSId;

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
     * Gets the value of the criterionModelSSId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getCriterionModelSSId() {
        return criterionModelSSId;
    }

    /**
     * Sets the value of the criterionModelSSId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setCriterionModelSSId(EntityIdentifier value) {
        this.criterionModelSSId = value;
    }

}
