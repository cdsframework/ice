
package org.omg.dss.query.requests;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * The DataRequirementCriterion is an object representing a knowledge module search criterion about Data Requirement Items. It contains a semantic signifier of the data information model and a list of allowed query information models. Each information model, data or query, is identfied using its semantic signifier identifier.   A knowledge module is considered to have satisfied the criterion if both of the following are true: (i) at least one of the Data Requirement Items belonging to the KM uses the specified information model, and (ii) the Data Requirement Item does not use a query model, or it uses one of the query information models specified.
 * 
 * 
 * <p>Java class for DataRequirementCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataRequirementCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion">
 *       &lt;sequence>
 *         &lt;element name="dataInformationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *         &lt;element name="queryInformationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRequirementCriterion", propOrder = {
    "dataInformationModelSSId",
    "queryInformationModelSSId"
})
public class DataRequirementCriterion
    extends KMCriterion
{

    @XmlElement(required = true)
    protected EntityIdentifier dataInformationModelSSId;
    protected List<EntityIdentifier> queryInformationModelSSId;

    /**
     * Gets the value of the dataInformationModelSSId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getDataInformationModelSSId() {
        return dataInformationModelSSId;
    }

    /**
     * Sets the value of the dataInformationModelSSId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setDataInformationModelSSId(EntityIdentifier value) {
        this.dataInformationModelSSId = value;
    }

    /**
     * Gets the value of the queryInformationModelSSId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the queryInformationModelSSId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQueryInformationModelSSId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getQueryInformationModelSSId() {
        if (queryInformationModelSSId == null) {
            queryInformationModelSSId = new ArrayList<EntityIdentifier>();
        }
        return this.queryInformationModelSSId;
    }

}
