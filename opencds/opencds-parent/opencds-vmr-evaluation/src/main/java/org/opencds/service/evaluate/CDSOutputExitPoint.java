/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.service.evaluate;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.pool.MarshallerFactory;
import org.opencds.config.api.ss.ExitPoint;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObjectFactory;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/*
 * <p>mapper to go from flat form based on javaBeans in the project opencds-vmr
 * to external XML data described by vmr.schema in project opencds-vmr-jaxb-from-schema.
 *
 * @author David Shields
 * @author Phillip Warner
 *
 */

public class CDSOutputExitPoint implements ExitPoint {
    private static Log log = LogFactory.getLog(CDSOutputExitPoint.class);
	private static final String EMPTY_STRING = "";

	public final MarshallerFactory marshallerFactory;

	public CDSOutputExitPoint() {
	    this.marshallerFactory = new MarshallerFactory();
	}

	@Override
    public byte[] buildOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EntityIdentifier ei = EntityIdentifierUtil.makeEI(dssRequestKMItem.getEvaluationRequestDataItem().getExternalFactModelSSId());
        log.debug("building output for data model: " + ei.toString());
        try {
            JAXBElement<?> jaxbCDSOutput = createOutput(resultSetBuilder, results, dssRequestKMItem);

            // show the tags, but empty payload
            if (jaxbCDSOutput == null) {
                return EMPTY_STRING.getBytes(StandardCharsets.UTF_8);
            }

            StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(output);

            Marshaller marshaller = marshallerFactory.create(CDSOutput.class);
            marshaller.marshal(jaxbCDSOutput, streamResult);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new EvaluationException("JAXBException in mappingOutbound marshalling cdsOutput: " + e.getMessage(),
                    e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("OpenCDS encountered Exception when building output: " + e.getMessage(),
                    e);
        }

        log.debug("Finished marshalling results to external VMR.");

        return output.toByteArray();
    }

    private JAXBElement<CDSOutput> createOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
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
