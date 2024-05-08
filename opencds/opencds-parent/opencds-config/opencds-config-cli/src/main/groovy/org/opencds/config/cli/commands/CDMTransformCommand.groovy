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

package org.opencds.config.cli.commands

import org.opencds.config.api.model.CDMId
import org.opencds.config.api.model.Concept
import org.opencds.config.api.model.ConceptDeterminationMethod
import org.opencds.config.api.model.ConceptMapping
import org.opencds.config.api.model.ValueSet
import org.opencds.config.api.model.impl.CDMIdImpl
import org.opencds.config.api.model.impl.ConceptDeterminationMethodImpl
import org.opencds.config.api.model.impl.ConceptImpl
import org.opencds.config.api.model.impl.ConceptMappingImpl
import org.opencds.config.api.model.impl.ValueSetImpl
import org.opencds.config.mapper.util.RestConfigUtil

class CDMTransformCommand {
	InputStream input
	OutputStream output
	String user

	public CDMTransformCommand(InputStream input, OutputStream output, String user) {
		this.input = input
		this.output = output
		this.user = user
	}

	def cdmToPsv = {
		RestConfigUtil restUtil = new RestConfigUtil()
		List<ConceptDeterminationMethod> cdms = restUtil.unmarshalCdms(input)
		output << "CDM_DISPLAY_NAME|CDM_VERSION|CDM_CODE|CDM_CODE_SYSTEM|"
		output << "TO_CONCEPT_DISPLAY_NAME|TO_CONCEPT_CODE|TO_CONCEPT_CODE_SYSTEM_NAME|TO_CONCEPT_CODE_SYSTEM|TO_CONCEPT_COMMENT|"
		output << "TO_CONCEPT_VALUE_SET_OID|TO_CONCEPT_VALUE_SET_NAME|"
		output << "FROM_CONCEPT_DISPLAY_NAME|FROM_CONCEPT_CODE|FROM_CONCEPT_CODE_SYSTEM_NAME|FROM_CONCEPT_CODE_SYSTEM|FROM_CONCEPT_COMMENT\n"
		for (ConceptDeterminationMethod cdm : cdms) {
			List<ConceptMapping> cms = cdm.getConceptMappings()
			for (ConceptMapping cm : cms){
				List<Concept> fromConcepts = cm.getFromConcepts()
				for (Concept fromConcept: fromConcepts){
					output << "${cdm.getDisplayName()}|"
					output << "${cdm.CDMId.version}|"
					output << "${cdm.CDMId.code}|"
					output << "${cdm.CDMId.codeSystem}|"

					output << "${cm.toConcept.displayName}|"
					output << "${cm.toConcept.code}|"
					output << "${cm.toConcept.codeSystemName}|"
					output << "${cm.toConcept.codeSystem}|"
					output << "${cm.toConcept.comment}|"

					if (cm.toConcept.valueSet){
						output <<  "${cm.toConcept.valueSet.oid}|"
						output << "${cm.toConcept.valueSet.name}|"
					}
					else {
						output <<  "null|"
						output << "null|"
					}



					output << "${fromConcept.displayName}|"
					output << "${fromConcept.code}|"
					output << "${fromConcept.codeSystemName}|"
					output << "${fromConcept.codeSystem}|"
					output << "${fromConcept.comment}|\n"
				}
			}
		}
	}

