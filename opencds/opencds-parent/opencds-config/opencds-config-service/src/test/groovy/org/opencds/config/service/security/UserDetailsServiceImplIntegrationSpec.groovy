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

package org.opencds.config.service.security;

import org.opencds.config.api.xml.JAXBContextService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

import spock.lang.Specification

class UserDetailsServiceImplIntegrationSpec extends Specification {

    UserDetailsServiceImpl service

    def "build a test configuration and find a user"() {
        given:
        def location = "src/test/resources/config-security.xml"
        def jaxbcs = JAXBContextService.get()
        def username = "username"
        PasswordEncoder encoder = new BCryptPasswordEncoder(15)

        when:
        service = new UserDetailsServiceImpl(jaxbcs, location, encoder)
        
        and:
        def result = service.loadUserByUsername(username)

        then:
        result
        result.username == 'username'
        encoder.matches('password', result.password)
        result.enabled
        result.authorities.authority == ['ROLE_CONFIG_USER']
        
        and:"default spring-required values are appropriately valued"
        result.accountNonExpired
        result.accountNonLocked
        result.credentialsNonExpired
    }
}
