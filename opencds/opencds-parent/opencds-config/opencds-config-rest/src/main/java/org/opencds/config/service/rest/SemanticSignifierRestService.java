package org.opencds.config.service.rest;

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
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.mapper.SemanticSignifierMapper;
import org.opencds.config.schema.Link;
import org.opencds.config.schema.ObjectFactory;
import org.opencds.config.schema.SemanticSignifiers;
import org.opencds.config.service.rest.util.Responses;
import org.opencds.config.util.URIUtil;

@Path("semanticsignifiers")
public class SemanticSignifierRestService {
    private static final String SELF = "self";
    private ConfigurationService configurationService;

    public SemanticSignifierRestService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // /semanticsignifiers

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public SemanticSignifiers getSemanticSignifiers(@Context UriInfo uriInfo) {
        SemanticSignifiers sses = SemanticSignifierMapper.external(configurationService.getKnowledgeRepository()
                .getSemanticSignifierService().getAll());
        for (org.opencds.config.schema.SemanticSignifier ss : sses.getSemanticSignifier()) {
            Link link = new Link();
            link.setRel(SELF);
            link.setHref(uriInfo.getAbsolutePathBuilder().path(URIUtil.buildSSIdString(ss.getIdentifier())).build()
                    .toString());
            ss.setLink(link);
        }
        return sses;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response createSemanticSignifier(@Context UriInfo uriInfo,
            JAXBElement<org.opencds.config.schema.SemanticSignifier> jaxSS) {
        org.opencds.config.schema.SemanticSignifier semanticSignifier = jaxSS.getValue();
        SemanticSignifier ss = SemanticSignifierMapper.internal(semanticSignifier);
        if (found(ss.getSSId())) {
            return Responses.conflict("SemanticSignifier already exists: id= "
                    + URIUtil.buildSSIdString(semanticSignifier.getIdentifier()));
        }
        try {
            configurationService.getKnowledgeRepository().getSemanticSignifierService().persist(ss);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.created(uriInfo, URIUtil.buildSSIdString(semanticSignifier.getIdentifier()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putSemanticSignifiers(@Context UriInfo uriInfo,
            org.opencds.config.schema.SemanticSignifiers semanticSignifiers) {
        try {
            configurationService.getKnowledgeRepository().getSemanticSignifierService()
                    .persist(SemanticSignifierMapper.internal(semanticSignifiers));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    // /semanticsignifiers/<SSId>

    @GET
    @Path("/{ssid}")
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<org.opencds.config.schema.SemanticSignifier> getSemanticSignifier(@Context UriInfo uriInfo,
            @PathParam("ssid") String ssidString) {
        SSId ssId = URIUtil.getSSId(ssidString);
        SemanticSignifier ss = find(ssId);
        if (ss == null) {
            throw new NotFoundException("Semantic Signifier not found: ssid= " + ssidString);
        }
        org.opencds.config.schema.SemanticSignifier externalSS = SemanticSignifierMapper.external(ss);
        Link link = new Link();
        link.setRel(SELF);
        link.setHref(uriInfo.getAbsolutePath().toString());
        externalSS.setLink(link);
        return new ObjectFactory().createSemanticSignifier(externalSS);
    }

    @PUT
    @Path("{ssid}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateSemanticSignifier(@Context UriInfo uriInfo, @PathParam("ssid") String ssidString,
            JAXBElement<org.opencds.config.schema.SemanticSignifier> jaxSS) {
        SSId ssId = URIUtil.getSSId(ssidString);
        boolean created = !found(ssId);
        org.opencds.config.schema.SemanticSignifier semanticSignifier = jaxSS.getValue();
        SemanticSignifier ss = SemanticSignifierMapper.internal(semanticSignifier);
        // TODO: Push deeper?
        if (!ssId.equals(ss.getSSId())) {
            return Responses.badRequest("SSId of request and document do not match");
        }
        try {
            configurationService.getKnowledgeRepository().getSemanticSignifierService().persist(ss);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo, URIUtil.buildSSIdString(semanticSignifier.getIdentifier()));
        } else {
            return Responses.ok();
        }
    }

    @DELETE
    @Path("{ssid}")
    public Response deleteSemanticSignfiier(@PathParam("ssid") String ssidString) {
        SSId ssId = URIUtil.getSSId(ssidString);
        if (!found(ssId)) {
            return Responses.notFound("SemanticSignifier not found: id= " + ssidString);
        }
        try {
            configurationService.getKnowledgeRepository().getSemanticSignifierService().delete(ssId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        // what if delete fails?
        return Responses.noContent();
    }

    private boolean found(SSId ssId) {
        return find(ssId) != null;
    }

    private SemanticSignifier find(SSId ssId) {
        return configurationService.getKnowledgeRepository().getSemanticSignifierService().find(ssId);
    }

}
