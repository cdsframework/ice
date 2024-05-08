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


import org.opencds.hooks.model.dstu2.request.prefetch.Dstu2PrefetchHelper
import org.opencds.hooks.model.request.CdsRequest
import org.opencds.hooks.model.request.WritableCdsRequest
import spock.lang.Specification

class Dstu2PrefetchHelperSpec extends Specification {
    
    Dstu2PrefetchHelper dstu2Prefetch = new Dstu2PrefetchHelper();

    def 'test prefetch helper'() {
        given:
        CdsRequest r = new WritableCdsRequest()
        String key = 'patient'

        when:        
        def result = r.getPrefetchResource(key, dstu2Prefetch);
        
        then:
        result
    }    

}
