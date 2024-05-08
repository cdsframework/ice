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

package org.opencds.plugin

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair
import org.joda.time.DateTime
import org.opencds.plugin.AcuityScore
import org.opencds.vmr.v1_0.internal.EvaluatedPerson

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import spock.lang.Specification
import weka.core.Attribute
import weka.core.Instance
import weka.core.Instances

class InstancesBuilderSpec extends Specification{
	
	def "test getArffHeaders"(){
		when:
		List<String> arffHeaders = []
		arffHeaders << "@relation 'Sepsis_48-weka.filters.unsupervised.attribute.Remove-R2-3-weka.filters.unsupervised.attribute.NumericToNominal-R1,2,5,6,9-19,30-33-weka.filters.unsupervised.attribute.Remove-V-R3,5,6,8,11,12,20-24,26,32,33'"
		arffHeaders << ""
		arffHeaders << "@attribute DISTCAT numeric"
		arffHeaders << "@attribute GENDER {M,F}"
		arffHeaders << "@attribute RACE {C,B,I,O,U,V,X,R}"
		arffHeaders << "@attribute AGE_AT_ADM numeric"
		arffHeaders << "@attribute ADM_SOURCE {0,1,2,4,5,6,8,18,23,24}"
		arffHeaders << "@attribute ADM_TYPE {1,2,3,5}"
		arffHeaders << "@attribute TEMP_TTL_NNUL numeric"
		arffHeaders << "@attribute RESP_RATE_TTL_NNUL numeric"
		arffHeaders << "@attribute PULSE_TTL_NNUL numeric"
		arffHeaders << "@attribute WBC_TTL_VALUE_NNUL numeric"
		arffHeaders << "@attribute SBP_TTL_NNUL numeric"
		arffHeaders << "@attribute BP_MAP_TTL_NNUL numeric"
		arffHeaders << "@attribute HOSP_SERVICE_GROUP {N-Surge,OB,Other,Medicine,Ortho,MicuPulm,Surgery,CTVSurgery,Neuro,Oncology,Rehab,Cardio,ED}"
		arffHeaders << "@attribute OUTCOME_POA {0,1}"
		arffHeaders << ""
		arffHeaders << "@data"
		
		def acuityScore
		def allFactLists
		def invalidValuesList
		
		Pair headers = InstancesBuilder.getArffHeaders(arffHeaders)
		def relation = headers.left
		Map<String, List<String>> attributes = headers.right
					
		// check order
		List<List<String>> list = new ArrayList<List<String>>(attributes.values())
		
		then:
		relation == "Sepsis_48-weka.filters.unsupervised.attribute.Remove-R2-3-weka.filters.unsupervised.attribute.NumericToNominal-R1,2,5,6,9-19,30-33-weka.filters.unsupervised.attribute.Remove-V-R3,5,6,8,11,12,20-24,26,32,33"
		list.get(0) == []
		list.get(1) == ["M", "F"]
		list.get(2) == ["C","B","I","O","U","V","X","R"]
		list.get(3) == []
		list.get(4) == ["0","1","2","4","5","6","8","18","23","24"]
		list.get(5) == ["1","2","3","5"]
		list.get(6) == []
		list.get(7) == []
		list.get(8) == []
		list.get(9) == []
		list.get(10) == []
		list.get(11) == []
		list.get(12) == ["N-Surge","OB","Other","Medicine","Ortho","MicuPulm","Surgery","CTVSurgery","Neuro","Oncology","Rehab","Cardio","ED"]
		list.get(13) == ["0","1"]
		attributes.get("DISTCAT") == []
		attributes.get("GENDER") == ["M","F"]
		attributes.get("RACE") == ["C","B","I","O","U","V","X","R"]
		attributes.get("AGE_AT_ADM") == []
		attributes.get("ADM_SOURCE") == ["0","1","2","4","5","6","8","18","23","24"]
		attributes.get("ADM_TYPE") == ["1","2","3","5"]
		attributes.get("TEMP_TTL_NNUL") == []
		attributes.get("RESP_RATE_TTL_NNUL") == []
		attributes.get("PULSE_TTL_NNUL") == []
		attributes.get("WBC_TTL_VALUE_NNUL") == []
		attributes.get("SBP_TTL_NNUL") == []
		attributes.get("BP_MAP_TTL_NNUL") == []
		attributes.get("HOSP_SERVICE_GROUP") == ["N-Surge","OB","Other","Medicine","Ortho","MicuPulm","Surgery","CTVSurgery","Neuro","Oncology","Rehab","Cardio","ED"]
		attributes.get("OUTCOME_POA") == ["0","1"]
	}
	
