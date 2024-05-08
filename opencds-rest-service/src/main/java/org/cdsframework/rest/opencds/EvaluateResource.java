package org.cdsframework.rest.opencds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBException;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.rest.opencds.pojos.CDMUpdateResult;
import org.cdsframework.rest.opencds.pojos.CdmIdCheck;
import org.cdsframework.rest.opencds.pojos.KMUpdateResult;
import org.cdsframework.rest.opencds.pojos.KmIdCheck;
import org.cdsframework.rest.opencds.pojos.PreEvaluateHookType;
import org.cdsframework.rest.opencds.pojos.UpdateCheck;
import org.cdsframework.rest.opencds.pojos.UpdateResponse;
import org.cdsframework.rest.opencds.pojos.UpdateResponseResult;
import org.cdsframework.rest.opencds.utils.ConfigUtils;
import org.cdsframework.rest.opencds.utils.MarshalUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ServiceRequestBase;
import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateResponse;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.omg.dss.evaluation.requestresponse.KMEvaluationRequest;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.dss.evaluate.impl.DSSEvaluation;
import org.opencds.dss.evaluate.util.DssUtil;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * REST Web Service
 *
 * @author sdn
 */
@Path("resources")
public class EvaluateResource {

    private static final Log log = LogFactory.getLog(EvaluateResource.class);

    private LazyInitializer<Client> preEvaluateClient = new LazyInitializer<Client>()
    {
        @Override
        protected Client initialize()
        {
            return createPreEvalClient();
        }
    };

    private final DSSEvaluation evaluationService;
    private final ConfigurationService configurationService;

