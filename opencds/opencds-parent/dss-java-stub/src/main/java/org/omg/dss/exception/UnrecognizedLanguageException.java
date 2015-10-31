
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The UnrecognizedLanguageException is thrown when the language is unrecognized.
 * 
 * <p>Java class for UnrecognizedLanguageException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnrecognizedLanguageException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="unrecognizedLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnrecognizedLanguageException", propOrder = {
    "unrecognizedLanguage"
})
public class UnrecognizedLanguageException
    extends DSSException
{

    @XmlElement(required = true)
    protected String unrecognizedLanguage;

    /**
     * Gets the value of the unrecognizedLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnrecognizedLanguage() {
        return unrecognizedLanguage;
    }

    /**
     * Sets the value of the unrecognizedLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnrecognizedLanguage(String value) {
        this.unrecognizedLanguage = value;
    }

}
