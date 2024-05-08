/*
 * Copyright 2015-2020 OpenCDS.org
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

package org.opencds.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.opencds.plugin.PluginContext.PostProcessPluginContext;
import org.opencds.plugin.util.VmrUtil;
import org.opencds.vmr.v1_0.internal.EvalTime;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.FocalPersonId;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.concepts.VmrOpenCdsConcept;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

public class ConceptsPlugin implements PostProcessPlugin {
    private static final String EQUALS = "=";
    private static final String PIPE = "|";

    private static final String OPENCDS_CS_OID = "2.16.840.1.113883.3.795.12.1.1";
    private static final String OPENCDS_CS_NAME = "OpenCDS concepts";

    private static final String DEBUG_CONCEPTS = "DEBUG_CONCEPTS";

    @Override
    public void execute(PostProcessPluginContext context) {
        Map<String, List<?>> results = context.getResultFactLists();

        Map<String, String> conceptList = new TreeMap<>();
        for (Map.Entry<String, List<?>> entry : results.entrySet()) {
            for (Object value : entry.getValue()) {
                if (value instanceof VmrOpenCdsConcept) {
                    VmrOpenCdsConcept concept = VmrOpenCdsConcept.class.cast(value);
                    conceptList.put(concept.getId(), concept.getDisplayName());
                }
                // append a pipe (check for size)
            }
        }
        StringBuilder obsValue = new StringBuilder();
        int size = conceptList.size();
        int counter = 0;
        for (Map.Entry<String, String> entry : conceptList.entrySet()) {
            counter++;
            obsValue.append(entry.getKey());
            obsValue.append(EQUALS);
            obsValue.append(entry.getValue());
            if (counter < size) {
                obsValue.append(PIPE);
            }
        }

        // add to vMR as ObservationResult
        IVLDate obsTime = VmrUtil.createObsTime((EvalTime) context.getAllFactLists().get(EvalTime.class).get(0));

        ObservationResult obsResult = VmrUtil.createObservationResult(
                ((EvaluatedPerson) context.getAllFactLists().get(EvaluatedPerson.class).get(0)).getId(), obsTime,
                ((FocalPersonId) context.getAllFactLists().get(FocalPersonId.class).get(0)).getId(), DEBUG_CONCEPTS,
                OPENCDS_CS_OID, OPENCDS_CS_NAME, obsValue.toString());

        Map<String, List<?>> resultFactLists = context.getResultFactLists();
        List<ObservationResult> obsResults = (List<ObservationResult>) resultFactLists.get(ObservationResult.class
                .getSimpleName());
        if (obsResults == null) {
            obsResults = new ArrayList<>();
            resultFactLists.put(ObservationResult.class.getSimpleName(), obsResults);
        }
        obsResults.add(obsResult);

    }

}
