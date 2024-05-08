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
import jakarta.ws.rs.NotSupportedException;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.config.api.util.URIUtil;
import org.opencds.config.mapper.KMIdMapper;
import org.opencds.config.mapper.KnowledgeModuleMapper;
import org.opencds.config.mapper.SupportingDataMapper;
import org.opencds.config.schema.KnowledgeModules;
import org.opencds.config.schema.Link;
import org.opencds.config.schema.ObjectFactory;
import org.opencds.config.schema.SupportingDataList;
import org.opencds.config.service.rest.util.KMIdUtil;
import org.opencds.config.service.rest.util.Responses;

import java.io.InputStream;
import java.util.List;

@Path("knowledgemodules")
public class KnowledgeModuleRestService {
    private static final String SELF = "self";
    private static final Log log = LogFactory.getLog(KnowledgeModuleRestService.class);
    private ConfigurationService configurationService;

    public KnowledgeModuleRestService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // /knowledgemodules

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public KnowledgeModules getKnowledgeModules(@Context UriInfo uriInfo) {
        KnowledgeModules kms = KnowledgeModuleMapper.external(configurationService.getKnowledgeRepository()
                .getKnowledgeModuleService().getAll());
        for (org.opencds.config.schema.KnowledgeModule km : kms.getKnowledgeModule()) {
            Link link = new Link();
            link.setRel(SELF);
            link.setHref(uriInfo.getAbsolutePathBuilder().path(URIUtil.buildKMIdString(km.getIdentifier())).build()
                    .toString());
            km.setLink(link);
        }
        return kms;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response createKnowledgeModule(@Context UriInfo uriInfo,
            JAXBElement<org.opencds.config.schema.KnowledgeModule> jaxKM) {
        org.opencds.config.schema.KnowledgeModule knowledgeModule = jaxKM.getValue();
        KMId kmId = KMIdMapper.internal(knowledgeModule.getIdentifier());
        if (found(kmId)) {
            return Responses.conflict("KnowledgeModule already exists: id= "
                    + URIUtil.buildKMIdString(knowledgeModule.getIdentifier()));
        }
        try {
            configurationService.getKnowledgeRepository().getKnowledgeModuleService()
                    .persist(KnowledgeModuleMapper.internal(knowledgeModule));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.created(uriInfo, URIUtil.buildKMIdString(knowledgeModule.getIdentifier()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putKnowledgeModules(@Context UriInfo uriInfo,
    		JAXBElement<org.opencds.config.schema.KnowledgeModules> jaxKMs) {
    		KnowledgeModules knowledgeModules = jaxKMs.getValue();
        try {
            configurationService.getKnowledgeRepository().getKnowledgeModuleService()
                    .persist(KnowledgeModuleMapper.internal(knowledgeModules));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    // /knowledgemodules/<kmid>
    @GET
    @Path("{kmid}")
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<org.opencds.config.schema.KnowledgeModule> getKnowledgeModule(@Context UriInfo uriInfo,
            @PathParam("kmid") String kmidString) throws Exception {
        KnowledgeModule km = find(URIUtil.getKMId(kmidString));
        if (km == null) {
            throw new NotFoundException("KnowledgeModule not found: id= " + kmidString);
        }
        org.opencds.config.schema.KnowledgeModule externalKM = KnowledgeModuleMapper.external(km);
        Link link = new Link();
        link.setRel(SELF);
        link.setHref(uriInfo.getAbsolutePath().toString());
        externalKM.setLink(link);
        return new ObjectFactory().createKnowledgeModule(externalKM);
    }

    @PUT
    @Path("{kmid}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateKnowledgeModule(@Context UriInfo uriInfo, @PathParam("kmid") String kmidString,
            JAXBElement<org.opencds.config.schema.KnowledgeModule> jaxKM) {
        KMId kmId = URIUtil.getKMId(kmidString);
        boolean created = false;
        if (!found(kmId)) {
            created = true;
        }
        org.opencds.config.schema.KnowledgeModule km = jaxKM.getValue();
        KnowledgeModule kmInternal = KnowledgeModuleMapper.internal(km);
        // TODO: Push deeper
        if (!kmId.equals(kmInternal.getKMId())) {
            return Responses.badRequest("KMID of request and document do not match.");
        }
        try {
            configurationService.getKnowledgeRepository().getKnowledgeModuleService().persist(kmInternal);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo, URIUtil.buildKMIdString(km.getIdentifier()));
        } else {
            return Responses.ok();
        }
    }

    @DELETE
    @Path("{kmid}")
    public Response deleteKnowledgeModule(@PathParam("kmid") String kmidString) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            throw new NotFoundException("KnowledgeModule not found: id= " + kmidString);
        }
        try {
            configurationService.getKnowledgeRepository().getKnowledgeModuleService().delete(kmId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    /*
     * KNOWLEDGE PACKAGES
     */

    // /knowledgemodules/<KMId>/package

    @GET
    @Path("{kmid}/package")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getKnowledgePackage(@PathParam("kmid") String kmidString) throws Exception {

        KMId kmId = URIUtil.getKMId(kmidString);
        KnowledgeModule km = configurationService.getKnowledgeRepository().getKnowledgeModuleService().find(kmId);
        if (km.getPackageType().equals("MAVEN")){
            throw new NotSupportedException("get knowledge package operation is not supported on MAVEN knowledge modules, kmid: " + kmidString);
        }
        if (!found(kmId)) {
            throw new NotFoundException("KnowledgeModule not found: id= " + kmidString);
        }
        InputStream inputStream = configurationService.getKnowledgeRepository().getKnowledgeModuleService()
                .getKnowledgePackage(kmId);
        if (inputStream == null) {
            throw new NotFoundException("Package does not exist (not found) for kmid: " + kmidString);
        }
        return inputStream;

    }

    // TODO: Probably want to support HEAD on this... to provide size and
    // potentially some other metadata.

    /**
     * Updates or creates...
     *
     * TODO: Provide size, and other metadata?
     *
     * @param kmidString
     * @param knowledgePackage
     * @return
     */
    @PUT
    @Path("{kmid}/package")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response putKnowledgePackage(@PathParam("kmid") String kmidString, InputStream knowledgePackage) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        try {
            configurationService.getKnowledgeRepository().getKnowledgeModuleService()
                    .persistKnowledgePackage(kmId, knowledgePackage);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    @DELETE
    @Path("{kmid}/package")
    public Response deleteKnowledgePackage(@PathParam("kmid") String kmidString) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        try {
            configurationService.getKnowledgeRepository().getKnowledgeModuleService().deleteKnowledgePackage(kmId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    /*
     * SUPPORTING DATA
     */

    // /knowledgemodules/<KMId>/supportingdata

    @Deprecated
    @GET
    @Path("{kmid}/supportingdata")
    @Produces(MediaType.APPLICATION_XML)
    public SupportingDataList getSupportingDataSet(@Context UriInfo uriInfo, @PathParam("kmid") String kmidString) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            throw new NotFoundException("KnowledgeModule not found: id= " + kmidString);
        }
        List<SupportingData> supportingDataInternal = configurationService.getKnowledgeRepository()
                .getSupportingDataService().find(kmId);
        SupportingDataList sdList = SupportingDataMapper.external(supportingDataInternal);
        for (org.opencds.config.schema.SupportingData sd : sdList.getSupportingData()) {
            Link link = new Link();
            link.setRel(SELF);
            link.setHref(uriInfo.getAbsolutePathBuilder().path(sd.getIdentifier()).build().toString());
            sd.setLink(link);
        }
        return sdList;
    }

    @Deprecated
    @POST
    @Path("{kmid}/supportingdata")
    @Consumes(MediaType.APPLICATION_XML)
    public Response createSupportingData(@Context UriInfo uriInfo, @PathParam("kmid") String kmidString,
            JAXBElement<org.opencds.config.schema.SupportingData> jaxSD) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        org.opencds.config.schema.SupportingData supportingData = jaxSD.getValue();
        KMId sdKMId = KMIdImpl.create(supportingData.getKmId().getScopingEntityId(), supportingData.getKmId().getBusinessId(), supportingData.getKmId().getVersion());
        if (!kmId.equals(sdKMId)) {
            return Responses.badRequest("SupportingData Identifier of request and document do not match");
        }
        if (found(kmId, supportingData.getIdentifier())) {
            return Responses.conflict("SupportingData already exists: kmId= " + kmidString + ", supportingDataId= "
                    + supportingData.getIdentifier());
        }
        try {
            configurationService.getKnowledgeRepository().getSupportingDataService()
                    .persist(SupportingDataMapper.internal(supportingData));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.created(uriInfo, supportingData.getIdentifier());
    }

    // /knowledgemodules/<KMId>/supportingdata/<SupportingDataId>

    @Deprecated
    @GET
    @Path("/{kmid}/supportingdata/{supportingDataId}")
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<org.opencds.config.schema.SupportingData> getSupportingData(@Context UriInfo uriInfo,
            @PathParam("kmid") String kmidString, @PathParam("supportingDataId") String supportingDataId) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            throw new NotFoundException("KnowledgeModule not found: id= " + kmidString);
        }
        SupportingData supportingData = find(kmId, supportingDataId);
        if (supportingData == null) {
            throw new NotFoundException("SupportingData metadata not found: kmId= " + kmidString
                    + ", supportingDataId= " + supportingDataId);
        }
        org.opencds.config.schema.SupportingData sd = SupportingDataMapper.external(supportingData);
        Link link = new Link();
        link.setRel(SELF);
        link.setHref(uriInfo.getAbsolutePath().toString());
        sd.setLink(link);
        return new ObjectFactory().createSupportingData(sd);
    }

    @Deprecated
    @PUT
    @Path("/{kmid}/supportingdata/{supportingDataId}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateSupportingData(@Context UriInfo uriInfo, @PathParam("kmid") String kmidString,
            @PathParam("supportingDataId") String supportingDataId, JAXBElement<org.opencds.config.schema.SupportingData> jaxSD) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        SupportingData supportingData = SupportingDataMapper.internal(jaxSD.getValue());
        if (!kmId.equals(supportingData.getKMId())) {
            return Responses.badRequest("KM Identifier of request and document do not match");
        } else if (!supportingDataId.equals(supportingData.getIdentifier())) {
            return Responses.badRequest("SupportingData Identifier of request and document do not match");
        }
        boolean created = false;
        if (!found(kmId, supportingData.getIdentifier())) {
            created = true;
        }
        try {
            configurationService.getKnowledgeRepository().getSupportingDataService().persist(supportingData);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo, supportingData.getIdentifier());
        } else {
            return Responses.noContent();
        }
    }

    @Deprecated
    @DELETE
    @Path("{kmid}/supportingdata/{supportingDataId}")
    public Response deleteSupportingData(@PathParam("kmid") String kmidString,
            @PathParam("supportingDataId") String supportingDataId) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        try {
            configurationService.getKnowledgeRepository().getSupportingDataService().delete(kmId, supportingDataId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    /*
     * SUPPORTING DATA - PACKAGE
     */

    // /config/knowledgemodules/<KMId>/supportingdata/<SupportingDataId>/package

    @Deprecated
    @GET
    @Path("{kmid}/supportingdata/{supportingDataId}/package")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getSupportingDataPackage(@PathParam("kmid") String kmidString,
            @PathParam("supportingDataId") String supportingDataId) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            throw new NotFoundException("KnowledgeModule not found: id= " + kmidString);
        }
        if (!found(kmId, supportingDataId)) {
            throw new NotFoundException("SupportingData metadata not found: kmId= " + kmidString
                    + ", supportingDataId= " + supportingDataId);
        }
        InputStream inputStream = configurationService.getKnowledgeRepository().getSupportingDataService()
                .getSupportingDataPackage(kmId, supportingDataId);
        if (inputStream == null) {
            throw new NotFoundException("Supporting data package does not exist (not found) for kmid: " + kmidString
                    + ", and supportingDataId: " + supportingDataId);
        }
        return inputStream;
    }

    /**
     * Creates or updates the supporting data package.
     *
     * @param kmidString
     * @param supportingDataId
     * @param supportingDataPackage
     * @return
     */
    @Deprecated
    @PUT
    @Path("{kmid}/supportingdata/{supportingDataId}/package")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response putSupportingDataPackage(@Context UriInfo uriInfo, @PathParam("kmid") String kmidString,
            @PathParam("supportingDataId") String supportingDataId, InputStream supportingDataPackage) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        if (!found(kmId, supportingDataId)) {
            return Responses.notFound("SupportingData metadata not found: kmId= " + kmidString + ", supportingDataId= "
                    + supportingDataId);
        }
        boolean created = false;
        if (!packageExists(kmId, supportingDataId)) {
            created = true;
        }
        try {
            configurationService.getKnowledgeRepository().getSupportingDataService()
                    .persistSupportingDataPackage(kmId, supportingDataId, supportingDataPackage);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo);
        } else {
            return Responses.noContent();
        }
    }

    @Deprecated
    @DELETE
    @Path("{kmid}/supportingdata/{supportingDataId}/package")
    public Response deleteSupportingDataPackage(@PathParam("kmid") String kmidString,
            @PathParam("supportingDataId") String supportingDataId) {
        KMId kmId = URIUtil.getKMId(kmidString);
        if (!found(kmId)) {
            return Responses.notFound("KnowledgeModule not found: id= " + kmidString);
        }
        if (!found(kmId, supportingDataId)) {
            return Responses.notFound("SupportingData metadata not found: kmId= " + kmidString + ", supportingDataId= "
                    + supportingDataId);
        }
        if (!packageExists(kmId, supportingDataId)) {
            return Responses.notFound("SupportingData package not found: kmId= " + kmidString + ", supportingDataId= "
                    + supportingDataId);
        }
        try {
            configurationService.getKnowledgeRepository().getSupportingDataService()
                    .deleteSupportingDataPackage(kmId, supportingDataId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    private boolean found(KMId kmId) {
        return find(kmId) != null;
    }

    private KnowledgeModule find(KMId kmId) {
        return KMIdUtil.find(configurationService.getKnowledgeRepository().getKnowledgeModuleService(), kmId);
    }

    @Deprecated
    private boolean found(KMId kmId, String identifier) {
        return find(kmId, identifier) != null;
    }

    @Deprecated
    private SupportingData find(KMId kmId, String identifier) {
        return configurationService.getKnowledgeRepository().getSupportingDataService().find(kmId, identifier);
    }

    @Deprecated
    private boolean packageExists(KMId kmId, String supportingDataId) {
        return configurationService.getKnowledgeRepository().getSupportingDataService()
                .packageExists(kmId, supportingDataId);
    }
}
