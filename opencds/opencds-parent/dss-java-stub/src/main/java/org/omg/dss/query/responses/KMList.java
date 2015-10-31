
package org.omg.dss.query.responses;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.knowledgemodule.KMDescription;


/**
 * A list of knowledge modules.
 * 
 * <p>Java class for KMList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmDescription" type="{http://www.omg.org/spec/CDSS/201105/dss}KMDescription" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMList", propOrder = {
    "kmDescription"
})
public class KMList {

    protected List<KMDescription> kmDescription;

    /**
     * Gets the value of the kmDescription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kmDescription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKmDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMDescription }
     * 
     * 
     */
    public List<KMDescription> getKmDescription() {
        if (kmDescription == null) {
            kmDescription = new ArrayList<KMDescription>();
        }
        return this.kmDescription;
    }

}
