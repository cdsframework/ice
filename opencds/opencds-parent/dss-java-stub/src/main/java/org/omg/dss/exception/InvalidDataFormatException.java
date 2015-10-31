
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * The InvalidDataFormatException is thrown when data are not consistent with the information model defined by a semantic signifier.
 * 
 * <p>Java class for InvalidDataFormatException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InvalidDataFormatException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
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
@XmlType(name = "InvalidDataFormatException", propOrder = {
    "informationModelSSId"
})
@XmlSeeAlso({
    InvalidDriDataFormatException.class,
    InvalidTraitCriterionDataFormatException.class
})
public class InvalidDataFormatException
    extends DSSException
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
