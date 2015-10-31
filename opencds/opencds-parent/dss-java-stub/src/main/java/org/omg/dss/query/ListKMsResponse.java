
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.query.responses.KMList;


/**
 * <p>Java class for listKMsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listKMsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kmList" type="{http://www.omg.org/spec/CDSS/201105/dss}KMList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listKMsResponse", propOrder = {
    "kmList"
})
public class ListKMsResponse {

    @XmlElement(required = true)
    protected KMList kmList;

    /**
     * Gets the value of the kmList property.
     * 
     * @return
     *     possible object is
     *     {@link KMList }
     *     
     */
    public KMList getKmList() {
        return kmList;
    }

    /**
     * Sets the value of the kmList property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMList }
     *     
     */
    public void setKmList(KMList value) {
        this.kmList = value;
    }

}