    @Context
    private ServletContext context;
    private final String instanceId;
    private final PreEvaluateHookType preEvaluateHookType;
    private final String preEvaluateHookUri;
    private final int preEvalHookConnectTimeout;
    private final int preEvalHookReadTimeout;
    private final boolean preEvalEnabled;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new instance of EvaluateResource
     *
     * @param evaluationService
     * @param configurationService
     */
    public EvaluateResource(final DSSEvaluation evaluationService,
            final ConfigurationService configurationService) {
        this.evaluationService = evaluationService;
        this.configurationService = configurationService;

        preEvaluateHookType = PreEvaluateHookType.valueOf(System.getProperty("preEvaluateHookType", PreEvaluateHookType.ENTITY_IDENTIFIER.name()));
        preEvaluateHookUri = System.getProperty("preEvaluateHookUri");
        boolean enabled =  Boolean.valueOf(System.getProperty("preEvaluateEnabled", "true"));
        if (preEvaluateHookUri == null || preEvaluateHookUri.trim().isEmpty())
            enabled = false;
        preEvalEnabled = enabled;

        instanceId = System.getProperty("preEvaluateUuid");
        if (preEvalEnabled && (StringUtils.isEmpty(instanceId)))
            throw new IllegalStateException("'preEvaluateUuid' is required when the preEvaluateHook is enabled");

        preEvalHookReadTimeout = Integer.parseInt(System.getProperty("preEvaluateTimeout", "10000"));
        preEvalHookConnectTimeout = Integer.parseInt(System.getProperty("preEvaluateConnectTimeout", "10000"));

        if (!preEvalEnabled)
            log.warn("preEvaluateHook is disabled");
        log.info(String.format("preEvaluate configuration: enabled=%s; type=%s; uri=%s; instanceId=%s; readTimeout=%d; connectTimeout=%d",
                preEvalEnabled, preEvaluateHookType, preEvaluateHookUri, instanceId, preEvalHookReadTimeout, preEvalHookConnectTimeout));
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("tz")
    public String tz() {
        return TimeZone.getDefault().getID();
    }

    /**
     * Retrieves representation of an instance of
     * org.cdsframework.rest.opencds.EvaluateResource
     *
     * @param evaluateString
     * @param headers
     * @param res
     * @return
     * @throws ParseException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws InvalidDriDataFormatExceptionFault
     * @throws UnrecognizedLanguageExceptionFault
     * @throws RequiredDataNotProvidedExceptionFault
     * @throws UnsupportedLanguageExceptionFault
     * @throws UnrecognizedScopedEntityExceptionFault
     * @throws EvaluationExceptionFault
     * @throws InvalidTimeZoneOffsetExceptionFault
     * @throws DSSRuntimeExceptionFault
     * @throws JAXBException
     * @throws TransformerException
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Path("evaluate")
    public Response evaluate(final String evaluateString, @Context final HttpHeaders headers,
            @Context final HttpServletResponse res)
            throws ParseException, UnsupportedEncodingException, IOException, InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault, RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault, UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault, JAXBException, TransformerException {
        final String METHODNAME = "evaluate ";

        long unmarshalTime = 0;
        long preEvalTime = -1;
        long evalTime = -1;
        long marshalTime = -1;
        boolean hasKmUpdates = false;
        boolean hasCdmUpdates = false;
        boolean success = false;

        StopWatch timer = new StopWatch();
        timer.start("unmarshal");
        Evaluate evaluate = unmarshal(evaluateString, Evaluate.class, headers);
        timer.stop();
        unmarshalTime = timer.getLastTaskTimeMillis();

        try {
            timer.start("preEvaluate");
            UpdateResponse updateResponse = preEvaluate(evaluate);
            timer.stop();
            preEvalTime = timer.getLastTaskTimeMillis();
            hasKmUpdates = hasKmUpdates(updateResponse);
            hasCdmUpdates = hasCdmUpdates(updateResponse);

            timer.start("evaluate");
            final EvaluateResponse evaluateResponse = evaluationService.evaluate(evaluate);
            timer.stop();
            evalTime = timer.getLastTaskTimeMillis();

            timer.start("marshal");
            Response response = createResponse(evaluateResponse.getEvaluationResponse(), headers);
            timer.stop();
            marshalTime = timer.getLastTaskTimeMillis();
            success = true;
            return response;
        } finally {
            log.info(String.format("%s eval=%s; success=%s; instanceId=%s; reqSize=%d; unmarshalTime=%d; kmUpdated=%s; cdmUpdated=%s; preEvalTime=%d; evalTime=%d; marshalTime=%d; totalTime=%d",
                    METHODNAME, getEvalInfo(evaluate), success, instanceId, evaluateString.length(), unmarshalTime, hasKmUpdates, hasCdmUpdates, preEvalTime, evalTime, marshalTime, timer.getTotalTimeMillis()));
        }
    }

    /**
     * Retrieves representation of an instance of
     * org.cdsframework.rest.opencds.EvaluateResource
     *
     * @param evaluateAtSpecifiedTimeString
     * @param headers
     * @param res
     * @return
     * @throws java.text.ParseException
     * @throws java.io.UnsupportedEncodingException
     * @throws org.omg.dss.InvalidDriDataFormatExceptionFault
     * @throws org.omg.dss.UnrecognizedLanguageExceptionFault
     * @throws org.omg.dss.RequiredDataNotProvidedExceptionFault
     * @throws org.omg.dss.UnsupportedLanguageExceptionFault
     * @throws org.omg.dss.UnrecognizedScopedEntityExceptionFault
     * @throws org.omg.dss.EvaluationExceptionFault
     * @throws org.omg.dss.InvalidTimeZoneOffsetExceptionFault
     * @throws org.omg.dss.DSSRuntimeExceptionFault
     * @throws jakarta.xml.bind.JAXBException
     * @throws javax.xml.transform.TransformerException
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Path("evaluateAtSpecifiedTime")
    public Response evaluateAtSpecifiedTime(final String evaluateAtSpecifiedTimeString,
            @Context final HttpHeaders headers, @Context final HttpServletResponse res)
            throws ParseException, UnsupportedEncodingException, IOException, InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault, RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault, UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault, JAXBException, TransformerException {
        final String METHODNAME = "evaluateAtSpecifiedTime ";

        long unmarshalTime = 0;
        long preEvalTime = -1;
        long evalTime = -1;
        long marshalTime = -1;
        boolean hasKmUpdates = false;
        boolean hasCdmUpdates = false;
        boolean success = false;

        StopWatch timer = new StopWatch();
        timer.start("unmarshal");
        EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = unmarshal(evaluateAtSpecifiedTimeString, EvaluateAtSpecifiedTime.class,
                headers);
        timer.stop();
        unmarshalTime = timer.getLastTaskTimeMillis();

        try {
            timer.start("preEvaluate");
            UpdateResponse updateResponse = preEvaluate(evaluateAtSpecifiedTime);
            timer.stop();
            preEvalTime = timer.getLastTaskTimeMillis();
            hasKmUpdates = hasKmUpdates(updateResponse);
            hasCdmUpdates = hasCdmUpdates(updateResponse);

            timer.start("evaluate");
            final EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTimeResponse = evaluationService
                    .evaluateAtSpecifiedTime(evaluateAtSpecifiedTime);
            timer.stop();
            evalTime = timer.getLastTaskTimeMillis();

            timer.start("marshal");
            Response response = createResponse(evaluateAtSpecifiedTimeResponse.getEvaluationResponse(), headers);
            timer.stop();
            marshalTime = timer.getLastTaskTimeMillis();
            success = true;
            return response;
        } finally {
            log.info(String.format("%s eval=%s; success=%s; instanceId=%s; reqSize=%d; unmarshalTime=%d; kmUpdated=%s; cdmUpdated=%s; preEvalTime=%d; evalTime=%d; marshalTime=%d; totalTime=%d",
                    METHODNAME, getEvalInfo(evaluateAtSpecifiedTime), success, instanceId, evaluateAtSpecifiedTimeString.length(), unmarshalTime, hasKmUpdates, hasCdmUpdates, preEvalTime, evalTime, marshalTime, timer.getTotalTimeMillis()));
        }
    }

    private static String getEvalInfo(EvaluateAtSpecifiedTime eval)
    {
        return getEvalInfo(eval, eval.getEvaluationRequest());
    }

    private static String getEvalInfo(Evaluate eval)
    {
        return getEvalInfo(eval, eval.getEvaluationRequest());
    }

    private static String getEvalInfo(ServiceRequestBase reqBase, EvaluationRequest evalReq)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(reqBase.getInteractionId().getInteractionId());
        sb.append(":");
        for (int i = 0; i < evalReq.getKmEvaluationRequest().size(); i++)
        {
            EntityIdentifier kmId = evalReq.getKmEvaluationRequest().get(i).getKmId();
            if (i > 0)
                sb.append(",");
            sb.append(DssUtil.makeEIString(kmId));
        }
        return sb.toString();
    }

    private static boolean hasKmUpdates(UpdateResponse res)
    {
        return res != null && res.getKmUpdates() != null && !res.getKmUpdates().isEmpty();
    }

    private static boolean hasCdmUpdates(UpdateResponse res)
    {
        return res != null && res.getCdmUpdates() != null && !res.getCdmUpdates().isEmpty();
    }

    private Response createResponse(EvaluationResponse evalResponse, HttpHeaders headers)
            throws JsonProcessingException, JAXBException
    {
        final List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();

        Response.ResponseBuilder responseBuilder;
        if (acceptableMediaTypes.contains(MediaType.APPLICATION_JSON_TYPE)) {
            final String data = mapper.writeValueAsString(evalResponse);
            responseBuilder = Response.ok(data).type(MediaType.APPLICATION_JSON);
        } else {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MarshalUtils.marshal(evalResponse, stream);
            stream.toByteArray();
            responseBuilder = Response.ok(new String(stream.toByteArray())).type(MediaType.APPLICATION_XML);
        }
        return responseBuilder.build();
    }

    private <T> T unmarshal(String payload, Class<T> type, HttpHeaders headers)
            throws JsonProcessingException, JAXBException, TransformerException
    {
        final MediaType mediaType = headers.getMediaType();

        log.trace(String.format("unmarshal: mediaType=%s", mediaType));
        switch (mediaType.toString()) {
            case MediaType.APPLICATION_JSON:
                return mapper.readValue(payload, type);
            case MediaType.APPLICATION_XML:
                return MarshalUtils.unmarshal(new ByteArrayInputStream(payload.getBytes()), type);
            default:
                throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }
    }

    /**
     * branch for evaluateAtSpecifiedTime/preEvaluate logic
     *
     * @param evaluateAtSpecifiedTime
     */
    private UpdateResponse preEvaluate(final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime) {
        final String METHODNAME = "preEvaluate ";
        if (evaluateAtSpecifiedTime == null || evaluateAtSpecifiedTime.getEvaluationRequest() == null) {
            log.trace(METHODNAME + "an evaluateAtSpecifiedTime element is null!");
            return null;
        }
        return preEvaluate(evaluateAtSpecifiedTime.getEvaluationRequest());
    }

    /**
     * branch for evaluate/preEvaluate logic
     *
     * @param evaluate
     */
    private UpdateResponse preEvaluate(final Evaluate evaluate) {
        final String METHODNAME = "preEvaluate ";
        if (evaluate == null || evaluate.getEvaluationRequest() == null) {
            log.trace(METHODNAME + "an evaluate element is null!");
            return null;
        }
        return preEvaluate(evaluate.getEvaluationRequest());
    }

    /**
     * main preEvaluate webhook logic
     *
     * @param evaluationRequest
     */
    private UpdateResponse preEvaluate(final EvaluationRequest evaluationRequest) {
        final String METHODNAME = "preEvaluate ";
        UpdateResponse updateResponse = null;
        if (!preEvalEnabled)
        {
            log.trace(METHODNAME + "skipping preEvaluate (not enabled)");
            return null;
        }
        if (evaluationRequest == null || evaluationRequest.getKmEvaluationRequest() == null
                || evaluationRequest.getKmEvaluationRequest().isEmpty()) {
            log.warn(METHODNAME + "evaluationRequest is incomplete!");
            return null;
        }

        final long start = System.nanoTime();
        try {
            Response response;
            final UpdateCheck updateCheck = new UpdateCheck();

            final CDMId defaultCdmId = ConfigUtils.getDefaultCdmId();
            if (defaultCdmId != null) {
                final CdmIdCheck cdmIdCheck = new CdmIdCheck(defaultCdmId, ConfigUtils.isCdmExists(defaultCdmId, configurationService));
                updateCheck.getCdmIdChecks().add(cdmIdCheck);
            }
            updateCheck.setInstanceId(instanceId);

            // set the environment
            final String environment = context.getContextPath().toLowerCase().contains("test") ? "TEST" : "PRODUCTION";
            log.trace(METHODNAME + "environment: " + environment);
            updateCheck.setEnvironment(environment);

            switch (preEvaluateHookType) {
                case ENTITY_IDENTIFIER:
                    final List<KMEvaluationRequest> kmEvaluationRequests = evaluationRequest.getKmEvaluationRequest();

                    for (final KMEvaluationRequest kmEvaluationRequest : kmEvaluationRequests) {

                        if (kmEvaluationRequest == null) {
                            log.error(METHODNAME + "kmEvaluationRequest is null!");
                            continue;
                        }
                        EntityIdentifier entityIdentifier = kmEvaluationRequest.getKmId();
                        String scopingEntityId = entityIdentifier.getScopingEntityId();
                        String businessId = entityIdentifier.getBusinessId();
                        String version = entityIdentifier.getVersion();
                        log.trace(String.format("%s: scopingEntityId=%s; businessId=%s; version=%s", METHODNAME, scopingEntityId,
                                businessId, version));

                        if (scopingEntityId == null || scopingEntityId.trim().isEmpty()) {
                            log.error(METHODNAME + "scopingEntityId is null!");
                            continue;
                        }

                        if (businessId == null || businessId.trim().isEmpty()) {
                            log.error(METHODNAME + "businessId is null!");
                            continue;
                        }

                        if (version == null || version.trim().isEmpty()) {
                            log.error(METHODNAME + "version is null!");
                            continue;
                        }

                        final KMId kmId = KMIdImpl.create(scopingEntityId, businessId, version);

                        final boolean exists = ConfigUtils.isKmExists(kmId, configurationService);
                        final KmIdCheck kmIdCheck = new KmIdCheck(kmId, exists);
                        log.trace(METHODNAME + "Added KmIdCheck: " + kmIdCheck);
                        updateCheck.getKmIdChecks().add(kmIdCheck);
                    }
                    log.debug(METHODNAME + "updateCheck: " + updateCheck);

                    UpdateResponseResult result = null;
                    try
                    {
                        response = sendPreEvalRequest(updateCheck);
                        if (response.getStatus() != 200)
                        {
                            String msg = String.format("preEvaluate call was unsuccessful: status=%d; body=%s",
                                    response.getStatus(), response.readEntity(String.class));
                            throw new PreEvaluateHTTPException(msg);
                        }
                        updateResponse = readEntity(response, UpdateResponse.class);
                        result = ConfigUtils.update(updateResponse, configurationService, environment, instanceId);
                        if (result.hasErrors())
                        {
                            log.warn("Sending preEvaluate failure notification due to errors processing one or more updates");
                            sendPreEvalFailedRequest(result);
                        }
                    }
                    catch (PreEvaluateHTTPException e)
                    {
                        // Any non-200 responses should just get propagated up as an error. We do not want/need
                        // to send this to the /failed endpoint since these errors would indicate that the preEval service
                        // did not successfully process the request and therefore did not flag the updates as delivered.
                        throw e;
                    }
                    catch (Exception e)
                    {
                        // Any other unexpected errors need to be reported to the failure endpoint so the preEval service
                        // can mark the updates as not delivered.
                        // Examples of unexpected errors include: read timeouts, malformed preEval response, and other unexpected
                        // exceptions while processing the updates received that aren't gracefully captured by ConfigUtils.
                        int status = 500;
                        String error = "Error processing preEvaluate request: " + ExceptionUtils.getStackTrace(e);
                        result= new UpdateResponseResult();
                        result.setEnvironment(environment);
                        result.setInstanceId(instanceId);
                        if (!updateCheck.getCdmIdChecks().isEmpty())
                            result.getCdms().add(new CDMUpdateResult(updateCheck.getCdmIdChecks().get(0).getCdmId(), status, error ));
                        for (KmIdCheck kmCheck: updateCheck.getKmIdChecks())
                            result.getKms().add(new KMUpdateResult(kmCheck.getKmId(), status, error));

                        log.warn("Sending preEvaluate failure notification due to unexpected error: " + e.getMessage());
                        sendPreEvalFailedRequest(result);
                        throw new RuntimeException("Error processing preEvaluate request", e);
                    }
                    break;
                case EVALUATION_REQUEST:
                    updateCheck.setEvaluationRequest(evaluationRequest);
                    response = sendPreEvalRequest(updateCheck);
                    final EvaluationRequest evaluationRequestResponse = readEntity(response, EvaluationRequest.class);
                    evaluationRequest.getKmEvaluationRequest().clear();
                    evaluationRequest.getKmEvaluationRequest()
                            .addAll(evaluationRequestResponse.getKmEvaluationRequest());
                    evaluationRequest.getDataRequirementItemData().clear();
                    evaluationRequest.getDataRequirementItemData()
                            .addAll(evaluationRequestResponse.getDataRequirementItemData());
                    break;
                default:
                    throw new IllegalStateException("Unhandled hook type: " + preEvaluateHookType.toString());
            }
        } finally {
            log.info(METHODNAME + "duration: " + ((System.nanoTime() - start) / 1000000) + "ms");
        }
        return updateResponse;
    }

    private Response sendPreEvalRequest(Object payload)
    {
        final WebTarget webTarget = getLazy(preEvaluateClient).target(preEvaluateHookUri);
        return doPreEvalRequest(webTarget, payload);
    }

    private Response sendPreEvalFailedRequest(Object payload)
    {
        log.info("Failure notification being sent: " + toJsonString(payload));
        final WebTarget webTarget = getLazy(preEvaluateClient).target(preEvaluateHookUri).path("failed");
        return doPreEvalRequest(webTarget, payload);
    }

    private Response doPreEvalRequest(WebTarget target, Object payload)
    {
        final String METHODNAME = "doPreEvalRequest";
        if (log.isTraceEnabled())
            log.trace(toJsonString(payload));

        if (log.isDebugEnabled())
            log.debug(String.format("%s Invoking PUT: uri=%s; connectTimeout=%d; readTimeout=%d",
                    METHODNAME, target.getUri(), preEvalHookConnectTimeout, preEvalHookReadTimeout));
        Builder builder = target.request(MediaType.APPLICATION_JSON);
        StopWatch timer = new StopWatch();
        timer.start();
        Response response = builder.put(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
        timer.stop();
        if (log.isDebugEnabled() || response.getStatus() != 200)
            log.info(String.format("%s PUT completed: status=%d; duration=%d",
                    METHODNAME, response.getStatus(), timer.getTotalTimeMillis()));

        return response;
    }

    private <T> T readEntity(Response response, Class<T> type)
    {
        T entity = response.readEntity(type);
        if (log.isTraceEnabled())
            log.trace(toJsonString(entity));
        return entity;
    }

    private String toJsonString(Object o)
    {
        try
        {
            return mapper.writeValueAsString(o);
        }
        catch (Exception e)
        {
            log.warn("Error trying to write object as json", e);
        }
        return o == null? "null": o.toString();
    }

    private Client createPreEvalClient()
    {
        final ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);
        final Client client = ClientBuilder.newClient(config);
        client.property(ClientProperties.CONNECT_TIMEOUT, preEvalHookConnectTimeout);
        client.property(ClientProperties.READ_TIMEOUT, preEvalHookReadTimeout);
        return client;
    }

    private <T> T getLazy(LazyInitializer<T> lazy)
    {
        try
        {
            return lazy.get();
        }
        catch (ConcurrentException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static final class PreEvaluateHTTPException extends RuntimeException
    {
        public PreEvaluateHTTPException(String message)
        {
            super(message);
        }
    }
}
