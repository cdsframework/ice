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
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.CommunicationRequest;
import ca.uhn.fhir.parser.IParser;


/**
 * <p/> We may not need this mapping for FHIR
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: OpenCDS</p>
 *
 * @author Tadesse Sefer
 * @version 2.0
 * @date 03-03-2015
 * 
 */
public class FhirOutputResultSetBuilder implements ResultSetBuilder<Bundle>{
	private static Log logger = LogFactory.getLog(FhirOutputResultSetBuilder.class);

    @Override
	public Bundle buildResultSet( Map<String,List<?>> results,
			EvaluationRequestKMItem		dssRequestKMItem) {
    	Bundle bundle = new Bundle();
        try {
        	 for (Map.Entry<String, List<?>> entry : results.entrySet()) {
                 List value = entry.getValue();
                 for (Iterator iterator = value.iterator(); iterator.hasNext();) {
     				IResource resource = (IResource)iterator.next();
             			 if (resource instanceof IResource) {
         					 bundle.addEntry().setResource(resource);
         				}
     			}
             }
        	
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("OpenCDS encountered Exception when building fhir output: " + e.getMessage(), e);
        }
        if(bundle.isEmpty()){
        	logger.debug("No CDS result output came from the Rule session. It may be an indication of no rule fired.");
        }
        return bundle;
	}
}
