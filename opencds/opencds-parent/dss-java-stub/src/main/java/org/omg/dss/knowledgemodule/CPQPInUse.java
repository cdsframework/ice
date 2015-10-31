
package org.omg.dss.knowledgemodule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * This object contains information about Consumer Provided Query Parameter objects that are used within a specific Data Requirement Item in its query instance. It contains the identifier of the Consumer Provided Query parameter and its path within the query instance. 
 * 
 * <p>Java class for CPQPInUse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CPQPInUse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="specificationPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cpqpItemId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CPQPInUse", propOrder = {
    "specificationPath",
    "cpqpItemId"
})
public class CPQPInUse {

    @XmlElement(required = true)
    protected String specificationPath;
    @XmlElement(required = true)
    protected ItemIdentifier cpqpItemId;

    /**
     * Gets the value of the specificationPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecificationPath() {
        return specificationPath;
    }

    /**
     * Sets the value of the specificationPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecificationPath(String value) {
        this.specificationPath = value;
    }

    /**
     * Gets the value of the cpqpItemId property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getCpqpItemId() {
        return cpqpItemId;
    }

    /**
     * Sets the value of the cpqpItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setCpqpItemId(ItemIdentifier value) {
        this.cpqpItemId = value;
    }

}
