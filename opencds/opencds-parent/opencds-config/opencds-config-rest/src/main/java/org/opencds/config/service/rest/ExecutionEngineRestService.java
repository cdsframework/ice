package org.opencds.config.service.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.mapper.ExecutionEngineMapper;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.Link;
import org.opencds.config.schema.ObjectFactory;
import org.opencds.config.service.rest.util.Responses;

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
