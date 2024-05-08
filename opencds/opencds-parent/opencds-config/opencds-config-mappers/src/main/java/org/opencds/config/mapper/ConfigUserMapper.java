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

package org.opencds.config.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencds.config.schema.ConfigAuthority;
import org.opencds.config.schema.ConfigSecurity.Users;
import org.opencds.config.schema.ConfigUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class ConfigUserMapper {
    public static UserDetails internal(ConfigUser configUser, PasswordEncoder passwordEncoder) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (ConfigAuthority authority : configUser.getGrantedAuthorities().getAuthority()) {
            if (authority != null) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.name()));
            }
        }
        // negation is weird...
        UserBuilder builder = User.withUsername(configUser.getUsername());
        builder.password(passwordEncoder.encode(configUser.getPassword()));
//        builder.passwordEncoder(password -> passwordEncoder.encode(password));
        builder.authorities(grantedAuthorities);
        return builder.build();
    }

    public static Set<UserDetails> internal(Users users, PasswordEncoder passwordEncoder) {
        if (users == null) {
            return null;
        }
        List<ConfigUser> configUsers = users.getUser();
        Set<UserDetails> userDetails = new HashSet<>();
        if (configUsers != null) {
            for (ConfigUser configUser : configUsers) {
                userDetails.add(internal(configUser, passwordEncoder));
            }
        }
        return userDetails;
    }
}
