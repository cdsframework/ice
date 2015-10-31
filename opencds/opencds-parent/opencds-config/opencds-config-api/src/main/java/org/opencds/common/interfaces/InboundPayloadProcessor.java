/**
 * Copyright 2011 OpenCDS.org
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

package org.opencds.common.interfaces;

import org.opencds.common.structures.Payload;
import org.opencds.config.api.model.SemanticSignifier;


/**
 * 
 * @author David Shields
 * 
 * @version 1.0
 */
public interface InboundPayloadProcessor {

	/**
	 * returns JAXBElement<org.opencds.vmr.v1_0.schema.CDSInput> as Object, must be cast
	 * 		to JAXBElement<org.opencds.vmr.v1_0.schema.CDSInput> before use
	 * @param payloadString
	 * @param evalTime
	 * @param allFactLists
	 */
	public Object buildInput(SemanticSignifier semanticSignifier, Payload payload);
}
