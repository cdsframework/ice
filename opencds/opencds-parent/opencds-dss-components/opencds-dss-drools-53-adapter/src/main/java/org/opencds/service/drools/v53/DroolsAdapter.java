/**
 * Copyright 2011, 2012 OpenCDS.org
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

package org.opencds.service.drools.v53;
 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.dss.evaluate.Evaluater;
import org.opencds.plugin.OpencdsPlugin;
import org.opencds.plugin.PluginContext;
import org.opencds.plugin.PluginContext.PostProcessPluginContext;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;


/**
 * DroolsAdapter.java
 * 
 * <p>Adapter to use Drools to process the evaluate operation of the DSS web service:
 * This class is designed to use data input in standard Java classes, to facilitate its use from various settings.
 * Mapping of the input data to the internal Java classes is done by input mappers and output mappers, with a set 
 * each for every external data format to be processed. 
 * 
 * 
 * Simply stated, input messages contain a list of rules (Knowledge Modules, or KMs) to run, and structured data
 * to run against those KMs.  The submitted data can be in any structure for which there is a mapper.  Currently,
 * OpenCDS supports the HL7 balloted VMR structure.
 * 
 * Additional structures for the submitted data may be developed, possibly including the CDA / CCD schema structure
 * <p/>
 * <p>Copyright 2011 OpenCDS.org</p>
 * <p>Company: OpenCDS</p>
 *
 * @author David Shields
 * @version 2.1 for Drools 5.3 / 5.4
 * @date 11-09-2011
 * 
 */
public class DroolsAdapter implements Evaluater {		
    private static Log log = LogFactory.getLog(DroolsAdapter.class);

    private static final String EVAL_TIME = "evalTime";
    private static final String CLIENT_LANG = "clientLanguage";
    private static final String CLIENT_TZ_OFFSET = "clientTimeZoneOffset";
    private static final String FOCAL_PERSON_ID = "focalPersonId";
    private static final String ASSERTIONS = "assertions";
    private static final String NAMED_OBJECTS = "namedObjects";
    
    private static final Set<String> ALL_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG,
            CLIENT_TZ_OFFSET, FOCAL_PERSON_ID, ASSERTIONS, NAMED_OBJECTS));
    private static final Set<String> FILTERED_GLOBALS =
            new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID));

    private static final String ALL_FACT_LISTS = "allFactLists";

    /** 
	 * big picture pseudo code for following method:
	 * 
	 * 		for this requestedKmId { 
	 * 			getResponse:
	 * 				create Drools session
	 * 				load KM into session
     *  			load globals into session
     *				load data from allFactLists into session 
     *  			KBase.execute (calls Drools)
     *  			unload result from KM to outputString
     * 		} 
	 * 
	 * This means that we are considering the OMG-CDSS concept of KnowledgeModule equivalent to
	 * the Drools concept of KnowledgeBase.
	 */	
    
    @Override
    public Map<String, List<?>> getOneResponse(KnowledgeRepository knowledgeRepository,
            EvaluationRequestKMItem evaluationRequestKMItem)
            throws InvalidDriDataFormatExceptionFault,
		RequiredDataNotProvidedExceptionFault, 
		EvaluationExceptionFault, 
		InvalidTimeZoneOffsetExceptionFault, 
		UnrecognizedScopedEntityExceptionFault, 
		UnrecognizedLanguageExceptionFault, 
		UnsupportedLanguageExceptionFault,
		DSSRuntimeExceptionFault {
		
        String requestedKmId = evaluationRequestKMItem.getRequestedKmId();
        KnowledgeModule knowledgeModule = knowledgeRepository.getKnowledgeModuleService().find(requestedKmId);
        // TODO: For now we'll leave supportingData support out of this work.
//      List<SupportingData> supportingDataList = knowledgeRepository.getSupportingDataService().find(knowledgeModule.getKMId());
        Map<String, org.opencds.plugin.SupportingData> supportingData = new LinkedHashMap<>();
//      for (SupportingData sd : supportingDataList) {
//          Object data = knowledgeRepository.getSupportingDataPackageService().get(sd);
//          supportingData.put(sd.getIdentifier(), org.opencds.plugin.SupportingData.create(sd.getIdentifier(), EntityIdentifierUtil.makeEIString(sd.getKMId()), sd.getPackageId(), sd.getPackageType(), data));
//      }

		EvaluationRequestDataItem evalRequestDataItem = evaluationRequestKMItem.getEvaluationRequestDataItem();
		
        Date evalTime = evalRequestDataItem.getEvalTime();
        String clientLanguage = evalRequestDataItem.getClientLanguage();
        String clientTimeZoneOffset = evalRequestDataItem.getClientTimeZoneOffset();
        String interactionId = evalRequestDataItem.getInteractionId();
		
        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " (" + knowledgeModule.getKMId() + ")"
                + ", SSId: " + evalRequestDataItem.getExternalFactModelSSId() + ", evalTime: " + evalTime
                + ", clTimeZone: " + clientTimeZoneOffset + ", clLang: " + clientLanguage);
		
        /**
         * Load fact map from specific externalFactModels, as specified in
         * externalFactModel SSId...
         * 
         * Every separately identified SSId, by definition, specifies separate
         * input and output mappings. Input mappings are used here, and then
         * output mappings are used following the session.execute.
         */

        Map<Class<?>, List<?>> allFactLists = evaluationRequestKMItem.getAllFactLists();
		
        /**
         * allFactLists are updated in place by the following call, including
         * both facts and concepts...
         * ==================================================================
         */
        Map<String, Object> namedObjects = new HashMap<>();
        Map<String, Object> globals = new HashMap<>();

        /*
         * PreProcess plugins
         */
        /*
         * ==================================================================
         * Plugin application here?
         * 
         * Plugins should be able to access/update allFactLists, namedObjects (below), and to add globals to the execution context.
         * Access to cdsResources from CDSInput (available in the factLists (see CdsInputFactListsBuilder)
         * -- Should have access to a cache region to insert data that are persistent across sessions (no lazy loading)
         *      - load each time (e.g., per cds resources)
         *      - load each time (lazy loading)
         *      - load initially (eager loading) -- think of how we might do this when we cache the knowledge package
         */
        log.debug("Plugin processing...");
        List<PluginId> plugins = knowledgeRepository.getPluginPackageService().getAllPluginIds();
        List<PluginId> allPreProcessPluginIds = knowledgeModule.getPreProcessPluginIds();
        if (allPreProcessPluginIds != null) {
            for (PluginId pluginId : plugins) {
                if (allPreProcessPluginIds.contains(pluginId)) {
                    log.debug("applying plugin: " + pluginId.toString());
                    OpencdsPlugin<PreProcessPluginContext> opencdsPlugin = knowledgeRepository.getPluginPackageService().load(pluginId);
                    PreProcessPluginContext preContext = PluginContext.createPreProcessPluginContext(allFactLists, namedObjects, globals, supportingData, knowledgeRepository.getPluginDataCacheService().getPluginDataCache(pluginId));
                    opencdsPlugin.execute(preContext);
                }
            }
        }
        log.debug("Plugin processing done.");
        
