package org.opencds.config.service.security;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.config.api.service.JAXBContextService;
import org.opencds.config.mapper.ConfigUserMapper;
import org.opencds.config.schema.ConfigSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
	private static final Logger log = LogManager.getLogger();
    private static final String CONFIG_SCHEMA_PATH = "org.opencds.config.schema";

    private Set<UserDetails> users = new HashSet<>();

    public UserDetailsServiceImpl(JAXBContextService jaxbContextService, String location) {
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
            users = ConfigUserMapper.internal(configSecMetadata.getValue().getUsers());
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
