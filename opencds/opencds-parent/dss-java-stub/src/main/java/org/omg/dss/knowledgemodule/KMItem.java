
package org.omg.dss.knowledgemodule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.DescribedDO;
import org.omg.dss.common.ItemIdentifier;


/**
 * The superclass of all knowledge module sub-items. For example, Data Requirement Group or Item. It contains the item identifier, which consists of the identifier of the scoping entity as well as a unique identifier within the scoping entity.  The scoping entity may be the knowledge module within which the KMItem resides, a different knowledge module, or an entity other than the knowledge module.  This approach enables items such as data requirements to be decoupled from specific knowledge modules and reused across knowledge modules.  This class inherits its name and description information from DescribedDO.
 * 
 * <p>Java class for KMItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMItem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DescribedDO">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMItem", propOrder = {
    "id"
})
@XmlSeeAlso({
    KMEvaluationResultSemantics.class,
    KMConsumerProvidedQueryParameter.class,
    KMDataRequirementItem.class,
    KMDataRequirementGroup.class
})
public abstract class KMItem
    extends DescribedDO
{

    @XmlElement(required = true)
    protected ItemIdentifier id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setId(ItemIdentifier value) {
        this.id = value;
    }

}
