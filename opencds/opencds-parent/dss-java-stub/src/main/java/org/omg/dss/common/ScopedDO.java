
package org.omg.dss.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.profile.ServiceProfile;
import org.omg.dss.metadata.semanticrequirement.SemanticRequirement;
import org.omg.dss.metadata.semanticsignifier.SemanticSignifier;
import org.omg.dss.metadata.trait.Trait;


/**
 * <p>Java class for ScopedDO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScopedDO">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DescribedDO">
 *       &lt;sequence>
 *         &lt;element name="entityId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScopedDO", propOrder = {
    "entityId"
})
@XmlSeeAlso({
    SemanticSignifier.class,
    Trait.class,
    SemanticRequirement.class,
    ServiceProfile.class
})
public abstract class ScopedDO
    extends DescribedDO
{

    @XmlElement(required = true)
    protected EntityIdentifier entityId;

    /**
     * Gets the value of the entityId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getEntityId() {
        return entityId;
    }

    /**
     * Sets the value of the entityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setEntityId(EntityIdentifier value) {
        this.entityId = value;
    }

}
