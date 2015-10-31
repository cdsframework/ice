
package org.omg.dss.exception;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * The abstract superclass for exceptions
 * 
 * <p>Java class for DSSException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DSSException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorMessage" type="{http://www.omg.org/spec/CDSS/201105/dss}ErrorMessage" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DSSException", propOrder = {
    "errorMessage"
})
@XmlSeeAlso({
    EvaluationException.class,
    DSSRuntimeException.class,
    UnrecognizedScopedEntityException.class,
    RequiredDataNotProvidedException.class,
    UnrecognizedLanguageException.class,
    InvalidTimeZoneOffsetException.class,
    UnrecognizedTraitCriterionException.class,
    UnrecognizedScopingEntityException.class,
    UnsupportedLanguageException.class,
    InvalidDataFormatException.class
})
public abstract class DSSException {

    @XmlElement(required = true)
    protected List<String> errorMessage;

    /**
     * Gets the value of the errorMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errorMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getErrorMessage() {
        if (errorMessage == null) {
            errorMessage = new ArrayList<String>();
        }
        return this.errorMessage;
    }

}
