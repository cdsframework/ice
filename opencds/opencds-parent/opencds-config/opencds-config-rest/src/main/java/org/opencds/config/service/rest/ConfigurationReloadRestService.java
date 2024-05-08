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

package org.opencds.config.service.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.opencds.config.api.ConfigurationService;

import java.util.Date;

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
