package org.opencds.config.service.rest;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opencds.config.api.ConfigurationService;

public class ConfigurationReloadRestService {
    private static final String CONFIG_RELOADED = "Configuration Reloaded";
    private static final String CONFIG_NOT_RELOADED = "Configuration was not reloaded (not supported for the given config strategy).";;
    
    private final ConfigurationService configurationService;
    
    public ConfigurationReloadRestService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    @GET
    @Path("/reload")
    public Response reloadConfiguration() {
        boolean reloaded = configurationService.reloadConfiguration();
        if (!reloaded) {
            return Response.status(Status.FORBIDDEN).entity(CONFIG_NOT_RELOADED).build();
        }
        return Response.ok().entity(CONFIG_RELOADED + "; Timestamp: " + new Date()).build();
    }
    
}
