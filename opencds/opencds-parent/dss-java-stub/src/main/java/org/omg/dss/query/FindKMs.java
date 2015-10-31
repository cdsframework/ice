
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.query.requests.KMSearchCriteria;


/**
 * <p>Java class for findKMs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findKMs">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="clientLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language"/>
 *         &lt;element name="kmSearchCriteria" type="{http://www.omg.org/spec/CDSS/201105/dss}KMSearchCriteria"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findKMs", propOrder = {
    "clientLanguage",
    "kmSearchCriteria"
})
public class FindKMs
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected String clientLanguage;
    @XmlElement(required = true)
    protected KMSearchCriteria kmSearchCriteria;

    /**
     * Gets the value of the clientLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientLanguage() {
        return clientLanguage;
    }

    /**
     * Sets the value of the clientLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientLanguage(String value) {
        this.clientLanguage = value;
    }

    /**
     * Gets the value of the kmSearchCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link KMSearchCriteria }
     *     
     */
    public KMSearchCriteria getKmSearchCriteria() {
        return kmSearchCriteria;
    }

    /**
     * Sets the value of the kmSearchCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMSearchCriteria }
     *     
     */
    public void setKmSearchCriteria(KMSearchCriteria value) {
        this.kmSearchCriteria = value;
    }

}
