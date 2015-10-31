
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.query.responses.RankedKMList;


/**
 * <p>Java class for findKMsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findKMsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rankedKMList" type="{http://www.omg.org/spec/CDSS/201105/dss}RankedKMList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findKMsResponse", propOrder = {
    "rankedKMList"
})
public class FindKMsResponse {

    @XmlElement(required = true)
    protected RankedKMList rankedKMList;

    /**
     * Gets the value of the rankedKMList property.
     * 
     * @return
     *     possible object is
     *     {@link RankedKMList }
     *     
     */
    public RankedKMList getRankedKMList() {
        return rankedKMList;
    }

    /**
     * Sets the value of the rankedKMList property.
     * 
     * @param value
     *     allowed object is
     *     {@link RankedKMList }
     *     
     */
    public void setRankedKMList(RankedKMList value) {
        this.rankedKMList = value;
    }

}
