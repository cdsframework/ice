/**
 * Copyright 2011 - 2013 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.dss.evaluate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationRequest;
import org.opencds.common.exceptions.RequiredDataNotProvidedException;
import org.opencds.common.exceptions.UnrecognizedScopedEntityException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;

/**
 * 
 * @author Andrew Iskander, mod by David Shields
 * 
 * @version 2.0
 */
public class EvaluationFactory {
    private static Log log = LogFactory.getLog(EvaluationFactory.class);

    /**
     * 
     * @param knowledgeRepository
     * @param oneRequest
     * @return evaluater
     * @throws DSSRuntimeExceptionFault
     * @throws RequiredDataNotProvidedExceptionFault
     * @throws UnrecognizedScopedEntityExceptionFault
     */
    public Evaluater createEvaluater(KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem dssRequestKMItem)
            throws DSSRuntimeExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnrecognizedScopedEntityExceptionFault {
        String engineIdentifier = "";
        try {
            log.debug("starting EvaluationFactory");
            ExecutionEngine engine = getExecutionEngine(knowledgeRepository, dssRequestKMItem.getRequestedKmId());
            engineIdentifier = engine.getIdentifier();
            log.debug("EvaluationFactory executionEngine Name: " + engineIdentifier);

            return knowledgeRepository.getExecutionEngineService().getExecutionEngineInstance(engine);
        } catch (UnrecognizedScopedEntityException e) {
            e.printStackTrace();
            throw new UnrecognizedScopedEntityExceptionFault(e.getMessage(), e);
        } catch (RequiredDataNotProvidedException e) {
            e.printStackTrace();
            throw new RequiredDataNotProvidedExceptionFault(e.getMessage(), e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new DSSRuntimeExceptionFault("Unrecognized Exception in EvaluationFactory: " + engineIdentifier + " "
                    + e.getMessage());
        }
    }

    private ExecutionEngine getExecutionEngine(KnowledgeRepository knowledgeRepository, String requestedKmId)
            throws UnrecognizedScopedEntityException,
            RequiredDataNotProvidedException {
        KnowledgeModule km = knowledgeRepository.getKnowledgeModuleService().find(requestedKmId);
        if (km == null) {
            throw new UnrecognizedScopedEntityException("Unable to find KnowledgeModule for requested KM ID: "
                    + requestedKmId);
        }
        ExecutionEngine engine = knowledgeRepository.getExecutionEngineService().find(km.getExecutionEngine());
        // FIXME The new config architecture shouldn't allow this type of
        // confuration...
        if (engine == null || (engine.getIdentifier() == null) || engine.getIdentifier().isEmpty()) {
            // TODO: is this the right exception?
            throw new RequiredDataNotProvidedException(
                    "ClassPathNameInvalidException trying to locate RequiredInferenceEngineAdapter by kmId: '"
                            + requestedKmId + "'; engine: " + engine + ".");
        }
        return engine;
    }

    /**
     * 
     * @param request
     * @return iterativeEvaluater
     */
    public static IterativeEvaluater createIterativeEvaluater(IterativeEvaluationRequest iterativeRequest)
            throws DSSRuntimeExceptionFault,
            RequiredDataNotProvidedExceptionFault
    // TODO this code is inoperative, and needs to be modified to implement the
    // IterativeEvaluation Request
    {
        IterativeEvaluater evaluater;
        // String adapterName = "GenericIterativeEvaluation";
        // String adapterName =
        // SimpleKnowledgeRepository.getInstance().getRequiredInferenceEngineAdapterForKMId(
        // iterativeRequest );
        // String adapterClassPathName = getAdapterClassPathName(
        // iterativeRequest ); //may need to be adjusted once iterative adapter
        // is created...
        String adapterClassPathName = "notImplementedYet";
        try {
            // String requestID = "GenericIterativeEvaluation";
            // Class<?> c = Class.forName("org.opencds.dss.evaluate." +
            // adapterName);
            Class<?> c = Class.forName(adapterClassPathName);
            evaluater = (IterativeEvaluater) c.newInstance();
            return evaluater;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RequiredDataNotProvidedExceptionFault("ClassNotFoundException: " + adapterClassPathName + " "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new DSSRuntimeExceptionFault("IllegalAccessException: " + adapterClassPathName + " " + e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new DSSRuntimeExceptionFault("InstantiationException: " + adapterClassPathName + " " + e.getMessage());
        }
    }

}
