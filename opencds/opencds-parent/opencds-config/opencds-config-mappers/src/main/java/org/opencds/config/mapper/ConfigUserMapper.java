package org.opencds.config.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencds.config.api.model.UserDetailsImpl;
import org.opencds.config.schema.ConfigAuthority;
import org.opencds.config.schema.ConfigUser;
import org.opencds.config.schema.ConfigSecurity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class ConfigUserMapper {
    public static UserDetails internal(ConfigUser configUser) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (ConfigAuthority authority : configUser.getGrantedAuthorities().getAuthority()) {
            if (authority != null) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.name()));
            }
        }
        // negation is weird...
        UserDetails user = new UserDetailsImpl(configUser.getUsername(), configUser.getPassword(),
                configUser.isEnabled(), true, true, true, grantedAuthorities);
        return user;
    }

    public static Set<UserDetails> internal(Users users) {
        if (users == null) {
            return null;
        }
        List<ConfigUser> configUsers = users.getUser();
        Set<UserDetails> userDetails = new HashSet<>();
        if (configUsers != null) {
            for (ConfigUser configUser : configUsers) {
                userDetails.add(internal(configUser));
            }
        }
        return userDetails;
    }
}
