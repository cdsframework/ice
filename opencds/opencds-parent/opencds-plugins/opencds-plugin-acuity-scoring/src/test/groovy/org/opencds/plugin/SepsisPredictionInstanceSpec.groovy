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
import org.opencds.plugin.SepsisPredictionInstance.Attribute

import spock.lang.Specification

class SepsisPredictionInstanceSpec extends Specification {

	SepsisPredictionInstance inst

	def "setup"() {
		inst = new SepsisPredictionInstance()
	}

	def "is within past hour if a second before"() {
		given:
		DateTime now = new DateTime()
		DateTime testDate = now.minusSeconds(1)

		when:
		def is = inst.withinPastHour(now, testDate)

		then:
		is == true
	}

	def "is within past hour if same time"() {
		given:
		DateTime now = new DateTime()
		DateTime testDate = now

		when:
		def is = inst.withinPastHour(now, testDate)

		then:
		is == true
	}

	def "is within past hour if a second after exactly an hour ago"() {
		given:
		DateTime now = new DateTime()
		DateTime testDate = now.minusHours(1).plusSeconds(1)

		when:
		def is = inst.withinPastHour(now, testDate)

		then:
		is == true
	}

	def "is within past hour if exactly an hour ago"() {
		given:
		DateTime now = new DateTime()
		DateTime testDate = now.minusHours(1)

		when:
		def is = inst.withinPastHour(now, testDate)

		then:
		is == true
	}

	def "is NOT within past hour if a second before exactly an hour ago"() {
		given:
		DateTime now = new DateTime()
		DateTime testDate = now.minusHours(1).minusSeconds(1)

		when:
		def is = inst.withinPastHour(now, testDate)

		then:
		is == false
	}

	def "temperature is stale"() {
		given:
		Double value = 38.0d;
		String unit = 'Cel'
		Date date = new DateTime().minusHours(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 1
		inst.staleInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Stale Temperature measurement."
		]
		inst.getTemperature() == null
	}

