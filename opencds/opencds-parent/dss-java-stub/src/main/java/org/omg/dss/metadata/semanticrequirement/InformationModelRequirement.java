
package org.omg.dss.metadata.semanticrequirement;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.omg.dss.common.EntityIdentifier;


/**
 * The InformationModelRequirement specifies the information models that (a) can or (b) must be used by DSS knowledge modules claiming conformance to this requirement.
 * 
 * This information model requirement consists of one or more of the following:
 * (i) allowedDataRequirement - specifies the superset of data requirement models and associated query models that can be used
 * (ii) requiredDataRequirement - specifies the data requirement models and associated query models, if any, that must be used
 * (iii) allowedWarningModelSSId - specifies the superset of models that can be used by the service to provide warnings regarding evaluations
 * (iv) allowedEvaluationResultModelSSId - specifies the superset of evaluation result models that can be used
 * (v) requiredEvaluationResultModelSSId - specifies the evaluation result models that must be used
 * 
 * <p>Java class for InformationModelRequirement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InformationModelRequirement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/CDSS/201105/dss}SemanticRequirement">
 *       &lt;sequence>
 *         &lt;element name="allowedWarningModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="allowedEvaluationResultModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="allowedDataRequirement" type="{http://www.omg.org/spec/CDSS/201105/dss}AllowedDataRequirement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="requiredDataRequirement" type="{http://www.omg.org/spec/CDSS/201105/dss}RequiredDataRequirement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="requiredEvaluationResultModelSSId" type="{http://www.omg.org/spec/CDSS/201105/dss}EntityIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InformationModelRequirement", propOrder = {
    "allowedWarningModelSSId",
    "allowedEvaluationResultModelSSId",
    "allowedDataRequirement",
    "requiredDataRequirement",
    "requiredEvaluationResultModelSSId"
})
public class InformationModelRequirement
    extends SemanticRequirement
{

    protected List<EntityIdentifier> allowedWarningModelSSId;
    protected List<EntityIdentifier> allowedEvaluationResultModelSSId;
    protected List<AllowedDataRequirement> allowedDataRequirement;
    protected List<RequiredDataRequirement> requiredDataRequirement;
    protected List<EntityIdentifier> requiredEvaluationResultModelSSId;

    /**
     * Gets the value of the allowedWarningModelSSId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedWarningModelSSId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedWarningModelSSId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getAllowedWarningModelSSId() {
        if (allowedWarningModelSSId == null) {
            allowedWarningModelSSId = new ArrayList<EntityIdentifier>();
        }
        return this.allowedWarningModelSSId;
    }

    /**
     * Gets the value of the allowedEvaluationResultModelSSId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedEvaluationResultModelSSId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedEvaluationResultModelSSId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getAllowedEvaluationResultModelSSId() {
        if (allowedEvaluationResultModelSSId == null) {
            allowedEvaluationResultModelSSId = new ArrayList<EntityIdentifier>();
        }
        return this.allowedEvaluationResultModelSSId;
    }

    /**
     * Gets the value of the allowedDataRequirement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedDataRequirement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedDataRequirement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllowedDataRequirement }
     * 
     * 
     */
    public List<AllowedDataRequirement> getAllowedDataRequirement() {
        if (allowedDataRequirement == null) {
            allowedDataRequirement = new ArrayList<AllowedDataRequirement>();
        }
        return this.allowedDataRequirement;
    }

    /**
     * Gets the value of the requiredDataRequirement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredDataRequirement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredDataRequirement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequiredDataRequirement }
     * 
     * 
     */
    public List<RequiredDataRequirement> getRequiredDataRequirement() {
        if (requiredDataRequirement == null) {
            requiredDataRequirement = new ArrayList<RequiredDataRequirement>();
        }
        return this.requiredDataRequirement;
    }

    /**
     * Gets the value of the requiredEvaluationResultModelSSId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredEvaluationResultModelSSId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredEvaluationResultModelSSId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityIdentifier }
     * 
     * 
     */
    public List<EntityIdentifier> getRequiredEvaluationResultModelSSId() {
        if (requiredEvaluationResultModelSSId == null) {
            requiredEvaluationResultModelSSId = new ArrayList<EntityIdentifier>();
        }
        return this.requiredEvaluationResultModelSSId;
    }

}
