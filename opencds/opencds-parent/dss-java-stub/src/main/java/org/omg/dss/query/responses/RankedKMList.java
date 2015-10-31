
package org.omg.dss.query.responses;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * List of KMs, ranked by relevant (most relevant KM listed first).  Note that KMs fully matching client search criteria (i.e., with a score of 100) may still have relative relevance expressed through the order used.
 * 
 * <p>Java class for RankedKMList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RankedKMList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rankedKM" type="{http://www.omg.org/spec/CDSS/201105/dss}RankedKM" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RankedKMList", propOrder = {
    "rankedKM"
})
public class RankedKMList {

    protected List<RankedKM> rankedKM;

    /**
     * Gets the value of the rankedKM property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rankedKM property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRankedKM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RankedKM }
     * 
     * 
     */
    public List<RankedKM> getRankedKM() {
        if (rankedKM == null) {
            rankedKM = new ArrayList<RankedKM>();
        }
        return this.rankedKM;
    }

}
