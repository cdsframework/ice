/**
 * Copyright 2011 - 2013 OpenCDS.org
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;

public class FhirInputFactListsBuilder implements FactListsBuilder {
	private static Log log = LogFactory.getLog(FhirInputFactListsBuilder.class);

	public FhirInputFactListsBuilder() {
	}

	/**
	 * Takes a FHIR bundle object and returns a map of Bundle class to FHIR resources objects.
	 */
	@Override
	public Map<Class<?>, List<?>> buildFactLists(KnowledgeRepository knowledgeRepository,
			KnowledgeModule knowledgeModule, Object payload, Date evalTime) {
		log.debug("buildFactLists");
		long t0 = System.nanoTime();
		Map<Class<?>, List<?>> allFactLists = new ConcurrentHashMap<>();
		List<IResource>   resourceList = new ArrayList<>();
		
		try {
            Bundle fhirBundle = (Bundle) payload; 
			if ( (fhirBundle == null || fhirBundle.isEmpty()) ) {
				throw new InvalidDriDataFormatException( "Error: No payload within the fhir bundle." ); 
			}
			List<BundleEntry> bundleEntries = fhirBundle.getEntries();
			for (Iterator iterator = bundleEntries.iterator(); iterator.hasNext();) {
				BundleEntry bundleEntry = (BundleEntry) iterator.next();
				resourceList.add(bundleEntry.getResource());
			}
			allFactLists.put(Bundle.class, resourceList);
			
		} catch (OpenCDSRuntimeException e) {
		    throw new OpenCDSRuntimeException("RuntimeException in FhirInputFactListsBuilder: " + e.getMessage());
        } catch (Exception e) {
            String unknownError = e.getMessage();
            e.printStackTrace();
            throw new InvalidDriDataFormatException("Unknown error initializing FhirInputFactListsBuilder: " 
                    + unknownError + ", therefore unable to complete unmarshalling input Semantic Payload: " + payload.toString() );
		}

		log.debug("FhirInputFactListsBuilder time : " + (System.nanoTime() - t0) / 1e6 + " ms");
		return allFactLists;
	}

}
