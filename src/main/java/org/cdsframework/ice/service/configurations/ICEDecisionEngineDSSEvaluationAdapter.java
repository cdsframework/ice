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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.ICEFactTypeFinding;
import org.cdsframework.ice.service.SeriesRules;
import org.cdsframework.ice.service.TargetDose;
import org.cdsframework.ice.service.TargetSeries;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.CommandFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ICEDecisionEngineDSSEvaluationAdapter
        implements ExecutionEngineAdapter<List<Command<?>>, ExecutionResults, IceKnowledgePackage>
{
    private static class DroolsAgendaEventLogger implements AgendaEventListener
    {
        private static final int MAX_LOG_LENGTH = 1024;

        private static String filterObjects(final List<Object> objects)
        {
            return objects.stream()
                    .map(ICEDecisionEngineDSSEvaluationAdapter::logObject)
                    .map("<%s>"::formatted)
                    .collect(Collectors.joining(" | ", "[", "]"));
        }

        private final Deque<String> focusQueue = new ArrayDeque<>();

        @Override
        public void matchCreated(final MatchCreatedEvent event)
        {
            droolsEventsLogger.debug("Match created: {}. Objects={}", event.getMatch().getRule().getName(),
                    StringUtils.truncate(filterObjects(event.getMatch().getObjects()), MAX_LOG_LENGTH));
        }

        @Override
        public void matchCancelled(final MatchCancelledEvent event)
        {
            String cause = "Unknown";
            try
            {
                final Object reason = event.getCause(); // Cause may be null or unsupported in older versions
                if (reason != null)
                    cause = reason.toString();
            }
            catch (final Exception ignored)
            {
                // Compatibility fallback
            }

            droolsEventsLogger.debug("Match cancelled: {}. Cause={}. Objects={}", event.getMatch().getRule().getName(), cause,
                    StringUtils.truncate(filterObjects(event.getMatch().getObjects()), MAX_LOG_LENGTH));
        }

        @Override
        public void beforeMatchFired(final BeforeMatchFiredEvent event)
        {
            droolsEventsLogger.info("Before match fired: {}. Objects={}", event.getMatch().getRule().getName(),
                    StringUtils.truncate(filterObjects(event.getMatch().getObjects()), MAX_LOG_LENGTH));
        }

        @Override
        public void afterMatchFired(final AfterMatchFiredEvent event)
        {
            droolsEventsLogger.info("After match fired: {}\n", event.getMatch().getRule().getName());
        }

        @Override
        public void agendaGroupPopped(final AgendaGroupPoppedEvent event)
        {
            final String popped = focusQueue.pop();
            droolsEventsLogger.info("Agenda group popped {}: queue={}", popped, focusQueue);
        }

        @Override
        public void agendaGroupPushed(final AgendaGroupPushedEvent event)
        {
            focusQueue.push(event.getAgendaGroup().getName());
            droolsEventsLogger.info("Agenda group pushed {}: queue={}", event.getAgendaGroup().getName(), focusQueue);
        }

        @Override
        public void beforeRuleFlowGroupActivated(final RuleFlowGroupActivatedEvent event)
        {
            droolsEventsLogger.info("Before rule flow group activated: {}", event.getRuleFlowGroup().getName());
        }

        @Override
        public void afterRuleFlowGroupActivated(final RuleFlowGroupActivatedEvent event)
        {
            droolsEventsLogger.info("After rule flow group activated: {}", event.getRuleFlowGroup().getName());
        }

        @Override
        public void beforeRuleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event)
        {
            droolsEventsLogger.info("Before rule flow group deactivated: {}", event.getRuleFlowGroup().getName());
        }

        @Override
        public void afterRuleFlowGroupDeactivated(final RuleFlowGroupDeactivatedEvent event)
        {
            droolsEventsLogger.info("After rule flow group deactivated: {}", event.getRuleFlowGroup().getName());
        }
    }

    private static class DroolsRuleRuntimeEventLogger implements RuleRuntimeEventListener
    {
        private static final int MAX_LOG_LENGTH = 1024;

        private static String log(final Object object)
        {
            return object instanceof ICEFactTypeFinding ? logObject(object) : object.toString();
        }

        @Override
        public void objectInserted(final ObjectInsertedEvent event)
        {
            droolsEventsLogger.info("Object inserted: {} - {}", event.getObject().getClass().getSimpleName(),
                    StringUtils.truncate(log(event.getObject()), MAX_LOG_LENGTH));
        }

        @Override
        public void objectUpdated(final ObjectUpdatedEvent event)
        {
            droolsEventsLogger.info("Object updated: {} - {}", event.getObject().getClass().getSimpleName(),
                    StringUtils.truncate(log(event.getObject()), MAX_LOG_LENGTH));
        }

        @Override
        public void objectDeleted(final ObjectDeletedEvent event)
        {
            droolsEventsLogger.info("Object deleted: {} - {}", event.getOldObject().getClass().getSimpleName(),
                    StringUtils.truncate(log(event.getOldObject()), MAX_LOG_LENGTH));
        }
    }

    private static final Logger droolsEventsLogger = LoggerFactory.getLogger("drools-events");
    @Setter
    private static IceProperties iceProperties;

    private static String logObject(final Object object)
    {
        return switch (object)
        {
            case final TargetSeries series -> "TargetSeries=%s".formatted(series.getSeriesName());
            case final TargetDose dose ->
                    "TargetDose uniqueId=%s, Vaccine=%s, TargetSeries=%s, Status=%s".formatted(dose.getUniqueId(),
                            dose.getAdministeredVaccine().getCdsConceptName(), dose.getTargetSeries().getSeriesName(),
                            dose.getStatus());
            case final SeriesRules rules -> "SeriesRules=%s".formatted(rules.getSeriesName());
            case final ICEFactTypeFinding fact -> "Fact=%s, %s, %s, %s".formatted(fact.getIceResultFinding(),
                    Optional.ofNullable(fact.getAssociatedTargetDose())
                            .map(ICEDecisionEngineDSSEvaluationAdapter::logObject)
                            .orElse(null), Optional.ofNullable(fact.getAssociatedTargetSeries())
                            .map(ICEDecisionEngineDSSEvaluationAdapter::logObject)
                            .orElse(null), Optional.ofNullable(fact.getAssociatedSeriesRules())
                            .map(ICEDecisionEngineDSSEvaluationAdapter::logObject)
                            .orElse(null));
            default -> object.toString();
        };
    }

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
        try (final KieSession knowledgeSession = kieBase.newKieSession())
        {
            if (log.isDebugEnabled())
                log.debug("KM (Drools) execution...");
            long d0 = 0L;

            if (iceProperties.getEnableDroolsEventLogging())
            {
                knowledgeSession.addEventListener(new DroolsAgendaEventLogger());
                knowledgeSession.addEventListener(new DroolsRuleRuntimeEventLogger());
            }

            if (log.isInfoEnabled())
                d0 = System.nanoTime();
            results = knowledgeSession.execute(CommandFactory.newBatchExecution(context.getInput()));
            // knowledgeSession.fireAllRules();

            if (log.isInfoEnabled())
                log.debug(_METHODNAME + "Drools Execution Duration: {} ms", (System.nanoTime() - d0) / 1e6);
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
            log.debug(_METHODNAME + "ICE Request Duration: {} ms", (System.nanoTime() - t0) / 1e6);

        return context;
    }
}
