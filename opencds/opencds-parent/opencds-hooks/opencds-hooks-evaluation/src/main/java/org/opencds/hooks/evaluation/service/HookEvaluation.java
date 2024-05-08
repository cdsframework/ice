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

package org.opencds.hooks.evaluation.service;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.hooks.evaluation.service.util.EvaluationUtil;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.response.CdsResponse;

public class HookEvaluation {
    private final ConfigurationService configurationService;
    private final EvaluationService evaluationService;
    private final ResourceListBuilder resourceListBuilder;
    private final FhirVersion fhirVersion;

    public HookEvaluation(ConfigurationService configurationService, EvaluationService evaluationService, ResourceListBuilder resourceListBuilder, FhirVersion fhirVersion) {
        this.configurationService = configurationService;
        this.evaluationService = evaluationService;
        this.resourceListBuilder = resourceListBuilder;
        this.fhirVersion = fhirVersion;
    }

	public CdsResponse evaluate(String serviceId, CdsRequest request, Date evalTime, URI baseUri) {
		KnowledgeRepository kr = configurationService.getKnowledgeRepository();
		KnowledgeModule knowledgeModule = kr.getKnowledgeModuleService()
                .find((KnowledgeModule km) -> km.getCDSHook() != null && serviceId.equals(km.getCDSHook().getId())
                        && evaluationSupportsFhirVersion(km.getCDSHook().getFhirVersion()));

		// set the FHIR version so that callers can have a reference to what types of resources they need to handle
		request.setFhirVersion(fhirVersion);
		if (knowledgeModule == null) {
		    throw new OpenCDSRuntimeException("Unable to find knowledge module for serviceId: " + serviceId);
		}

		EvaluationRequestDataItem dataItem = EvaluationUtil.createEvaluationRequestDataItem(
				EvaluationUtil.getPatientId(request), serviceId, knowledgeModule.getSSId(), evalTime == null ? new Date() : evalTime, baseUri);

		Map<Class<?>, List<?>> allFactLists = resourceListBuilder.buildAllFactLists(request);

		EvaluationRequestKMItem evaluationRequestKMItem = new EvaluationRequestKMItem(
				EntityIdentifierUtil.makeEIString(knowledgeModule.getKMId()), dataItem, allFactLists);
		EvaluationResponseKMItem response = evaluationService.evaluate(kr, evaluationRequestKMItem);

		return EvaluationUtil.buildCdsResponse(response);
	}

    public boolean evaluationSupportsFhirVersion(FhirVersion fhirVersion) {
        return this.fhirVersion == fhirVersion;
    }

}
