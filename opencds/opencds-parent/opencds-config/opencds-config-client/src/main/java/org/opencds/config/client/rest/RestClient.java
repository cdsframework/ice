/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.config.client.rest;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Close the input stream.
 *
 * @author phillip
 *
 */
public class RestClient {
    private final WebTarget target;

    public RestClient(WebTarget target, String username, String password) {
        this.target = target;
        HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(username, password);
        target.register(authFeature);
    }

    public <T> T get(String path, Class<T> clazz) {
        return target.path(path).request(MediaType.APPLICATION_XML).get(clazz);
    }

    public <T> void post(String path, T data) {
        Response response = target.path(path).request().post(Entity.entity(data, MediaType.APPLICATION_XML));
        handleError(response);
    }

    public <T> void put(String path, T data) {
        Response response = target.path(path).request().put(Entity.entity(data, MediaType.APPLICATION_XML));
        handleError(response);
    }

    public void delete(String path) {
        Response response = target.path(path).request().delete();
        handleError(response);
    }

    public <T> T getBinary(String path, Class<T> clazz) {
        return target.path(path).request(MediaType.APPLICATION_OCTET_STREAM).get(clazz);
    }

    public <T> void putBinary(String path, T data) {
        Response response = target.path(path).request().put(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
        System.err.println(response);
    }

    private boolean isErrorResponse(int status) {
        return status == 400 || status == 404 || status == 409 || status == 500;
    }

    private void handleError(Response response) {
        System.err.println(response);
        if (!isErrorResponse(response.getStatus())) {
            return;
        }
        String message = "";
        try {
            InputStream in = (InputStream) response.getEntity();
            message = new BufferedReader(new InputStreamReader(in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String exceptionMessage = "HTTP/1.1 " + response.getStatus() + " " + response.getStatusInfo() + " : " + message;
        if (response.getStatus() == 400) {
            throw new BadRequestException(exceptionMessage);
        } else if (response.getStatus() == 404) {
            throw new NotFoundException(exceptionMessage);
        } else if (response.getStatus() == 409) {
            throw new ClientErrorException(exceptionMessage, 409);
        } else if (response.getStatus() == 500) {
            throw new InternalServerErrorException(exceptionMessage);
        }
    }
}
