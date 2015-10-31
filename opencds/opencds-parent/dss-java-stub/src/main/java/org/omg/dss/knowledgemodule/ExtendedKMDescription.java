
package org.omg.dss.knowledgemodule;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.query.responses.RelatedKM;


/**
 * The response object of the getKMDescription operation in the Query interface. It contains different sections, each describing an aspect of the knowledge module. The knowledge module sections include traits and related knowledge modules.
 * 
 * <p>Java class for ExtendedKMDescription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtendedKMDescription">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}KMDescriptionBase">
 *       &lt;sequence>
 *         &lt;element name="relatedKM" type="{http://www.omg.org/spec/CDSS/201105/dss}RelatedKM" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedKMDescription", propOrder = {
    "relatedKM"
})
public class ExtendedKMDescription
    extends KMDescriptionBase
{

    protected List<RelatedKM> relatedKM;

    /**
     * Gets the value of the relatedKM property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedKM property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedKM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelatedKM }
     * 
     * 
     */
    public List<RelatedKM> getRelatedKM() {
        if (relatedKM == null) {
            relatedKM = new ArrayList<RelatedKM>();
        }
        return this.relatedKM;
    }

}
