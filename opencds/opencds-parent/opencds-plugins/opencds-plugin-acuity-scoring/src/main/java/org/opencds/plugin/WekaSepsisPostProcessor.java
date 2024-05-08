/*
 * Copyright 2016-2020 OpenCDS.org
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.plugin.PluginContext.PostProcessPluginContext;
import org.opencds.plugin.SepsisPredictionInstance.Attribute;
import org.opencds.service.weka.WekaInput;
import org.opencds.service.weka.WekaOutput;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.FocalPersonId;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.VMR;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.REAL;

public class WekaSepsisPostProcessor implements PostProcessPlugin {
	private static final String WEKA = "WEKA";
	private static final String ACUITY = "ACUITY";

	private static final String OPENCDS_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.1.1";
	private static final String OPENCDS_CODE_SYSTEM_NAME = "OpenCDS Terminology";
	private static final String INTERPRETATION_DISPLAY_NAME = "Invalid Value";
	private static final String INTERPRETATION_CODE = "C3739";

	private <T> List<T> getResults(Map<String, List<?>> resultFactLists, Class<T> clazz) {
		List<T> list = (List<T>) resultFactLists.get(clazz.getSimpleName());
		if (list == null) {
			list = new ArrayList<>();
			resultFactLists.put(clazz.getSimpleName(), list);
		}
		return list;
	}

	private <T> List<T> get(Map<Class<?>, List<?>> allFactLists, Class<T> clazz) {
		List<T> list = (List<T>) allFactLists.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			allFactLists.put(clazz, list);
		}
		return list;
	}

	@Override
	public void execute(PostProcessPluginContext context) {
		List<WekaInput> inputs = get(context.getAllFactLists(), WekaInput.class);
		SepsisPredictionWekaInput input = null;
		if (inputs != null && inputs.size() > 0) {
			input = (SepsisPredictionWekaInput) inputs.get(0);
		} else {
			throw new OpenCDSRuntimeException(
					"WekaInput was not passed through to the post-process plugin. (Expected in allFactLists.)");
		}

		List<WekaOutput> outputs = getResults(context.getResultFactLists(), WekaOutput.class);
		if (outputs == null || outputs.size() == 0) {
			// TODO we had no output... let's find out why
			// if it's high acuity, then we want to send back a true result
			Map<String, List<?>> results = null;
			if (input.getInstance().preemptProcess()) {
				results = buildResults(context, input.getInstance(), null, -1.0, true, ACUITY);
			} else {
				results = buildResults(context, input.getInstance(), null, -1.0, false, ACUITY);
			}
			context.getResultFactLists().putAll(results);

		} else {
			double avgProbability;
			WekaOutput output = outputs.get(0);
			int counter = 0;
			double sum = 0.0;
			Queue<double[]> dq = output.getDistributions();
			while (dq.peek() != null) {
				double[] dist = dq.poll();
				sum += dist[1];
				counter++;
			}
			avgProbability = sum / counter;
			boolean prediction = input.getInstance().getPrediction(avgProbability);
			Map<String, List<?>> results = buildResults(context, input.getInstance(), output, avgProbability,
					prediction, WEKA);
			context.getResultFactLists().putAll(results);
		}

	}

	public Map<String, List<?>> buildResults(PostProcessPluginContext context, SepsisPredictionInstance instance,
			WekaOutput output, double probability, boolean prediction, String moduleCreatingResult) {
		List<CDSInput> cdsInputList = new ArrayList<>();
		cdsInputList.addAll(get(context.getAllFactLists(), CDSInput.class));

		List<VMR> vmrOutputList = new ArrayList<>();
		vmrOutputList.addAll(get(context.getAllFactLists(), VMR.class));

		List<FocalPersonId> focalPersonIds = new ArrayList<>();
		focalPersonIds.addAll(get(context.getAllFactLists(), FocalPersonId.class));

		List<EvaluatedPerson> evaluatedPersonList = new ArrayList<>();
		evaluatedPersonList.addAll(get(context.getAllFactLists(), EvaluatedPerson.class));

		List<ObservationResult> obsResultsList = new ArrayList<>();

		String patientId = "";
		if (focalPersonIds.size() > 0) {
			patientId = focalPersonIds.get(0).getId();
		}
		/* Add Default Observation Results */
		ObservationResult obsResult = buildSepsisOutputObsResult(patientId, Boolean.valueOf(prediction));
		obsResultsList.add(obsResult);
		
		ObservationResult obsModuleCreatingResult = buildOutputObsResultModuleCreating(patientId, moduleCreatingResult);
		obsResultsList.add(obsModuleCreatingResult);
		
		if (probability >= 0) {
			ObservationResult obsWekaProbability = buildOutputObsWekaProbability(patientId, probability);
			obsResultsList.add(obsWekaProbability);
		}
		
		ObservationResult obsAcuityScore = buildAcuityScoreOutputObsResult(patientId,
				Integer.valueOf(instance.getAcuityScore().getScore()));
		obsResultsList.add(obsAcuityScore);

		/* return all vital signs used if we have an acuity score */
		if (instance.getAcuityScore().getScore() != -1) {
			obsResultsList.add(buildVitalSignObservationResult(patientId, Attribute.TEMP_TTL_NNUL, instance.getTemperature()));
			obsResultsList.add(buildVitalSignObservationResult(patientId, Attribute.DBP, instance.getDiastolic().doubleValue()));
			obsResultsList.add(buildVitalSignObservationResult(patientId, Attribute.SBP_TTL_NNUL, instance.getSystolic().doubleValue()));
			obsResultsList.add(buildVitalSignObservationResult(patientId, Attribute.BP_MAP_TTL_NNUL, instance.getMapBP().doubleValue()));
			obsResultsList.add(buildVitalSignObservationResult(patientId, Attribute.RESP_RATE_TTL_NNUL, instance.getRespRate()));
			obsResultsList.add(buildVitalSignObservationResult(patientId, Attribute.PULSE_TTL_NNUL, instance.getPulse()));
		}
		
		/* include an obervationResult for each invalid value */
		for (Entry<Attribute, List<String>> invalidValue : instance.getInvalidInputValues().entrySet()) {
			for (String message : invalidValue.getValue()) {
				ObservationResult invalid = buildObservationResult(patientId, invalidValue.getKey(), message);
				obsResultsList.add(invalid);
			}
		}
		for (Entry<Attribute, List<String>> missingValue : instance.getMissingInputValues().entrySet()) {
			for (String message : missingValue.getValue()) {
				ObservationResult missing = buildObservationResult(patientId, missingValue.getKey(), message);
				obsResultsList.add(missing);
			}
		}
		for (Entry<Attribute, List<String>> staleValue : instance.getStaleInputValues().entrySet()) {
			for (String message : staleValue.getValue()) {
				ObservationResult missing = buildObservationResult(patientId, staleValue.getKey(), message);
				obsResultsList.add(missing);
			}
		}
		
		Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();
		resultFactLists.put("CDSInput", cdsInputList);
		resultFactLists.put("VMR", vmrOutputList);
		resultFactLists.put("FocalPersonId", focalPersonIds);
		resultFactLists.put("EvaluatedPerson", evaluatedPersonList);
		resultFactLists.put("ObservationResult", obsResultsList);
		if (output == null) {
			// resultFactLists.put("Error",
			// Arrays.asList(output.getErrors().get("Error")));
		}
		return resultFactLists;

	}

	private ObservationResult buildSepsisOutputObsResult(String evaluatedPersonId, boolean prediction) {

		ObservationResult obs = new ObservationResult();
		obs.setClinicalStatementToBeRoot(true);
		obs.setToBeReturned(true);

		obs.setId(UUID.randomUUID().toString());
		CD obsFocusValue = new CD();
		obsFocusValue.setCode("C308");
		obsFocusValue.setCodeSystem("2.16.840.1.113883.3.795.12.1.1");
		obsFocusValue.setCodeSystemName("OpenCDS Terminology");
		obsFocusValue.setDisplayName("Sepsis");
		obs.setObservationFocus(obsFocusValue);
		ObservationValue observationValue = new ObservationValue();
		BL _boolean = new BL();
		_boolean.setValue(prediction);
		observationValue.set_boolean(_boolean);
		obs.setObservationValue(observationValue);

		obs.setEvaluatedPersonId(evaluatedPersonId);

		return obs;
	}

	private ObservationResult buildAcuityScoreOutputObsResult(String evaluatedPersonId, int acuityScore) {

		ObservationResult obs = new ObservationResult();
		obs.setClinicalStatementToBeRoot(true);
		obs.setToBeReturned(true);

		obs.setId(UUID.randomUUID().toString());
		CD obsFocusValue = new CD();
		obsFocusValue.setCode("ACUITY_SCORE");
		obsFocusValue.setCodeSystem("2.16.840.1.113883.3.795.12.1.1");
		obsFocusValue.setCodeSystemName("OpenCDS Terminology");
		obsFocusValue.setDisplayName("OpenCDS Acuity score result");
		obs.setObservationFocus(obsFocusValue);
		ObservationValue observationValue = new ObservationValue();
		INT _int = new INT();
		_int.setValue(acuityScore);
		observationValue.setInteger(_int);
		obs.setObservationValue(observationValue);

		obs.setEvaluatedPersonId(evaluatedPersonId);

		return obs;
	}

	private ObservationResult buildOutputObsResultModuleCreating(String evaluatedPersonId,
			String moduleCreatingResult) {
		ObservationResult obs = new ObservationResult();
		obs.setClinicalStatementToBeRoot(true);
		obs.setToBeReturned(true);

		obs.setId(UUID.randomUUID().toString());
		CD obsFocusValue = new CD();
		obsFocusValue.setCode("MODULE_CREATING_RESULT");
		obsFocusValue.setCodeSystem("2.16.840.1.113883.3.795.12.1.1");
		obsFocusValue.setCodeSystemName("OpenCDS Terminology");
		obsFocusValue.setDisplayName("OpenCDS Module Creating Result");
		obs.setObservationFocus(obsFocusValue);
		ObservationValue observationValue = new ObservationValue();
		observationValue.setText(moduleCreatingResult);
		obs.setObservationValue(observationValue);

		obs.setEvaluatedPersonId(evaluatedPersonId);

		return obs;
	}

	private ObservationResult buildOutputObsWekaProbability(String patientId, double wekaProbability) {
		ObservationResult obs = new ObservationResult();
		obs.setClinicalStatementToBeRoot(true);
		obs.setToBeReturned(true);

		obs.setId(UUID.randomUUID().toString());
		CD obsFocusValue = new CD();
		obsFocusValue.setCode("WEKA_PROBABILITY");
		obsFocusValue.setCodeSystem("2.16.840.1.113883.3.795.12.1.1");
		obsFocusValue.setCodeSystemName("OpenCDS Terminology");
		obsFocusValue.setDisplayName("OpenCDS WEKA Module probability result");
		obs.setObservationFocus(obsFocusValue);
		ObservationValue observationValue = new ObservationValue();
		REAL value = new REAL();
		value.setValue(wekaProbability);
		observationValue.setDecimal(value);
		obs.setObservationValue(observationValue);

		obs.setEvaluatedPersonId(patientId);

		return obs;
	}

	private ObservationResult buildObservationResult(String evaluatedPersonId, Attribute attribute, String message) {

		ObservationResult observation = new ObservationResult();
		observation.setClinicalStatementToBeRoot(true);
		observation.setToBeReturned(true);
		observation.setId(UUID.randomUUID().toString());

		// interpretation
		List<CD> interpretationList = new ArrayList<>();
		interpretationList.add(buildCD(INTERPRETATION_CODE, OPENCDS_CODE_SYSTEM, OPENCDS_CODE_SYSTEM_NAME,
				INTERPRETATION_DISPLAY_NAME));
		observation.setInterpretation(interpretationList);

		// observation focus
		observation.setObservationFocus(buildCD(attribute.getCode(), OPENCDS_CODE_SYSTEM, OPENCDS_CODE_SYSTEM_NAME,
				attribute.getDescription()));

		observation.setEvaluatedPersonId(evaluatedPersonId);
		ObservationValue value = new ObservationValue();
		value.setText(message);
		observation.setObservationValue(value);

		return observation;
	}

	private ObservationResult buildVitalSignObservationResult(String evaluatedPersonId, Attribute attribute, double vitalValue) {
		
		ObservationResult observation = new ObservationResult();
		observation.setClinicalStatementToBeRoot(true);
		observation.setToBeReturned(true);
		observation.setId(UUID.randomUUID().toString());
		
		// observation focus
		observation.setObservationFocus(buildCD(attribute.getCode(), OPENCDS_CODE_SYSTEM, OPENCDS_CODE_SYSTEM_NAME,
				attribute.getDescription()));
		
		observation.setEvaluatedPersonId(evaluatedPersonId);
		ObservationValue value = new ObservationValue();
		REAL real = new REAL();
		real.setValue(vitalValue);
		value.setDecimal(real);
		observation.setObservationValue(value);
		
		return observation;
	}
	
	private CD buildCD(String code, String codeSystem, String codeSystemName, String displayName) {
		CD cd = new CD();
		cd.setCode(code);
		if (codeSystem != null) {
			cd.setCodeSystem(codeSystem);
		}
		if (codeSystemName != null) {
			cd.setCodeSystemName(codeSystemName);
		}
		if (displayName != null) {
			cd.setDisplayName(displayName);
		}
		return cd;
	}

}
