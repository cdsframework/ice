
package org.omg.dss.metadata.profile;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Service profiles grouped by type.
 * 
 * <p>Java class for ProfilesByType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProfilesByType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="profilesOfType" type="{http://www.omg.org/spec/CDSS/201105/dss}ProfilesOfType" maxOccurs="3" minOccurs="3"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProfilesByType", propOrder = {
    "profilesOfType"
})
public class ProfilesByType {

    @XmlElement(required = true)
    protected List<ProfilesOfType> profilesOfType;

    /**
     * Gets the value of the profilesOfType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profilesOfType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfilesOfType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfilesOfType }
     * 
     * 
     */
    public List<ProfilesOfType> getProfilesOfType() {
        if (profilesOfType == null) {
            profilesOfType = new ArrayList<ProfilesOfType>();
        }
        return this.profilesOfType;
    }

}
