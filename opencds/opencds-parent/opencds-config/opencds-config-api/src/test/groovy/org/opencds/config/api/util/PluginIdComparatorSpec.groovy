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

package org.opencds.config.api.util

import org.opencds.config.api.model.PluginId
import org.opencds.config.api.model.PrePostProcessPluginId
import org.opencds.config.api.model.impl.PluginIdImpl
import org.opencds.config.api.model.impl.PrePostProcessPluginIdImpl
import org.opencds.config.api.util.PluginIdComparator

import spock.lang.Specification

class PluginIdComparatorSpec extends Specification {

	def 'test compare equals'() {
		given:
		PrePostProcessPluginId procPluginId = PrePostProcessPluginIdImpl.create('scope', 'business', 'version', ['sd1', 'sd2'])
		PluginId pluginId = PluginIdImpl.create('scope', 'business', 'version')

		when:
		boolean result = PluginIdComparator.compare(procPluginId, pluginId)

		then:
		result
	}

	def 'test compare not equals'() {
		given:
		PrePostProcessPluginId procPluginId = PrePostProcessPluginIdImpl.create('scope1', 'business', 'version', ['sd1', 'sd2'])
		PluginId pluginId = PluginIdImpl.create('scope', 'business', 'version')

		when:
		boolean result = PluginIdComparator.compare(procPluginId, pluginId)

		then:
		!result
	}

	def 'test contains'() {
		given:
		PrePostProcessPluginId procPluginId = PrePostProcessPluginIdImpl.create('scope', 'business', 'version', ['sd1', 'sd2'])
		PluginId pluginId = PluginIdImpl.create('scope', 'business', 'version')

		when:
		def result = PluginIdComparator.match([pluginId], procPluginId)

		then:
		result
	}

	def 'test not contains'() {
		given:
		PrePostProcessPluginId procPluginId = PrePostProcessPluginIdImpl.create('scope1', 'business', 'version', ['sd1', 'sd2'])
		PluginId pluginId = PluginIdImpl.create('scope', 'business', 'version')

		when:
		def result = PluginIdComparator.match([pluginId], procPluginId)

		then:
		!result
	}

	def 'test intersect'() {
		given:
		PrePostProcessPluginId procPluginId = PrePostProcessPluginIdImpl.create('scope', 'business', 'version', ['sd1', 'sd2'])
		PluginId pluginId = PluginIdImpl.create('scope', 'business', 'version')

		when:
		List result = PluginIdComparator.intersect([procPluginId], [pluginId])

		then:
		result.size() == 1
	}

	def 'test not intersect'() {
		given:
		PrePostProcessPluginId procPluginId = PrePostProcessPluginIdImpl.create('scope1', 'business', 'version', ['sd1', 'sd2'])
		PluginId pluginId = PluginIdImpl.create('scope', 'business', 'version')

		when:
		List result = PluginIdComparator.intersect([procPluginId], [pluginId])

		then:
		result.size() == 0
	}
}
