
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The InvalidTimeZoneOffsetException is thrown when the time zone offset specified is unrecognized.  The time zone offset represents the offset from Universal Coordinated Time (UTC).  The offset should be expressed as +/- hh:mm, e.g., 00:00, -05:00, +07:00
 * 
 * <p>Java class for InvalidTimeZoneOffsetException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvalidTimeZoneOffsetException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="invalidTimeZoneOffset" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvalidTimeZoneOffsetException", propOrder = {
    "invalidTimeZoneOffset"
})
public class InvalidTimeZoneOffsetException
    extends DSSException
{

    @XmlElement(required = true)
    protected String invalidTimeZoneOffset;

    /**
     * Gets the value of the invalidTimeZoneOffset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvalidTimeZoneOffset() {
        return invalidTimeZoneOffset;
    }

    /**
     * Sets the value of the invalidTimeZoneOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvalidTimeZoneOffset(String value) {
        this.invalidTimeZoneOffset = value;
    }

}
