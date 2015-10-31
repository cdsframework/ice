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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.OutboundPayloadProcessor;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.parser.IParser;

/**
 *
 * @author Tadesse Sefer
 * 
 */
public class FHIROutboundPayloadProcessor implements OutboundPayloadProcessor {
    private static Log log = LogFactory.getLog(FHIROutboundPayloadProcessor.class);
    private FhirContext fhirContext;

    public FHIROutboundPayloadProcessor() {
        fhirContext = new FhirContext();
    }

    @Override
    public String buildOutput(KnowledgeRepository knowledgeRepository, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
        String payloadMessage = null;
        Bundle bundle = new Bundle();
        try {
            for (Map.Entry<String, List<?>> entry : results.entrySet()) {
                List value = entry.getValue();
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    IResource resource = (IResource) iterator.next();
                    // filter the fhir resources that are marked with "Output" extension
                    if ((resource instanceof IResource) && (hasOutputFlag(resource))) {
                        bundle.addEntry().setResource(resource);
                    }
                }
            }
            IParser jsonParser = fhirContext.newJsonParser();
            payloadMessage = jsonParser.encodeBundleToString(bundle);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("OpenCDS encountered Exception when building fhir output: " + e.getMessage(), e);
        }
        return payloadMessage;
    }

    private boolean hasOutputFlag(IResource resource) {
        BaseResource baseResource = (BaseResource) resource;
        List<ExtensionDt> extensions = baseResource.getAllUndeclaredExtensions();
        for (ExtensionDt extDt : extensions) {
            if(extDt.getValue() instanceof CodeDt) {
                CodeDt code = (CodeDt) extDt.getValue();
                if (extDt.getUrl().equals("http://org.cognitive.cds.invocation.fhir.datanature") && code.getValueAsString().equals("Output")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
