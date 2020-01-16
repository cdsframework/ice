
package org.omg.dss.evaluation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;




/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.omg.spec.cdss._201105.dss package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory { 

    private final static QName _EvaluateResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateResponse");
    private final static QName _EvaluateIterativelyResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIterativelyResponse");
    private final static QName _EvaluateIterativelyAtSpecifiedTime_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIterativelyAtSpecifiedTime");
    private final static QName _EvaluateIteratively_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIteratively");
    private final static QName _EvaluateIterativelyAtSpecifiedTimeResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateIterativelyAtSpecifiedTimeResponse");
    private final static QName _Evaluate_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluate");
    private final static QName _EvaluateAtSpecifiedTimeResponse_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateAtSpecifiedTimeResponse");
    private final static QName _EvaluateAtSpecifiedTime_QNAME = new QName("http://www.omg.org/spec/CDSS/201105/dss", "evaluateAtSpecifiedTime");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.omg.spec.cdss._201105.dss
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EvaluateAtSpecifiedTime }
     * 
     */
    public EvaluateAtSpecifiedTime createEvaluateAtSpecifiedTime() {
        return new EvaluateAtSpecifiedTime();
    }

    /**
     * Create an instance of {@link EvaluateIterativelyAtSpecifiedTimeResponse }
     * 
     */
    public EvaluateIterativelyAtSpecifiedTimeResponse createEvaluateIterativelyAtSpecifiedTimeResponse() {
        return new EvaluateIterativelyAtSpecifiedTimeResponse();
    }

    /**
     * Create an instance of {@link EvaluateIteratively }
     * 
     */
    public EvaluateIteratively createEvaluateIteratively() {
        return new EvaluateIteratively();
    }

    /**
     * Create an instance of {@link EvaluateIterativelyAtSpecifiedTime }
     * 
     */
    public EvaluateIterativelyAtSpecifiedTime createEvaluateIterativelyAtSpecifiedTime() {
        return new EvaluateIterativelyAtSpecifiedTime();
    }

    /**
     * Create an instance of {@link Evaluate }
     * 
     */
    public Evaluate createEvaluate() {
        return new Evaluate();
    }

    /**
     * Create an instance of {@link EvaluateResponse }
     * 
     */
    public EvaluateResponse createEvaluateResponse() {
        return new EvaluateResponse();
    }

    /**
     * Create an instance of {@link EvaluateAtSpecifiedTimeResponse }
     * 
     */
    public EvaluateAtSpecifiedTimeResponse createEvaluateAtSpecifiedTimeResponse() {
        return new EvaluateAtSpecifiedTimeResponse();
    }

    /**
     * Create an instance of {@link EvaluateIterativelyResponse }
     * 
     */
    public EvaluateIterativelyResponse createEvaluateIterativelyResponse() {
        return new EvaluateIterativelyResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateResponse")
    public JAXBElement<EvaluateResponse> createEvaluateResponse(EvaluateResponse value) {
        return new JAXBElement<EvaluateResponse>(_EvaluateResponse_QNAME, EvaluateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIterativelyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIterativelyResponse")
    public JAXBElement<EvaluateIterativelyResponse> createEvaluateIterativelyResponse(EvaluateIterativelyResponse value) {
        return new JAXBElement<EvaluateIterativelyResponse>(_EvaluateIterativelyResponse_QNAME, EvaluateIterativelyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIterativelyAtSpecifiedTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIterativelyAtSpecifiedTime")
    public JAXBElement<EvaluateIterativelyAtSpecifiedTime> createEvaluateIterativelyAtSpecifiedTime(EvaluateIterativelyAtSpecifiedTime value) {
        return new JAXBElement<EvaluateIterativelyAtSpecifiedTime>(_EvaluateIterativelyAtSpecifiedTime_QNAME, EvaluateIterativelyAtSpecifiedTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIteratively }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIteratively")
    public JAXBElement<EvaluateIteratively> createEvaluateIteratively(EvaluateIteratively value) {
        return new JAXBElement<EvaluateIteratively>(_EvaluateIteratively_QNAME, EvaluateIteratively.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateIterativelyAtSpecifiedTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateIterativelyAtSpecifiedTimeResponse")
    public JAXBElement<EvaluateIterativelyAtSpecifiedTimeResponse> createEvaluateIterativelyAtSpecifiedTimeResponse(EvaluateIterativelyAtSpecifiedTimeResponse value) {
        return new JAXBElement<EvaluateIterativelyAtSpecifiedTimeResponse>(_EvaluateIterativelyAtSpecifiedTimeResponse_QNAME, EvaluateIterativelyAtSpecifiedTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Evaluate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluate")
    public JAXBElement<Evaluate> createEvaluate(Evaluate value) {
        return new JAXBElement<Evaluate>(_Evaluate_QNAME, Evaluate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateAtSpecifiedTimeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateAtSpecifiedTimeResponse")
    public JAXBElement<EvaluateAtSpecifiedTimeResponse> createEvaluateAtSpecifiedTimeResponse(EvaluateAtSpecifiedTimeResponse value) {
        return new JAXBElement<EvaluateAtSpecifiedTimeResponse>(_EvaluateAtSpecifiedTimeResponse_QNAME, EvaluateAtSpecifiedTimeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateAtSpecifiedTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omg.org/spec/CDSS/201105/dss", name = "evaluateAtSpecifiedTime")
    public JAXBElement<EvaluateAtSpecifiedTime> createEvaluateAtSpecifiedTime(EvaluateAtSpecifiedTime value) {
        return new JAXBElement<EvaluateAtSpecifiedTime>(_EvaluateAtSpecifiedTime_QNAME, EvaluateAtSpecifiedTime.class, null, value);
    }

}
