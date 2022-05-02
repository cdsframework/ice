/**
 * Copyright 2011, 2012 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.service.evaluate;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.ModelExitPoint;
import org.opencds.common.interfaces.OutboundPayloadProcessor;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.api.pool.MarshallerPool;
import org.opencds.config.util.EntityIdentifierUtil;


/**
 * <p>mapper to go from flat form based on javaBeans in the project opencds-vmr
 * to external XML data described by vmr.schema in project opencds-vmr-jaxb-from-schema.
 * 
 * @author David Shields
 * @author Phillip Warner
 * 
 */

public class VMROutboundPayloadProcessor implements OutboundPayloadProcessor {
	private static final Logger log = LogManager.getLogger();
	private static final String EMPTY_STRING = "";
	
	public final MarshallerPool marshallerPool;
	
	public VMROutboundPayloadProcessor(MarshallerPool marshallerPool) {
	    this.marshallerPool = marshallerPool;
	}
	
	@Override
    public byte[] buildOutput(KnowledgeRepository knowledgeRepository, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EntityIdentifier ei = EntityIdentifierUtil.makeEI(dssRequestKMItem.getEvaluationRequestDataItem().getExternalFactModelSSId());
        SSId ssId = SSIdImpl.create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
        SemanticSignifier semanticSignifier = knowledgeRepository.getSemanticSignifierService().find(ssId);
        log.debug("building output for data model: " + ei.toString());
        try {
            Class<ModelExitPoint<?>> exitPoint = (Class<ModelExitPoint<?>>) Class.forName(semanticSignifier
                    .getExitPoint());
            ModelExitPoint<?> wrapper = exitPoint.newInstance();

            JAXBElement<?> jaxbCDSOutput = wrapper.createOutput(knowledgeRepository.getSemanticSignifierService().getResultSetBuilder(ssId), results, dssRequestKMItem);

            // show the tags, but empty payload
            if (jaxbCDSOutput == null) {
                return EMPTY_STRING.getBytes(StandardCharsets.UTF_8);
            }

            StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(output);

            Marshaller marshaller = marshallerPool.borrowObject(semanticSignifier);
            marshaller.marshal(jaxbCDSOutput, streamResult);
            marshallerPool.returnObject(semanticSignifier, marshaller);
        } catch (ClassNotFoundException e) {
            throw new OpenCDSRuntimeException("Cannot find instance of outbound object factory: "
                    + semanticSignifier.getExitPoint(), e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new OpenCDSRuntimeException("Cannot instantiate output object factory: " + semanticSignifier.getExitPoint(),
                    e);
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
	
}


			
		


