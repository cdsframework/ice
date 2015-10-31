
package org.omg.dss.knowledgemodule;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * <p>Java class for KMDescriptionBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMDescriptionBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *         &lt;element name="status" type="{http://www.omg.org/spec/CDSS/201105/dss}KMStatus"/>
 *         &lt;element name="traitValue" type="{http://www.omg.org/spec/CDSS/201105/dss}KMTraitValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMDescriptionBase", propOrder = {
    "kmId",
    "status",
    "traitValue"
})
@XmlSeeAlso({
    KMDescription.class,
    ExtendedKMDescription.class
})
public abstract class KMDescriptionBase {

    @XmlElement(required = true)
    protected EntityIdentifier kmId;
    @XmlElement(required = true)
    protected KMStatus status;
    protected List<KMTraitValue> traitValue;

    /**
     * Gets the value of the kmId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getKmId() {
        return kmId;
    }

    /**
     * Sets the value of the kmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setKmId(EntityIdentifier value) {
        this.kmId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link KMStatus }
     *     
     */
    public KMStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMStatus }
     *     
     */
    public void setStatus(KMStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the traitValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the traitValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTraitValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMTraitValue }
     * 
     * 
     */
    public List<KMTraitValue> getTraitValue() {
        if (traitValue == null) {
            traitValue = new ArrayList<KMTraitValue>();
        }
        return this.traitValue;
    }

}
