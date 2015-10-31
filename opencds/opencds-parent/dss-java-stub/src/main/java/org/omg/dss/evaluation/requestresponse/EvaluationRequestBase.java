
package org.omg.dss.evaluation.requestresponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * The EvaluationRequestBase is the superclass of all knowledge modules evaluation requests. It contains the data passed as input for evaluating one or more knowledge modules.
 * 
 * 
 * <p>Java class for EvaluationRequestBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EvaluationRequestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="clientLanguage" use="required" type="{http://www.omg.org/spec/CDSS/201105/dss}Language" />
 *       &lt;attribute name="clientTimeZoneOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvaluationRequestBase")
@XmlSeeAlso({
    IterativeEvaluationRequest.class,
    EvaluationRequest.class
})
public abstract class EvaluationRequestBase {

    @XmlAttribute(name = "clientLanguage", required = true)
    protected String clientLanguage;
    @XmlAttribute(name = "clientTimeZoneOffset", required = true)
    protected String clientTimeZoneOffset;

    /**
     * Gets the value of the clientLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientLanguage() {
        return clientLanguage;
    }

    /**
     * Sets the value of the clientLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientLanguage(String value) {
        this.clientLanguage = value;
    }

    /**
     * Gets the value of the clientTimeZoneOffset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientTimeZoneOffset() {
        return clientTimeZoneOffset;
    }

    /**
     * Sets the value of the clientTimeZoneOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientTimeZoneOffset(String value) {
        this.clientTimeZoneOffset = value;
    }

}
