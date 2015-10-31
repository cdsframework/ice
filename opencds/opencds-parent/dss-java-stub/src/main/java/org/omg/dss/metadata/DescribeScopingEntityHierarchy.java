
package org.omg.dss.metadata;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ServiceRequestBase;


/**
 * <p>Java class for describeScopingEntityHierarchy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeScopingEntityHierarchy">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="scopingEntityId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maximumDescendantDepth" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeScopingEntityHierarchy", propOrder = {
    "scopingEntityId",
    "maximumDescendantDepth"
})
public class DescribeScopingEntityHierarchy
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected String scopingEntityId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maximumDescendantDepth;

    /**
     * Gets the value of the scopingEntityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScopingEntityId() {
        return scopingEntityId;
    }

    /**
     * Sets the value of the scopingEntityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScopingEntityId(String value) {
        this.scopingEntityId = value;
    }

    /**
     * Gets the value of the maximumDescendantDepth property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumDescendantDepth() {
        return maximumDescendantDepth;
    }

    /**
     * Sets the value of the maximumDescendantDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumDescendantDepth(BigInteger value) {
        this.maximumDescendantDepth = value;
    }

}
