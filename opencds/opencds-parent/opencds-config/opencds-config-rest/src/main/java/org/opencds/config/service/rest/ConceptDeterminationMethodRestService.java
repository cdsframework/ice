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
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.util.URIUtil;
import org.opencds.config.mapper.ConceptDeterminationMethodMapper;
import org.opencds.config.schema.ConceptDeterminationMethods;
import org.opencds.config.schema.Link;
import org.opencds.config.schema.ObjectFactory;
import org.opencds.config.service.rest.util.Responses;

import java.util.List;

@Path("conceptdeterminationmethods")
public class ConceptDeterminationMethodRestService {
    private static final String SELF = "self";
    private ConfigurationService configurationService;

    public ConceptDeterminationMethodRestService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // /conceptdeterminationmethods

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ConceptDeterminationMethods getConceptDeterminationMethods(@Context UriInfo uriInfo) {
        List<ConceptDeterminationMethod> cdms = configurationService.getKnowledgeRepository()
                .getConceptDeterminationMethodService().getAll();
        ConceptDeterminationMethods externalCdms = ConceptDeterminationMethodMapper.external(cdms);
        for (org.opencds.config.schema.ConceptDeterminationMethod cdm : externalCdms.getConceptDeterminationMethod()) {
            Link link = new Link();
            link.setRel(SELF);
            link.setHref(uriInfo.getAbsolutePathBuilder().path(URIUtil.buildCDMIdString(cdm)).build().toString());
            cdm.setLink(link);
        }
        return externalCdms;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response createConceptDeterminationMethod(@Context UriInfo uriInfo,
            JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod> jaxCDM) {
        org.opencds.config.schema.ConceptDeterminationMethod cdm = jaxCDM.getValue();
        ConceptDeterminationMethod internalCDM = ConceptDeterminationMethodMapper.internal(cdm);
        if (found(internalCDM.getCDMId())) {
            return Responses.conflict("ConceptDeterminationMethod already exists: " + URIUtil.buildCDMIdString(cdm));
        }
        try {
            save(internalCDM);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.created(uriInfo, URIUtil.buildCDMIdString(cdm));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putConceptDeterminationMethods(@Context UriInfo uriInfo,
            org.opencds.config.schema.ConceptDeterminationMethods cdms) {
        try {
            save(ConceptDeterminationMethodMapper.internal(cdms));
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    // /conceptdeterminationmethods/<CDMId>

    @GET
    @Path("{cdmId}")
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod> getConceptDeterminationMethod(@Context UriInfo uriInfo,
            @PathParam("cdmId") String cdmidString) throws Exception {
        CDMId cdmId = URIUtil.getCDMId(cdmidString);
        ConceptDeterminationMethod cdm = find(cdmId);
        if (cdm == null) {
            throw new NotFoundException("ConceptDeterminationMethod not found: id= " + cdmidString);
        }
        org.opencds.config.schema.ConceptDeterminationMethod externalCdm = ConceptDeterminationMethodMapper
                .external(cdm);
        Link link = new Link();
        link.setRel(SELF);
        link.setHref(uriInfo.getAbsolutePath().toString());
        externalCdm.setLink(link);
        return new ObjectFactory().createConceptDeterminationMethod(externalCdm);
    }

    @PUT
    @Path("{cdmId}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateConceptDeterminationMethod(@Context UriInfo uriInfo, @PathParam("cdmId") String cdmidString,
            JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod> jaxCDM) {
        CDMId cdmId = URIUtil.getCDMId(cdmidString);
        org.opencds.config.schema.ConceptDeterminationMethod cdm = jaxCDM.getValue();
        ConceptDeterminationMethod cdmInternal = ConceptDeterminationMethodMapper.internal(cdm);
        if (!cdmId.equals(cdmInternal.getCDMId())) {
            return Responses.badRequest("CDMId of request and document do not match");
        }
        boolean created = false;
        if (!found(cdmInternal.getCDMId())) {
            created = true;
        }
        try {
            save(cdmInternal);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        if (created) {
            return Responses.created(uriInfo, URIUtil.buildCDMIdString(cdm));
        } else {
            return Responses.noContent();
        }
    }

    @DELETE
    @Path("{cdmId}")
    public Response deleteCDM(@PathParam("cdmId") String cdmidString) {
        CDMId cdmId = URIUtil.getCDMId(cdmidString);
        // TODO: What if delete fails?
        if (!found(cdmId)) {
            Responses.notFound("ConceptDeterminationMethod not found: id= " + cdmidString);
        }
        try {
            configurationService.getKnowledgeRepository().getConceptDeterminationMethodService().delete(cdmId);
        } catch (Exception e) {
            return Responses.internalServerError(e.getMessage());
        }
        return Responses.noContent();
    }

    private ConceptDeterminationMethod find(CDMId cdmId) {
        return configurationService.getKnowledgeRepository().getConceptDeterminationMethodService().find(cdmId);
    }

    private boolean found(CDMId cdmId) {
        return find(cdmId) != null;
    }

    private void save(ConceptDeterminationMethod cdmInternal) {
        configurationService.getKnowledgeRepository().getConceptDeterminationMethodService().persist(cdmInternal);
    }

    private void save(List<ConceptDeterminationMethod> cdmsInternal) {
        configurationService.getKnowledgeRepository().getConceptDeterminationMethodService().persist(cdmsInternal);
    }

}
