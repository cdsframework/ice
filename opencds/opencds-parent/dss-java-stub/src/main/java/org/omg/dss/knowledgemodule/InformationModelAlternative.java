
package org.omg.dss.knowledgemodule;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.SemanticPayload;


/**
 * This object represents one of potentially several alternative information models for a KM data requirement item.  This object specifies the expected information model for the data using a semantic signifier. Optionally, it may specify the semantic signifier of the query model and its instance. In addition, it may also specify one or more Consumer Provided Query Parameters that are used in the query. For each query parameter in use, it specifies its identifier and its path in the query.
 * 
 * 
 * <p>Java class for InformationModelAlternative complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InformationModelAlternative">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="informationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *         &lt;element name="query" type="{http://www.omg.org/spec/CDSS/201105/dss}SemanticPayload" minOccurs="0"/>
 *         &lt;element name="cpqpInUse" type="{http://www.omg.org/spec/CDSS/201105/dss}CPQPInUse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InformationModelAlternative", propOrder = {
    "informationModelSSId",
    "query",
    "cpqpInUse"
})
public class InformationModelAlternative {

    @XmlElement(required = true)
    protected EntityIdentifier informationModelSSId;
    protected SemanticPayload query;
    protected List<CPQPInUse> cpqpInUse;

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
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link SemanticPayload }
     *     
     */
    public SemanticPayload getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link SemanticPayload }
     *     
     */
    public void setQuery(SemanticPayload value) {
        this.query = value;
    }

    /**
     * Gets the value of the cpqpInUse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpqpInUse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCpqpInUse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CPQPInUse }
     * 
     * 
     */
    public List<CPQPInUse> getCpqpInUse() {
        if (cpqpInUse == null) {
            cpqpInUse = new ArrayList<CPQPInUse>();
        }
        return this.cpqpInUse;
    }

}
