
package org.omg.dss.evaluation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationRequest;


/**
 * <p>Java class for evaluateIterativelyAtSpecifiedTime complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evaluateIterativelyAtSpecifiedTime">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}ServiceRequestBase">
 *       &lt;sequence>
 *         &lt;element name="specifiedTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="iterativeEvaluationRequest" type="{http://www.omg.org/spec/CDSS/201105/dss}IterativeEvaluationRequest"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluateIterativelyAtSpecifiedTime", propOrder = {
    "specifiedTime",
    "iterativeEvaluationRequest"
})
public class EvaluateIterativelyAtSpecifiedTime
    extends ServiceRequestBase
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar specifiedTime;
    @XmlElement(required = true)
    protected IterativeEvaluationRequest iterativeEvaluationRequest;

    /**
     * Gets the value of the specifiedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSpecifiedTime() {
        return specifiedTime;
    }

    /**
     * Sets the value of the specifiedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSpecifiedTime(XMLGregorianCalendar value) {
        this.specifiedTime = value;
    }

    /**
     * Gets the value of the iterativeEvaluationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link IterativeEvaluationRequest }
     *     
     */
    public IterativeEvaluationRequest getIterativeEvaluationRequest() {
        return iterativeEvaluationRequest;
    }

    /**
     * Sets the value of the iterativeEvaluationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link IterativeEvaluationRequest }
     *     
     */
    public void setIterativeEvaluationRequest(IterativeEvaluationRequest value) {
        this.iterativeEvaluationRequest = value;
    }

}
