
package org.omg.dss.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.query.requests.KMTraitInclusionSpecification;


/**
 * <p>Java class for listKMs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listKMs">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="clientLanguage" type="{http://www.omg.org/spec/CDSS/201105/dss}Language"/>
 *         &lt;element name="kmTraitInclusionSpecification" type="{http://www.omg.org/spec/CDSS/201105/dss}KMTraitInclusionSpecification"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listKMs", propOrder = {
    "clientLanguage",
    "kmTraitInclusionSpecification"
})
public class ListKMs
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected String clientLanguage;
    @XmlElement(required = true)
    protected KMTraitInclusionSpecification kmTraitInclusionSpecification;

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
     * Gets the value of the kmTraitInclusionSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link KMTraitInclusionSpecification }
     *     
     */
    public KMTraitInclusionSpecification getKmTraitInclusionSpecification() {
        return kmTraitInclusionSpecification;
    }

    /**
     * Sets the value of the kmTraitInclusionSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link KMTraitInclusionSpecification }
     *     
     */
    public void setKmTraitInclusionSpecification(KMTraitInclusionSpecification value) {
        this.kmTraitInclusionSpecification = value;
    }

}
