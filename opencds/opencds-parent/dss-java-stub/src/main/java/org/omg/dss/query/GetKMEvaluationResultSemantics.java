
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ServiceRequestBase;


/**
 * <p>Java class for getKMEvaluationResultSemantics complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getKMEvaluationResultSemantics">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="kmId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getKMEvaluationResultSemantics", propOrder = {
    "kmId"
})
public class GetKMEvaluationResultSemantics
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected EntityIdentifier kmId;

    /**
     * Gets the value of the kmId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getKmId() {
        return kmId;
    }

    /**
     * Sets the value of the kmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setKmId(EntityIdentifier value) {
        this.kmId = value;
    }

}