	def "test createWekaInstances with missing attributes"(){
		when:
		String relation = "Sepsis_48-weka.filters.unsupervised.attribute.Remove-R2-3-weka.filters.unsupervised.attribute.NumericToNominal-R1,2,5,6,9-19,30-33-weka.filters.unsupervised.attribute.Remove-V-R3,5,6,8,11,12,20-24,26,32,33"
		LinkedHashMap arffAttributes = 
			[
			"DISTCAT":[], 
			"GENDER":["M", "F"], 
			"RACE":["C", "B", "I", "O", "U", "V", "X", "R"], 
			"AGE_AT_ADM":[], 
			"ADM_SOURCE":["0", "1", "2", "4", "5", "6", "8", "18", "23", "24"], 
			"ADM_TYPE":["1", "2", "3", "5"], 
			"TEMP_TTL_NNUL":[], 
			"RESP_RATE_TTL_NNUL":[], 
			"PULSE_TTL_NNUL":[], 
			"WBC_TTL_VALUE_NNUL":[], 
			"SBP_TTL_NNUL":[], 
			"BP_MAP_TTL_NNUL":[], 
			"HOSP_SERVICE_GROUP":["N-Surge", "OB", "Other", "Medicine", "Ortho", "MicuPulm", "Surgery", "CTVSurgery", "Neuro", "Oncology", "Rehab", "Cardio", "ED"],
			"OUTCOME_POA":["0", "1"]
			]

		int outcomePoa = 13
		
		Table<Integer, String, Number> arffData = HashBasedTable.create()
		//arffData.put(0, "DISTCAT", 1973.24)
		//arffData.put(0, "GENDER", 0)
		arffData.put(0, "RACE", 0)
		arffData.put(0, "AGE_AT_ADM", 25)
		arffData.put(0, "ADM_SOURCE", 0)
		arffData.put(0, "ADM_TYPE", 0)
		arffData.put(0, "TEMP_TTL_NNUL", 39)
		arffData.put(0, "RESP_RATE_TTL_NNUL", 40)
		arffData.put(0, "PULSE_TTL_NNUL", 20)
		arffData.put(0, "WBC_TTL_VALUE_NNUL", 15000)
		arffData.put(0, "SBP_TTL_NNUL", 20)
		arffData.put(0, "BP_MAP_TTL_NNUL", 20)
		arffData.put(0, "HOSP_SERVICE_GROUP", 0)
		//arffData.put(0, "OUTCOME_POA", 0)
		
		List<String> missingValuesList = new ArrayList<>()
		missingValuesList.add("DISTCAT")
		missingValuesList.add("GENDER")
		
//		InstancesBuilder.missingValuesList = missingValuesList
		
		Instances instances = InstancesBuilder.createWekaInstances(relation, arffAttributes, arffData, 13)	
		Instance instance = instances.firstInstance()
		
		then:
		instance.isMissing(0) == true
		instance.isMissing(1) == true
		instance.toString().contains("?,?,C,25,0,1,39,40,20,15000,20,20,N-Surge,?")
	}
	
	def "test public method to get the Instances"() {
		given:
		Date date = new Date()
		SepsisPredictionInstance inst = new SepsisPredictionInstance()
		inst.setPostalCodeDist(120.0)
		inst.setBirthDate(new DateTime().minus(234789168).toDate())
		inst.setAdmDate(new Date())
		inst.setAdmSource("4")
		inst.setAdmType("3")
		inst.setTemperature(99.5, "Far", date)
		inst.setRespRate(30, "/min", date)
		inst.setPulse(72, "/min", date)
		inst.setWbcValue(15000, "mcL")
		inst.setSystolic(90.0, "mm[Hg]", date)
		inst.setDiastolic(120.0, "mm[Hg]", date)
		inst.setGender('M')
		inst.setHospServiceGroup("OB")
		inst.setRace("I")
		
		inst.setAcuityScoreThreshold(5)
		inst.setWekaThreshold(0.5)
		
		and:
		List<String> arffHeaders = []
		arffHeaders << "@relation 'Sepsis_48-weka.filters.unsupervised.attribute.Remove-R2-3-weka.filters.unsupervised.attribute.NumericToNominal-R1,2,5,6,9-19,30-33-weka.filters.unsupervised.attribute.Remove-V-R3,5,6,8,11,12,20-24,26,32,33'"
		arffHeaders << ""
		arffHeaders << "@attribute DISTCAT numeric"
		arffHeaders << "@attribute GENDER {M,F}"
		arffHeaders << "@attribute RACE {C,B,I,O,U,V,X,R}"
		arffHeaders << "@attribute AGE_AT_ADM numeric"
		arffHeaders << "@attribute ADM_SOURCE {0,1,2,4,5,6,8,18,23,24}"
		arffHeaders << "@attribute ADM_TYPE {1,2,3,5}"
		arffHeaders << "@attribute TEMP_TTL_NNUL numeric"
		arffHeaders << "@attribute RESP_RATE_TTL_NNUL numeric"
		arffHeaders << "@attribute PULSE_TTL_NNUL numeric"
		arffHeaders << "@attribute WBC_TTL_VALUE_NNUL numeric"
		arffHeaders << "@attribute SBP_TTL_NNUL numeric"
		arffHeaders << "@attribute BP_MAP_TTL_NNUL numeric"
		arffHeaders << "@attribute HOSP_SERVICE_GROUP {N-Surge,OB,Other,Medicine,Ortho,MicuPulm,Surgery,CTVSurgery,Neuro,Oncology,Rehab,Cardio,ED}"
		arffHeaders << "@attribute OUTCOME_POA {0,1}"
		arffHeaders << ""
		arffHeaders << "@data"

		when:
		Instances instances = InstancesBuilder.build(inst, arffHeaders)
		Instance instance = instances.firstInstance()
		
		then:
		instances
		inst.invalidInputValues.isEmpty()
		inst.missingInputValues.isEmpty()
		instance.toString().contains("120,M,I,0,4,3,37.5,30,72,15000,90,110,OB,?")
	}
}
