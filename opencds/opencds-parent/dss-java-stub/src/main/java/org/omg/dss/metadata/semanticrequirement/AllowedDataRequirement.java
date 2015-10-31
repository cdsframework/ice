
package org.omg.dss.metadata.semanticrequirement;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.knowledgemodule.DataRequirementBase;


/**
 * <p>Java class for AllowedDataRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AllowedDataRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DataRequirementBase">
 *       &lt;sequence>
 *         &lt;element name="allowedQueryModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllowedDataRequirement", propOrder = {
    "allowedQueryModelSSId"
})
public class AllowedDataRequirement
    extends DataRequirementBase
{

    protected List<EntityIdentifier> allowedQueryModelSSId;

    /**
     * Gets the value of the allowedQueryModelSSId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedQueryModelSSId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedQueryModelSSId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getAllowedQueryModelSSId() {
        if (allowedQueryModelSSId == null) {
            allowedQueryModelSSId = new ArrayList<EntityIdentifier>();
        }
        return this.allowedQueryModelSSId;
    }

}
