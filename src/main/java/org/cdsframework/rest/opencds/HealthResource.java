package org.cdsframework.rest.opencds;

import org.cdsframework.rest.opencds.pojos.HealthCheck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Health REST Web Service
 *
 * @author sdn
 */
@Slf4j
public class HealthResource
{
    /**
     * Returns health status.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("healthcheck")
    public Response healthcheck() throws JsonProcessingException
    {
        log.debug("/healthcheck called!");
        return Response.ok(new ObjectMapper().writeValueAsString(new HealthCheck(200, "all's well")))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Returns quick up status.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("_k8s-health-check")
    public Response k8sHealthCheck()
    {
        log.debug("/_k8s-health-check called!");
        return Response.ok().build();
    }
}
