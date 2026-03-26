package org.opencds.dss.evaluate;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.IterativeEvaluationRequest;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.opencds.common.exceptions.RequiredDataNotProvidedException;
import org.opencds.common.exceptions.UnrecognizedScopedEntityException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.dss.evaluation.service.Evaluater;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated(forRemoval = true)
public class EvaluationFactory
{
    @Deprecated(forRemoval = true)
    public static IterativeEvaluater createIterativeEvaluater(final IterativeEvaluationRequest iterativeRequest)
            throws DSSRuntimeExceptionFault, RequiredDataNotProvidedExceptionFault
    {
        final IterativeEvaluater evaluater;
        final String adapterClassPathName = "notImplementedYet";
        try
        {
            final Class<?> c = Class.forName(adapterClassPathName);
            evaluater = (IterativeEvaluater) c.newInstance();
            return evaluater;
        }
        catch (final ClassNotFoundException e)
        {
            log.error(e.getMessage(), e);
            throw new RequiredDataNotProvidedExceptionFault(
                    "ClassNotFoundException: " + adapterClassPathName + " " + e.getMessage());
        }
        catch (final IllegalAccessException e)
        {
            log.error(e.getMessage(), e);
            throw new DSSRuntimeExceptionFault("IllegalAccessException: " + adapterClassPathName + " " + e.getMessage());
        }
        catch (final InstantiationException e)
        {
            log.error(e.getMessage(), e);
            throw new DSSRuntimeExceptionFault("InstantiationException: " + adapterClassPathName + " " + e.getMessage());
        }
    }

    @Deprecated(forRemoval = true)
    public Evaluater createEvaluater(final KnowledgeRepository knowledgeRepository, final EvaluationRequestKMItem dssRequestKMItem)
            throws DSSRuntimeExceptionFault, RequiredDataNotProvidedExceptionFault, UnrecognizedScopedEntityExceptionFault
    {
        String engineIdentifier = "";
        try
        {
            log.debug("starting EvaluationFactory");
            final ExecutionEngine engine = getExecutionEngine(knowledgeRepository, dssRequestKMItem.requestedKmId());
            engineIdentifier = engine.getIdentifier();
            log.debug("EvaluationFactory executionEngine Name: {}", engineIdentifier);

            return knowledgeRepository.executionEngineService().getExecutionEngineInstance(engine);
        }
        catch (final UnrecognizedScopedEntityException e)
        {
            log.error(e.getMessage(), e);
            throw new UnrecognizedScopedEntityExceptionFault(e.getMessage(), e);
        }
        catch (final RequiredDataNotProvidedException e)
        {
            log.error(e.getMessage(), e);
            throw new RequiredDataNotProvidedExceptionFault(e.getMessage(), e);
        }
        catch (final RuntimeException e)
        {
            log.error(e.getMessage(), e);
            throw new DSSRuntimeExceptionFault(
                    "Unrecognized Exception in EvaluationFactory: " + engineIdentifier + " " + e.getMessage());
        }
    }

    private ExecutionEngine getExecutionEngine(final KnowledgeRepository knowledgeRepository, final String requestedKmId)
            throws UnrecognizedScopedEntityException, RequiredDataNotProvidedException
    {
        final KnowledgeModule km = knowledgeRepository.knowledgeModuleService().find(requestedKmId);
        if (km == null)
            throw new UnrecognizedScopedEntityException("Unable to find KnowledgeModule for requested KM ID: " + requestedKmId);
        final ExecutionEngine engine = knowledgeRepository.executionEngineService().find(km.getExecutionEngine());
        if (engine == null || (engine.getIdentifier() == null) || engine.getIdentifier().isEmpty())
        {
            throw new RequiredDataNotProvidedException(
                    "ClassPathNameInvalidException trying to locate RequiredInferenceEngineAdapter by kmId: '" + requestedKmId
                            + "'; engine: " + engine + ".");
        }
        return engine;
    }
}
