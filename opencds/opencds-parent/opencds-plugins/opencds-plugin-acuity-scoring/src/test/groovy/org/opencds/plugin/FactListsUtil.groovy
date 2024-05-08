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

import org.joda.time.LocalDateTime
import org.opencds.plugin.AcuityScore
import org.opencds.vmr.v1_0.internal.Demographics
import org.opencds.vmr.v1_0.internal.EncounterEvent
import org.opencds.vmr.v1_0.internal.EvaluatedPerson
import org.opencds.vmr.v1_0.internal.ObservationResult
import org.opencds.vmr.v1_0.internal.ObservationValue
import org.opencds.vmr.v1_0.internal.concepts.ObservationCodedValueConcept
import org.opencds.vmr.v1_0.internal.concepts.ObservationFocusConcept
import org.opencds.vmr.v1_0.internal.concepts.RaceConcept
import org.opencds.vmr.v1_0.internal.datatypes.AD
import org.opencds.vmr.v1_0.internal.datatypes.ADXP
import org.opencds.vmr.v1_0.internal.datatypes.AddressPartType
import org.opencds.vmr.v1_0.internal.datatypes.CD
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate
import org.opencds.vmr.v1_0.internal.datatypes.PQ

class FactListsUtil {

    static Map<Map<Class<?>, List<?>>, Boolean> buildFactLists(LocalDateTime now, String sepsisTest, int sepsisHours) {
        List<String> testData = new File(sepsisTest).text.split('\n') as List

        Map<Map<Class<?>, List<?>>, Boolean> aflMap = new LinkedHashMap<>()
        for (int i = 18; i < testData.size(); i++) {
            Map<Class<?>, List<?>> fl = new HashMap<>()
            fl.put(EvaluatedPerson.class, [])
            fl.put(RaceConcept.class, [])
            fl.put(EncounterEvent.class, [])
            fl.put(ObservationFocusConcept.class, [])
            fl.put(ObservationResult.class, [])
            fl.put(ObservationCodedValueConcept.class, [])

            List<String> data = testData[i].split(',')
            // distcat (distance between zip codes)

            // gender
            EvaluatedPerson p = new EvaluatedPerson()
            p.demographics = new Demographics()
            p.demographics.gender = new CD()
            p.demographics.gender.code = data[1]
            // added below (age_at_adm)

            // race
            RaceConcept rc = new RaceConcept()
            rc.openCdsConceptCode = data[2]
            fl.get(RaceConcept.class).add(rc)

            // admission time...
            EncounterEvent ee = new EncounterEvent()
            ee.encounterEventTime = new IVLDate()
            ee.encounterEventTime.low = now.minusHours(sepsisHours).toDate()
            fl.get(EncounterEvent.class).add(ee)

            // age_at_adm
            p.demographics.birthTime = now.minusYears(Integer.valueOf(data[3])).minusMonths(6).toDate()
            fl.get(EvaluatedPerson.class).add(p)

            // adm_source
            ObservationCodedValueConcept admsource = new ObservationCodedValueConcept()
            admsource.openCdsConceptCode = "C4001"
            admsource.conceptTargetId = "2"
            fl.get(ObservationCodedValueConcept.class).add(admsource)
            ObservationResult oradmsource = new ObservationResult()
            oradmsource.id = "2"
            oradmsource.observationFocus = new CD()
            oradmsource.observationValue = new ObservationValue()
            oradmsource.observationValue.concept = new CD()
            oradmsource.observationValue.concept.code = data[4]
            fl.get(ObservationResult).add(oradmsource)

            // adm_type
            ObservationCodedValueConcept admtype = new ObservationCodedValueConcept()
            admtype.openCdsConceptCode = "C4002"
            admtype.conceptTargetId = "3"
            fl.get(ObservationCodedValueConcept.class).add(admtype)
            ObservationResult oradmtype = new ObservationResult()
            oradmtype.id = "3"
            oradmtype.observationFocus = new CD()
            oradmtype.observationValue = new ObservationValue()
            oradmtype.observationValue.concept = new CD()
            oradmtype.observationValue.concept.code = data[5]
            fl.get(ObservationResult).add(oradmtype)

            // temp
            ObservationResult temp = new ObservationResult()
            temp.observationFocus = new CD()
            temp.observationFocus.code = "8310-5"
            temp.observationValue = new ObservationValue()
            temp.observationValue.physicalQuantity = new PQ()
            temp.observationValue.physicalQuantity.value = Double.valueOf(data[6])
            temp.observationValue.physicalQuantity.unit = "Cel"
            fl.get(ObservationResult.class).add(temp)

            // resp_rate
            ObservationResult resp = new ObservationResult()
            resp.observationFocus = new CD()
            resp.observationFocus.code = "9279-1"
            resp.observationValue = new ObservationValue()
            resp.observationValue.physicalQuantity = new PQ()
            resp.observationValue.physicalQuantity.value = Double.valueOf(data[7])
            resp.observationValue.physicalQuantity.unit = "/min"
            fl.get(ObservationResult.class).add(resp)

            // pulse
            ObservationResult pulse = new ObservationResult()
            pulse.observationFocus = new CD()
            pulse.observationFocus.code = "8867-4"
            pulse.observationValue = new ObservationValue()
            pulse.observationValue.physicalQuantity = new PQ()
            pulse.observationValue.physicalQuantity.value = Double.valueOf(data[8])
            pulse.observationValue.physicalQuantity.unit = "/min"
            fl.get(ObservationResult.class).add(pulse)

            // wbc
            ObservationFocusConcept focus = new ObservationFocusConcept()
            focus.openCdsConceptCode = "C4000"
            fl.get(ObservationFocusConcept.class).add(focus)
            ObservationResult or = new ObservationResult()
            or.id = "1"
            focus.conceptTargetId = "1"
            or.observationFocus = new CD()
            or.observationValue = new ObservationValue()
            or.observationValue.physicalQuantity = new PQ()
            or.observationValue.physicalQuantity.value = Double.parseDouble(data[9])
            or.observationValue.physicalQuantity.unit = "mcL"
            fl.get(ObservationResult.class).add(or)

            // sbp
            ObservationResult sbp = new ObservationResult()
            sbp.observationFocus = new CD()
            sbp.observationFocus.code = "8480-6"
            sbp.observationValue = new ObservationValue()
            sbp.observationValue.physicalQuantity = new PQ()
            sbp.observationValue.physicalQuantity.value = Double.valueOf(data[10])
            sbp.observationValue.physicalQuantity.unit = "mm[Hg]"
            fl.get(ObservationResult.class).add(sbp)

            double systolic = sbp.observationValue.physicalQuantity.value

            // bp_map - convert to systolic
            ObservationResult dbp = new ObservationResult()
            dbp.observationFocus = new CD()
            dbp.observationFocus.code = "8462-4"
            dbp.observationValue = new ObservationValue()
            dbp.observationValue.physicalQuantity = new PQ()

            /*
             * Mean Arterial Pressure approximate
             * value = 2/3*DP + 1/3*SP
             *
             *        2DP + SP
             * v = ------------------
             *           3
             *
             * 3v = 2DP + SP
             *
             * 3v - SP = 2DP
             *
             * 3v - SP
             * ------- = DP
             *    2
             * sp = (3 * value)
             */
            double diastolic = (3.0 * Double.valueOf(data[11]) - systolic) / 2.0

            dbp.observationValue.physicalQuantity.value = diastolic
            dbp.observationValue.physicalQuantity.unit = "mm[Hg]"
            fl.get(ObservationResult.class).add(dbp)

            // hosp_service_group
            ObservationCodedValueConcept hsg = new ObservationCodedValueConcept()
            hsg.openCdsConceptCode = data[12]
            fl.get(ObservationCodedValueConcept).add(hsg)

            Boolean outcome = data[13] == "0"
            aflMap.put(fl, outcome)

        }
        aflMap
    }

}
