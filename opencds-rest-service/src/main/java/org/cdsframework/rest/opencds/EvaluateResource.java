package org.cdsframework.rest.opencds;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateResponse;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.opencds.dss.evaluate.EvaluationService;

/**
 * REST Web Service
 *
 * @author sdn
 */
@Path("resources")
public class EvaluateResource {

    private static final Log log = LogFactory.getLog(EvaluateResource.class);

    private final EvaluationService evaluationService;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of EvaluateResource
     *
     * @param evaluationService
     */
    public EvaluateResource(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
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
     * @param header
     * @param response
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
    public Response evaluate(
            String evaluateString,
            @Context HttpHeaders header,
            @Context HttpServletResponse response)
            throws ParseException,
            UnsupportedEncodingException,
            IOException,
            InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault,
            JAXBException,
            TransformerException {

        final String METHODNAME = "evaluate ";

        ObjectMapper mapper = new ObjectMapper();

        Evaluate evaluate;
        MediaType mediaType = header.getMediaType();

        log.debug(METHODNAME + "mediaType=" + mediaType);
        log.debug(METHODNAME + "mediaType.toString()=" + mediaType.toString());
        log.debug(METHODNAME + "MediaType.APPLICATION_JSON=" + MediaType.APPLICATION_JSON);
        log.debug(METHODNAME + "mediaType.toString().equals(MediaType.APPLICATION_JSON)=" + mediaType.toString().equals(MediaType.APPLICATION_JSON));
        log.debug(METHODNAME + "mediaType.toString().equals(MediaType.APPLICATION_XML)=" + mediaType.toString().equals(MediaType.APPLICATION_XML));

        if (mediaType.toString().equals(MediaType.APPLICATION_JSON)) {
            evaluate = mapper.readValue(evaluateString, Evaluate.class);
        } else if (mediaType.toString().equals(MediaType.APPLICATION_XML)) {
            evaluate = MarshalUtils.unmarshal(
                    new ByteArrayInputStream(evaluateString.getBytes()),
                    Evaluate.class);
        } else {
            throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }

        try {

            Response.ResponseBuilder responseBuilder;
            EvaluateResponse evaluateResponse = evaluationService.evaluate(evaluate);
            EvaluationResponse evaluationResponse = evaluateResponse.getEvaluationResponse();

            List<MediaType> acceptableMediaTypes = header.getAcceptableMediaTypes();
            log.debug(METHODNAME + "acceptableMediaTypes=" + acceptableMediaTypes);

            if (acceptableMediaTypes.contains(MediaType.APPLICATION_JSON_TYPE)) {
                String data = mapper.writeValueAsString(evaluationResponse);
                responseBuilder = Response.ok(data).type(MediaType.APPLICATION_JSON);
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                MarshalUtils.marshal(evaluationResponse, stream);
                stream.toByteArray();
                responseBuilder = Response.ok(new String(stream.toByteArray())).type(MediaType.APPLICATION_XML);
            }
            return responseBuilder.build();
        } finally {

        }
    }
    
    /**
     * Retrieves representation of an instance of
     * org.cdsframework.rest.opencds.EvaluateResource
     *
     * @param evaluateAtSpecifiedTimeString
     * @param header
     * @param response
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
     * @throws javax.xml.bind.JAXBException
     * @throws javax.xml.transform.TransformerException
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Path("evaluateAtSpecifiedTime")
    public Response evaluateAtSpecifiedTime(
            String evaluateAtSpecifiedTimeString,
            @Context HttpHeaders header,
            @Context HttpServletResponse response)
            throws ParseException,
            UnsupportedEncodingException,
            IOException,
            InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault,
            JAXBException,
            TransformerException {

        final String METHODNAME = "evaluateAtSpecifiedTime ";

        ObjectMapper mapper = new ObjectMapper();

        EvaluateAtSpecifiedTime evaluateAtSpecifiedTime;
        MediaType mediaType = header.getMediaType();

        log.debug(METHODNAME + "mediaType=" + mediaType);
        log.debug(METHODNAME + "mediaType.toString()=" + mediaType.toString());
        log.debug(METHODNAME + "MediaType.APPLICATION_JSON=" + MediaType.APPLICATION_JSON);
        log.debug(METHODNAME + "mediaType.toString().equals(MediaType.APPLICATION_JSON)=" + mediaType.toString().equals(MediaType.APPLICATION_JSON));
        log.debug(METHODNAME + "mediaType.toString().equals(MediaType.APPLICATION_XML)=" + mediaType.toString().equals(MediaType.APPLICATION_XML));

        if (mediaType.toString().equals(MediaType.APPLICATION_JSON)) {
            evaluateAtSpecifiedTime = mapper.readValue(evaluateAtSpecifiedTimeString, EvaluateAtSpecifiedTime.class);
        } else if (mediaType.toString().equals(MediaType.APPLICATION_XML)) {
            evaluateAtSpecifiedTime = MarshalUtils.unmarshal(
                    new ByteArrayInputStream(evaluateAtSpecifiedTimeString.getBytes()),
                    EvaluateAtSpecifiedTime.class);
        } else {
            throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }

        try {

            Response.ResponseBuilder responseBuilder;
            EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTimeResponse = evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime);
            EvaluationResponse evaluationResponse = evaluateAtSpecifiedTimeResponse.getEvaluationResponse();

            List<MediaType> acceptableMediaTypes = header.getAcceptableMediaTypes();
            log.debug(METHODNAME + "acceptableMediaTypes=" + acceptableMediaTypes);

            if (acceptableMediaTypes.contains(MediaType.APPLICATION_JSON_TYPE)) {
                String data = mapper.writeValueAsString(evaluationResponse);
                responseBuilder = Response.ok(data).type(MediaType.APPLICATION_JSON);
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                MarshalUtils.marshal(evaluationResponse, stream);
                stream.toByteArray();
                responseBuilder = Response.ok(new String(stream.toByteArray())).type(MediaType.APPLICATION_XML);
            }
            return responseBuilder.build();
        } finally {

        }
    }
}
