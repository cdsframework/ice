package org.opencds.service.evaluate;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.ModelExitPoint;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;
import ca.uhn.fhir.model.dstu2.resource.CommunicationRequest;

public class FhirOutputModelExitPoint implements ModelExitPoint<CommunicationRequest> {
    private static Log log = LogFactory.getLog(FhirOutputModelExitPoint.class);

    @Override
    public JAXBElement<CommunicationRequest> createOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
        String interactionId = dssRequestKMItem.getEvaluationRequestDataItem().getInteractionId();
        String requestedKmId = dssRequestKMItem.getRequestedKmId();

        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " begin buildVMRSchemaResultSet");

        // build the vMR result set
        CommunicationRequest cdsXMLOutput = (CommunicationRequest) resultSetBuilder.buildResultSet(results, dssRequestKMItem);

        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " finish buildVMRSchemaResultSet");
        
        // we are not using this class, it's a requirement for the semantic signifier configuration.
        // We will use the HAPI Fhir xml parser if there is need.
        JAXBElement<CommunicationRequest> jaxbCDSOutput = null;

       /* try {
            log.debug("II: " + interactionId + " KMId: " + requestedKmId + " begin factory.createCdsOutput");

            ObjectFactory factory = new ObjectFactory();
            jaxbCDSOutput = factory.createCdsOutput(cdsXMLOutput);

            log.debug("II: " + interactionId + " KMId: " + requestedKmId + " finish factory.createCdsOutput");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("RuntimeException in OutputFactoryWrapper: "
                    + e.getMessage() + ", vmrOutput=" + cdsXMLOutput.getVmrOutput().toString(), e);
        }*/
        
        return jaxbCDSOutput; 
    }

}
