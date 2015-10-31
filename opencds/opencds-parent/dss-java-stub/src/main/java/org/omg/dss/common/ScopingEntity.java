
package org.omg.dss.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * A scoping entity has a unique, fully-qualified identifier, which must start with lowercase English representations of one of the top-level Internet domain names, currently com, edu, gov, mil, net, org, or one of the English two-letter codes identifying countries as specified in ISO Standard 3166-1.  Subsequently, the id must start by defining the domain name that is associated with the scoping entity (e.g., com.clinica, com.dbmotion, edu.duke, org.hl7).  Subsequent identification within the domain associated with the scoping entity, if any, may be specified as is appropriate for the internal naming conventions by the scoping entity.  
 * 
 * A scoping entity may create hierarchical structures.
 * 
 * <p>Java class for ScopingEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScopingEntity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DescribedDO">
 *       &lt;sequence>
 *         &lt;element name="childScopingEntity" type="{http://www.omg.org/spec/CDSS/201105/dss}ScopingEntity" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parentSEId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScopingEntity", propOrder = {
    "childScopingEntity"
})
public class ScopingEntity
    extends DescribedDO
{

    protected List<ScopingEntity> childScopingEntity;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "parentSEId")
    protected String parentSEId;

    /**
     * Gets the value of the childScopingEntity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childScopingEntity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildScopingEntity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScopingEntity }
     * 
     * 
     */
    public List<ScopingEntity> getChildScopingEntity() {
        if (childScopingEntity == null) {
            childScopingEntity = new ArrayList<ScopingEntity>();
        }
        return this.childScopingEntity;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the parentSEId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentSEId() {
        return parentSEId;
    }

    /**
     * Sets the value of the parentSEId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentSEId(String value) {
        this.parentSEId = value;
    }

}
