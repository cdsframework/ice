/*
 * Copyright 2017-2020 OpenCDS.org
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

package org.opencds.plugin.support

import org.junit.After
import org.opencds.plugin.SupportingData
import org.opencds.plugin.SupportingDataPackage
import org.opencds.plugin.SupportingDataPackageSupplier
import org.opencds.plugin.support.PluginDataCacheImpl.PluginDataCacheRegion

import spock.lang.Specification

class PluginDataCacheImplSpec extends Specification {

	def "test caching of a given key"() {
		given:
		PluginDataCacheImpl cache = new PluginDataCacheImpl()
		SupportingData sd

		when:
		sd = create()
		println sd
		cache.put(sd, new Object())
		sd = create()
		println sd
		cache.put(sd, new Object())
		sd = create()
		println sd
		cache.put(sd, new Object())

		then:
		cache.cache.cache.get(PluginDataCacheRegion.PLUGIN_DATA).keySet().size() == 1
	}

	def "test caching two different keys that appear to be the same"() {
		given:
		PluginDataCacheImpl cache = new PluginDataCacheImpl()
		SupportingData sd1 = create()
		println " >>> $sd1"
		
		and:
		Object obj = new Object()
		cache.put(sd1, obj)
		sleep 1000
		
		when:
		int first = cache.cache.getCacheKeys(PluginDataCacheRegion.PLUGIN_DATA).size()
		SupportingData sd2 = create()
		println " >>> $sd2"
		Object obj2 = cache.get(sd2)
		int second = cache.cache.getCacheKeys(PluginDataCacheRegion.PLUGIN_DATA).size()
		
		then:"the cache should have been cleared"
		first == 1
		second == 0
		
		and:"get now have an empty cache"
		obj2 == null
		obj != obj2
		
	}

	def "test caching two different keys that are created at the same instant"() {
		given:
		PluginDataCacheImpl cache = new PluginDataCacheImpl()
		SupportingData sd1 = create()
		println " >>> $sd1"
		
		and:
		Object obj = new Object()
		cache.put(sd1, obj)
		
		when:
		int first = cache.cache.getCacheKeys(PluginDataCacheRegion.PLUGIN_DATA).size()
		SupportingData sd2 = create(sd1.getTimestamp())
		println " >>> $sd2"
		Object obj2 = cache.get(sd2)
		int second = cache.cache.getCacheKeys(PluginDataCacheRegion.PLUGIN_DATA).size()
		
		then:"first == second == 1"
		first == second
		second == 1
		
		and:"our timestamps are the same, so no cache removal"
		println "${sd1.timestamp.toInstant().toEpochMilli()} == ${sd2.timestamp.toInstant().toEpochMilli()}"
		sd1.timestamp.toInstant().toEpochMilli() == sd2.timestamp.toInstant().toEpochMilli()
		
		and:"our cache has a single object, and what we put in is the same one we get out"
		obj2 != null
		obj == obj2
		
	}

		
	private SupportingData create() {
		return create(new Date())
	}
	private SupportingData create(Date date) {
		return SupportingData.create(
				'zipcode lookup',
				'',
				'org.opencds.plugin^WekaSepsisPreProcessor^0.1',
				'zipcode_lookup.csv',
				'csv',
				date,
				[get :
					[getFile : {
					new File(UUID.randomUUID().toString())
				}, getBytes : {
					UUID.randomUUID().toString().bytes
				}] as SupportingDataPackage
					] as SupportingDataPackageSupplier)
	}
}
