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

package org.opencds.hooks.model.dstu2.response

import org.hl7.fhir.dstu2.model.Parameters
import org.hl7.fhir.dstu2.model.StringType
import org.opencds.hooks.model.dstu2.util.Dstu2JsonUtil
import org.opencds.hooks.model.response.Card
import org.opencds.hooks.model.response.CdsResponse
import org.opencds.hooks.model.response.Indicator

import spock.lang.Specification

class CdsResponseSpec extends Specification {
    Dstu2JsonUtil jsonUtil = new Dstu2JsonUtil()
    
    def 'serialize a cds response with an extension'() {
        given:
        CdsResponse response = new CdsResponse()
        Card card = new Card()
        card.detail = 'my detail'
        card.indicator = Indicator.WARNING
        card.summary = 'my summary'
        
        and:
        MyExtension ext = new MyExtension()
        ext.item1 = new Parameters()
        ext.item1.addParameter(new Parameters.ParametersParameterComponent().setName('job').setValue(new StringType('12345')))
        ext.item2 = new Parameters()
        ext.item2.addParameter(new Parameters.ParametersParameterComponent().setName('med').setValue(new StringType('SOMETHING')))
        Parameters gl = new Parameters()
        gl.addParameter(new Parameters.ParametersParameterComponent().setName('GuideLine'))
        gl.addParameter(new Parameters.ParametersParameterComponent().setName('Level').setValue(new StringType('ONE')))
        ext.item2.addParameter(new Parameters.ParametersParameterComponent().setResource(gl))
        card.setExtension(ext)
        
        and:
        response.addCard(card)
        
        when:
        String json = jsonUtil.toJson(response)
        
        then:
        json
        println json
    }
    
    def 'test deserialization or cdsresponse with extension'() {
        given:
        String json = new File('src/test/resources/cds-response-with-extension.json').text
        
        when:
        CdsResponse response = jsonUtil.fromJson(json, CdsResponse)
        
        then:
        response
        println response
        println jsonUtil.toJson(response)
    }
    
    private static class MyExtension {
        private Parameters item1
        private Parameters item2
    }
    
}
