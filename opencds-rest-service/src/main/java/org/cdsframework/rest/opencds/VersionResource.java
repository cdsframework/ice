package org.cdsframework.rest.opencds;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Health REST Web Service
 *
 * @author sdn
 */
@Path("version")
public class VersionResource
{
    private static final Log log = LogFactory.getLog(VersionResource.class);
    private final Map<String, String> versionData;

    /**
     * Creates a new instance of HealthResource
     *
     * @param versionData
     */
    public VersionResource(Map<String, String> versionData)
    {
        this.versionData = versionData;
    }

    /**
     * Returns version details.
     *
     * @return
     * @throws JsonProcessingException
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response version() throws JsonProcessingException
    {
        log.debug("/version called!");
        final ObjectMapper mapper = new ObjectMapper();
        final String response = mapper.writeValueAsString(versionData);
        final Response.ResponseBuilder responseBuilder = Response.ok(response).type(MediaType.APPLICATION_JSON);
        return responseBuilder.build();
    }
}
