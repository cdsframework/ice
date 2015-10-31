
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ServiceRequestBase;


/**
 * <p>Java class for describeSemanticSignifier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeSemanticSignifier">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="semanticSignifierId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeSemanticSignifier", propOrder = {
    "semanticSignifierId"
})
public class DescribeSemanticSignifier
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    protected EntityIdentifier semanticSignifierId;

    /**
     * Gets the value of the semanticSignifierId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getSemanticSignifierId() {
        return semanticSignifierId;
    }

    /**
     * Sets the value of the semanticSignifierId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setSemanticSignifierId(EntityIdentifier value) {
        this.semanticSignifierId = value;
    }

}
