package org.opencds.config.client.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

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
        target.path(path).request().put(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
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
