
package org.omg.dss.query.requests;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * If the trait criterion is satisfied, then the knowledge module is considered to have satisfied the criterion.
 * 
 * 
 * <p>Java class for KMTraitCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMTraitCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion">
 *       &lt;sequence>
 *         &lt;element name="kmTraitCriterionValue" type="{http://www.omg.org/spec/CDSS/201105/dss}KMTraitCriterionValue"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMTraitCriterion", propOrder = {
    "kmTraitCriterionValue"
})
public class KMTraitCriterion
    extends KMCriterion
{

    @XmlElement(required = true)
    protected KMTraitCriterionValue kmTraitCriterionValue;

    /**
     * Gets the value of the kmTraitCriterionValue property.
     * 
     * @return
     *     possible object is
     *     {@link KMTraitCriterionValue }
     *     
     */
    public KMTraitCriterionValue getKmTraitCriterionValue() {
        return kmTraitCriterionValue;
    }

    /**
     * Sets the value of the kmTraitCriterionValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMTraitCriterionValue }
     *     
     */
    public void setKmTraitCriterionValue(KMTraitCriterionValue value) {
        this.kmTraitCriterionValue = value;
    }

}
