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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.opencds.vmr.v1_0.internal.EncounterEvent;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.concepts.ObservationCodedValueConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationFocusConcept;
import org.opencds.vmr.v1_0.internal.concepts.RaceConcept;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.ADXP;
import org.opencds.vmr.v1_0.internal.datatypes.AddressPartType;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class SepsisPredictionInstanceBuilder {
	private static final Log log = LogFactory.getLog(SepsisPredictionInstanceBuilder.class);
	
	private static final Map<String, String> admSourceToPMMap = ImmutableMap.<String, String>builder()
			.put("19", "23")
			.put("20", "24")
			.build();
	private static final Map<String, String> admTypeToPMMap = ImmutableMap.<String, String>builder()
			.put("5", "3")
			.put("7", "1")
			.put("8", "2")
			.put("9", "5")
			.build();
	private static final Set<String> hospServiceGroups = ImmutableSet.<String>builder()
			.add("N-Surge")
			.add("OB")
			.add("Other")
			.add("Ortho")
			.add("Oncology")
			.add("Surgery")
			.add("Medicine")
			.add("CTVSurgery")
			.add("Neuro")
			.add("Rehab")
			.add("Cardio")
			.add("MicuPulm")
			.add("ED")
			.build();
	
	public static SepsisPredictionInstance buildInstance(Map<Class<?>, List<?>> allFactLists, Map<String, String> zcLookup, int acuityScoreThreshold, double wekaThreshold) {
		SepsisPredictionInstance model = new SepsisPredictionInstance();
		model.setAcuityScoreThreshold(acuityScoreThreshold);
		model.setWekaThreshold(wekaThreshold);
		for (Entry<Class<?>, List<?>> entry : allFactLists.entrySet()) {
			List<?> factlist = entry.getValue();
			for (Object fact : factlist) {
				if (fact instanceof ObservationResult) {
					process((ObservationResult) fact, model);
				} else if (fact instanceof RaceConcept) {
					process((RaceConcept) fact, model);
				} else if (fact instanceof EvaluatedPerson) {
					process((EvaluatedPerson) fact, model, zcLookup);
				} else if (fact instanceof EncounterEvent) {
					process((EncounterEvent) fact, model);
				} else if (fact instanceof ObservationFocusConcept) {
					process((ObservationFocusConcept) fact, model, allFactLists);
				} else if (fact instanceof ObservationCodedValueConcept) {
					process((ObservationCodedValueConcept) fact, model, allFactLists);
				}
			}
		}
		return model;
	}

	private static void process(ObservationResult fact, SepsisPredictionInstance model) {
		if ("8310-5".equals(fact.getObservationFocus().getCode())) {
			model.setTemperature(fact.getObservationValue().getPhysicalQuantity().getValue(),
					fact.getObservationValue().getPhysicalQuantity().getUnit(), fact.getObservationEventTime().getLow());
		} else if ("8480-6".equals(fact.getObservationFocus().getCode())) {
			model.setSystolic(fact.getObservationValue().getPhysicalQuantity().getValue(),
					fact.getObservationValue().getPhysicalQuantity().getUnit(), fact.getObservationEventTime().getLow());
		} else if ("8462-4".equals(fact.getObservationFocus().getCode())) {
			model.setDiastolic(fact.getObservationValue().getPhysicalQuantity().getValue(),
					fact.getObservationValue().getPhysicalQuantity().getUnit(), fact.getObservationEventTime().getLow());
		} else if ("9279-1".equals(fact.getObservationFocus().getCode())) {
			model.setRespRate(fact.getObservationValue().getPhysicalQuantity().getValue(),
					fact.getObservationValue().getPhysicalQuantity().getUnit(), fact.getObservationEventTime().getLow());
		} else if ("8867-4".equals(fact.getObservationFocus().getCode())) {
			model.setPulse(fact.getObservationValue().getPhysicalQuantity().getValue(),
					fact.getObservationValue().getPhysicalQuantity().getUnit(), fact.getObservationEventTime().getLow());
		}
	}

	private static void process(RaceConcept fact, SepsisPredictionInstance model) {
		model.setRace(fact.getOpenCdsConceptCode());
	}
	
	/**
	 * Birth Date, Gender, and Postal Code Distance.
	 * 
	 * @param fact
	 * @param model
	 * @param zcLookup
	 */
	private static void process(EvaluatedPerson fact, SepsisPredictionInstance model, Map<String, String> zcLookup) {
		model.setBirthDate(fact.getDemographics().getBirthTime()); // TODO What about a null demographics?
		model.setGender(fact.getDemographics().getGender().getCode());
		// zip code distance
		model.setPostalCodeDist(getPostalCodeDistance(zcLookup, getPostalCode(fact)));
	}
	
	private static void process(EncounterEvent fact, SepsisPredictionInstance model) {
		model.setAdmDate(fact.getEncounterEventTime().getLow());
	}

	private static void process(ObservationFocusConcept fact, SepsisPredictionInstance model, Map<Class<?>, List<?>> allFactLists) {
		if ("C4000".equals(fact.getOpenCdsConceptCode())) {
			ObservationResult wbcResult = findObservationResult(
					getFactList(allFactLists, ObservationResult.class),
					fact.getConceptTargetId());
			if (wbcResult != null && wbcResult.getObservationValue() != null
					&& wbcResult.getObservationValue().getPhysicalQuantity() != null) {
				model.setWbcValue(wbcResult.getObservationValue().getPhysicalQuantity().getValue(),
						wbcResult.getObservationValue().getPhysicalQuantity().getUnit());
			} else {
				log.debug("WBC Result with null PQ values? : " + wbcResult);
			}
		}
	}

	private static void process(ObservationCodedValueConcept fact, SepsisPredictionInstance model, Map<Class<?>, List<?>> allFactLists) {
		if ("C4001".equals(fact)) {
			ObservationResult admSourceOR = findObservationResult(getFactList(allFactLists, ObservationResult.class),
					fact.getConceptTargetId());
			if (admSourceOR != null) {
				model.setAdmSource(admSourceToPMMap.get(admSourceOR.getObservationValue().getConcept().getCode()));
			}
		} else if ("C4002".equals(fact)) {
			ObservationResult admTypeOR = findObservationResult(getFactList(allFactLists, ObservationResult.class),
					fact.getConceptTargetId());
			if (admTypeOR != null) {
				model.setAdmType(admTypeToPMMap.get(admTypeOR.getObservationValue().getConcept().getCode()));
			}
		} else if (hospServiceGroups.contains(fact.getOpenCdsConceptCode())) { // TODO: Think about case... (tree-set with case-insensitive comparator)
			model.setHospServiceGroup(fact.getOpenCdsConceptCode());
		}
	}

    private static String getPostalCode(EvaluatedPerson evaluatedPerson) {
        String postalCode = "";
        List<AD> addresses = evaluatedPerson.getDemographics().getAddress();
        for (AD address : addresses) {
            List<ADXP> parts = address.getPart();
            for (ADXP part : parts) {
                if (part.getType() == AddressPartType.ZIP) {
                    postalCode = part.getValue();
                    break;
                }
            }
        }
        return postalCode;
    }

    private static Double getPostalCodeDistance(Map<String, String> zcLookup, String postalCode) {
        Double distance = null;
        String strDistance = zcLookup.get(postalCode);
        if (strDistance != null && !strDistance.isEmpty()){
        	try {
        		distance = Double.valueOf(zcLookup.get(postalCode));
        	} catch (NumberFormatException e) {
        		log.error("Error with zip code lookup; distance unparsable as a double: " + strDistance,e);
        	}
        }
        return distance;
    }

	private static ObservationResult findObservationResult(List<ObservationResult> obsResultList,
			String focusCode) {
		for (ObservationResult obsResult : obsResultList) {
			if (focusCode.equals(obsResult.getId())) {
				return obsResult;
			}
		}

		return null;
	}

	private static <T> List<T> getFactList(Map<Class<?>, List<?>> allFactLists, Class<T> clazz) {
		List<T> list = (List<T>) allFactLists.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			allFactLists.put(clazz, list);
		}
		return list;
	}

}