//        EncountersFromProblemAndProcedureConcepts plugin = new EncountersFromProblemAndProcedureConcepts();

        Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();
        Set<String> assertions = new HashSet<>();
        
        /**
         * Get the KMs and Load them into a stateless session
         * 
         * Currently, assumption is made that each requested knowledge module
         * will be run separately (i.e., as part of a separate distinct
         * knowledge base)
         * 
         */
        @SuppressWarnings("rawtypes")
        List<Command> cmds = Collections.synchronizedList(new ArrayList<Command>());
        /**
         * Load the Globals: evalTime, language, timezoneOffset, focalPersonId,
         * assertions, namedObjects
         */
        cmds.add(CommandFactory.newSetGlobal(EVAL_TIME, evalTime));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_LANG, clientLanguage));
        cmds.add(CommandFactory.newSetGlobal(CLIENT_TZ_OFFSET, clientTimeZoneOffset));

        // following global used to store flags for inter-task communication in
        // a JBPM Process
        cmds.add(CommandFactory.newSetGlobal(ASSERTIONS, assertions));

        // following global used to return facts added by rules, such as new
        // observationResults
        cmds.add(CommandFactory.newSetGlobal(NAMED_OBJECTS, namedObjects));

        /*
         * Add globals provided by plugin; don't allow any global that have the same name as our globals.
         */
        for (Map.Entry<String, Object> global : globals.entrySet()) {
            if (ALL_GLOBALS.contains(global.getKey())) {
                log.error("Global from Plugin is not allowed to overwrite expected global; choose a different name for the global: name= " + global.getKey());
            } else {
                log.info("Adding global from plugin: name= " + global.getKey());
                cmds.add(CommandFactory.newSetGlobal(global.getKey(), global.getValue()));
            }
        }
        
        /**
         * Load the FactLists into Commands: Only ones that are not empty...
         */
        for (Map.Entry<Class<?>, List<?>> factListEntry : allFactLists.entrySet()) {
            if (factListEntry.getValue().size() > 0) {
                cmds.add(CommandFactory.newInsertElements((List<?>) factListEntry.getValue(), factListEntry.getKey()
                        .getSimpleName(), true, null));
            }
        }

        /**
         * If this is a PKG (for package with process, initiate the configured
         * Primary Process for JBPM.
         */
        if (knowledgeModule.getPrimaryProcess() != null && !knowledgeModule.getPrimaryProcess().isEmpty()) {
            cmds.add(CommandFactory.newStartProcess(knowledgeModule.getPrimaryProcess()));
            log.debug("II: " + interactionId + " KMId: " + requestedKmId + " knowledgeBase Primary Process: "
                    + knowledgeModule.getPrimaryProcess());
        }
        
        /**
         * Use Drools to process everything Added try/catch around stateless
         * session. because Drools has an unhandled exception when a JBPM
         * Process improperly re-declares a global that is constraining a
         * gateway and the resultant global is null - des 20120727
         ********************************************************************************
         */
        ExecutionResults results = null;
        KnowledgeBase knowledgeBase = null;
        try {
            log.debug("Borrowing package from pool...");
            knowledgeBase = knowledgeRepository.getKnowledgePackageService().borrowKnowledgePackage(knowledgeModule);
            log.debug("Package borrowed.");
            StatelessKnowledgeSession statelessKnowledgeSession = knowledgeBase.newStatelessKnowledgeSession();
            log.debug("KM (Drools) execution...");
            results = statelessKnowledgeSession.execute(CommandFactory.newBatchExecution((cmds)));
            log.debug("KM (Drools) execution done.");
        } catch (Exception e) {
            String err = "OpenCDS call to Drools.execute failed with error: " + e.getMessage();
            log.error(err, e);
            throw new DSSRuntimeExceptionFault(err);
        } finally {
            if (knowledgeBase != null) {
                log.debug("Returning package to pool...");
                knowledgeRepository.getKnowledgePackageService().returnKnowledgePackage(knowledgeModule, knowledgeBase);
                log.debug("Package returned.");
            }
        }

        /**
         ********************************************************************************
         * END Drools
         * 
         */
        // grab session logging of whichever type was started...
