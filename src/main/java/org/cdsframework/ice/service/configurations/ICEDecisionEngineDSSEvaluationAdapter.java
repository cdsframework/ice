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

import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ICEDecisionEngineDSSEvaluationAdapter()
        implements ExecutionEngineAdapter<List<Command<?>>, ExecutionResults, IceKnowledgePackage>
{
    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> execute(final IceKnowledgePackage knowledgePackage,
            final ExecutionEngineContext<List<Command<?>>, ExecutionResults> context) throws DSSRuntimeExceptionFault
    {
        final String _METHODNAME = "execute(): ";

        long t0 = 0L;
        if (log.isInfoEnabled())
            t0 = System.nanoTime();

        final KieBase kieBase = knowledgePackage.kieBase();
        final ExecutionResults results;
        try
        {
            final StatelessKieSession knowledgeSession = kieBase.newStatelessKieSession();
            if (log.isDebugEnabled())
                log.debug("KM (Drools) execution...");
            long d0 = 0L;
            if (log.isInfoEnabled())
                d0 = System.nanoTime();
            results = knowledgeSession.execute(CommandFactory.newBatchExecution(context.getInput()));
            /////// knowledgeSession.fireAllRules();

            if (log.isInfoEnabled())
                log.info(_METHODNAME + "Drools Execution Duration: {} ms", (System.nanoTime() - d0) / 1e6);
            if (log.isDebugEnabled())
                log.debug("KM (Drools) execution done.");
        }
        catch (final Exception e)
        {
            final String err = "OpenCDS call to Drools.execute failed with error: " + e.getMessage();
            log.error(err, e);
            throw new DSSRuntimeExceptionFault(err);
        }

        context.setResults(results);

        if (log.isDebugEnabled())
            log.debug("KMId: {} completed Drools inferencing engine", knowledgePackage.kmId());
        if (log.isInfoEnabled())
            log.info(_METHODNAME + "ICE Request Duration: {} ms", (System.nanoTime() - t0) / 1e6);

        return context;
    }
}