	def "temperature has unsupported unit"() {
		given:
		Double value = 38.0d;
		String unit = 'Unsupported'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Invalid temperature unit: " + unit
		]
		inst.staleInputValues.size() == 0
		inst.getTemperature() == null
	}

	def "temperature is missing"() {
		given:
		Double value = null
		String unit = 'Cel'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Missing Temperature measurement."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getTemperature() == null
	}

	def "temperature is out of range (high)"() {
		given:
		Double value = 50.1d;
		String unit = 'Cel'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Invalid temperature value (greater than 50): 50.1 (Cel)"
		]
		inst.staleInputValues.size() == 0
		inst.getTemperature() == null
	}

	def "temperature is out of range (low)"() {
		given:
		Double value = 34.9d;
		String unit = 'Cel'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Invalid temperature value (less than 35): 34.9 (Cel)"
		]
		inst.staleInputValues.size() == 0
		inst.getTemperature() == null
	}

	def "Fahrenheit temperature is out of range (high)"() {
		given:
		Double value = 122.18d;
		String unit = 'Far'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Invalid temperature value (greater than 50): 122.18 (Far)"
		]
		inst.staleInputValues.size() == 0
		inst.getTemperature() == null
	}

	def "Fahrenheit temperature is out of range (low)"() {
		given:
		Double value = 94.82d;
		String unit = 'Far'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.TEMP_TTL_NNUL) == [
			"Invalid temperature value (less than 35): 94.82 (Far)"
		]
		inst.staleInputValues.size() == 0
		inst.getTemperature() == null
	}

	def "temperature is as expected in Fahrenheit"() {
		given:
		Double value = 98.6d;
		String unit = 'Far'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getTemperature() == 37.0 // Celcius
	}

	def "temperature is as expected in Celcius"() {
		given:
		Double value = 45.0d;
		String unit = 'Cel'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setTemperature(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getTemperature() == 45.0
	}

	def "diastolic is stale"() {
		given:
		Double value = 80.0d
		String unit = 'mm[Hg]'
		Date date = new DateTime().minusHours(2).toDate()

		when:
		inst.setDiastolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 1
		inst.staleInputValues.get(Attribute.DBP) == [
			"Stale Diastolic measurement."
		]
		inst.getDiastolic() == null
	}

	def "diastolic is missing"() {
		given:
		Double value = null
		String unit = 'mm[Hg]'
		Date date = new DateTime().minusMinutes(20).toDate()

		when:
		inst.setDiastolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.get(Attribute.DBP) == [
			"Missing Diastolic Blood Pressure."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getDiastolic() == null
	}

	def "diastolic unit is invalid"() {
		given:
		Double value = 80.0d
		String unit = 'bad'
		Date date = new DateTime().minusMinutes(20).toDate()

		when:
		inst.setDiastolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.DBP) == [
			"Invalid Diastolic Blood Pressure unit: bad"
		]
		inst.staleInputValues.size() == 0
		inst.getDiastolic() == null
	}

	def "diastolic is valid"() {
		given:
		Double value = 80.0d
		String unit = 'mm[Hg]'
		Date date = new DateTime().minusMinutes(20).toDate()

		when:
		inst.setDiastolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getDiastolic() == 80
	}

	def "systolic is stale"() {
		given:
		Double value = 120.0d
		String unit = 'mm[Hg]'
		Date date = new DateTime().minusHours(2).toDate()

		when:
		inst.setSystolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 1
		inst.staleInputValues.get(Attribute.SBP_TTL_NNUL) == [
			"Stale Systolic measurement."
		]
		inst.getSystolic() == null
	}

	def "systolic is missing"() {
		given:
		Double value = null
		String unit = 'mm[Hg]'
		Date date = new DateTime().minusMinutes(20).toDate()

		when:
		inst.setSystolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.get(Attribute.SBP_TTL_NNUL) == [
			"Missing Systolic Blood Pressure."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getSystolic() == null
	}

	def "systolic unit is invalid"() {
		given:
		Double value = 120.0d
		String unit = 'bad'
		Date date = new DateTime().minusMinutes(20).toDate()

		when:
		inst.setSystolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.SBP_TTL_NNUL) == [
			"Invalid Systolic Blood Pressure unit: bad"
		]
		inst.staleInputValues.size() == 0
		inst.getSystolic() == null
	}

	def "systolic is valid"() {
		given:
		Double value = 120.0d
		String unit = 'mm[Hg]'
		Date date = new DateTime().minusMinutes(20).toDate()

		when:
		inst.setDiastolic(value, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getDiastolic() == 120
	}

	def "mapBP should be provided if diastolic and systolic are not null"() {
		given:
		inst.diastolic = 120.0
		inst.systolic = 80.0

		when:
		def mapBP = inst.getMapBP()

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		mapBP == 110.0
	}

	def "mapBP should be null if diastolic and systolic are null"() {
		given:
		inst.diastolic = null
		inst.systolic = null

		when:
		def mapBP = inst.getMapBP()

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.getAt(Attribute.BP_MAP_TTL_NNUL) == [
			"Unable to calculate BP_MAP due to missing/invalid Diastolic and/or Systolic BP values."
		]
		inst.staleInputValues.size() == 0
		mapBP == null
	}

	def "mapBP should be null if diastolic is null"() {
		given:
		inst.diastolic = null
		inst.systolic = 80.0

		when:
		def mapBP = inst.getMapBP()

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.getAt(Attribute.BP_MAP_TTL_NNUL) == [
			"Unable to calculate BP_MAP due to missing/invalid Diastolic and/or Systolic BP values."
		]
		inst.staleInputValues.size() == 0
		mapBP == null
	}

	def "mapBP should be null if systolic is null"() {
		given:
		inst.diastolic = 120.0
		inst.systolic = null

		when:
		def mapBP = inst.getMapBP()

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.getAt(Attribute.BP_MAP_TTL_NNUL) == [
			"Unable to calculate BP_MAP due to missing/invalid Diastolic and/or Systolic BP values."
		]
		inst.staleInputValues.size() == 0
		mapBP == null
	}

	def "respRate is state"() {
		given:
		Double rate = 30.0
		String unit = '/min'
		Date date = new DateTime().minusHours(2).toDate()

		when:
		inst.setRespRate(rate, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 1
		inst.staleInputValues.getAt(Attribute.RESP_RATE_TTL_NNUL) == [
			"Stale Respiratory Rate measurement."
		]
		inst.getRespRate() == null
	}

	def "respRate has bad units"() {
		given:
		Double rate = 30.0
		String unit = 'bad'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setRespRate(rate, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.getAt(Attribute.RESP_RATE_TTL_NNUL) == [
			"Invalid Respiratory Rate unit: bad"
		]
		inst.staleInputValues.size() == 0
		inst.getRespRate() == null
	}

	def "respRate has missing value"() {
		given:
		Double rate = null
		String unit = '/min'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setRespRate(rate, unit, date)

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.getAt(Attribute.RESP_RATE_TTL_NNUL) == [
			"Missing Respiratory Rate measurement."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getRespRate() == null
	}

	def "respRate is valid"() {
		given:
		Double rate = 30.0
		String unit = '/min'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setRespRate(rate, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getRespRate() == 30.0
	}

	def "pulse is state"() {
		given:
		Double rate = 30.0
		String unit = '/min'
		Date date = new DateTime().minusHours(2).toDate()

		when:
		inst.setPulse(rate, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 1
		inst.staleInputValues.getAt(Attribute.PULSE_TTL_NNUL) == [
			"Stale Pulse measurement."
		]
		inst.getPulse() == null
	}

	def "pulse has bad units"() {
		given:
		Double rate = 30.0
		String unit = 'bad'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setPulse(rate, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.getAt(Attribute.PULSE_TTL_NNUL) == [
			"Invalid Pulse unit: bad"
		]
		inst.staleInputValues.size() == 0
		inst.getPulse() == null
	}

	def "pulse has missing value"() {
		given:
		Double rate = null
		String unit = '/min'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setPulse(rate, unit, date)

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.getAt(Attribute.PULSE_TTL_NNUL) == [
			"Missing Pulse measurement."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getPulse() == null
	}

	def "pulse is valid"() {
		given:
		Double rate = 30.0
		String unit = '/min'
		Date date = new DateTime().minusMinutes(2).toDate()

		when:
		inst.setPulse(rate, unit, date)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.getPulse() == 30.0
	}

	def "getAgeAtAdm null birthDate"() {
		given:
		inst.birthDate = null
		inst.admDate = new Date()

		when:
		def ageAtAdm = inst.getAgeAtAdm()

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.getAt(Attribute.AGE_AT_ADM) == [
			"Cannot calculate Age at Admission; Missing birth date."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		ageAtAdm == null
	}

	def "getAgeAtAdm null admDate"() {
		given:
		inst.birthDate = new Date()
		inst.admDate = null

		when:
		def ageAtAdm = inst.getAgeAtAdm()

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.getAt(Attribute.AGE_AT_ADM) == [
			"Cannot calcualte Age at Admission; Missing Admission date."
		]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		ageAtAdm == null
	}

	def "getAgeAtAdm is valid"() {
		given:
		inst.birthDate = new DateTime().minusYears(75).minusMonths(10).toDate()
		inst.admDate = new Date()

		when:
		def ageAtAdm = inst.getAgeAtAdm()

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		ageAtAdm == 75
	}

	def "wbcValue is null"() {
		given:
		Double wbc = null
		String unit = "mcL"

		when:
		inst.setWbcValue(wbc, unit)

		then:
		inst.missingInputValues.size() == 1
		inst.missingInputValues.get(Attribute.WBC_TTL_VALUE_NNUL) == ["Missing WBC measurement."]
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.wbcValue == null
	}

	def "wbc unit is bad"() {
		given:
		Double wbc = 15000.0
		String unit = "bad"

		when:
		inst.setWbcValue(wbc, unit)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 1
		inst.invalidInputValues.get(Attribute.WBC_TTL_VALUE_NNUL) == ["Invalid WBC unit: bad"]
		inst.staleInputValues.size() == 0
		inst.wbcValue == null
	}

	def "wbc unit is valid"() {
		given:
		Double wbc = 15000.0
		String unit = "mcL"

		when:
		inst.setWbcValue(wbc, unit)

		then:
		inst.missingInputValues.size() == 0
		inst.invalidInputValues.size() == 0
		inst.staleInputValues.size() == 0
		inst.wbcValue == 15000.0
	}

	def "getAcutiyScore with no missing values"() {
		given:
		inst.temperature = 45.0
		inst.systolic = 120
		inst.respRate = 35
		inst.pulse = 80

		when:
		def ac = inst.getAcuityScore()

		then:
		ac
		ac.score == 6
		!ac.hasMissingValues()
	}

	def "getAcutiyScore with missing temperature"() {
		given:
		//inst.temperature = 45.0
		inst.systolic = 120
		inst.respRate = 35
		inst.pulse = 80

		when:
		def ac = inst.getAcuityScore()

		then:
		ac
		ac.score == -1
		ac.hasMissingValues()
		ac.missingValues.size() == 1
		ac.missingValues.contains(Attribute.TEMP_TTL_NNUL)
	}

	def "getAcutiyScore with missing systolic"() {
		given:
		inst.temperature = 45.0
		inst.respRate = 35
		inst.pulse = 80

		when:
		def ac = inst.getAcuityScore()

		then:
		ac
		ac.score == -1
		ac.hasMissingValues()
		ac.missingValues.size() == 1
		ac.missingValues.contains(Attribute.SBP_TTL_NNUL)
	}

	def "getAcutiyScore with missing respRate"() {
		given:
		inst.temperature = 45.0
		inst.systolic = 120
		inst.pulse = 80

		when:
		def ac = inst.getAcuityScore()

		then:
		ac
		ac.score == -1
		ac.hasMissingValues()
		ac.missingValues.size() == 1
		ac.missingValues.contains(Attribute.RESP_RATE_TTL_NNUL)
	}

	def "getAcutiyScore with missing pulse"() {
		given:
		inst.temperature = 45.0
		inst.systolic = 120
		inst.respRate = 35

		when:
		def ac = inst.getAcuityScore()

		then:
		ac
		ac.score == -1
		ac.hasMissingValues()
		ac.missingValues.size() == 1
		ac.missingValues.contains(Attribute.PULSE_TTL_NNUL)
	}

	def "getAcutiyScore with missing vitals"() {
		when:
		def ac = inst.getAcuityScore()

		then:
		ac
		ac.score == -1
		ac.hasMissingValues()
		ac.missingValues.size() == 4
		ac.missingValues.contains(Attribute.TEMP_TTL_NNUL)
		ac.missingValues.contains(Attribute.SBP_TTL_NNUL)
		ac.missingValues.contains(Attribute.RESP_RATE_TTL_NNUL)
		ac.missingValues.contains(Attribute.PULSE_TTL_NNUL)
	}
}
