package org.opencds.config.service.rest;

import java.io.InputStream;

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
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.mapper.PluginPackageMapper;
import org.opencds.config.schema.Link;
import org.opencds.config.schema.ObjectFactory;
import org.opencds.config.schema.PluginPackages;
import org.opencds.config.service.rest.util.Responses;
import org.opencds.config.util.URIUtil;

@Path("pluginpackages")
public class PluginPackageRestService {
    private static final String SELF = "self";
    private ConfigurationService configurationService;

    public PluginPackageRestService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // /pluginPackages

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public PluginPackages getPluginPackages(@Context UriInfo uriInfo) {
        PluginPackages pps = PluginPackageMapper.external(configurationService.getKnowledgeRepository()
                .getPluginPackageService().getAll());
        for (org.opencds.config.schema.PluginPackage pp : pps.getPluginPackage()) {
            Link link = new Link();
            link.setRel(SELF);
            link.setHref(uriInfo.getAbsolutePathBuilder().path(URIUtil.buildPPIdString(pp.getIdentifier())).build()
                    .toString());
            pp.setLink(link);
        }
        return pps;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response createPluginPackage(@Context UriInfo uriInfo, JAXBElement<org.opencds.config.schema.PluginPackage> jaxPP) {
        org.opencds.config.schema.PluginPackage pluginPackage = jaxPP.getValue();
        PluginPackage pp = PluginPackageMapper.internal(pluginPackage);
        if (found(pp.getIdentifier())) {
            return Responses.conflict("PluginPackage already exists: identifier= "
                    + URIUtil.buildPPIdString(pluginPackage.getIdentifier()));
        }
        try {
            configurationService.getKnowledgeRepository().getPluginPackageService().persist(pp);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.created(uriInfo, URIUtil.buildPPIdString(pluginPackage.getIdentifier()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putPluginPacakges(@Context UriInfo uriInfo, PluginPackages pluginPackages) {
        try {
            configurationService.getKnowledgeRepository().getPluginPackageService()
                    .persist(PluginPackageMapper.internal(pluginPackages));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    // /pluginPackages/<ppid>
    @GET
    @Path("{ppid}")
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<org.opencds.config.schema.PluginPackage> getPluginPackage(@Context UriInfo uriInfo,
            @PathParam("ppid") String ppidString) {
        PluginPackage internal = find(URIUtil.getPPId(ppidString));
        if (internal == null) {
            throw new NotFoundException("PluginPackage not found: identifier= " + ppidString);
        }
        org.opencds.config.schema.PluginPackage pluginPackage = PluginPackageMapper.external(internal);
        Link link = new Link();
        link.setRel(SELF);
        link.setHref(uriInfo.getAbsolutePath().toString());
        pluginPackage.setLink(link);
        return new ObjectFactory().createPluginPackage(pluginPackage);
    }

    @PUT
    @Path("{ppid}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updatePluginPackage(@Context UriInfo uriInfo, @PathParam("ppid") String ppidString,
            JAXBElement<org.opencds.config.schema.PluginPackage> jaxPP) {
        PPId ppId = URIUtil.getPPId(ppidString);
        boolean created = false;
        if (!found(ppId)) {
            created = true;
        }
        org.opencds.config.schema.PluginPackage pluginPackage = jaxPP.getValue();
        PluginPackage internal = PluginPackageMapper.internal(pluginPackage);

        if (!ppId.equals(internal.getIdentifier())) {
            return Responses.badRequest("PPId of request and document do not match.");
        }
        try {
            configurationService.getKnowledgeRepository().getPluginPackageService().persist(internal);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo, URIUtil.buildPPIdString(pluginPackage.getIdentifier()));
        } else {
            return Responses.ok();
        }
    }

    @DELETE
    @Path("{ppid}")
    public Response deletePluginPackage(@PathParam("ppid") String ppidString) {
        PPId ppId = URIUtil.getPPId(ppidString);
        if (!found(ppId)) {
            return Responses.notFound("PluginPackage not found: identifier= " + ppidString);
        }
        try {
            configurationService.getKnowledgeRepository().getPluginPackageService().delete(ppId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    // /pluginPackages/<PPId>/jar

    @GET
    @Path("{ppid}/jar")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getPluginPackageJar(@PathParam("ppid") String ppidString) throws Exception {
        PPId ppId = URIUtil.getPPId(ppidString);
        if (!found(ppId)) {
            throw new NotFoundException("PluginPackage not found: identifier= " + ppidString);
        }
        InputStream inputStream = configurationService.getKnowledgeRepository().getPluginPackageService().getJar(ppId);
        if (inputStream == null) {
            throw new NotFoundException("Jar does not exists (not found) for PPId: " + ppidString);
        }
        return inputStream;
    }

    @PUT
    @Path("{ppid}/jar")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response putPluginPackageJar(@PathParam("ppid") String ppidString, InputStream jar) {
        PPId ppId = URIUtil.getPPId(ppidString);
        if (!found(ppId)) {
            return Responses.notFound("PluginPackage not found: identifier= " + ppidString);
        }
        try {
            configurationService.getKnowledgeRepository().getPluginPackageService().persistJar(ppId, jar);
        } catch (Exception e) {
            Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    @DELETE
    @Path("{ppid}/jar")
    public Response deletePluginPackageJar(@PathParam("ppid") String ppidString) {
        PPId ppId = URIUtil.getPPId(ppidString);
        if (!found(ppId)) {
            return Responses.notFound("PluginPackage not found: identifier= " + ppidString);
        }
        try {
            configurationService.getKnowledgeRepository().getPluginPackageService().deleteJar(ppId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    private boolean found(PPId identifier) {
        return find(identifier) != null;
    }

    private PluginPackage find(PPId identifier) {
        return configurationService.getKnowledgeRepository().getPluginPackageService().find(identifier);
    }
}
