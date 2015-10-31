
package org.omg.dss.query.requests;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * The abstract base class for a KM criterion.
 * 
 * <p>Java class for KMCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KMCriterion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KMCriterion")
@XmlSeeAlso({
    RelatedKMSearchCriterion.class,
    DataRequirementCriterion.class,
    KMStatusCriterion.class,
    EvaluationResultCriterion.class,
    KMTraitCriterion.class
})
public abstract class KMCriterion {


}
