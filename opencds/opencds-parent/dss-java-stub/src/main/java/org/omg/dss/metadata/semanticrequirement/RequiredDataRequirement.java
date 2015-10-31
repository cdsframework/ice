
package org.omg.dss.metadata.semanticrequirement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.knowledgemodule.DataRequirementBase;


/**
 * <p>Java class for RequiredDataRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequiredDataRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DataRequirementBase">
 *       &lt;sequence>
 *         &lt;element name="requiredQueryModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequiredDataRequirement", propOrder = {
    "requiredQueryModelSSId"
})
public class RequiredDataRequirement
    extends DataRequirementBase
{

    protected EntityIdentifier requiredQueryModelSSId;

    /**
     * Gets the value of the requiredQueryModelSSId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getRequiredQueryModelSSId() {
        return requiredQueryModelSSId;
    }

    /**
     * Sets the value of the requiredQueryModelSSId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setRequiredQueryModelSSId(EntityIdentifier value) {
        this.requiredQueryModelSSId = value;
    }

}
