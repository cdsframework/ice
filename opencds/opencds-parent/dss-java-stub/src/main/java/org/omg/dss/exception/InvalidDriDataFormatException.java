
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ItemIdentifier;


/**
 * A InvalidDriDataFormatException is thrown when trying to evaluate a KM but data provided for one of its Data Requirement Items has an invalid format.
 * 
 * <p>Java class for InvalidDriDataFormatException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvalidDriDataFormatException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}InvalidDataFormatException">
 *       &lt;sequence>
 *         &lt;element name="driId" type="{http://www.omg.org/spec/CDSS/201105/dss}ItemIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidDriDataFormatException", propOrder = {
    "driId"
})
public class InvalidDriDataFormatException
    extends InvalidDataFormatException
{

    @XmlElement(required = true)
    protected ItemIdentifier driId;

    /**
     * Gets the value of the driId property.
     * 
     * @return
     *     possible object is
     *     {@link ItemIdentifier }
     *     
     */
    public ItemIdentifier getDriId() {
        return driId;
    }

    /**
     * Sets the value of the driId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemIdentifier }
     *     
     */
    public void setDriId(ItemIdentifier value) {
        this.driId = value;
    }

}
