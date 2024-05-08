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

import org.joda.time.DateTime
import org.opencds.plugin.PluginContext.PostProcessPluginContext
import org.opencds.plugin.support.PluginDataCacheImpl
import org.opencds.service.weka.WekaInput
import org.opencds.service.weka.WekaOutput
import org.opencds.vmr.v1_0.internal.CDSInput
import org.opencds.vmr.v1_0.internal.EvaluatedPerson
import org.opencds.vmr.v1_0.internal.FocalPersonId
import org.opencds.vmr.v1_0.internal.ObservationResult
import org.opencds.vmr.v1_0.internal.VMR

import spock.lang.Specification

class WekaSepsisPostProcessorSpec extends Specification {
	enum X {
		MODULE_CREATING_RESULT,
		WEKA_PROBABILITY,
		ACUITY_SCORE,
		C28,
		C225,
		C308,
		C486,
		C487,
		C2559,
		C2560,
		C3734,
		C3739,
		C3741,
		C3742,
		C3743,
		C3744,
		C3745,
		C3746,
		C3747
	}

	private WekaSepsisPostProcessor proc

	def setup() {
		proc = new WekaSepsisPostProcessor()
	}

	def "test successful post processing"() {
		given:
		SepsisPredictionInstance inst = new SepsisPredictionInstance()
		Date date = new DateTime().minusMinutes(30).toDate()
		inst.setTemperature(38.0d, 'Cel', date)
		inst.setDiastolic(80.0, 'mm[Hg]', date)
		inst.setSystolic(120, 'mm[Hg]', date)
		inst.setRespRate(30, '/min', date)
		inst.setPulse(81, '/min', date)

		SepsisPredictionWekaInput input = new SepsisPredictionWekaInput(inst)
		Map allFL = [
			(WekaInput.class) : [input],
			(CDSInput.class) : [new CDSInput()],
			(VMR.class) : [new VMR()],
			(EvaluatedPerson.class) : [new EvaluatedPerson()],
			(FocalPersonId.class) : [new FocalPersonId()]]


		List<double[]> dists = new ArrayList<>()
		dists.add([1, 0.5, 1, 1, 1] as double[])
		def output = new WekaOutput(dists)
		Map resultFL = [WekaOutput : [output]]
		PostProcessPluginContext context = PostProcessPluginContext.create(allFL,[:],[] as Set,resultFL,[:],new PluginDataCacheImpl())

		when:
		proc.execute(context)

		then:
		println resultFL.keySet()
		def results = obsResultsToMap(resultFL.ObservationResult)
		results.C308 == [true]
		results.MODULE_CREATING_RESULT == ['WEKA']
		results.WEKA_PROBABILITY == [0.5]
		results.ACUITY_SCORE == [3]
		results.C28 == null
		results.C225 == null
		results.C486 == null
		results.C487 == null
		results.C2559 == [80.0]
		results.C2560 == [120.0]
		results.C3734 == null
		results.C3739 == null
		results.C3741 == null
		results.C3742 == null
		results.C3743 == [38.0]
		results.C3744 == [30.0]
		results.C3745 == [81.0]
		results.C3746 == null
		results.C3747 == [93.0]
	}

	def "test post processing with missing values"() {
		given:
		SepsisPredictionInstance inst = new SepsisPredictionInstance()
		Date date = new DateTime().minusMinutes(30).toDate()
		inst.setTemperature(null, 'Cel', date)
		inst.setDiastolic(null, 'mm[Hg]', date)
		inst.setSystolic(null, 'mm[Hg]', date)
		inst.setRespRate(null, '/min', date)
		inst.setPulse(null, '/min', date)
		inst.setWbcValue(null, 'mcL')
		inst.getMapBP()
		inst.getAgeAtAdm()

		SepsisPredictionWekaInput input = new SepsisPredictionWekaInput(inst)
		Map allFL = [
			(WekaInput.class) : [input],
			(CDSInput.class) : [new CDSInput()],
			(VMR.class) : [new VMR()],
			(EvaluatedPerson.class) : [new EvaluatedPerson()],
			(FocalPersonId.class) : [new FocalPersonId()]]


		List<double[]> dists = new ArrayList<>()
		Map resultFL = [:]
		PostProcessPluginContext context = PostProcessPluginContext.create(allFL,[:],[] as Set,resultFL,[:],new PluginDataCacheImpl())

		when:
		proc.execute(context)

		then:
		println resultFL.keySet()
		def results = obsResultsToMap(resultFL.ObservationResult)
		results.C308 == [false]
		results.MODULE_CREATING_RESULT == ['ACUITY']
		results.WEKA_PROBABILITY == null
		results.ACUITY_SCORE == [-1]
		results.C28 == null
		results.C225 == null
		results.C486 == null
		results.C487 == null
		results.C2559 == ['Missing Diastolic Blood Pressure.']
		results.C2560 == ['Missing Systolic Blood Pressure.']
		results.C3734 == null
		results.C3739 == null
		results.C3741 == null
		results.C3742 == ['Cannot calculate Age at Admission; Missing birth date.']
		results.C3743 == ['Missing Temperature measurement.']
		results.C3744 == ['Missing Respiratory Rate measurement.']
		results.C3745 == ['Missing Pulse measurement.']
		results.C3746 == ['Missing WBC measurement.']
		results.C3747 == ['Unable to calculate BP_MAP due to missing/invalid Diastolic and/or Systolic BP values.']
	}

