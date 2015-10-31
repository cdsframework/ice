
package org.omg.dss.metadata.semanticsignifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.ScopedDO;


/**
 * A SemanticSignifier uniquely specifies an information model
 * 
 * <p>Java class for SemanticSignifier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SemanticSignifier">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ScopedDO">
 *       &lt;sequence>
 *         &lt;element name="xsdComputableDefinition" type="{http://www.omg.org/spec/CDSS/201105/dss}XSDComputableDefinition"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticSignifier", propOrder = {
    "xsdComputableDefinition"
})
public class SemanticSignifier
    extends ScopedDO
{

    @XmlElement(required = true)
    protected XSDComputableDefinition xsdComputableDefinition;

    /**
     * Gets the value of the xsdComputableDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link XSDComputableDefinition }
     *     
     */
    public XSDComputableDefinition getXsdComputableDefinition() {
        return xsdComputableDefinition;
    }

    /**
     * Sets the value of the xsdComputableDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link XSDComputableDefinition }
     *     
     */
    public void setXsdComputableDefinition(XSDComputableDefinition value) {
        this.xsdComputableDefinition = value;
    }

}
