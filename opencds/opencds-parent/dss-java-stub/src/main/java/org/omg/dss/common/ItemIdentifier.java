
package org.omg.dss.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The Item Identifier (ItemIdentifier) consists of the Entity Identifier of its containing entity, as well as a String "itemId."  The "itemId" attribute must be unique within the scope of the containing entity, and the complete ItemIdentifier (i.e., combination of containingEntityId + itemId) must be globally unique.
 * 
 * 
 * <p>Java class for ItemIdentifier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemIdentifier">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="containingEntityId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier"/>
 *       &lt;/sequence>
 *       &lt;attribute name="itemId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemIdentifier", propOrder = {
    "containingEntityId"
})
public class ItemIdentifier {

    @XmlElement(required = true)
    protected EntityIdentifier containingEntityId;
    @XmlAttribute(name = "itemId", required = true)
    protected String itemId;

    /**
     * Gets the value of the containingEntityId property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getContainingEntityId() {
        return containingEntityId;
    }

    /**
     * Sets the value of the containingEntityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setContainingEntityId(EntityIdentifier value) {
        this.containingEntityId = value;
    }

    /**
     * Gets the value of the itemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Sets the value of the itemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemId(String value) {
        this.itemId = value;
    }

}
