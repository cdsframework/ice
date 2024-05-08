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


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;

public class ICEDecisionEngineDSS7EvaluationAdapter implements ExecutionEngineAdapter<List<Command<?>>, ExecutionResults, IceKnowledgePackage> {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public ExecutionEngineContext<List<Command<?>>, ExecutionResults> execute(final IceKnowledgePackage knowledgePackage,
			final ExecutionEngineContext<List<Command<?>>, ExecutionResults> context) throws DSSRuntimeExceptionFault
	{
		String _METHODNAME = "execute(): ";

		long t0 = 0L;
		if (logger.isInfoEnabled()) {
			t0 = System.nanoTime();
		}

		/**
		 * Use Drools to process everything Added try/catch around stateless
		 * session. because Drools has an unhandled exception when a JBPM
		 * Process improperly re-declares a global that is constraining a
		 * gateway and the resultant global is null - des 20120727
		 ********************************************************************************
		 */
		final KieBase kieBase = knowledgePackage.kieBase();
		ExecutionResults results = null;
		StatelessKieSession knowledgeSession = null;
		try {
			knowledgeSession = kieBase.newStatelessKieSession();

			/*
			// Log events START
			knowledgeSession.addEventListener( new DefaultAgendaEventListener() {
				public void beforeMatchFired(BeforeMatchFiredEvent event) {
					super.beforeMatchFired(event);
					logger.info("Before Match Fired: " + event);
				}
			    public void afterMatchFired(AfterMatchFiredEvent event) {
			        super.afterMatchFired( event );
			        logger.info("After Match Fired: " + event);
			    }
			    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
					super.agendaGroupPushed( event );
			        logger.info("Agenda Group Pushed: " + event);
			    }
			    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
					super.agendaGroupPopped( event );
			        logger.info("Agenda Group Popped: " + event);
			    }
			    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
					super.beforeRuleFlowGroupActivated( event );
			       logger.info("RuleFlow Group Activated: " + event);
				}
			    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
					super.afterRuleFlowGroupActivated( event );
			       logger.info("RuleFlow Group Popped: " + event);
				}
			});
			// Log events END
			*/

			if (logger.isDebugEnabled()) {
				logger.debug("KM (Drools) execution...");
			}
			long d0 = 0L;
			if (logger.isInfoEnabled()) {
				d0 = System.nanoTime();
			}
			results = knowledgeSession.execute(CommandFactory.newBatchExecution(context.getInput()));
			/////// knowledgeSession.fireAllRules();

			if (logger.isInfoEnabled()) {
				logger.info(_METHODNAME + "Drools Execution Duration: " + (System.nanoTime() - d0) / 1e6 + " ms");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("KM (Drools) execution done.");
			}
		}
		catch (Exception e) {
			String err = "OpenCDS call to Drools.execute failed with error: " + e.getMessage();
			logger.error(err, e);
			throw new DSSRuntimeExceptionFault(err);
		}

		/**
		 ********************************************************************************
		 * END Drools
		 *
		 */

		context.setResults(results);

		if (logger.isDebugEnabled()) {
			logger.debug("KMId: " + knowledgePackage.kmId() + " completed Drools inferencing engine");
		}
		if (logger.isInfoEnabled()) {
			logger.info(_METHODNAME + "ICE Request Duration: " + (System.nanoTime() - t0) / 1e6 + " ms");
		}

		return context;
	}
}
