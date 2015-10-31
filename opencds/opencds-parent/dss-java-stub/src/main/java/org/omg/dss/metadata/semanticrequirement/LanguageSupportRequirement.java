
package org.omg.dss.metadata.semanticrequirement;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LanguageSupportRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LanguageSupportRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement">
 *       &lt;sequence>
 *         &lt;element name="supportedLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguageSupportRequirement", propOrder = {
    "supportedLanguage"
})
public class LanguageSupportRequirement
    extends SemanticRequirement
{

    @XmlElement(required = true)
    protected List<String> supportedLanguage;

    /**
     * Gets the value of the supportedLanguage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedLanguage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSupportedLanguage() {
        if (supportedLanguage == null) {
            supportedLanguage = new ArrayList<String>();
        }
        return this.supportedLanguage;
    }

}
