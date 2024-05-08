/*
 * Copyright 2015-2020 OpenCDS.org
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

package org.opencds.config.api;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;

public class EvaluationContext {
    private final String focalPersonId;
    private final Date evalTime;
    private final URI serverBaseUri;
    private final String clientLanguage;
    private final String clientTimeZoneOffset;
    private final Set<String> assertions;
    private final Map<String, Object> namedObjects;
    private final Map<String, Object> globals;
    private final Map<Class<?>, List<?>> allFactLists;
    private final String primaryProcess;

    private EvaluationContext(String focalPersonId, Date evalTime, URI serverBaseUri, String clientLanguage,
            String clientTimeZoneOffset, Set<String> assertions, Map<String, Object> namedObjects,
            Map<String, Object> globals, Map<Class<?>, List<?>> allFactLists, String primaryProcess) {
        this.focalPersonId = focalPersonId;
        this.evalTime = evalTime;
        this.serverBaseUri = serverBaseUri;
        this.clientLanguage = clientLanguage;
        this.clientTimeZoneOffset = clientTimeZoneOffset;
        this.assertions = assertions;
        this.namedObjects = namedObjects;
        this.globals = globals;
        this.allFactLists = allFactLists;
        this.primaryProcess = primaryProcess;
    }

    public static EvaluationContext create(EvaluationRequestKMItem evaluationRequestKMItem, String primaryProcess) {
        EvaluationRequestDataItem evalRequestDataItem = evaluationRequestKMItem.getEvaluationRequestDataItem();
        // TODO: validate input
        return new EvaluationContext(evalRequestDataItem.getFocalPersonId(), evalRequestDataItem.getEvalTime(),
                evalRequestDataItem.getServerBaseUri(), evalRequestDataItem.getClientLanguage(),
                evalRequestDataItem.getClientTimeZoneOffset(), new HashSet<String>(), new HashMap<String, Object>(),
                new HashMap<String, Object>(), evaluationRequestKMItem.getAllFactLists(), primaryProcess);
    }

    public String getFocalPersonId() {
        return focalPersonId;
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

    public Set<String> getAssertions() {
        return assertions;
    }

    public Map<String, Object> getNamedObjects() {
        return namedObjects;
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public Map<Class<?>, List<?>> getAllFactLists() {
        return allFactLists;
    }

    public String getPrimaryProcess() {
        return primaryProcess;
    }

}
