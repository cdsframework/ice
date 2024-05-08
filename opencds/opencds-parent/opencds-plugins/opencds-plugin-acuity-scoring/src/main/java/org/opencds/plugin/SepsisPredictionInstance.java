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
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;

/**
 * (E) Temperature (E) Diastolic (E) Systolic (E) Respiratory Rate (E) Pulse (E)
 * Race (E) Postal Code (Distance) (E) Birth Date with Admit Date (Age at
 * Admission) (E) Gender (I) Admission Source (I) Admission Type (I) Hospital
 * Service Group (I) WBC
 */
class SepsisPredictionInstance {

	public enum Attribute {
		DISTCAT("C3741", "Address - Zip Code") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getPostalCodeDist();
			}
		},
		GENDER("C28", "Gender") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getGender();
			}
		},
		RACE("C225", "Race") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getRace();
			}
		},
		AGE_AT_ADM("C3742", "Age at Admission") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getAgeAtAdm();
			}
		},
		ADM_SOURCE("C386", "Admission Source") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getAdmSource();
			}
		},
		ADM_TYPE("C487", "Admission Status or Type") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getAdmType();
			}
		},
		TEMP_TTL_NNUL("C3743", "Temperature Last 8 Hours") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getTemperature();
			}
		},
		RESP_RATE_TTL_NNUL("C3744", "Respiration Rate Last 8 Hours") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getRespRate();
			}
		},
		PULSE_TTL_NNUL("C3745", "Pulse Last 8 Hours") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getPulse();
			}
		},
		WBC_TTL_VALUE_NNUL("C3746", "WBC Last 36 Hours") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getWbcValue();
			}
		},
		SBP_TTL_NNUL("C2560", "Blood Pressure, Systolic") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getSystolic();
			}
		},
		BP_MAP_TTL_NNUL("C3747", "Blood Pressure, Mean Arterial Pressure") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getMapBP();
			}
		},
		HOSP_SERVICE_GROUP("C3732", "UUHC Hospital Service Group") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getHospServiceGroup();
			}
		},
		OUTCOME_POA("", "OUTCOME POA") {
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return null;
			}
		},
		DBP("C2559", "Blood Pressure, Diastolic") { // unused in Weka Sepsis
													// module
			@Override
			public <T> T getValueFromInstance(SepsisPredictionInstance instance) {
				return (T) instance.getDiastolic();
			}
		};

		private String code;
		private String description;

		Attribute(String code, String description) {
			this.code = code;
			this.description = description;
		}

		public abstract <T> T getValueFromInstance(SepsisPredictionInstance instance);

		public String getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}

	}

	private Double temperature;
	private BigDecimal diastolic;
	private BigDecimal systolic;
	private BigDecimal mapBP;
	private Double respRate;
	private Double pulse;
	private String race;
	private Double postalCodeDist;
	private Date birthDate;
	private Date admDate;
	private Integer ageAtAdm;
	private String gender;
	private String admSource;
	private String admType;
	private String hospServiceGroup;
	private Double wbcValue;

	private AcuityScore score;
	private int acuityThreshold;
	private double wekaThreshold;
	private Map<Attribute, List<String>> invalidInputValues = new HashMap<>();
	private Map<Attribute, List<String>> missingInputValues = new HashMap<>();
	private Map<Attribute, List<String>> staleInputValues = new HashMap<>();

	private DateTime now = new DateTime();

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double value, String unit, Date measureTime) {
		// check that the measurement time is within the past hour
		if (!withinPastHour(now, new DateTime(measureTime))) {
			addStaleInputValue(Attribute.TEMP_TTL_NNUL, "Stale Temperature measurement.");
		} else if (value == null) {
			addMissingInputValue(Attribute.TEMP_TTL_NNUL, "Missing Temperature measurement.");
		} else if ("Far".equals(unit) || "Cel".equals(unit)) {
			Double celValue = value;
			if (unit.equals("Far")) {
				celValue = (value - 32) * 5 / 9;
			}
			if (celValue > 50) {
				addInvalidInputValue(Attribute.TEMP_TTL_NNUL,
						"Invalid temperature value (greater than 50): " + value + " (" + unit + ")");
			} else if (celValue < 35) {
				addInvalidInputValue(Attribute.TEMP_TTL_NNUL,
						"Invalid temperature value (less than 35): " + value + " (" + unit + ")");
			} else {
				this.temperature = celValue;
			}
		} else {
			addInvalidInputValue(Attribute.TEMP_TTL_NNUL, "Invalid temperature unit: " + unit);
		}
	}

	public BigDecimal getDiastolic() {
		return diastolic;
	}

	public void setDiastolic(Double diastolic, String unit, Date measureTime) {
		// check that the measurement time is within the past hour
		if (!withinPastHour(now, new DateTime(measureTime))) {
			addStaleInputValue(Attribute.DBP, "Stale Diastolic measurement.");
		} else if (diastolic == null) {
			// we check this here, as the InstanceBuilder doesn't use this value
			// except indirectly through getMapBP() (below).
			addMissingInputValue(Attribute.DBP, "Missing Diastolic Blood Pressure.");
		} else if (!"mm[Hg]".equals(unit)) { // TODO: equalsIgnoreCase
			addInvalidInputValue(Attribute.DBP, "Invalid Diastolic Blood Pressure unit: " + unit);
		} else {
			this.diastolic = new BigDecimal(diastolic);
		}
	}

	public BigDecimal getSystolic() {
		return systolic;
	}

	public void setSystolic(Double systolic, String unit, Date measureTime) {
		// check that the measurement time is within the past hour
		if (!withinPastHour(now, new DateTime(measureTime))) {
			addStaleInputValue(Attribute.SBP_TTL_NNUL, "Stale Systolic measurement.");
		} else if (systolic == null) {
			addMissingInputValue(Attribute.SBP_TTL_NNUL, "Missing Systolic Blood Pressure.");
		} else if (!"mm[Hg]".equals(unit)) { // TODO: equalsIgnoreCase?  default if missing? (currently no; discussion when code review with Mike)
			addInvalidInputValue(Attribute.SBP_TTL_NNUL, "Invalid Systolic Blood Pressure unit: " + unit);
		} else {
			this.systolic = new BigDecimal(systolic);
		}
	}

	public BigDecimal getMapBP() {
		if (mapBP == null) {
			if (systolic != null && diastolic != null) {
				MathContext mc = new MathContext(2); // 2 precision
				/*
				 * Mean Arterial Pressure approximate value 2/3*DP + 1/3*SP
				 */
				mapBP = diastolic.multiply(new BigDecimal(0.6666), mc)
						.add(systolic.multiply(new BigDecimal(0.3333), mc), mc);

			} else {
				addInvalidInputValue(Attribute.BP_MAP_TTL_NNUL,
						"Unable to calculate BP_MAP due to missing/invalid Diastolic and/or Systolic BP values.");
			}
		}
		return mapBP;
	}

	public Double getRespRate() {
		return respRate;
	}

	public void setRespRate(Double rate, String unit, Date measureTime) {
		// check that the measurement time is within the past hour
		if (!withinPastHour(now, new DateTime(measureTime))) {
			addStaleInputValue(Attribute.RESP_RATE_TTL_NNUL, "Stale Respiratory Rate measurement.");
		} else if (rate == null) {
			addMissingInputValue(Attribute.RESP_RATE_TTL_NNUL, "Missing Respiratory Rate measurement.");
		} else if (!"/min".equals(unit)) {
			addInvalidInputValue(Attribute.RESP_RATE_TTL_NNUL, "Invalid Respiratory Rate unit: " + unit);
		} else {
			this.respRate = rate;
		}
	}

	public Double getPulse() {
		return pulse;
	}

	public void setPulse(Double pulse, String unit, Date measureTime) {
		// check that the measurement time is within the past hour
		if (!withinPastHour(now, new DateTime(measureTime))) {
			addStaleInputValue(Attribute.PULSE_TTL_NNUL, "Stale Pulse measurement.");
		} else if (pulse == null) {
			addMissingInputValue(Attribute.PULSE_TTL_NNUL, "Missing Pulse measurement.");
		} else if (!"/min".equals(unit)) {
			addInvalidInputValue(Attribute.PULSE_TTL_NNUL, "Invalid Pulse unit: " + unit);
		} else {
			this.pulse = pulse;
		}
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public Double getPostalCodeDist() {
		return postalCodeDist;
	}

	public void setPostalCodeDist(Double postalCodeDist) {
		this.postalCodeDist = postalCodeDist;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date date) {
		this.birthDate = date;
	}

	public Date getAdmDate() {
		return admDate;
	}

	public void setAdmDate(Date admDate) {
		this.admDate = admDate;
	}

	public Integer getAgeAtAdm() {
		if (ageAtAdm == null) {
			if (birthDate == null) {
				addMissingInputValue(Attribute.AGE_AT_ADM, "Cannot calculate Age at Admission; Missing birth date.");
			} else if (admDate == null) {
				addMissingInputValue(Attribute.AGE_AT_ADM,
						"Cannot calcualte Age at Admission; Missing Admission date.");
			} else {
				LocalDate bd = new LocalDate(birthDate);
				LocalDate admissionDate = new LocalDate(admDate);
				ageAtAdm = Years.yearsBetween(bd, admissionDate).getYears();
			}
		}
		return ageAtAdm;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAdmSource() {
		return admSource;
	}

	public void setAdmSource(String admSource) {
		this.admSource = admSource;
	}

	public String getAdmType() {
		return admType;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public String getHospServiceGroup() {
		return hospServiceGroup;
	}

	public void setHospServiceGroup(String hospServiceGroup) {
		this.hospServiceGroup = hospServiceGroup;
	}

	public Double getWbcValue() {
		return wbcValue;
	}

	public void setWbcValue(Double wbcValue, String unit) {
		if (wbcValue == null) {
			addMissingInputValue(Attribute.WBC_TTL_VALUE_NNUL, "Missing WBC measurement.");
		} else if (!"mcL".equals(unit) && !"k/uL".equals(unit)) {
			addInvalidInputValue(Attribute.WBC_TTL_VALUE_NNUL, "Invalid WBC unit: " + unit);
		} else {
			this.wbcValue = wbcValue;
		}
	}

	public AcuityScore getAcuityScore() {
		if (score == null) {
			score = new AcuityScore(temperature, systolic, respRate, pulse);
		}
		return score;
	}

	public void setAcuityScoreThreshold(int acuityScoreThreshold) {
		this.acuityThreshold = acuityScoreThreshold;
	}

	public void setWekaThreshold(double wekaThreshold) {
		this.wekaThreshold = wekaThreshold;
	}

	public boolean preemptProcess() {
		return getAcuityScore().getScore() > acuityThreshold;
	}

	public boolean getPrediction(double avgProbability) {
		return avgProbability >= wekaThreshold;
	}

	public Map<Attribute, List<String>> getInvalidInputValues() {
		return invalidInputValues;
	}

	public void addInvalidInputValue(Attribute attribute, String message) {
		if (invalidInputValues.get(attribute) == null) {
			invalidInputValues.put(attribute, new ArrayList<String>(Arrays.<String>asList(message)));
		} else {
			invalidInputValues.get(attribute).add(message);
		}
	}

	public Map<Attribute, List<String>> getMissingInputValues() {
		return missingInputValues;
	}

	public void addMissingInputValue(org.opencds.plugin.SepsisPredictionInstance.Attribute attribute, String message) {
		if (missingInputValues.get(attribute) == null) {
			missingInputValues.put(attribute, new ArrayList<String>(Arrays.<String>asList(message)));
		} else {
			missingInputValues.get(attribute).add(message);
		}
	}

	public Map<Attribute, List<String>> getStaleInputValues() {
		return staleInputValues;
	}

	private void addStaleInputValue(Attribute attribute, String message) {
		if (staleInputValues.get(attribute) == null) {
			staleInputValues.put(attribute, new ArrayList<String>(Arrays.<String>asList(message)));
		} else {
			staleInputValues.get(attribute).add(message);
		}
	}

	private boolean withinPastHour(DateTime reference, DateTime measureTime) {
		return measureTime.isBefore(reference.plusSeconds(1))
				&& measureTime.isAfter(reference.minusHours(1).minusSeconds(1));
	}

}