	def "test successful post processing with high acuity"() {
		given:
		SepsisPredictionInstance inst = new SepsisPredictionInstance()
		Date date = new DateTime().minusMinutes(30).toDate()
		inst.setTemperature(45.0d, 'Cel', date)
		inst.setDiastolic(80.0, 'mm[Hg]', date)
		inst.setSystolic(170, 'mm[Hg]', date)
		inst.setRespRate(46, '/min', date)
		inst.setPulse(120, '/min', date)

		SepsisPredictionWekaInput input = new SepsisPredictionWekaInput(inst)
		Map allFL = [
			(WekaInput.class) : [input],
			(CDSInput.class) : [new CDSInput()],
			(VMR.class) : [new VMR()],
			(EvaluatedPerson.class) : [new EvaluatedPerson()],
			(FocalPersonId.class) : [new FocalPersonId()]]


		List<double[]> dists = new ArrayList<>()
		Map resultFL = [:]
		PostProcessPluginContext context = PostProcessPluginContext.create(allFL,[:],[] as Set,resultFL,[:],new PluginDataCacheImpl())

		when:
		proc.execute(context)

		then:
		println resultFL.keySet()
		def results = obsResultsToMap(resultFL.ObservationResult)
		results.C308 == [true]
		results.MODULE_CREATING_RESULT == ['ACUITY']
		results.WEKA_PROBABILITY == null
		results.ACUITY_SCORE == [8]
		results.C28 == null
		results.C225 == null
		results.C486 == null
		results.C487 == null
		results.C2559 == [80.0]
		results.C2560 == [170.0]
		results.C3734 == null
		results.C3739 == null
		results.C3741 == null
		results.C3742 == null
		results.C3743 == [45.0]
		results.C3744 == [46.0]
		results.C3745 == [120.0]
		results.C3746 == null
		results.C3747 == [110.0]
	}

	private Map obsResultsToMap(List<ObservationResult> obsResults) {
		Map results = [:]
		
		for (ObservationResult or : obsResults) {
			println or.getObservationFocus().code
			println or.getObservationValue()
			println ""
			X code = X.valueOf(or.getObservationFocus().code)
			if (results."${code.name()}" == null) {
				results."${code.name()}" = []
			}
			switch (code) {
				case X.C308:
					results."${code.name()}" << or.getObservationValue()._boolean.value
					break;
				case X.WEKA_PROBABILITY:
					results."${code.name()}" << or.getObservationValue().decimal.value
					break;
				case X.ACUITY_SCORE:
					results."${code.name()}" << or.getObservationValue().integer.value
					break;
				case [
					X.MODULE_CREATING_RESULT,
					X.C28,
					X.C225,
					X.C486,
					X.C487,
					X.C2559,
					X.C2560,
					X.C3734,
					X.C3739,
					X.C3741,
					X.C3742,
					X.C3743,
					X.C3744,
					X.C3745,
					X.C3746,
					X.C3747
				]:
					if (or.getObservationValue().text) {
						results."${code.name()}" << or.getObservationValue().text
					} else if (or.getObservationValue().decimal) {
						results."${code.name()}" << or.getObservationValue().decimal.value
					}
					break;
				default:
					break;
			}
		}



		//		DISTCAT('C3741',null),
		//		GENDER('C28',null),
		//		RACE('C225',null),
		//		AGE_AT_ADM('C3742',null),
		//		ADM_SOURCE('C486','2.16.840.1.113883.3.795.5.2.12.88'),
		//		ADM_TYPE('C487','2.16.840.1.113883.3.795.5.2.12.14'),
		//		TEMP_TTL_NNUL('C3743',null),
		//		RESP_RATE_TTL_NNUL('C3744',null),
		//		PULSE_TTL_NNUL('C3745',null),
		//		WBC_TTL_VALUE_NNUL('C3746',null),
		//		SBP_TTL_NNUL('C2560',null),
		//		DBP('C2559',null),
		//		BP_MAP_TTL_NNUL('C3747',null),
		//		HOSP_SERVICE_GROUP('C3734','2.16.840.1.113883.3.795.5.2.12.99'),
		//		MODULE_CREATING_RESULT('MODULE_CREATING_RESULT',null),
		//		WEKA_PROBABILITY('WEKA_PROBABILITY',null),
		//		ACUITY_SCORE('ACUITY_SCORE',null),
		//		INVALID_VALUE('C3739',null),
		//		SEPSIS_EVALUATION('C308', '2.16.840.1.113883.3.795.12.1.1')

		results
	}
}
