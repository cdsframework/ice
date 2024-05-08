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

package org.opencds.hooks.model.util

import java.time.LocalDateTime
import java.time.ZoneId

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class DateUtilSpec extends Specification {

	@Unroll
    @Ignore
	def 'test getting a Date'() {
		expect:
		DateUtil.iso8601StringToDate(dateString) == date

		where:
		dateString | date
		'2018-04-12T23:19:30Z' | Date.from(LocalDateTime.of(2018,4,12,23,19,30).atZone(ZoneId.of('UTC')).toInstant())
		'2018-04-12T23:19:30-05:00' | Date.from(LocalDateTime.of(2018,4,13,4,19,30).atZone(ZoneId.of('UTC')).toInstant())
		'2018-04-12T23:19:30' | Date.from(LocalDateTime.of(2018,4,13,5,19,30).atZone(ZoneId.of('UTC')).toInstant())
	}

}
