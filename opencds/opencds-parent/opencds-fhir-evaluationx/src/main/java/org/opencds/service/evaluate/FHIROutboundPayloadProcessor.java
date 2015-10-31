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
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.CommunicationRequest;
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
     				IResource resource = (IResource)iterator.next();
     				    // At this time we are interested in only CommunicationRequest and Provenance that are set through namedObject Drools global valiable..
             			 if ((resource instanceof IResource) && (resource.getResourceName().equalsIgnoreCase("CommunicationRequest") ||
             				resource.getResourceName().equalsIgnoreCase("Provenance") )) {
         					 bundle.addEntry().setResource(resource);
         				}
     			}
             }
        	 // Even if the rule didn't fire we want to send empty bundle.
        	//if(!bundle.isEmpty()){ 
        		IParser jsonParser = fhirContext.newJsonParser();
         		payloadMessage = jsonParser.encodeBundleToString(bundle);
        	/*}
        	else {
        		log.debug("No CDS result output came from the Rule session. It may be an indication of no rule fired.");
        		//return "No CDS result output came from the Rule session. It may be an indication of no rule fired.";
        		return "";
        	}*/
           
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("OpenCDS encountered Exception when building fhir output: " + e.getMessage(), e);
        }
        log.debug("Finished marshalling results to CommunicatioRequest FHIR profile.");

        return payloadMessage;
    }
	
}


			
		


