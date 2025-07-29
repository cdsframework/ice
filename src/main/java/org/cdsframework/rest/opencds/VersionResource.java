package org.cdsframework.rest.opencds;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Health REST Web Service
 *
 * @author sdn
 */
@RequiredArgsConstructor
@Slf4j
@Path("version")
public class VersionResource
{
    private final Map<String, String> versionData;

    /**
     * Returns version details.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response version() throws JsonProcessingException
    {
        log.debug("/version called!");
        return Response.ok(new ObjectMapper().writeValueAsString(versionData)).type(MediaType.APPLICATION_JSON).build();
    }
}
