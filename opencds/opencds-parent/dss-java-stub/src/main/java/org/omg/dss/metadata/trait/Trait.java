
package org.omg.dss.metadata.trait;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ScopedDO;


/**
 * Traits represent attributes of objects.  In the DSS context, traits are used to describe knowledge modules.
 * 
 * <p>Java class for Trait complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Trait">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ScopedDO">
 *       &lt;sequence>
 *         &lt;element name="traitValueIsLanguageDependent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="informationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *         &lt;element name="allowedTraitCriterion" type="{http://www.omg.org/spec/CDSS/201105/dss}TraitCriterion" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Trait", propOrder = {
    "traitValueIsLanguageDependent",
    "informationModelSSId",
    "allowedTraitCriterion"
})
public class Trait
    extends ScopedDO
{

    protected boolean traitValueIsLanguageDependent;
    @XmlElement(required = true)
    protected EntityIdentifier informationModelSSId;
    protected List<TraitCriterion> allowedTraitCriterion;

    /**
     * Gets the value of the traitValueIsLanguageDependent property.
     * 
     */
    public boolean isTraitValueIsLanguageDependent() {
        return traitValueIsLanguageDependent;
    }

    /**
     * Sets the value of the traitValueIsLanguageDependent property.
     * 
     */
    public void setTraitValueIsLanguageDependent(boolean value) {
        this.traitValueIsLanguageDependent = value;
    }

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

    /**
     * Gets the value of the allowedTraitCriterion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedTraitCriterion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedTraitCriterion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TraitCriterion }
     * 
     * 
     */
    public List<TraitCriterion> getAllowedTraitCriterion() {
        if (allowedTraitCriterion == null) {
            allowedTraitCriterion = new ArrayList<TraitCriterion>();
        }
        return this.allowedTraitCriterion;
    }

}
