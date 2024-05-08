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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.xml.JAXBContextService;
import org.opencds.config.mapper.ConfigUserMapper;
import org.opencds.config.schema.ConfigSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Log log = LogFactory.getLog(UserDetailsServiceImpl.class);
    private static final String CONFIG_SCHEMA_PATH = "org.opencds.config.schema";

    private Set<UserDetails> users = new HashSet<>();

    public UserDetailsServiceImpl(JAXBContextService jaxbContextService, String location, PasswordEncoder passwordEncoder) {
        try {
            JAXBContext jxbc = jaxbContextService.getJAXBContext(CONFIG_SCHEMA_PATH);
            Unmarshaller marshaller = jxbc.createUnmarshaller();
            File configLocation = new File(location);
            if (!configLocation.exists()) {
                log.warn("Configuration Security metadata not found; configuration REST service will be inaccessible.  Location provided: "
                        + location);
            }
            JAXBElement<ConfigSecurity> configSecMetadata = (JAXBElement<ConfigSecurity>) marshaller
                    .unmarshal(configLocation);
            users = ConfigUserMapper.internal(configSecMetadata.getValue().getUsers(), passwordEncoder);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = null;
        if (users != null) {
            for (UserDetails u : users) {
                if (u.getUsername().equals(username)) {
                    log.info("Operation is being requested by user : " + username);
                    user = u;
                }
            }
        }
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

}
