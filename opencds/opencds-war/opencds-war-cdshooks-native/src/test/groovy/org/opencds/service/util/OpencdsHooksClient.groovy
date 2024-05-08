package org.opencds.service.util

import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.client.WebTarget
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import org.apache.commons.lang3.tuple.Pair
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.JerseyClientBuilder

import static org.glassfish.jersey.client.ClientProperties.EXPECT_100_CONTINUE
import static org.glassfish.jersey.client.ClientProperties.REQUEST_ENTITY_PROCESSING
import static org.glassfish.jersey.client.RequestEntityProcessing.BUFFERED

class OpencdsHooksClient {
    private static final String CDS_SERVICES_URL = "http://localhost:38080/opencds-test/r4/hooks/cds-services"
    Client client

    OpencdsHooksClient() {
        final ClientBuilder builder = new JerseyClientBuilder()
        [
                (ClientProperties.FOLLOW_REDIRECTS)               : false,
                (ClientProperties.JSON_PROCESSING_FEATURE_DISABLE): true,
                (ClientProperties.CHUNKED_ENCODING_SIZE)          : 32768,
                (REQUEST_ENTITY_PROCESSING)                       : BUFFERED,
                (EXPECT_100_CONTINUE)                             : false
        ].forEach(builder::property)
        client = builder.build()
    }

    private WebTarget webTarget(String url) {
        return client.target(url)
    }

    String cdsServices() {
        WebTarget wt = webTarget(CDS_SERVICES_URL)
        return wt
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get()
                .readEntity(String.class)
    }

    Pair<MultivaluedMap<String, Object>, String> call(String id, String cdsRequest) {
        WebTarget wt = webTarget(CDS_SERVICES_URL)
        var response = wt
                .path(id)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(cdsRequest, MediaType.APPLICATION_JSON))
        return Pair.of(
                response.getHeaders(),
                response.readEntity(String.class)
        )
    }

    MultivaluedMap<String, Object> options() {
        return webTarget(CDS_SERVICES_URL)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .options()
                .getHeaders()
    }
}
