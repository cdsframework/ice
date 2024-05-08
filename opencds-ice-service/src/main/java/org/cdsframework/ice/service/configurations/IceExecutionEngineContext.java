/**
 * Copyright (C) 2024 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service.configurations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.ice.util.ICEVersionUtil;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.ExecutionEngineContext;

public class IceExecutionEngineContext implements ExecutionEngineContext<List<Command<?>>, ExecutionResults> {
    private static final Logger logger = LogManager.getLogger();

    private static final String EVAL_TIME = "evalTime";
    private static final String CLIENT_LANG = "clientLanguage";
    private static final String CLIENT_TZ_OFFSET = "clientTimeZoneOffset";
    private static final String FOCAL_PERSON_ID = "focalPersonId";
    private static final String ASSERTIONS = "assertions";
    private static final String NAMED_OBJECTS = "namedObjects";
    private static final String ICE_VERSION = "iceVersion";
    private static final Set<String> ALL_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID, ASSERTIONS, NAMED_OBJECTS));
    private static final Set<String> FILTERED_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID));

    private EvaluationContext evaluationContext;
    private Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();

    @Override
    public List<Command<?>> getInput() {
        String _METHODNAME = "getInput(): ";

        List<Command<?>> cmds = Collections.synchronizedList(new ArrayList<>());
        Date evalTime = evaluationContext.getEvalTime();
        // Date evalTime = new Date();
        String clientLanguage = evaluationContext.getClientLanguage();
        String clientTimeZoneOffset = evaluationContext.getClientTimeZoneOffset();
        String iceVersion = ICEVersionUtil.getIceVersion();

        /**
         * Load fact map from specific externalFactModels, as specified in externalFactModel SSId...
         *
         * Every separately identified SSId, by definition, specifies separate input and output mappings. Input mappings are used here, and then
         * output mappings are used following the session.execute.
         */

        Map<Class<?>, List<?>> allFactLists = evaluationContext.getAllFactLists();

        /**
         * Get the KMs and Load them into a stateless session. Currently, assumption is made that each requested knowledge module will be run separately
         * (i.e., as part of a separate distinct knowledge base)
         */

        /**
         * to create a new Drools Working Memory log for in depth Drools debugging - Use either the InMemorylog to record logs on all
         * input, or use the Filelog for debugging of one input at a time in Drools and JBPM
         */

        /**
         * Load the Globals: evalTime, language, timezoneOffset, focalPersonId,
         * assertions, namedObjects
         */
        cmds.add(CommandFactory.newSetGlobal(EVAL_TIME, evalTime));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_LANG, clientLanguage));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_TZ_OFFSET, clientTimeZoneOffset));
        cmds.add(CommandFactory.newSetGlobal(ICE_VERSION, iceVersion));

        // following global used to store flags for inter-task communication in a JBPM Process
        cmds.add(CommandFactory.newSetGlobal(ASSERTIONS, evaluationContext.getAssertions()));

        // following global used to return facts added by rules, such as new
        // observationResults
        cmds.add(CommandFactory.newSetGlobal(NAMED_OBJECTS, evaluationContext.getNamedObjects()));

        /*
         * Add globals provided by plugin; don't allow any global that have the same name as our globals.
         */
        for (Map.Entry<String, Object> global : evaluationContext.getGlobals().entrySet()) {
            if (ALL_GLOBALS.contains(global.getKey())) {
                logger.error("Global from Plugin is not allowed to overwrite expected global; choose a different name for the global: name= " + global.getKey());
            }
            else {
                logger.debug("Adding global from plugin: name= " + global.getKey());
                cmds.add(CommandFactory.newSetGlobal(global.getKey(), global.getValue()));
            }
        }

        /**
         * Load the FactLists into Commands: Only ones that are not empty...
         */
        for (Map.Entry<Class<?>, List<?>> factListEntry : allFactLists.entrySet()) {
            if (factListEntry.getValue().size() > 0) {
                // TODO: Create Debug statements to see facts coming into OpenCDS
                cmds.add(CommandFactory.newInsertElements((List<?>) factListEntry.getValue(), factListEntry.getKey().getSimpleName(), true, null));
            }
        }

        /**
         * If this is a PKG (for package with process, initiate the configured
         * Primary Process for JBPM.
         */
        if (evaluationContext.getPrimaryProcess() != null && !evaluationContext.getPrimaryProcess().isEmpty()) {
            cmds.add(CommandFactory.newStartProcess(evaluationContext.getPrimaryProcess()));
            if (logger.isDebugEnabled()) {
                logger.debug("knowledgeBase Primary Process: " + evaluationContext.getPrimaryProcess());
            }
        }

        return cmds;
    }

    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> setResults(final ExecutionResults results) {
        resultFactLists = new ConcurrentHashMap<>();

        // update original entries from allFactLists to capture any new or updated elements
        // ** need to look for every possible fact list, because rules may have created new ones...
        // NOTE that results contains the original objects passed in via CMD
        // structure, with any changes introduced by rules.

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
        resultFactLists.put(ASSERTIONS, Arrays.asList(evaluationContext.getNamedObjects()));
        resultFactLists.put(NAMED_OBJECTS, Arrays.asList(evaluationContext.getNamedObjects()));

        return this;
    }

    @Override
    public Map<String, List<?>> getResults() {
        return resultFactLists;
    }

    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> setEvaluationContext(final EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
        return this;
    }
}
