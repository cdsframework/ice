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

package org.opencds.evaluation.service

import org.opencds.config.api.EvaluationContext
import org.opencds.config.api.KnowledgeRepository
import org.opencds.config.api.model.KMStatus
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.PluginId
import org.opencds.config.api.model.PrePostProcessPluginId
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.config.api.model.impl.KnowledgeModuleImpl
import org.opencds.config.api.model.impl.PluginIdImpl
import org.opencds.config.api.model.impl.PrePostProcessPluginIdImpl
import org.opencds.config.api.service.PluginPackageService
import org.opencds.config.api.util.EntityIdentifierUtil
import org.opencds.config.api.util.PluginIdTuple
import org.opencds.plugin.PreProcessPlugin
import org.opencds.plugin.SupportingData
import org.opencds.plugin.SupportingDataPackageSupplier
import spock.lang.Specification

class PluginProcessorSpec extends Specification {

	def 'test filterSupportingData, two match from km'() {
		given:
		PluginId left = PluginIdImpl.create('scopingEID', 'businessID', 'version')
		PrePostProcessPluginId right = PrePostProcessPluginIdImpl.create('scopingEID','businessID', 'version', ['sd1', 'sd5'])
		Map<String, SupportingData> supportingData = [
			'sd1' : SupportingData.create('sd1','kmid',null,'package1','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd2' : SupportingData.create('sd2','kmid',null,'package2','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd3' : SupportingData.create('sd3','kmid',null,'package3','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd4' : SupportingData.create('sd4','kmid',null,'package4','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd5' : SupportingData.create('sd5','kmid',null,'package5','type',new Date(), [:] as SupportingDataPackageSupplier)
		]
		PluginIdTuple tuple = PluginIdTuple.create(left, right)

		when:
		Map<String, SupportingData> result = PluginProcessor.filterSupportingData(tuple, supportingData)

		then:
		result.size() == 2
		result.sd1 == supportingData.sd1
		result.sd5 == supportingData.sd5
	}

	def 'test filterSupportingData, no match from km'() {
		given:
		PluginId left = PluginIdImpl.create('scopingEID', 'businessID', 'version')
		PrePostProcessPluginId right = PrePostProcessPluginIdImpl.create('scopingEID','businessID', 'version', ['sd7', 'sd6'])
		Map<String, SupportingData> supportingData = [
			'sd1' : SupportingData.create('sd1','kmid',null,'package1','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd2' : SupportingData.create('sd2','kmid',null,'package2','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd3' : SupportingData.create('sd3','kmid',null,'package3','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd4' : SupportingData.create('sd4','kmid',null,'package4','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd5' : SupportingData.create('sd5','kmid',null,'package5','type',new Date(), [:] as SupportingDataPackageSupplier)
		]
		PluginIdTuple tuple = PluginIdTuple.create(left, right)

		when:
		Map<String, SupportingData> result = PluginProcessor.filterSupportingData(tuple, supportingData)

		then:
		result.isEmpty()
	}
	
	def 'test filterSupportingData, two match loadedBy'() {
		given:
		PluginId left = PluginIdImpl.create('scopingEID', 'businessID', 'version')
		PrePostProcessPluginId right = PrePostProcessPluginIdImpl.create('scopingEID','businessID', 'version', [])
		Map<String, SupportingData> supportingData = [
			'sd1' : SupportingData.create('sd1','kmid',EntityIdentifierUtil.makeEIString(left),'package1','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd2' : SupportingData.create('sd2','kmid',null,'package2','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd3' : SupportingData.create('sd3','kmid',null,'package3','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd4' : SupportingData.create('sd4','kmid',null,'package4','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd5' : SupportingData.create('sd5','kmid',EntityIdentifierUtil.makeEIString(left),'package5','type',new Date(), [:] as SupportingDataPackageSupplier)
		]
		PluginIdTuple tuple = PluginIdTuple.create(left, right)

		when:
		Map<String, SupportingData> result = PluginProcessor.filterSupportingData(tuple, supportingData)

		then:
		result.size() == 2
		result.sd1 == supportingData.sd1
		result.sd5 == supportingData.sd5
	}

	def 'test filterSupportingData, no match loadedBy'() {
		given:
		PluginId left = PluginIdImpl.create('scopingEID', 'businessID', 'version')
		PrePostProcessPluginId right = PrePostProcessPluginIdImpl.create('scopingEID','businessID', 'version', [])
		Map<String, SupportingData> supportingData = [
			'sd1' : SupportingData.create('sd1','kmid',EntityIdentifierUtil.makeEIString(PluginIdImpl.create('scopingEID2', 'businessID2', 'version2')),'package1','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd2' : SupportingData.create('sd2','kmid',null,'package2','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd3' : SupportingData.create('sd3','kmid',null,'package3','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd4' : SupportingData.create('sd4','kmid',null,'package4','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd5' : SupportingData.create('sd5','kmid',EntityIdentifierUtil.makeEIString(PluginIdImpl.create('scopingEID2', 'businessID2', 'version2')),'package5','type',new Date(), [:] as SupportingDataPackageSupplier)
		]
		PluginIdTuple tuple = PluginIdTuple.create(left, right)

		when:
		Map<String, SupportingData> result = PluginProcessor.filterSupportingData(tuple, supportingData)

		then:
		result.isEmpty()
	}

	def 'test filterSupportingData, one match from km, one with loadedBy which is ignored (priority with KM config)'() {
		given:
		PluginId left = PluginIdImpl.create('scopingEID', 'businessID', 'version')
		PrePostProcessPluginId right = PrePostProcessPluginIdImpl.create('scopingEID','businessID', 'version', ['sd1'])
		Map<String, SupportingData> supportingData = [
			'sd1' : SupportingData.create('sd1','kmid',null,'package1','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd2' : SupportingData.create('sd2','kmid',null,'package2','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd3' : SupportingData.create('sd3','kmid',null,'package3','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd4' : SupportingData.create('sd4','kmid',null,'package4','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd5' : SupportingData.create('sd5','kmid',EntityIdentifierUtil.makeEIString(left),'package5','type',new Date(), [:] as SupportingDataPackageSupplier)
		]
		PluginIdTuple tuple = PluginIdTuple.create(left, right)

		when:
		Map<String, SupportingData> result = PluginProcessor.filterSupportingData(tuple, supportingData)

		then:
		result.size() == 1
		result.sd1 == supportingData.sd1
	}

	def 'test preProcess, two match from km'() {
		given:
		PluginId left = PluginIdImpl.create('scopingEID', 'businessID', 'version')
		PrePostProcessPluginId right = PrePostProcessPluginIdImpl.create('scopingEID','businessID', 'version', ['sd1', 'sd5'])
		Map<String, SupportingData> supportingData = [
			'sd1' : SupportingData.create('sd1','kmid',null,'package1','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd2' : SupportingData.create('sd2','kmid',null,'package2','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd3' : SupportingData.create('sd3','kmid',null,'package3','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd4' : SupportingData.create('sd4','kmid',null,'package4','type',new Date(), [:] as SupportingDataPackageSupplier),
			'sd5' : SupportingData.create('sd5','kmid',null,'package5','type',new Date(), [:] as SupportingDataPackageSupplier)
		]
		PluginIdTuple tuple = PluginIdTuple.create(left, right)
		KnowledgeModule km = KnowledgeModuleImpl.create(
			KMIdImpl.create('1', '2', '3'),
			KMStatus.APPROVED, 
			null, //cdsHook,
			null, //executionEngine,
			null, //ssId,
			null, //primaryCDM,
			null, //secondaryCDMs,
			'packageType', //packageType,
			'packageId', //packageId,
			true, //preload,
			null, //primaryProcess,
			null, //traitIds,
			[right], //preProcPlugins, 
			null, //postProcPlugins,
			new Date(), //timestamp,
			'userid' //userId
			)
		Set assertions = []
		Map namedObjects = [:]
		Map globals = [:]
		Map factlists = [:]
		String primaryproc = ''
		
		and:
		KnowledgeRepository kr = Mock(KnowledgeRepository)
		PluginPackageService pps = Mock(PluginPackageService)
		2 * kr.getPluginPackageService() >> pps
		1 * pps.getAllPluginIds() >> [left]
		EvaluationContext context = new EvaluationContext('1', new Date(), null, 'lang','tzos',assertions, namedObjects, globals, factlists, primaryproc)
		1 * pps.load(left) >> {[execute: {}] as PreProcessPlugin}

		when:
		PluginProcessor.preProcess(kr, km, supportingData, context)

		then:
		true
	}

}
