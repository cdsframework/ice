
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The IntermediateState contains contextual information regarding previous evaluation iterations for a knowledge module. The class implementations are vendor-specific.
 * 
 * 
 * <p>Java class for IntermediateState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntermediateState">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="base64EncodedStateData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntermediateState", propOrder = {
    "base64EncodedStateData"
})
public class IntermediateState {

    @XmlElement(required = true)
    protected byte[] base64EncodedStateData;

    /**
     * Gets the value of the base64EncodedStateData property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBase64EncodedStateData() {
        return base64EncodedStateData;
    }

    /**
     * Sets the value of the base64EncodedStateData property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBase64EncodedStateData(byte[] value) {
        this.base64EncodedStateData = ((byte[]) value);
    }

}
