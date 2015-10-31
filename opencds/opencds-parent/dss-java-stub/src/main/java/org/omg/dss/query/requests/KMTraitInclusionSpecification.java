
package org.omg.dss.query.requests;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * Specification of which KM traits to include in the the search result.
 * 
 * <p>Java class for KMTraitInclusionSpecification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMTraitInclusionSpecification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfTraitToIncludeInSearchResult" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMTraitInclusionSpecification", propOrder = {
    "idOfTraitToIncludeInSearchResult"
})
public class KMTraitInclusionSpecification {

    protected List<EntityIdentifier> idOfTraitToIncludeInSearchResult;

    /**
     * Gets the value of the idOfTraitToIncludeInSearchResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idOfTraitToIncludeInSearchResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdOfTraitToIncludeInSearchResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getIdOfTraitToIncludeInSearchResult() {
        if (idOfTraitToIncludeInSearchResult == null) {
            idOfTraitToIncludeInSearchResult = new ArrayList<EntityIdentifier>();
        }
        return this.idOfTraitToIncludeInSearchResult;
    }

}
