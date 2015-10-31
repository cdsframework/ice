
package org.omg.dss.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * The UnrecognizedScopingEntityException is an exception that is thrown when the service receives a request regarding a scoping entity that is not recognized using its specified identifier.
 * 
 * 
 * <p>Java class for UnrecognizedScopingEntityException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnrecognizedScopingEntityException">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}DSSException">
 *       &lt;attribute name="scopingEntityId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnrecognizedScopingEntityException")
public class UnrecognizedScopingEntityException
    extends DSSException
{

    @XmlAttribute(name = "scopingEntityId", required = true)
    protected String scopingEntityId;

    /**
     * Gets the value of the scopingEntityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScopingEntityId() {
        return scopingEntityId;
    }

    /**
     * Sets the value of the scopingEntityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScopingEntityId(String value) {
        this.scopingEntityId = value;
    }

}
