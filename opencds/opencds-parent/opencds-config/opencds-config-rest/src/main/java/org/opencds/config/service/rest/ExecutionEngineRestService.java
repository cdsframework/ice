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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBElement;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.mapper.ExecutionEngineMapper;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.Link;
import org.opencds.config.schema.ObjectFactory;
import org.opencds.config.service.rest.util.Responses;

import java.util.List;

@Path("executionengines")
public class ExecutionEngineRestService {
    private static final String SELF = "self";
    private ConfigurationService configurationService;

    public ExecutionEngineRestService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // /executionengines

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    public ExecutionEngines getExecutionEngines(@Context UriInfo uriInfo) throws Exception {
        ExecutionEngines ees = ExecutionEngineMapper.external(configurationService.getKnowledgeRepository()
                .getExecutionEngineService().getAll());
        for (org.opencds.config.schema.ExecutionEngine ee : ees.getExecutionEngine()) {
            Link link = new Link();
            link.setRel(SELF);
            link.setHref(uriInfo.getAbsolutePathBuilder().path(ee.getIdentifier()).build().toString());
            ee.setLink(link);
        }
        return ees;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response createExecutionEngine(@Context UriInfo uriInfo,
            JAXBElement<org.opencds.config.schema.ExecutionEngine> jaxEE) {
        org.opencds.config.schema.ExecutionEngine executionEngine = jaxEE.getValue();
        ExecutionEngine internalEE = ExecutionEngineMapper.internal(executionEngine);
        if (found(internalEE.getIdentifier())) {
            return Responses.conflict("ExecutionEngine already exists: id= " + internalEE.getIdentifier());
        }
        try {
            save(ExecutionEngineMapper.internal(executionEngine));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.created(uriInfo, internalEE.getIdentifier());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putExecutionEngines(@Context UriInfo uriInfo,
            org.opencds.config.schema.ExecutionEngines executionEngines) {
        try {
            save(ExecutionEngineMapper.internal(executionEngines));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    // /executionengines/<ExecutionEngineId>

    @GET
    @Path("{executionengineid}")
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<org.opencds.config.schema.ExecutionEngine> getExecutionEngine(@Context UriInfo uriInfo,
            @PathParam("executionengineid") String identifier) {
        ExecutionEngine ee = find(identifier);
        if (ee == null) {
            throw new NotFoundException("Execution Engine not found: id= " + identifier);
        }
        org.opencds.config.schema.ExecutionEngine externalEE = ExecutionEngineMapper.external(ee);
        Link link = new Link();
        link.setRel(SELF);
        link.setHref(uriInfo.getAbsolutePath().toString());
        externalEE.setLink(link);
        return new ObjectFactory().createExecutionEngine(externalEE);
    }

    @PUT
    @Path("{executionengineid}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateExecutionEngine(@Context UriInfo uriInfo, @PathParam("executionengineid") String identifier,
            JAXBElement<org.opencds.config.schema.ExecutionEngine> jaxEE) {
        org.opencds.config.schema.ExecutionEngine executionEngine = jaxEE.getValue();
        ExecutionEngine ee = ExecutionEngineMapper.internal(executionEngine);
        if (!identifier.equals(ee.getIdentifier())) {
            return Responses.badRequest("Identifier of request and document do not match");
        }
        boolean created = false;
        if (!found(ee.getIdentifier())) {
            created = true;
        }
        try {
            save(ee);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo, ee.getIdentifier());
        } else {
            return Responses.noContent();
        }
    }

    @DELETE
    @Path("{executionengineid}")
    public Response deleteExecutionEngine(@PathParam("executionengineid") String identifier) {
        if (!found(identifier)) {
            return Responses.notFound("ExecutionEngine not found: id= " + identifier);
        }
        try {
            configurationService.getKnowledgeRepository().getExecutionEngineService().delete(identifier);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    private ExecutionEngine find(String identifier) {
        return configurationService.getKnowledgeRepository().getExecutionEngineService().find(identifier);
    }

    private boolean found(String identifier) {
        return find(identifier) != null;
    }

    private void save(ExecutionEngine ee) {
        configurationService.getKnowledgeRepository().getExecutionEngineService().persist(ee);
    }

    private void save(List<ExecutionEngine> internal) {
        configurationService.getKnowledgeRepository().getExecutionEngineService().persist(internal);
    }

}
