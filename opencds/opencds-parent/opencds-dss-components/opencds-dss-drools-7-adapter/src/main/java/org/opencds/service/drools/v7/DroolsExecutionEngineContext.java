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

package org.opencds.service.drools.v7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.ExecutionEngineContext;

public class DroolsExecutionEngineContext implements ExecutionEngineContext<List<Command<?>>, ExecutionResults> {
    private static final Log log = LogFactory.getLog(DroolsExecutionEngineContext.class);
    private static final String EVAL_TIME = "evalTime";
    private static final String CLIENT_LANG = "clientLanguage";
    private static final String CLIENT_TZ_OFFSET = "clientTimeZoneOffset";
    private static final String FOCAL_PERSON_ID = "focalPersonId";
    private static final String ASSERTIONS = "assertions";
    private static final String NAMED_OBJECTS = "namedObjects";
    private static final Set<String> ALL_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG,
            CLIENT_TZ_OFFSET, FOCAL_PERSON_ID, ASSERTIONS, NAMED_OBJECTS));
    private static final Set<String> FILTERED_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG,
            CLIENT_TZ_OFFSET, FOCAL_PERSON_ID));

    private EvaluationContext evaluationContext;
    private Map<String, List<?>> resultFactLists;

    // TODO: Make final
    // private Date evalTime;
    // private String clientLanguage;
    // private String clientTimeZoneOffset;
    // private String focalPersonId;
    // private Set<String> assertions;
    // private Map<String, Object> namedObjects;
    // private Map<String, Object> globals;
    // private Map<Class<?>, List<?>> allFactLists;
    // private String primaryProcess;

    @Override
    public List<Command<?>> getInput() {
        List<Command<?>> cmds = Collections.synchronizedList(new ArrayList<Command<?>>());
        if (evaluationContext.getFocalPersonId() != null) {
        	cmds.add(CommandFactory.newSetGlobal(FOCAL_PERSON_ID, evaluationContext.getFocalPersonId()));
        }
        cmds.add(CommandFactory.newSetGlobal(EVAL_TIME, evaluationContext.getEvalTime()));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_LANG, evaluationContext.getClientLanguage()));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_TZ_OFFSET, evaluationContext.getClientTimeZoneOffset()));
        cmds.add(CommandFactory.newSetGlobal(ASSERTIONS, evaluationContext.getAssertions()));
        cmds.add(CommandFactory.newSetGlobal(NAMED_OBJECTS, evaluationContext.getNamedObjects()));
        /*
         * Add globals provided by plugin; don't allow any global that have the
         * same name as our globals.
         */
        Map<String, Object> globals = evaluationContext.getGlobals();
        if (globals != null) {
            for (Map.Entry<String, Object> global : globals.entrySet()) {
                if (ALL_GLOBALS.contains(global.getKey())) {
                    log.error("Global from Plugin is not allowed to overwrite expected global; choose a different name for the global: name= "
                            + global.getKey());
                } else {
                    log.info("Adding global from plugin: name= " + global.getKey());
                    cmds.add(CommandFactory.newSetGlobal(global.getKey(), global.getValue()));
                }
            }
        }

        /**
         * Load the FactLists into Commands: Only ones that are not empty...
         */
        Map<Class<?>, List<?>> allFactLists = evaluationContext.getAllFactLists();
        if (allFactLists != null) {
            for (Map.Entry<Class<?>, List<?>> factListEntry : allFactLists.entrySet()) {
                if (factListEntry.getValue().size() > 0) {
                    final String className = factListEntry.getKey().getSimpleName();
                	if ("FocalPersonId".equals(className) && evaluationContext.getFocalPersonId() == null) {
                		cmds.add(CommandFactory.newSetGlobal(FOCAL_PERSON_ID, factListEntry.getValue().get(0).toString()));
                	}
                	cmds.add(CommandFactory.newInsertElements((List<?>) factListEntry.getValue(), className, true, null));
                }
            }
        }

        /**
         * If this is a PKG (for package with process, initiate the configured
         * Primary Process for JBPM.
         */
        String primaryProcess = evaluationContext.getPrimaryProcess();
        if (primaryProcess != null && !primaryProcess.isEmpty()) {
            cmds.add(CommandFactory.newStartProcess(primaryProcess));
            // log.debug("II: " + interactionId + " KMId: " + requestedKmId +
            // " knowledgeBase Primary Process: "
            // + knowledgeModule.getPrimaryProcess());
        }

        return cmds;
    }

    /*
     * We create the results eagerly--there is a bug (feature?) somewhere that
     * yields multiple sets of results when ExecutionResults is read more than
     * once.
     *
     * @see
     * org.opencds.config.api.ExecutionEngineContext#setResults(java.lang.Object
     * )
     */
    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> setResults(ExecutionResults results) {
        resultFactLists = new ConcurrentHashMap<>();

        // update original entries from allFactLists to capture any new or
        // updated elements
        // ** need to look for every possible fact list, because rules may have
        // created new ones...
        // NOTE that results contains the original objects passed in via CMD
        // structure, with any
        // changes introduced by rules.

        Collection<String> allResultNames = results.getIdentifiers();
        // includes concepts but not globals?
        for (String oneName : allResultNames) {
            if (!FILTERED_GLOBALS.contains(oneName)) {
                // ignore these submitted globals, they should not have been
                // changed by rules, and look at everything else

                resultFactLists.put(oneName, (List<?>) results.getValue(oneName));
            }
        }
        // assertions and namedObjects are no longer being passed in the results
        // from drools.
        // TODO: FIXME more properly.
        resultFactLists.put(ASSERTIONS, Arrays.asList(evaluationContext.getNamedObjects()));
        resultFactLists.put(NAMED_OBJECTS, Arrays.asList(evaluationContext.getNamedObjects()));
        return this;
    }

    @Override
    public Map<String, List<?>> getResults() {
        return resultFactLists;
    }

    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
        return this;
    }

}
