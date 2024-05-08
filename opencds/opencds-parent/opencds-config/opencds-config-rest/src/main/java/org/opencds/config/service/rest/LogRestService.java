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
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

public class LogRestService {
    private static final String ROOT = "root";

    @GET
    @Path("/log/{logger}/{level}")
    public Response rootLogLevel(@PathParam("logger") String loggerId, @PathParam("level") String requestedLevel) {
        Logger logger;
        String loggerName;
        if (loggerId.equalsIgnoreCase(ROOT)) {
            loggerName = "";
        } else {
            loggerName = loggerId;
        }
        Level oldLevel = LogManager.getLogger(loggerName).getLevel();
        Level newLevel = Level.toLevel(requestedLevel);
        Configurator.setLevel(loggerId, newLevel);
        String oldLevelString = oldLevel != null ? oldLevel.toString() : "none";
        return Response.ok().entity("Log level for logger '" + loggerId + "' changed from '" + oldLevelString + "' to '" + newLevel.toString() + "'; " + new Date() + "\r\n").build();
    }

    @GET
    @Path("log/loggers")
    public Response getLoggers() {
        Collection<Logger> loggers = LoggerContext.getContext().getLoggers();
        SortedMap<String, String> allLoggers = new TreeMap<>();
        StringBuilder response = new StringBuilder("Enabled Loggers:\n");
        loggers.stream()
                .sorted(Comparator.comparing(Logger::getName))
                .forEach(logger -> {
                    if (logger.getLevel() != null) {
                        response.append(logger.getName()).append("=").append(logger.getLevel().toString()).append("\r\n");
                    }
                });
        return Response.ok().entity(response.toString()).build();
    }

}
