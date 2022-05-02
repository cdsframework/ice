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

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.InboundPayloadProcessor;
import org.opencds.common.structures.Payload;
import org.opencds.common.utilities.StreamUtility;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.pool.UnmarshallerPool;

/**
 * @author David Shields
 * @author Phillip Warner
 *
 */
public class VMRInboundPayloadProcessor implements InboundPayloadProcessor, Cloneable {

	private static final Logger log = LogManager.getLogger();
	
	private final UnmarshallerPool unmarshallerPool;
	
	public VMRInboundPayloadProcessor(UnmarshallerPool unmarshallerPool) {
	    this.unmarshallerPool = unmarshallerPool;
	}
    
    private final StreamUtility streamUtility = new StreamUtility();

	@Override
    public JAXBElement<?> buildInput(
            SemanticSignifier semanticSignifier, Payload payload) {

		// this begins the guts for building fact lists for input data based on one particular fact model (opencds-vmr-1_0)			
		log.debug("starting VMRInboundPayloadProcessor");
        JAXBElement<?> cdsInput = null;

		try {
		    log.debug("Get unmarshaller for semanticSignifier: " + semanticSignifier);
				Class<?> entryPoint = Class.forName(semanticSignifier.getEntryPoint());
				StreamSource payloadStream = new StreamSource(new ByteArrayInputStream(payload.getPayload()));

				Unmarshaller unmarshaller = unmarshallerPool.borrowObject(semanticSignifier);
				cdsInput = unmarshaller.unmarshal( payloadStream, entryPoint);
				unmarshallerPool.returnObject(semanticSignifier, unmarshaller);
		} catch (JAXBException e) {
//			String jaxBError = e.getLinkedException().fillInStackTrace().getMessage();
			e.printStackTrace();
			throw new InvalidDriDataFormatException(e.getMessage() + ", therefore unable to unmarshal input Semantic Payload xml string: " + payload.getPayload(), e);
		} catch (ClassNotFoundException e) {
		    throw new OpenCDSRuntimeException("Unable to find entry point class (as configured): " + semanticSignifier.getEntryPoint(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OpenCDSRuntimeException(e.getMessage(), e);	
		}
		
		log.debug("finished VMRInboundPayloadProcessor");
		
		return cdsInput;
	}
	
}
