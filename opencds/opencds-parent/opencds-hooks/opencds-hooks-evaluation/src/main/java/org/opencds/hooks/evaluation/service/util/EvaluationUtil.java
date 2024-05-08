/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.hooks.evaluation.service.util;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.hooks.model.context.HookContext;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.response.CdsResponse;

public class EvaluationUtil {
    private static final String NAMED_OBJECTS = "namedObjects";

    public static EvaluationRequestDataItem createEvaluationRequestDataItem(String focalPersonId, String interactionId, SSId ssId, Date evalTime, URI baseUri) {
		EvaluationRequestDataItem evalRequestDataItem = new EvaluationRequestDataItem();
		if (focalPersonId == null) {
			throw new OpenCDSRuntimeException("No focal person id (patient ID) found.");
		}
		evalRequestDataItem.setFocalPersonId(focalPersonId);
		evalRequestDataItem.setInteractionId(interactionId);
		evalRequestDataItem.setEvalTime(evalTime);
		evalRequestDataItem.setExternalFactModelSSId(EntityIdentifierUtil.makeEIString(ssId));
		evalRequestDataItem.setServerBaseUri(baseUri);
		return evalRequestDataItem;
	}

	public static String getPatientId(CdsRequest request) {
		HookContext context = request.getContext();
		return context.get("patientId", String.class);
	}
	
	public static CdsResponse buildCdsResponse(EvaluationResponseKMItem response) {
		List<?> namedObjects = response.getResultFactLists().get(NAMED_OBJECTS);
		if (namedObjects != null && namedObjects.size() > 0) {
            Map<String, CdsResponse> cdsResponseMap = (Map<String, CdsResponse>) namedObjects.stream()
                    .filter(namedObject -> {
                        return (namedObject instanceof Map) && ((Map) namedObject).containsKey("CdsResponse");
                    }).findFirst().orElse(null);
    		if (cdsResponseMap != null) {
    		    return cdsResponseMap.get("CdsResponse");
    		}
		}
        CdsResponse cdsResponse = (CdsResponse) response.getResultFactLists().entrySet().stream()
                .filter(e -> e.getKey().equals("CdsResponse")).map(e -> e.getValue()).filter(l -> l.size() > 0)
                .map(l -> l.get(0)).findFirst().orElse(null);
	    if (cdsResponse != null) {
	        return cdsResponse;
	    }
		return new CdsResponse();
	}

}