//         log.debug(memoryLogger.getEvents());
//        fileLogger.writeToDisk();

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

        /**
         * now process the returned namedObjects and add them to the
         * resultFactLists
         */
        for (String key : namedObjects.keySet()) {
            Object oneNamedObject = namedObjects.get(key);
            if (oneNamedObject != null) {
                // String className = oneNamedObject.getClass().getSimpleName();
                List<Object> oneList = (List<Object>) resultFactLists.get(oneNamedObject.getClass().getSimpleName());
                if (oneList == null) {
                    oneList = new ArrayList<Object>();
                    oneList.add(oneNamedObject);
                } else {
                    oneList.add(oneNamedObject);
                }
                resultFactLists.put(oneNamedObject.getClass().getSimpleName(), oneList);
            }
        }

        /**
         * Retrieve the Results for this requested KM and stack them in the DSS
         * fkmResponse NOTE: Each additional requested KM will have a separate
         * output payload
         */
        
        List<PluginId> allPostProcessPluginIds = knowledgeModule.getPostProcessPluginIds();
        if (allPostProcessPluginIds != null) {
            for (PluginId pluginId : plugins) {
        // and supporting data loadedBy plugin
                if (allPostProcessPluginIds.contains(pluginId)) {
                    log.debug("applying plugin: " + pluginId.toString());
                    OpencdsPlugin<PostProcessPluginContext> opencdsPlugin = knowledgeRepository
                            .getPluginPackageService().load(pluginId);
                    PostProcessPluginContext postContext = PluginContext.createPostProcessPluginContext(allFactLists,
                            namedObjects, assertions, resultFactLists, supportingData, knowledgeRepository.getPluginDataCacheService().getPluginDataCache(pluginId));
                    opencdsPlugin.execute(postContext);
                }
            }
        }
        log.debug("Plugin post-processing done.");

        log.debug("II: " + interactionId + " KMId: " + requestedKmId + " completed Drools inferencing engine");

        return resultFactLists;
	}
	
    @Override
    public KnowledgeBase loadKnowledgePackage(KnowledgePackageService knowledgePackageService, KnowledgeModule knowledgeModule) {
      KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
          KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
          InputStream packageInputStream = knowledgePackageService.getPackageInputStream(knowledgeModule);
          if (packageInputStream != null) {
              knowledgeBuilder.add(ResourceFactory.newInputStreamResource(packageInputStream),
                      ResourceType.getResourceType(knowledgeModule.getPackageType()));
              if (knowledgeBuilder.hasErrors()) {
                  throw new OpenCDSRuntimeException("KnowledgeBuilder had errors on build of: '"
                          + knowledgeModule.getKMId() + "', " + knowledgeBuilder.getErrors().toString());
              }
              if (knowledgeBuilder.getKnowledgePackages().size() == 0) {
                  throw new OpenCDSRuntimeException(
                          "KnowledgeBuilder did not find any VALID '.drl', '.bpmn' or '.pkg' files for: '"
                                  + knowledgeModule.getKMId() + "', " + knowledgeBuilder.getErrors().toString());
              }
              knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
              knowledgePackageService.putPackage(knowledgeModule, knowledgeBase);
          } else {
              throw new OpenCDSRuntimeException(
                      "KnowledgeModule package cannot be found (possibly due to misconfiguration?); packageId= "
                              + knowledgeModule.getPackageId() + ", packageType= " + knowledgeModule.getPackageType());
          }
          log.debug("Loaded KnowledgeModule package; kmId= " + knowledgeModule.getKMId());
          return knowledgeBase;
    }

}
