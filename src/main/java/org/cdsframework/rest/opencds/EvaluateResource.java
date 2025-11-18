package org.cdsframework.rest.opencds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import jakarta.servlet.ServletContext;
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
import lombok.extern.slf4j.Slf4j;

/**
 * REST Web Service
 *
 * @author sdn
 */
@Slf4j
@Path("resources")
public class EvaluateResource
{
    private static final class PreEvaluateHTTPException extends RuntimeException
    {
        public PreEvaluateHTTPException(final String message)
        {
            super(message);
        }
    }

    private static String getEvalInfo(final EvaluateAtSpecifiedTime eval)
    {
        return getEvalInfo(eval, eval.getEvaluationRequest());
    }

    private static String getEvalInfo(final Evaluate eval)
    {
        return getEvalInfo(eval, eval.getEvaluationRequest());
    }

    private static String getEvalInfo(final ServiceRequestBase reqBase, final EvaluationRequest evalReq)
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(reqBase.getInteractionId().getInteractionId());
        sb.append(":");
        for (int i = 0; i < evalReq.getKmEvaluationRequest().size(); i++)
        {
            final EntityIdentifier kmId = evalReq.getKmEvaluationRequest().get(i).getKmId();
            if (i > 0)
                sb.append(",");
            sb.append(DssUtil.makeEIString(kmId));
        }
        return sb.toString();
    }

    private static boolean hasKmUpdates(final UpdateResponse res)
    {
        return res != null && res.getKmUpdates() != null && !res.getKmUpdates().isEmpty();
    }

    private static boolean hasCdmUpdates(final UpdateResponse res)
    {
        return res != null && res.getCdmUpdates() != null && !res.getCdmUpdates().isEmpty();
    }

    private final DSSEvaluation evaluationService;
    private final ConfigurationService configurationService;
    private final String instanceId;
    private final PreEvaluateHookType preEvaluateHookType;
    private final String preEvaluateHookUri;
    private final int preEvalHookConnectTimeout;
    private final int preEvalHookReadTimeout;
    private final boolean preEvalEnabled;
    private final ObjectMapper mapper = new ObjectMapper();
    private final LazyInitializer<Client> preEvaluateClient = new LazyInitializer<>()
    {
        @Override
        protected Client initialize()
        {
            return createPreEvalClient();
        }
    };
    @Context
    private ServletContext context;

    /**
     * Creates a new instance of EvaluateResource
     */
    public EvaluateResource(final DSSEvaluation evaluationService, final ConfigurationService configurationService)
    {
        this.evaluationService = evaluationService;
        this.configurationService = configurationService;

        preEvaluateHookType = PreEvaluateHookType.valueOf(
                System.getProperty("preEvaluateHookType", PreEvaluateHookType.ENTITY_IDENTIFIER.name()));
        preEvaluateHookUri = System.getProperty("preEvaluateHookUri");
        boolean enabled = Boolean.parseBoolean(System.getProperty("preEvaluateEnabled", "true"));
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
        log.info("preEvaluate configuration: enabled={}; type={}; uri={}; instanceId={}; readTimeout={}; connectTimeout={}",
                preEvalEnabled, preEvaluateHookType, preEvaluateHookUri, instanceId, preEvalHookReadTimeout,
                preEvalHookConnectTimeout);
    }

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    @Path("tz")
    public String tz()
    {
        return TimeZone.getDefault().getID();
    }

    /**
     * Retrieves representation of an instance of
     * org.cdsframework.rest.opencds.EvaluateResource
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Path("evaluate")
    public Response evaluate(final String evaluateString, @Context final HttpHeaders headers)
            throws IOException, InvalidDriDataFormatExceptionFault, UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault, UnsupportedLanguageExceptionFault, UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault, InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault, JAXBException,
            TransformerException
    {
        final String METHODNAME = "evaluate ";

        final long unmarshalTime;
        long preEvalTime = -1;
        long evalTime = -1;
        long marshalTime = -1;
        boolean hasKmUpdates = false;
        boolean hasCdmUpdates = false;
        boolean success = false;

        final StopWatch timer = new StopWatch();
        timer.start("unmarshal");
        final Evaluate evaluate = unmarshal(evaluateString, Evaluate.class, headers);
        timer.stop();
        unmarshalTime = timer.lastTaskInfo().getTimeMillis();

        try
        {
            timer.start("preEvaluate");
            final UpdateResponse updateResponse = preEvaluate(evaluate);
            timer.stop();
            preEvalTime = timer.lastTaskInfo().getTimeMillis();
            hasKmUpdates = hasKmUpdates(updateResponse);
            hasCdmUpdates = hasCdmUpdates(updateResponse);

            timer.start("evaluate");
            final EvaluateResponse evaluateResponse = evaluationService.evaluate(evaluate);
            timer.stop();
            evalTime = timer.lastTaskInfo().getTimeMillis();

            timer.start("marshal");
            final Response response = createResponse(evaluateResponse.getEvaluationResponse(), headers);
            timer.stop();
            marshalTime = timer.lastTaskInfo().getTimeMillis();
            success = true;
            return response;
        }
        finally
        {
            log.info(
                    "{} eval={}; success={}; instanceId={}; reqSize={}; unmarshalTime={}; kmUpdated={}; cdmUpdated={}; preEvalTime={}; evalTime={}; marshalTime={}; totalTime={}",
                    METHODNAME, getEvalInfo(evaluate), success, instanceId, evaluateString.length(), unmarshalTime, hasKmUpdates,
                    hasCdmUpdates, preEvalTime, evalTime, marshalTime, timer.getTotalTimeMillis());
        }
    }

    /**
     * Retrieves representation of an instance of
     * org.cdsframework.rest.opencds.EvaluateResource
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Path("evaluateAtSpecifiedTime")
    public Response evaluateAtSpecifiedTime(final String evaluateAtSpecifiedTimeString, @Context final HttpHeaders headers)
            throws IOException, InvalidDriDataFormatExceptionFault, UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault, UnsupportedLanguageExceptionFault, UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault, InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault, JAXBException,
            TransformerException
    {
        final String METHODNAME = "evaluateAtSpecifiedTime ";

        final long unmarshalTime;
        long preEvalTime = -1;
        long evalTime = -1;
        long marshalTime = -1;
        boolean hasKmUpdates = false;
        boolean hasCdmUpdates = false;
        boolean success = false;

        final StopWatch timer = new StopWatch();
        timer.start("unmarshal");
        final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime =
                unmarshal(evaluateAtSpecifiedTimeString, EvaluateAtSpecifiedTime.class, headers);
        timer.stop();
        unmarshalTime = timer.lastTaskInfo().getTimeMillis();

        try
        {
            timer.start("preEvaluate");
            final UpdateResponse updateResponse = preEvaluate(evaluateAtSpecifiedTime);
            timer.stop();
            preEvalTime = timer.lastTaskInfo().getTimeMillis();
            hasKmUpdates = hasKmUpdates(updateResponse);
            hasCdmUpdates = hasCdmUpdates(updateResponse);

            timer.start("evaluate");
            final EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTimeResponse =
                    evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime);
            timer.stop();
            evalTime = timer.lastTaskInfo().getTimeMillis();

            timer.start("marshal");
            final Response response = createResponse(evaluateAtSpecifiedTimeResponse.getEvaluationResponse(), headers);
            timer.stop();
            marshalTime = timer.lastTaskInfo().getTimeMillis();
            success = true;
            return response;
        }
        finally
        {
            log.info(
                    "{} eval={}; success={}; instanceId={}; reqSize={}; unmarshalTime={}; kmUpdated={}; cdmUpdated={}; preEvalTime={}; evalTime={}; marshalTime={}; totalTime={}",
                    METHODNAME, getEvalInfo(evaluateAtSpecifiedTime), success, instanceId, evaluateAtSpecifiedTimeString.length(),
                    unmarshalTime, hasKmUpdates, hasCdmUpdates, preEvalTime, evalTime, marshalTime, timer.getTotalTimeMillis());
        }
    }

    private Response createResponse(final EvaluationResponse evalResponse, final HttpHeaders headers)
            throws JsonProcessingException, JAXBException
    {
        final List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();

        final Response.ResponseBuilder responseBuilder;
        if (acceptableMediaTypes.contains(MediaType.APPLICATION_JSON_TYPE))
        {
            responseBuilder = Response.ok(mapper.writeValueAsString(evalResponse)).type(MediaType.APPLICATION_JSON);
        }
        else
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MarshalUtils.marshal(evalResponse, stream);
            responseBuilder = Response.ok(stream.toString()).type(MediaType.APPLICATION_XML);
        }

        return responseBuilder.build();
    }

    private <T> T unmarshal(final String payload, final Class<T> type, final HttpHeaders headers)
            throws JsonProcessingException, JAXBException, TransformerException
    {
        final MediaType mediaType = headers.getMediaType();

        log.trace("unmarshal: mediaType={}", mediaType);
        return switch (mediaType.toString())
        {
            case MediaType.APPLICATION_JSON -> mapper.readValue(payload, type);
            case MediaType.APPLICATION_XML -> MarshalUtils.unmarshal(new ByteArrayInputStream(payload.getBytes()), type);
            default -> throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        };
    }

    /**
     * branch for evaluateAtSpecifiedTime/preEvaluate logic
     */
    private UpdateResponse preEvaluate(final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime)
    {
        final String METHODNAME = "preEvaluate ";
        if (evaluateAtSpecifiedTime == null || evaluateAtSpecifiedTime.getEvaluationRequest() == null)
        {
            log.trace(METHODNAME + "an evaluateAtSpecifiedTime element is null!");
            return null;
        }

        return preEvaluate(evaluateAtSpecifiedTime.getEvaluationRequest());
    }

    /**
     * branch for evaluate/preEvaluate logic
     */
    private UpdateResponse preEvaluate(final Evaluate evaluate)
    {
        final String METHODNAME = "preEvaluate ";
        if (evaluate == null || evaluate.getEvaluationRequest() == null)
        {
            log.trace(METHODNAME + "an evaluate element is null!");
            return null;
        }

        return preEvaluate(evaluate.getEvaluationRequest());
    }

    /**
     * main preEvaluate webhook logic
     */
    private UpdateResponse preEvaluate(final EvaluationRequest evaluationRequest)
    {
        final String METHODNAME = "preEvaluate ";
        UpdateResponse updateResponse = null;
        if (!preEvalEnabled)
        {
            log.trace(METHODNAME + "skipping preEvaluate (not enabled)");
            return null;
        }

        if (evaluationRequest == null || ObjectUtils.isEmpty(evaluationRequest.getKmEvaluationRequest()))
        {
            log.warn(METHODNAME + "evaluationRequest is incomplete!");
            return null;
        }

        final long start = System.nanoTime();
        try
        {
            final Response response;
            final UpdateCheck updateCheck = new UpdateCheck();

            final CDMId defaultCdmId = ConfigUtils.getDefaultCdmId();
            final CdmIdCheck cdmIdCheck = new CdmIdCheck(defaultCdmId, ConfigUtils.isCdmExists(defaultCdmId, configurationService));
            updateCheck.getCdmIdChecks().add(cdmIdCheck);
            updateCheck.setInstanceId(instanceId);

            // set the environment
            final String environment = context.getContextPath().toLowerCase().contains("test") ? "TEST" : "PRODUCTION";
            log.trace(METHODNAME + "environment: {}", environment);
            updateCheck.setEnvironment(environment);

            switch (preEvaluateHookType)
            {
                case ENTITY_IDENTIFIER ->
                {
                    final List<KMEvaluationRequest> kmEvaluationRequests = evaluationRequest.getKmEvaluationRequest();

                    for (final KMEvaluationRequest kmEvaluationRequest : kmEvaluationRequests)
                    {
                        if (kmEvaluationRequest == null)
                        {
                            log.error(METHODNAME + "kmEvaluationRequest is null!");
                            continue;
                        }

                        final EntityIdentifier entityIdentifier = kmEvaluationRequest.getKmId();
                        final String scopingEntityId = entityIdentifier.getScopingEntityId();
                        final String businessId = entityIdentifier.getBusinessId();
                        final String version = entityIdentifier.getVersion();
                        log.trace("{}: scopingEntityId={}; businessId={}; version={}", METHODNAME, scopingEntityId, businessId,
                                version);

                        if (scopingEntityId == null || scopingEntityId.trim().isEmpty())
                        {
                            log.error(METHODNAME + "scopingEntityId is null!");
                            continue;
                        }

                        if (businessId == null || businessId.trim().isEmpty())
                        {
                            log.error(METHODNAME + "businessId is null!");
                            continue;
                        }

                        if (version == null || version.trim().isEmpty())
                        {
                            log.error(METHODNAME + "version is null!");
                            continue;
                        }

                        final KMId kmId = KMIdImpl.create(scopingEntityId, businessId, version);

                        final boolean exists = ConfigUtils.isKmExists(kmId, configurationService);
                        final KmIdCheck kmIdCheck = new KmIdCheck(kmId, exists);
                        log.trace(METHODNAME + "Added KmIdCheck: {}", kmIdCheck);
                        updateCheck.getKmIdChecks().add(kmIdCheck);
                    }
                    log.debug(METHODNAME + "updateCheck: {}", updateCheck);

                    UpdateResponseResult result;
                    try
                    {
                        response = sendPreEvalRequest(updateCheck);
                        if (response.getStatus() != 200)
                        {
                            final String msg =
                                    String.format("preEvaluate call was unsuccessful: status=%d; body=%s", response.getStatus(),
                                            response.readEntity(String.class));
                            throw new PreEvaluateHTTPException(msg);
                        }
                        updateResponse = readEntity(response, UpdateResponse.class);
                        result = ConfigUtils.update(updateResponse, configurationService, environment, instanceId);
                        if (result.hasErrors())
                        {
                            log.warn("Sending preEvaluate failure notification due to errors processing one or more updates");
                            sendPreEvalFailedRequest(result).close();
                        }
                    }
                    catch (final PreEvaluateHTTPException e)
                    {
                        // Any non-200 responses should just get propagated up as an error. We do not want/need
                        // to send this to the /failed endpoint since these errors would indicate that the preEval service
                        // did not successfully process the request and therefore did not flag the updates as delivered.
                        throw e;
                    }
                    catch (final Exception e)
                    {
                        // Any other unexpected errors need to be reported to the failure endpoint so the preEval service
                        // can mark the updates as not delivered.
                        // Examples of unexpected errors include: read timeouts, malformed preEval response, and other unexpected
                        // exceptions while processing the updates received that aren't gracefully captured by ConfigUtils.
                        final int status = 500;
                        final String error = "Error processing preEvaluate request: " + ExceptionUtils.getStackTrace(e);
                        result = new UpdateResponseResult();
                        result.setEnvironment(environment);
                        result.setInstanceId(instanceId);
                        if (!updateCheck.getCdmIdChecks().isEmpty())
                            result.getCdms()
                                    .add(new CDMUpdateResult(updateCheck.getCdmIdChecks().getFirst().getCdmId(), status, error));
                        for (final KmIdCheck kmCheck : updateCheck.getKmIdChecks())
                            result.getKms().add(new KMUpdateResult(kmCheck.getKmId(), status, error));

                        log.warn("Sending preEvaluate failure notification due to unexpected error: {}", e.getMessage());
                        sendPreEvalFailedRequest(result).close();
                        throw new RuntimeException("Error processing preEvaluate request", e);
                    }
                }
                case EVALUATION_REQUEST ->
                {
                    updateCheck.setEvaluationRequest(evaluationRequest);
                    response = sendPreEvalRequest(updateCheck);
                    final EvaluationRequest evaluationRequestResponse = readEntity(response, EvaluationRequest.class);
                    evaluationRequest.getKmEvaluationRequest().clear();
                    evaluationRequest.getKmEvaluationRequest().addAll(evaluationRequestResponse.getKmEvaluationRequest());
                    evaluationRequest.getDataRequirementItemData().clear();
                    evaluationRequest.getDataRequirementItemData().addAll(evaluationRequestResponse.getDataRequirementItemData());
                }
                default -> throw new IllegalStateException("Unhandled hook type: " + preEvaluateHookType);
            }
        }
        finally
        {
            log.info(METHODNAME + "duration: {}ms", (System.nanoTime() - start) / 1000000);
        }
        return updateResponse;
    }

    private Response sendPreEvalRequest(final Object payload)
    {
        final WebTarget webTarget = getLazy(preEvaluateClient).target(preEvaluateHookUri);
        return doPreEvalRequest(webTarget, payload);
    }

    private Response sendPreEvalFailedRequest(final Object payload)
    {
        log.info("Failure notification being sent: {}", toJsonString(payload));
        final WebTarget webTarget = getLazy(preEvaluateClient).target(preEvaluateHookUri).path("failed");
        return doPreEvalRequest(webTarget, payload);
    }

    private Response doPreEvalRequest(final WebTarget target, final Object payload)
    {
        final String METHODNAME = "doPreEvalRequest";
        if (log.isTraceEnabled())
            log.trace(toJsonString(payload));

        if (log.isDebugEnabled())
            log.debug("{} Invoking PUT: uri={}; connectTimeout={}; readTimeout={}", METHODNAME, target.getUri(),
                    preEvalHookConnectTimeout, preEvalHookReadTimeout);
        final Builder builder = target.request(MediaType.APPLICATION_JSON);
        final StopWatch timer = new StopWatch();
        timer.start();
        final Response response = builder.put(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
        timer.stop();
        if (log.isDebugEnabled() || response.getStatus() != 200)
            log.info("{} PUT completed: status={}; duration={}", METHODNAME, response.getStatus(), timer.getTotalTimeMillis());

        return response;
    }

    private <T> T readEntity(final Response response, final Class<T> type)
    {
        final T entity = response.readEntity(type);
        if (log.isTraceEnabled())
            log.trace(toJsonString(entity));
        return entity;
    }

    private String toJsonString(final Object o)
    {
        try
        {
            return mapper.writeValueAsString(o);
        }
        catch (final Exception e)
        {
            log.warn("Error trying to write object as json", e);
        }
        return o == null ? "null" : o.toString();
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

    private <T> T getLazy(final LazyInitializer<T> lazy)
    {
        try
        {
            return lazy.get();
        }
        catch (final ConcurrentException e)
        {
            throw new RuntimeException(e);
        }
    }
}
