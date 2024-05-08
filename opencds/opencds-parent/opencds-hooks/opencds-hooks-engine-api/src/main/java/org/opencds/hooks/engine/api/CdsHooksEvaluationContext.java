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

package org.opencds.hooks.engine.api;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CdsHooksEvaluationContext {
    private final Date evalTime;
    private final URI serverBaseUri;
    private final String clientLanguage;
    private final String clientTimeZoneOffset;
    private final Map<String, Object> namedObjects;
    private final Map<Class<?>, List<?>> allFactLists;
    private final Map<String, Object> globals;

    private CdsHooksEvaluationContext(Date evalTime, URI serverBaseUri, String clientLanguage, String clientTimezoneOffset,
            Map<String, Object> namedObjects, Map<Class<?>, List<?>> allFactLists, Map<String, Object> globals) {
        this.evalTime = evalTime;
        this.serverBaseUri = serverBaseUri;
        this.clientLanguage = clientLanguage;
        this.clientTimeZoneOffset = clientTimezoneOffset;
        this.namedObjects = namedObjects;
        this.allFactLists = allFactLists;
        this.globals = Collections.unmodifiableMap(globals);
    }

    public static CdsHooksEvaluationContext create(Date evalTime, URI serverBaseUri, String clientLanguage, String clientTimezoneOffset,
            Map<String, Object> namedObjects, Map<Class<?>, List<?>> allFactLists, Map<String, Object> globals) {
        return new CdsHooksEvaluationContext(evalTime, serverBaseUri, clientLanguage, clientTimezoneOffset, namedObjects, allFactLists, globals);
    }

    public Date getEvalTime() {
        return evalTime;
    }
    
    public URI getServerBaseUri() {
        return serverBaseUri;
    }

    public String getClientLanguage() {
        return clientLanguage;
    }

    public String getClientTimeZoneOffset() {
        return clientTimeZoneOffset;
    }
    
    public Map<String, Object> getNamedObjects() {
        return namedObjects;
    }

    public Map<Class<?>, List<?>> getAllFactLists() {
        return allFactLists;
    }
    
    public Map<String, Object> getGlobals() {
        return globals;
    }
}
