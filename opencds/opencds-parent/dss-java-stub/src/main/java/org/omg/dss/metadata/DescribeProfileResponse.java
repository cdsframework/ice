
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.profile.ServiceProfile;


/**
 * <p>Java class for describeProfileResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeProfileResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceProfile" type="{http://www.omg.org/spec/CDSS/201105/dss}ServiceProfile"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeProfileResponse", propOrder = {
    "serviceProfile"
})
public class DescribeProfileResponse {

    @XmlElement(required = true)
    protected ServiceProfile serviceProfile;

    /**
     * Gets the value of the serviceProfile property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceProfile }
     *     
     */
    public ServiceProfile getServiceProfile() {
        return serviceProfile;
    }

    /**
     * Sets the value of the serviceProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceProfile }
     *     
     */
    public void setServiceProfile(ServiceProfile value) {
        this.serviceProfile = value;
    }

}
