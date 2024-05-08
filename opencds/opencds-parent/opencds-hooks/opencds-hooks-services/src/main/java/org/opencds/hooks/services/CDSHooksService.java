/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.hooks.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.ConfigurationService;
import org.opencds.hooks.evaluation.service.HookEvaluation;
import org.opencds.hooks.lib.json.JsonUtil;
import org.opencds.hooks.model.feedback.FeedbackList;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.response.CdsResponse;
import org.opencds.hooks.model.util.DateUtil;
import org.opencds.hooks.services.util.Responses;
import org.opencds.hooks.services.util.ServicesBuilder;

import java.util.Date;

@Path("cds-services")
public class CDSHooksService {
    private static final Log log = LogFactory.getLog(CDSHooksService.class);
    private static final String EVAL_TIME = "eval-time";

    private final ConfigurationService configurationService;
    private final HookEvaluation hookEvaluation;
    private final JsonUtil jsonUtil;

    public CDSHooksService(ConfigurationService configurationService, HookEvaluation hookEvaluation,
                           JsonUtil jsonUtil) {
        this.configurationService = configurationService;
        this.hookEvaluation = hookEvaluation;
        this.jsonUtil = jsonUtil;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response cdsServices(@Context UriInfo uriInfo) {
        // list CDS Services
        return Responses.ok(jsonUtil.toJson(ServicesBuilder.build(configurationService.getKnowledgeRepository()
                .getKnowledgeModuleService().getAll(km -> km.getCDSHook() != null))));
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response cdsServicesOptions(@Context UriInfo uriInfo) {
        return Responses.ok();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{serviceId}")
    public Response invokeCdsService(@PathParam("serviceId") String serviceId, String payload, @Context UriInfo uriInfo,
                                     @Context HttpServletRequest req) {
        try {
            log.debug("CdsRequest Payload: " + payload);
            CdsRequest request = jsonUtil.fromJson(payload, CdsRequest.class);
            if (request == null) {
                return Responses.badRequest("no content provided");
            }
            log.debug("Base URI: " + uriInfo.getBaseUri());
            Date evalTime = null;
            if (req.getHeader(EVAL_TIME) != null) {
                try {
                    evalTime = DateUtil.iso8601StringToDate(req.getHeader(EVAL_TIME));
                } catch (Exception e) {
                    log.warn("Unable to parse eval-time header: " + req.getHeader(EVAL_TIME));
                }
            }
            // TODO validation?
            CdsResponse response = hookEvaluation.evaluate(serviceId, request, evalTime, uriInfo.getBaseUri());
            String responsePayload = jsonUtil.toJson(response);
            log.debug("CdsResponse Payload: " + responsePayload);
            return Responses.ok(responsePayload);
        } catch (Exception e) {
            e.printStackTrace();
            return Responses.internalServerError(e.getMessage());
        }
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{serviceId}")
    public Response invokeCdsServicesOptions(@Context UriInfo uriInfo) {
        return Responses.ok();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{serviceId}/feedback")
    public Response invokeFeedback(@PathParam("serviceId") String serviceId, String payload, @Context UriInfo uriInfo,
                                   @Context HttpServletRequest req) {
        try {
            log.debug("CdsFeedback Payload: " + payload);
            FeedbackList feedbackList = jsonUtil.fromJson(payload, FeedbackList.class);
            if (feedbackList.isNullOrEmpty()) {
                return Responses.badRequest("no content provided");
            }
            log.debug("Base URI: " + uriInfo.getBaseUri());
            // TODO: WHat shall we do with the feedback?
            return Responses.created(uriInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Responses.internalServerError(e.getMessage());
        }
    }

}
