
package org.omg.dss.query.requests;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * The RelatedKMSearchCriterion is an object representing a criterion on the relation type between knowledge modules. A knowledge module satisfies this criterion if it has a relation of type relationType to at least one of the specified knowledge modules.  For the purpose of searching, the specified KMs shall be considered the target of the relationship. In other words, the DSS shall look for KMs that fulfill the following pattern: [the KMs returned by the search] [relationship type statement] [target KMs].  For example, if the relationship target KMs are KM A and KM B, and the search relationshipType is SUPERCEDES, then the DSS must look for KMs that supercede KM A and/or KM B.
 * 
 * <p>Java class for RelatedKMSearchCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelatedKMSearchCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion">
 *       &lt;sequence>
 *         &lt;element name="relationType" type="{http://www.omg.org/spec/CDSS/201105/dss}KMRelationshipType"/>
 *         &lt;element name="targetKMId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelatedKMSearchCriterion", propOrder = {
    "relationType",
    "targetKMId"
})
public class RelatedKMSearchCriterion
    extends KMCriterion
{

    @XmlElement(required = true)
    protected KMRelationshipType relationType;
    @XmlElement(required = true)
    protected List<EntityIdentifier> targetKMId;

    /**
     * Gets the value of the relationType property.
     * 
     * @return
     *     possible object is
     *     {@link KMRelationshipType }
     *     
     */
    public KMRelationshipType getRelationType() {
        return relationType;
    }

    /**
     * Sets the value of the relationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMRelationshipType }
     *     
     */
    public void setRelationType(KMRelationshipType value) {
        this.relationType = value;
    }

    /**
     * Gets the value of the targetKMId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the targetKMId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTargetKMId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getTargetKMId() {
        if (targetKMId == null) {
            targetKMId = new ArrayList<EntityIdentifier>();
        }
        return this.targetKMId;
    }

}
