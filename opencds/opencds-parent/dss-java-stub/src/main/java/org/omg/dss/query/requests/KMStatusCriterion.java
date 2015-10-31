
package org.omg.dss.query.requests;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.knowledgemodule.KMStatus;


/**
 * If a KM has one of the specified statuses, then the knowledge module is considered to have satisfied the criterion.
 * 
 * <p>Java class for KMStatusCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMStatusCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMCriterion">
 *       &lt;sequence>
 *         &lt;element name="kmStatus" type="{http://www.omg.org/spec/CDSS/201105/dss}KMStatus" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMStatusCriterion", propOrder = {
    "kmStatus"
})
public class KMStatusCriterion
    extends KMCriterion
{

    @XmlElement(required = true)
    protected List<KMStatus> kmStatus;

    /**
     * Gets the value of the kmStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kmStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKmStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KMStatus }
     * 
     * 
     */
    public List<KMStatus> getKmStatus() {
        if (kmStatus == null) {
            kmStatus = new ArrayList<KMStatus>();
        }
        return this.kmStatus;
    }

}
