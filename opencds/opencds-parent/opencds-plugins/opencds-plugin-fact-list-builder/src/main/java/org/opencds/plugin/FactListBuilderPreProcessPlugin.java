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

package org.opencds.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.convertors.VersionConvertor_10_30;
import org.hl7.fhir.dstu2.model.Extension;
import org.hl7.fhir.dstu2.model.Parameters;
import org.hl7.fhir.dstu2.model.Patient;
import org.hl7.fhir.dstu2.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.opencds.hooks.model.dstu2.request.prefetch.Dstu2PrefetchHelper;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.service.evaluate.FhirAdapterResourceListBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FactListBuilderPreProcessPlugin implements PreProcessPlugin {
    private static final String PATIENT = "patient";
    private static final String INPUT_PARAMETERS = "inputParameters";
    private static final Set<String> BUNDLE_PARAMS = new HashSet<>(Arrays.asList(
            "relatedperson", "encounters", "procedurerequests", "babyobservations", "motherobservations", "procedures", "radiobuttons"
    ));

    private static final Log log = LogFactory.getLog(FactListBuilderPreProcessPlugin.class);
    private static final Dstu2PrefetchHelper prefetchHelper = new Dstu2PrefetchHelper();

    @Override
    public void execute(PreProcessPluginContext context) {
        log.debug(getClass().getSimpleName() + ": Processing input data.");
        Map<Class<?>, List<?>> factList = context.getAllFactLists();
        if (!factList.isEmpty() && factList.containsKey(CdsRequest.class)) {
            for (Object obj : factList.get(CdsRequest.class)) {
                if (obj instanceof CdsRequest) {
                    CdsRequest cdsRequest = (CdsRequest) obj;
                    factList.remove(CdsRequest.class);
                    factList.putAll(getResourceList(cdsRequest));
                    break;
                }

            }
        }
    }

    private static Map<Class<?>, List<?>> getResourceList(CdsRequest cdsRequest) {

        FhirAdapterResourceListBuilder builder = new FhirAdapterResourceListBuilder();
        Bundle inputDataBundle = new Bundle();

        org.hl7.fhir.dstu3.model.Patient patient = null;
        org.hl7.fhir.dstu3.model.Parameters inputParameters = null;

        List<String> keys = new ArrayList<String>(cdsRequest.getPrefetchKeys());
        for (String key : keys) {
            if (key.equalsIgnoreCase(INPUT_PARAMETERS)) {
                inputParameters = convertParameters(cdsRequest.getPrefetchResource(key, prefetchHelper).getResource(Parameters.class));
            } else if (key.equalsIgnoreCase(PATIENT)) {
                patient = convertPatient(cdsRequest.getPrefetchResource(key, prefetchHelper).getResource(Patient.class));
            } else if (BUNDLE_PARAMS.contains(key.toLowerCase())) {
                Bundle bundle = convertBundle(cdsRequest.getPrefetchResource(key, prefetchHelper).getResource(org.hl7.fhir.dstu2.model.Bundle.class));
                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    inputDataBundle.addEntry(entry);
                }
            }
        }

        List<org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent> inputData = new ArrayList<>();

        org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent inputDataParamComp = new org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent();
        inputDataParamComp.setResource(inputDataBundle);
        inputData.add(inputDataParamComp);

        return builder.buildAllFactLists(inputParameters, inputData, patient);
    }

    /**
     * inputParameters : Parameters
     * relatedPerson : Bundle<RelatedPerson>
     * encounters : Bundle<Encounter>
     * patient : Patient
     * procedureRequests : Bundle<ProcedureRequest>
     * babyObservations : Bundle<Observation>
     * motherObservation : Bundle<Observation>
     * procedures : Bundle<Procedure>
     * radioButtons : Bundle<Observation>
     */

    private static org.hl7.fhir.dstu3.model.Parameters convertParameters(Parameters src) {
        return (org.hl7.fhir.dstu3.model.Parameters) VersionConvertor_10_30.convertResource(src);
    }

    private static org.hl7.fhir.dstu3.model.RelatedPerson convertRelatedPerson(RelatedPerson src) {
        return (org.hl7.fhir.dstu3.model.RelatedPerson) VersionConvertor_10_30.convertResource(src);
    }

    private static org.hl7.fhir.dstu3.model.Bundle convertBundle(org.hl7.fhir.dstu2.model.Bundle src) {
        return (Bundle) VersionConvertor_10_30.convertResource(src);
    }

    private static org.hl7.fhir.dstu3.model.Patient convertPatient(Patient src) {
        org.hl7.fhir.dstu3.model.Patient stu3p = (org.hl7.fhir.dstu3.model.Patient) VersionConvertor_10_30.convertResource(src);

        if(src.getBirthDateElement() != null) {
            List<Extension> extensions = src.getBirthDateElement().getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/patient-birthTime");
            if(extensions != null && !extensions.isEmpty()) {
                Extension dstu2Ext = extensions.get(0);
                org.hl7.fhir.dstu3.model.Extension stu3Ext = new org.hl7.fhir.dstu3.model.Extension();
                stu3Ext.setUrl(dstu2Ext.getUrl());
                stu3Ext.setValue(new DateTimeType(((org.hl7.fhir.dstu2.model.DateTimeType)dstu2Ext.getValue()).getValue()));
                stu3p.getBirthDateElement().addExtension(stu3Ext);
            }
        }

        return stu3p;
    }

}
