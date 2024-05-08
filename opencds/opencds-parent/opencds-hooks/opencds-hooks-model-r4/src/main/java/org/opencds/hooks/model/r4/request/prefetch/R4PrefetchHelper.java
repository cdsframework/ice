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

package org.opencds.hooks.model.r4.request.prefetch;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Resource;
import org.opencds.hooks.model.request.prefetch.PrefetchHelper;
import org.opencds.hooks.model.request.prefetch.annotation.PrefetchFhirVersion;

@PrefetchFhirVersion(FhirVersionEnum.R4)
public class R4PrefetchHelper implements PrefetchHelper<OperationOutcome, Resource> {

    @Override
    public Class<OperationOutcome> getOperationOutcomeClass() {
        return OperationOutcome.class;
    }

    @Override
    public Class<Resource> getResourceType() {
        return Resource.class;
    }

}
