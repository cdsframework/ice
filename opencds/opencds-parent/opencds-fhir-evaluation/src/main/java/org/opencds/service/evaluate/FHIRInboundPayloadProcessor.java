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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.InboundPayloadProcessor;
import org.opencds.common.structures.Payload;
import org.opencds.config.api.model.SemanticSignifier;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.parser.IParser;

/**
 * @author Tadesse Sefer
 * @author Jerry Goodnough
 *
 */
public class FHIRInboundPayloadProcessor implements InboundPayloadProcessor, Cloneable {

	private static Log log = LogFactory.getLog(FHIRInboundPayloadProcessor.class);
	
	private final FhirContext ctx;
	
	public FHIRInboundPayloadProcessor() {
	    ctx = new FhirContext();
	}
    
    @Override
    public Bundle buildInput(SemanticSignifier semanticSignifier, Payload payload) {
		
		log.debug("starting fhirInboundPayloadProcessor");
		Bundle  bundle = null;

		try {
		    log.debug("Get unmarshaller for semanticSignifier: " + semanticSignifier);
		    //System.out.println("Unmashall = "+payload.getPayload());
			IParser jsonParser = ctx.newJsonParser();
			bundle = jsonParser.parseBundle(payload.getPayload());
			
		} catch (Exception e) {
			log.error("Error unmarshalling",e);
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new OpenCDSRuntimeException(e.getMessage(), e);	
		}
		
		log.debug("finished FhirInboundPayloadProcessor");
		
		return bundle;
	}

}
