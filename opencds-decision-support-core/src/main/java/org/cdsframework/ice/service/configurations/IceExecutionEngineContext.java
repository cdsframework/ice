/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.ExecutionEngineContext;
import org.springframework.util.ObjectUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IceExecutionEngineContext implements ExecutionEngineContext<List<Command<?>>, ExecutionResults>
{
    private static final String EVAL_TIME = "evalTime";
    private static final String CLIENT_LANG = "clientLanguage";
    private static final String CLIENT_TZ_OFFSET = "clientTimeZoneOffset";
    private static final String FOCAL_PERSON_ID = "focalPersonId";
    private static final String ASSERTIONS = "assertions";
    private static final String NAMED_OBJECTS = "namedObjects";
    private static final String ICE_VERSION = "iceVersion";
    private static final Set<String> ALL_GLOBALS =
            Set.of(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID, ASSERTIONS, NAMED_OBJECTS);
    private static final Set<String> FILTERED_GLOBALS = Set.of(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID);
    @Setter
    private static String iceVersion;
    private EvaluationContext evaluationContext;
    private Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();

    @Override
    public List<Command<?>> getInput()
    {
        final List<Command<?>> cmds = Collections.synchronizedList(new ArrayList<>());
        final Date evalTime = evaluationContext.evalTime();
        // Date evalTime = new Date();
        final String clientLanguage = evaluationContext.clientLanguage();
        final String clientTimeZoneOffset = evaluationContext.clientTimeZoneOffset();

        final Map<Class<?>, List<?>> allFactLists = evaluationContext.allFactLists();

        cmds.add(CommandFactory.newSetGlobal(EVAL_TIME, evalTime));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_LANG, clientLanguage));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_TZ_OFFSET, clientTimeZoneOffset));
        cmds.add(CommandFactory.newSetGlobal(ICE_VERSION, iceVersion));

        // following global used to store flags for inter-task communication in a JBPM Process
        cmds.add(CommandFactory.newSetGlobal(ASSERTIONS, evaluationContext.assertions()));

        // following global used to return facts added by rules, such as new
        // observationResults
        cmds.add(CommandFactory.newSetGlobal(NAMED_OBJECTS, evaluationContext.namedObjects()));

        /*
         * Add globals provided by plugin; don't allow any global that have the same name as our globals.
         */
        for (final Map.Entry<String, Object> global : evaluationContext.globals().entrySet())
        {
            if (ALL_GLOBALS.contains(global.getKey()))
            {
                log.error(
                        "Global from Plugin is not allowed to overwrite expected global; choose a different name for the global: name= {}",
                        global.getKey());
            }
            else
            {
                log.debug("Adding global from plugin: name= {}", global.getKey());
                cmds.add(CommandFactory.newSetGlobal(global.getKey(), global.getValue()));
            }
        }

        for (final Map.Entry<Class<?>, List<?>> factListEntry : allFactLists.entrySet())
        {
            if (!factListEntry.getValue().isEmpty())
            {
                // TODO: Create Debug statements to see facts coming into OpenCDS
                cmds.add(CommandFactory.newInsertElements(factListEntry.getValue(), factListEntry.getKey().getSimpleName(), true,
                        null));
            }
        }

        if (!ObjectUtils.isEmpty(evaluationContext.primaryProcess()))
        {
            cmds.add(CommandFactory.newStartProcess(evaluationContext.primaryProcess()));
            if (log.isDebugEnabled())
                log.debug("knowledgeBase Primary Process: {}", evaluationContext.primaryProcess());
        }

        return cmds;
    }

    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> setResults(final ExecutionResults results)
    {
        final Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();

        // update original entries from allFactLists to capture any new or updated elements
        // ** need to look for every possible fact list, because rules may have created new ones...
        // NOTE that results contains the original objects passed in via CMD
        // structure, with any changes introduced by rules.

        // includes concepts but not globals?
        for (final String oneName : results.getIdentifiers())
        {
            if (!FILTERED_GLOBALS.contains(oneName))
            {
                // ignore these submitted globals, they should not have been
                // changed by rules, and look at everything else

                resultFactLists.put(oneName, (List<?>) results.getValue(oneName));
            }
        }

        // assertions and namedObjects are no longer being passed in the results
        // from drools.
        resultFactLists.put(ASSERTIONS, List.of(evaluationContext.namedObjects()));
        resultFactLists.put(NAMED_OBJECTS, List.of(evaluationContext.namedObjects()));

        this.resultFactLists = resultFactLists;

        return this;
    }

    @Override
    public Map<String, List<?>> getResults()
    {
        return resultFactLists;
    }

    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> setEvaluationContext(
            final EvaluationContext evaluationContext)
    {
        this.evaluationContext = evaluationContext;

        return this;
    }
}
