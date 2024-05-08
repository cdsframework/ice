/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.vmr.v1_0.mappings.utilities


import spock.lang.Specification
import spock.lang.Unroll

class TSDateFormatSpec extends Specification {

    @Unroll
    def "test values without an offset"() {
        expect:
        TSDateFormat.forInput(dateString) == expectedFormat
        
        where:
        dateString          | expectedFormat
        "2021"              | "yyyy"
        "20219"             | "yyyyM"
        "20219"             | "yyyyM"
        "2021091"           | "yyyyMMd"
        "20210912"          | "yyyyMMdd"
        "202109122"         | "yyyyMMddH"
        "2021091220"        | "yyyyMMddHH"
        "20210912205"       | "yyyyMMddHHm"
        "202109122056"      | "yyyyMMddHHmm"
        "2021091220569"     | "yyyyMMddHHmms"
        "20210912205635"    | "yyyyMMddHHmmss"
        "20210912205635.1"  | "yyyyMMddHHmmss.S"
        "20210912205635.12" | "yyyyMMddHHmmss.SS"
        "20210912205635.123"| "yyyyMMddHHmmss.SSS"
    }

    @Unroll
    def "test values with an offset"() {
        expect:
        TSDateFormat.forInput(dateString) == expectedFormat

        where:
        dateString               | expectedFormat
        "2021+0000"              | "yyyyZ"
        "20219+0000"             | "yyyyMZ"
        "202109+0000"            | "yyyyMMZ"
        "2021091+0000"           | "yyyyMMdZ"
        "20210912+0000"          | "yyyyMMddZ"
        "202109122+0000"         | "yyyyMMddHZ"
        "2021091220+0000"        | "yyyyMMddHHZ"
        "20210912205+0000"       | "yyyyMMddHHmZ"
        "202109122056+0000"      | "yyyyMMddHHmmZ"
        "2021091220569+0000"     | "yyyyMMddHHmmsZ"
        "20210912205635+0000"    | "yyyyMMddHHmmssZ"
        "20210912205635.1+0000"  | "yyyyMMddHHmmss.SZ"
        "20210912205635.12+0000" | "yyyyMMddHHmmss.SSZ"
        "20210912205635.123+0000"| "yyyyMMddHHmmss.SSSZ"
    }

    def "test unrecognizable format"() {
        given:
        def input = "202109122056351"

        when:
        def result = TSDateFormat.forInput(input)

        then:
        thrown(IllegalArgumentException)
    }
    
}
