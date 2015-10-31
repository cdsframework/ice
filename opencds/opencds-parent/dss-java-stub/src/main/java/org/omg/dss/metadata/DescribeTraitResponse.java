
package org.omg.dss.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.metadata.trait.Trait;


/**
 * <p>Java class for describeTraitResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="describeTraitResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="trait" type="{http://www.omg.org/spec/CDSS/201105/dss}Trait"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "describeTraitResponse", propOrder = {
    "trait"
})
public class DescribeTraitResponse {

    @XmlElement(required = true)
    protected Trait trait;

    /**
     * Gets the value of the trait property.
     * 
     * @return
     *     possible object is
     *     {@link Trait }
     *     
     */
    public Trait getTrait() {
        return trait;
    }

    /**
     * Sets the value of the trait property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trait }
     *     
     */
    public void setTrait(Trait value) {
        this.trait = value;
    }

}
