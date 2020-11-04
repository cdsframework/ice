package org.opencds.vmr.v1_0.mappings.out;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.ModelExitPoint;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObjectFactory;

public class CdsOutputModelExitPoint implements ModelExitPoint<CDSOutput> {

	private static final Logger log = LogManager.getLogger();

    @Override
    public JAXBElement<CDSOutput> createOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
        String interactionId = dssRequestKMItem.getEvaluationRequestDataItem().getInteractionId();
        String requestedKmId = dssRequestKMItem.getRequestedKmId();

        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " begin buildVMRSchemaResultSet");

        // build the vMR result set
        CDSOutput cdsXMLOutput = (CDSOutput) resultSetBuilder.buildResultSet(results, dssRequestKMItem);

        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " finish buildVMRSchemaResultSet");

        if ((null == cdsXMLOutput) || (( null == cdsXMLOutput.getSimpleOutput() ) && ( null == cdsXMLOutput.getVmrOutput() )) ) {          
            // show the tags, but empty payload
            return null;
        }
        
        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " finished building results as external VMR: " 
                + cdsXMLOutput.getVmrOutput().getTemplateId().toString());

        JAXBElement<CDSOutput> jaxbCDSOutput;

        try {
            log.debug("II: " + interactionId + " KMId: " + requestedKmId + " begin factory.createCdsOutput");

            ObjectFactory factory = new ObjectFactory();
            jaxbCDSOutput = factory.createCdsOutput(cdsXMLOutput);

            log.debug("II: " + interactionId + " KMId: " + requestedKmId + " finish factory.createCdsOutput");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("RuntimeException in OutputFactoryWrapper: "
                    + e.getMessage() + ", vmrOutput=" + cdsXMLOutput.getVmrOutput().toString(), e);
        }
        
        return jaxbCDSOutput;
    }

}