	def psvToCdm = {
		boolean hasData = false
		Map cdmMap = [:]
		List metadata = []
		input.eachLine { String line ->
			if (hasData) {
				List list = line.split('\\|')

				Map data = [:]
				for (int i = 0; i < metadata.size(); i++) {
					data.put(metadata[i], list[i])
				}
				String cdmKey = "${data.CDM_CODE_SYSTEM}^${data.CDM_CODE}^${data.CDM_VERSION}"
				String toConceptKey = "${data.TO_CONCEPT_CODE_SYSTEM}^${data.TO_CONCEPT_CODE}"
				if (cdmMap.containsKey(cdmKey)) {
					Map mapping = cdmMap.get(cdmKey).conceptMappings.get(toConceptKey)
					if (mapping) {
						mapping.fromConcepts.add([
							displayName: data.FROM_CONCEPT_DISPLAY_NAME,
							code: data.FROM_CONCEPT_CODE,
							codeSystemName: data.FROM_CONCEPT_CODE_SYSTEM_NAME,
							codeSystem: data.FROM_CONCEPT_CODE_SYSTEM,
							comment: data.FROM_CONCEPT_COMMENT
						])
					} else {
						Map newMapping = [
							toConcept: [
								displayName: data.TO_CONCEPT_DISPLAY_NAME,
								code: data.TO_CONCEPT_CODE,
								codeSystemName: data.TO_CONCEPT_CODE_SYSTEM_NAME,
								codeSystem: data.TO_CONCEPT_CODE_SYSTEM,
								comment: data.TO_CONCEPT_COMMENT,
								valueSet : [
									oid: data.TO_CONCEPT_VALUE_SET_OID,
									name: data.TO_CONCEPT_VALUE_SET_NAME
								]
							],
							fromConcepts:[
								[
									displayName: data.FROM_CONCEPT_DISPLAY_NAME,
									code: data.FROM_CONCEPT_CODE,
									codeSystemName: data.FROM_CONCEPT_CODE_SYSTEM_NAME,
									codeSystem: data.FROM_CONCEPT_CODE_SYSTEM,
									comment: data.FROM_CONCEPT_COMMENT
								]
							]
						]
						if (!(cdmMap.get(cdmKey).conceptMappings instanceof Map)) {
							cdmMap.get(cdmKey).conceptMappings = [:]
						}
						cdmMap.get(cdmKey).conceptMappings.put(toConceptKey, newMapping)
					}
				} else {
					Map cdm = [
						displayName:list[0],
						cdmid: [
							version: list[1],
							code: list[2],
							codeSystem: list[3]
						],
						conceptMappings: [(toConceptKey) : [
								toConcept: [
									displayName: data.TO_CONCEPT_DISPLAY_NAME,
									code: data.TO_CONCEPT_CODE,
									codeSystemName: data.TO_CONCEPT_CODE_SYSTEM_NAME,
									codeSystem: data.TO_CONCEPT_CODE_SYSTEM,
									comment: data.TO_CONCEPT_COMMENT,
									valueSet : [
										oid: data.TO_CONCEPT_VALUE_SET_OID,
										name: data.TO_CONCEPT_VALUE_SET_NAME
									]
								],
								fromConcepts:[
									[
										displayName: data.FROM_CONCEPT_DISPLAY_NAME,
										code: data.FROM_CONCEPT_CODE,
										codeSystemName: data.FROM_CONCEPT_CODE_SYSTEM_NAME,
										codeSystem: data.FROM_CONCEPT_CODE_SYSTEM,
										comment: data.FROM_CONCEPT_COMMENT
									]
								]
							]
						],
					]
					cdmMap.put(cdmKey, cdm)
				}
			} else if (!hasData && line.startsWith('CDM_DISPLAY_NAME')) {
				hasData = true
				metadata = line.split('\\|')
			}
		}
		RestConfigUtil restConfigUtil = new RestConfigUtil()
		List<ConceptDeterminationMethod> cdms = new ArrayList<>();
		cdmMap.each {String key, Map c ->
			CDMId cdmId = CDMIdImpl.create(c.cdmid.codeSystem, c.cdmid.code, c.cdmid.version)
			List cms = []
			c.conceptMappings.each {String cmKey, Map cmp ->
				Concept toConcept = ConceptImpl.create(
						cmp.toConcept.code,
						cmp.toConcept.codeSystem,
						cmp.toConcept.codeSystemName,
						cmp.toConcept.displayName,
						cmp.toConcept.comment,
						createValueSet(cmp.toConcept.valueSet))
				List fromConcepts = []
				cmp.fromConcepts.each {Map fc ->
					fromConcepts.add(ConceptImpl.create(fc.code, fc.codeSystem, fc.codeSystemName, fc.displayName, fc.comment, null))
				}
				ConceptMapping cm = ConceptMappingImpl.create(toConcept, fromConcepts)
				cms.add(cm)
			}
			ConceptDeterminationMethod cdm = ConceptDeterminationMethodImpl.create(cdmId, c.displayName, null, new Date(), user, cms)
			cdms.add(cdm)
		}
		restConfigUtil.marshalCdms(cdms, output)
	}

	private ValueSet createValueSet(Map valueSet) {
		if (valueSet.oid) {
			return ValueSetImpl.create(valueSet.oid, valueSet.name)
		}
		null
	}
}
