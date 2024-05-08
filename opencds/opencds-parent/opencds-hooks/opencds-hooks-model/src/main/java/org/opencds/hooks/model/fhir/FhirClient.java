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

package org.opencds.hooks.model.fhir;

import java.net.URL;
import java.util.function.Supplier;

import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import org.opencds.hooks.model.request.FhirAuthorization;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;

/**
 * Only OAuth 2.0 is supported (Bearer token is required).
 *
 * @author phillip
 *
 */
public class FhirClient {
    private static final String BEARER = "Bearer";

    private final FhirContext fhirContext;

    public FhirClient(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
    }

    public IGenericClient get(URL fhirServer, FhirAuthorization fhirAuthorization) {
        return get(fhirServer, fhirAuthorization, -1);
    }

    public IGenericClient get(URL fhirServer, FhirAuthorization fhirAuthorization, int socketTimeout) {
        if (fhirServer == null) {
            return null;
        }
        IRestfulClientFactory clientFactory = fhirContext.getRestfulClientFactory();
        if (socketTimeout > -1) {
            clientFactory.setSocketTimeout(socketTimeout);
        }
        IGenericClient client = clientFactory.newGenericClient(fhirServer.toString());
        if (fhirAuthorization != null && BEARER.equalsIgnoreCase(fhirAuthorization.getTokenType())) {
            client.registerInterceptor(getOAuthInterceptor(fhirAuthorization.getAccessToken()));
        }
        return client;
    }

    public Supplier<IGenericClient> getSupplier(URL fhirServer, FhirAuthorization fhirAuthorization) {
        return () -> get(fhirServer, fhirAuthorization);
    }

    private BearerTokenAuthInterceptor getOAuthInterceptor(String token) {
        return new BearerTokenAuthInterceptor(token);
    }

}
