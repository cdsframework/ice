
package org.omg.dss.knowledgemodule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * A KMConsumerProvidedQueryParameter defines a parameter within the query instance of a data requirement item which is unknown at design time and must be set by the consumer at runtime before knowledge module evaluation. It extends KMItem for common knowledge module item data like name and description.
 * 
 * 
 * <p>Java class for KMConsumerProvidedQueryParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMConsumerProvidedQueryParameter">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMItem">
 *       &lt;sequence>
 *         &lt;element name="informationModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMConsumerProvidedQueryParameter", propOrder = {
    "informationModelSSId"
})
public class KMConsumerProvidedQueryParameter
    extends KMItem
{

    @XmlElement(required = true)
    protected EntityIdentifier informationModelSSId;

    /**
     * Gets the value of the informationModelSSId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getInformationModelSSId() {
        return informationModelSSId;
    }

    /**
     * Sets the value of the informationModelSSId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setInformationModelSSId(EntityIdentifier value) {
        this.informationModelSSId = value;
    }

}
