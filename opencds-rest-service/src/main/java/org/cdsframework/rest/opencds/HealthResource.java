package org.cdsframework.rest.opencds;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.rest.opencds.pojos.HealthCheck;

/**
 * Health REST Web Service
 *
 * @author sdn
 */
public class HealthResource {

    private static final Log log = LogFactory.getLog(HealthResource.class);

    /**
     * Creates a new instance of HealthResource
     *
     */
    public HealthResource() {
    }

    /**
     * Returns health status.
     *
     * @return
     * @throws JsonProcessingException
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("healthcheck")
    public Response healthcheck() throws JsonProcessingException {
        log.debug("/healthcheck called!");
        final ObjectMapper mapper = new ObjectMapper();
        final String response = mapper.writeValueAsString(new HealthCheck(200, "all's well"));
        final Response.ResponseBuilder responseBuilder = Response.ok(response).type(MediaType.APPLICATION_JSON);
        return responseBuilder.build();
    }

    /**
     * Returns quick up status.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("_k8s-health-check")
    public Response k8sHealthCheck() {
        log.debug("/_k8s-health-check called!");
        return Response.ok().build();
    }

}
