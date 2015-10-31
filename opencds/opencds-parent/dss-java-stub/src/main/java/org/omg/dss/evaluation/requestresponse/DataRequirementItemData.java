
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;
import org.omg.dss.common.SemanticPayload;


/**
 * The DataRequirementItemData is an object containing data for a particular data requirement item passed as input for a KM evaluation request.
 * 
 * <p>Java class for DataRequirementItemData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataRequirementItemData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="driId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *         &lt;element name="data" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticPayload"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRequirementItemData", propOrder = {
    "driId",
    "data"
})
public class DataRequirementItemData {

    @XmlElement(required = true)
    protected ItemIdentifier driId;
    @XmlElement(required = true)
    protected SemanticPayload data;

    /**
     * Gets the value of the driId property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getDriId() {
        return driId;
    }

    /**
     * Sets the value of the driId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setDriId(ItemIdentifier value) {
        this.driId = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticPayload }
     *     
     */
    public SemanticPayload getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPayload }
     *     
     */
    public void setData(SemanticPayload value) {
        this.data = value;
    }

}
