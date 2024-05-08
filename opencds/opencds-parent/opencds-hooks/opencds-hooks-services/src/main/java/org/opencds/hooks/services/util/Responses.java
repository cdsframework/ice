/*
 * Copyright 2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.hooks.services.util;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

public class Responses {

    // 200
    public static Response ok() {
        return Response.ok().build();
    }

    public static Response ok(String content) {
    	return Response.ok(content).build();
    }

	public static Response ok(Map<String, String> headers) {
    	ResponseBuilder responseBuilder = Response.ok();
    	for (Map.Entry<String, String> entry : headers.entrySet()) {
    		responseBuilder.header(entry.getKey(), entry.getValue());
    	}
    	return responseBuilder.build();
	}

    public static Response ok(String content, Map<String, String> headers) {
    	ResponseBuilder responseBuilder = Response.ok(content);
    	for (Map.Entry<String, String> entry : headers.entrySet()) {
    		responseBuilder.header(entry.getKey(), entry.getValue());
    	}
    	return responseBuilder.build();
    }

    public static Response ok(byte[] content, Map<String, String> headers) {
    	ResponseBuilder responseBuilder = Response.ok(content);
    	for (Map.Entry<String, String> entry : headers.entrySet()) {
    		responseBuilder.header(entry.getKey(), entry.getValue());
    	}
    	return responseBuilder.build();
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
        return Response.status(Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build();
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
