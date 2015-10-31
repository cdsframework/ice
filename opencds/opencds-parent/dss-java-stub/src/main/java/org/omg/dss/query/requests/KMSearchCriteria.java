
package org.omg.dss.query.requests;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The KMSearchCriteria is an object containing knowledge module search criteria. 
 * 
 * <p>Java class for KMSearchCriteria complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMSearchCriteria">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inclusionCriterion" type="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion" maxOccurs="unbounded"/>
 *         &lt;element name="exclusionCriterion" type="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="maximumKMsToReturn" type="{http://www.omg.org/spec/CDSS/201105/dss}MaximumKMsToReturn"/>
 *         &lt;element name="minimumKMSearchScore" type="{http://www.omg.org/spec/CDSS/201105/dss}KMSearchScore"/>
 *         &lt;element name="kmTraitInclusionSpecification" type="{http://www.omg.org/spec/CDSS/201105/dss}KMTraitInclusionSpecification"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMSearchCriteria", propOrder = {
    "inclusionCriterion",
    "exclusionCriterion",
    "maximumKMsToReturn",
    "minimumKMSearchScore",
    "kmTraitInclusionSpecification"
})
public class KMSearchCriteria {

    @XmlElement(required = true)
    protected List<KMCriterion> inclusionCriterion;
    protected List<KMCriterion> exclusionCriterion;
    @XmlElement(required = true)
    protected BigInteger maximumKMsToReturn;
    protected int minimumKMSearchScore;
    @XmlElement(required = true)
    protected KMTraitInclusionSpecification kmTraitInclusionSpecification;

    /**
     * Gets the value of the inclusionCriterion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inclusionCriterion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInclusionCriterion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMCriterion }
     * 
     * 
     */
    public List<KMCriterion> getInclusionCriterion() {
        if (inclusionCriterion == null) {
            inclusionCriterion = new ArrayList<KMCriterion>();
        }
        return this.inclusionCriterion;
    }

    /**
     * Gets the value of the exclusionCriterion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exclusionCriterion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExclusionCriterion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMCriterion }
     * 
     * 
     */
    public List<KMCriterion> getExclusionCriterion() {
        if (exclusionCriterion == null) {
            exclusionCriterion = new ArrayList<KMCriterion>();
        }
        return this.exclusionCriterion;
    }

    /**
     * Gets the value of the maximumKMsToReturn property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumKMsToReturn() {
        return maximumKMsToReturn;
    }

    /**
     * Sets the value of the maximumKMsToReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumKMsToReturn(BigInteger value) {
        this.maximumKMsToReturn = value;
    }

    /**
     * Gets the value of the minimumKMSearchScore property.
     * 
     */
    public int getMinimumKMSearchScore() {
        return minimumKMSearchScore;
    }

    /**
     * Sets the value of the minimumKMSearchScore property.
     * 
     */
    public void setMinimumKMSearchScore(int value) {
        this.minimumKMSearchScore = value;
    }

    /**
     * Gets the value of the kmTraitInclusionSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link KMTraitInclusionSpecification }
     *     
     */
    public KMTraitInclusionSpecification getKmTraitInclusionSpecification() {
        return kmTraitInclusionSpecification;
    }

    /**
     * Sets the value of the kmTraitInclusionSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMTraitInclusionSpecification }
     *     
     */
    public void setKmTraitInclusionSpecification(KMTraitInclusionSpecification value) {
        this.kmTraitInclusionSpecification = value;
    }

}
