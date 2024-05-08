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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;

public class KieScannerAdapter implements ExecutionEngineAdapter<List<Command<?>>, ExecutionResults, KieContainer> {
    private static Log log = LogFactory.getLog(KieScannerAdapter.class);

    @Override
    public ExecutionEngineContext<List<Command<?>>, ExecutionResults> execute(KieContainer kContainer,
            ExecutionEngineContext<List<Command<?>>, ExecutionResults> context) throws DSSRuntimeExceptionFault {
        ExecutionResults results = null;
        try {
        	
        	StatelessKieSession kSession = kContainer.newStatelessKieSession();
        	
        	log.debug("KM (Drools) execution...");
            results = kSession.execute(CommandFactory.newBatchExecution(context.getInput()));
            context.setResults(results);
            log.debug("KM (Drools) execution done.");
        } catch (Exception e) {
            String err = "OpenCDS call to Drools.execute failed with error: " + e.getMessage();
            log.error(err, e);
            throw new DSSRuntimeExceptionFault(err);
        }

        return context;
    }

}
