package org.cdsframework.rest.opencds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTimeResponse;
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
    @Path("test")
    public String test() {
        return "hello world!";
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
    @Consumes({MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
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

        EvaluateAtSpecifiedTime evaluateAtSpecifiedTime = MarshalUtils.unmarshal(
                new ByteArrayInputStream(evaluateAtSpecifiedTimeString.getBytes()),
                EvaluateAtSpecifiedTime.class);

        try {

            EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTimeResponse = evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime);
            EvaluationResponse evaluationResponse = evaluateAtSpecifiedTimeResponse.getEvaluationResponse();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            MarshalUtils.marshal(evaluationResponse, stream);
            stream.toByteArray();
            Response.ResponseBuilder responseBuilder = Response.ok(new String(stream.toByteArray())).type(MediaType.APPLICATION_XML);
            return responseBuilder.build();
        } finally {

        }
//        } catch (DSSRuntimeExceptionFault | EvaluationExceptionFault | InvalidDriDataFormatExceptionFault | InvalidTimeZoneOffsetExceptionFault | RequiredDataNotProvidedExceptionFault | UnrecognizedLanguageExceptionFault | UnrecognizedScopedEntityExceptionFault | UnsupportedLanguageExceptionFault e) {
//            System.out.println(METHODNAME + "e.getMessage()=" + e.getMessage());
//            Response.ResponseBuilder responseBuilder = Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
//            return responseBuilder.build();
//        }
    }
}
