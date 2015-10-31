
package org.omg.dss.metadata.semanticsignifier;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A computable information model based on the use of an XML Schema Definition (XSD), zero or more Schematrons, an optional narrative guide to further restrict the model, and the name of the global element that serves as the root element of the information model.  Note that an XSD used in this context must have the root element defined as a global element so that it can be directly used for automated instance validation.
 * 
 * Other potential approaches to defining computable information models are possible (e.g., using Document Type Definitions), but this is the computable information model required for the DSS's XML Web Service Platform Specific Model. 
 * 
 * 
 * <p>Java class for XSDComputableDefinition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XSDComputableDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ComputableDefinition">
 *       &lt;sequence>
 *         &lt;element name="xsdRootGlobalElementName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xsdURL" type="{http://www.omg.org/spec/CDSS/201105/dss}URL"/>
 *         &lt;element name="schematronURL" type="{http://www.omg.org/spec/CDSS/201105/dss}URL" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="narrativeModelRestrictionGuideURL" type="{http://www.omg.org/spec/CDSS/201105/dss}URL" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XSDComputableDefinition", propOrder = {
    "xsdRootGlobalElementName",
    "xsdURL",
    "schematronURL",
    "narrativeModelRestrictionGuideURL"
})
public class XSDComputableDefinition
    extends ComputableDefinition
{

    @XmlElement(required = true)
    protected String xsdRootGlobalElementName;
    @XmlElement(required = true)
    protected String xsdURL;
    protected List<String> schematronURL;
    protected String narrativeModelRestrictionGuideURL;

    /**
     * Gets the value of the xsdRootGlobalElementName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXsdRootGlobalElementName() {
        return xsdRootGlobalElementName;
    }

    /**
     * Sets the value of the xsdRootGlobalElementName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXsdRootGlobalElementName(String value) {
        this.xsdRootGlobalElementName = value;
    }

    /**
     * Gets the value of the xsdURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXsdURL() {
        return xsdURL;
    }

    /**
     * Sets the value of the xsdURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXsdURL(String value) {
        this.xsdURL = value;
    }

    /**
     * Gets the value of the schematronURL property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the schematronURL property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSchematronURL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSchematronURL() {
        if (schematronURL == null) {
            schematronURL = new ArrayList<String>();
        }
        return this.schematronURL;
    }

    /**
     * Gets the value of the narrativeModelRestrictionGuideURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarrativeModelRestrictionGuideURL() {
        return narrativeModelRestrictionGuideURL;
    }

    /**
     * Sets the value of the narrativeModelRestrictionGuideURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarrativeModelRestrictionGuideURL(String value) {
        this.narrativeModelRestrictionGuideURL = value;
    }

}
