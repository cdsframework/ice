
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.profile.ProfilesByType;


/**
 * <p>Java class for listProfilesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listProfilesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="profilesByType" type="{http://www.omg.org/spec/CDSS/201105/dss}ProfilesByType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listProfilesResponse", propOrder = {
    "profilesByType"
})
public class ListProfilesResponse {

    @XmlElement(required = true)
    protected ProfilesByType profilesByType;

    /**
     * Gets the value of the profilesByType property.
     * 
     * @return
     *     possible object is
     *     {@link ProfilesByType }
     *     
     */
    public ProfilesByType getProfilesByType() {
        return profilesByType;
    }

    /**
     * Sets the value of the profilesByType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfilesByType }
     *     
     */
    public void setProfilesByType(ProfilesByType value) {
        this.profilesByType = value;
    }

}
