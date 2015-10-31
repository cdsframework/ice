
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ScopingEntity;


/**
 * <p>Java class for describeScopingEntityResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeScopingEntityResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="scopingEntity" type="{http://www.omg.org/spec/CDSS/201105/dss}ScopingEntity"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeScopingEntityResponse", propOrder = {
    "scopingEntity"
})
public class DescribeScopingEntityResponse {

    @XmlElement(required = true)
    protected ScopingEntity scopingEntity;

    /**
     * Gets the value of the scopingEntity property.
     * 
     * @return
     *     possible object is
     *     {@link ScopingEntity }
     *     
     */
    public ScopingEntity getScopingEntity() {
        return scopingEntity;
    }

    /**
     * Sets the value of the scopingEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScopingEntity }
     *     
     */
    public void setScopingEntity(ScopingEntity value) {
        this.scopingEntity = value;
    }

}
