package org.opencds.config.service.rest.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

public class Responses {

    // 200
    public static Response ok() {
        return Response.ok().build();
    }

    // 201
    public static Response created(UriInfo uriInfo) {
        return Response.created(uriInfo.getAbsolutePath()).build();
    }
    
    // 201
    public static Response created(UriInfo uriInfo, String uriSegment) {
        return Response.created(uriInfo.getAbsolutePathBuilder().path(uriSegment).build()).build();
    }
    
    // 204
    public static Response noContent() {
        return Response.noContent().build();
    }
    
    // 400
    public static Response badRequest(String message) {
        return Response.status(Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
    }

    // 404
    public static Response notFound(String message) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
    
    // 409
    public static Response conflict(String message) {
        return Response.status(Status.CONFLICT).entity(message).build();
    }

    // 500
    public static Response internalServerError(String message) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build();
    }

}
