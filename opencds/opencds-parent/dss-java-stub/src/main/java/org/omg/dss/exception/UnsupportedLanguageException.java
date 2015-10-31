
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The UnsupportedLanguageException is thrown when the language is recognized but not supported.
 * 
 * 
 * <p>Java class for UnsupportedLanguageException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnsupportedLanguageException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;sequence>
 *         &lt;element name="unsupportedLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsupportedLanguageException", propOrder = {
    "unsupportedLanguage"
})
public class UnsupportedLanguageException
    extends DSSException
{

    @XmlElement(required = true)
    protected String unsupportedLanguage;

    /**
     * Gets the value of the unsupportedLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnsupportedLanguage() {
        return unsupportedLanguage;
    }

    /**
     * Sets the value of the unsupportedLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnsupportedLanguage(String value) {
        this.unsupportedLanguage = value;
    }

}
