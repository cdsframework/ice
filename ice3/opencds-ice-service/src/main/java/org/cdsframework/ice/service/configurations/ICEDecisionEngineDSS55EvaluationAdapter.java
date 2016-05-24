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

package org.cdsframework.ice.service.configurations;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.util.FileNameWithExtensionFilterImpl;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.definition.KnowledgePackage;
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
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.util.EntityIdentifierUtil;
import org.opencds.dss.evaluate.Evaluater;
import org.opencds.plugin.OpencdsPlugin;
import org.opencds.plugin.PluginContext;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;

/**
 * DroolsAdapter.java
 * 
 * <p>
 * Adapter to use Drools to process the evaluate operation of the DSS web
 * service: This class is designed to use data input in standard Java classes,
 * to facilitate its use from various settings. Mapping of the input data to the
 * internal Java classes is done by input mappers and output mappers, with a set
 * each for every external data format to be processed.
 * 
 * 
 * Simply stated, input messages contain a list of rules (Knowledge Modules, or
 * KMs) to run, and structured data to run against those KMs. The submitted data
 * can be in any structure for which there is a mapper. Currently, OpenCDS
 * supports the HL7 balloted VMR structure.
 * 
 * Additional structures for the submitted data may be developed, possibly
 * including the CDA / CCD schema structure
 * <p/>
 * <p>
 * Copyright 2011 OpenCDS.org
 * </p>
 * <p>
 * Company: OpenCDS
 * </p>
 */

public class ICEDecisionEngineDSS55EvaluationAdapter implements Evaluater {

	private String baseRulesScopingKmId = null;
	private Schedule schedule = null;
	
	private static Log logger = LogFactory.getLog(ICEDecisionEngineDSS55EvaluationAdapter.class);

	private static final String EVAL_TIME = "evalTime";
	private static final String CLIENT_LANG = "clientLanguage";
	private static final String CLIENT_TZ_OFFSET = "clientTimeZoneOffset";
	private static final String FOCAL_PERSON_ID = "focalPersonId";
	private static final String ASSERTIONS = "assertions";
	private static final String NAMED_OBJECTS = "namedObjects";

	private static final Set<String> ALL_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG,
			CLIENT_TZ_OFFSET, FOCAL_PERSON_ID, ASSERTIONS, NAMED_OBJECTS));
	private static final Set<String> FILTERED_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID));

	private static final String ALL_FACT_LISTS = "allFactLists";

	/**
	 * big picture pseudo code for following method:
	 * 
	 * for this requestedKmId { getResponse: create Drools session load KM into
	 * session load globals into session load data from allFactLists into
	 * session KBase.execute (calls Drools) unload result from KM to
	 * outputString }
	 * 
	 * This means that we are considering the OMG-CDSS concept of
	 * KnowledgeModule equivalent to the Drools concept of KnowledgeBase.
	 */

	@Override
	public Map<String, List<?>> getOneResponse(KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem evaluationRequestKMItem)
			throws InvalidDriDataFormatExceptionFault,
			RequiredDataNotProvidedExceptionFault,
			EvaluationExceptionFault,
			InvalidTimeZoneOffsetExceptionFault,
			UnrecognizedScopedEntityExceptionFault,
			UnrecognizedLanguageExceptionFault,
			UnsupportedLanguageExceptionFault,
			DSSRuntimeExceptionFault {

		String _METHODNAME = "getOneResponse(): ";
		
		long t0 = 0L;
		if (logger.isInfoEnabled()) {
			t0 = System.nanoTime();
		}
		
		String requestedKmId = evaluationRequestKMItem.getRequestedKmId();
		/*
		 * Prior:
			KnowledgeModule knowledgeModule = knowledgeRepository.getKnowledgeModuleService().find(requestedKmId);
			// TODO: For now we'll leave supportingData support out of this work.
			//        List<SupportingData> supportingDataList = knowledgeRepository.getSupportingDataService().find(knowledgeModule.getKMId());
			Map<String, org.opencds.plugin.SupportingData> supportingData = new LinkedHashMap<>();
			//        for (SupportingData sd : supportingDataList) {
			//            Object data = knowledgeRepository.getSupportingDataPackageService().get(sd);
			//            supportingData.put(sd.getIdentifier(), org.opencds.plugin.SupportingData.create(sd.getIdentifier(), EntityIdentifierUtil.makeEIString(sd.getKMId()), sd.getPackageId(), sd.getPackageType(), data));
			//        }
		 */
		
		KnowledgeModule knowledgeModule = knowledgeRepository.getKnowledgeModuleService().find(requestedKmId);
        List<SupportingData> supportingDataList = knowledgeRepository.getSupportingDataService().find(knowledgeModule.getKMId());
        Map<String, org.opencds.plugin.SupportingData> supportingData = new LinkedHashMap<>();
        for (SupportingData sd : supportingDataList) {
            byte[] data = knowledgeRepository.getSupportingDataPackageService().getPackageBytes(sd);
            supportingData.put(sd.getIdentifier(), org.opencds.plugin.SupportingData.create(sd.getIdentifier(), EntityIdentifierUtil.makeEIString(sd.getKMId()), 
            		EntityIdentifierUtil.makeEIString(sd.getLoadedBy()), sd.getPackageId(), sd.getPackageType(), data));
        }
        
		EvaluationRequestDataItem evalRequestDataItem = evaluationRequestKMItem.getEvaluationRequestDataItem();

		Date evalTime = evalRequestDataItem.getEvalTime();
		// Date evalTime = new Date();
		String clientLanguage = evalRequestDataItem.getClientLanguage();
		String clientTimeZoneOffset = evalRequestDataItem.getClientTimeZoneOffset();
		String interactionId = evalRequestDataItem.getInteractionId();

		if (logger.isDebugEnabled()) {
			logger.debug("II: " + interactionId + " KMId: " + requestedKmId + " (" + knowledgeModule.getKMId() + ")" + ", SSId: " + evalRequestDataItem.getExternalFactModelSSId() + 
					", evalTime: " + evalTime + ", clTimeZone: " + clientTimeZoneOffset + ", clLang: " + clientLanguage);
		}

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
		 * ==================================================================
		 */
		if (logger.isDebugEnabled()) {
			logger.debug("II: " + interactionId + " KMId: " + requestedKmId + " built fact/concept lists");
		}

		/**
		 * Get the KMs and Load them into a stateless session
		 * 
		 * Currently, assumption is made that each requested knowledge module
		 * will be run separately (i.e., as part of a separate distinct
		 * knowledge base)
		 * 
		 */

		/**
		 * to create a new Drools Working Memory log for in depth Drools
		 * debugging - Use either the InMemorylog to record logs on all
		 * input, or use the Filelog for debugging of one input at a time in
		 * Drools and JBPM
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
		if (logger.isDebugEnabled()) {
			logger.debug("Plugin processing...");
		}
		List<PluginId> plugins = knowledgeRepository.getPluginPackageService().getAllPluginIds();
		List<PluginId> allPreProcessPluginIds = knowledgeModule.getPreProcessPluginIds();
		if (allPreProcessPluginIds != null) {
			for (PluginId pluginId : plugins) {
				if (allPreProcessPluginIds.contains(pluginId)) {
					if (logger.isDebugEnabled()) {
						logger.debug("applying plugin: " + pluginId.toString());
					}
					OpencdsPlugin<PreProcessPluginContext> opencdsPlugin = knowledgeRepository.getPluginPackageService().load(pluginId);
					PreProcessPluginContext preContext = PluginContext.createPreProcessPluginContext(allFactLists, namedObjects, globals, supportingData, knowledgeRepository.getPluginDataCacheService().getPluginDataCache(pluginId));
					opencdsPlugin.execute(preContext);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Plugin processing done.");
		}

		//        EncountersFromProblemAndProcedureConcepts plugin = new EncountersFromProblemAndProcedureConcepts();

		Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();
		Set<String> assertions = new HashSet<>();

		/**
		 * Load the Globals and Fact lists: evalTime, language, timezoneOffset
		 */

		// WorkingMemoryInMemoryLogger memorylog = new WorkingMemoryInMemoryLogger (statelessKnowledgeSession);
		// WorkingMemoryFileLogger filelog = new WorkingMemoryFilelLogger (statelessKnowledgeSession);      
		// If using the Filelog, Set the log file that we will be using to log Working Memory (aka session)          
		// filelog.setFileName("/Users/phillip/lib/tomcat/logs/drools-event-log"); 

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

		if (requestedKmId == null || requestedKmId.length() == 0) {
			// This shouldn't happen 
			String lErrStr = "knowledge module ID not supplied to drools adapter cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		cmds.add(CommandFactory.newSetGlobal("patientAgeTimeOfInterest", null));
		
		// Initialize the schedule
		if (this.baseRulesScopingKmId == null || this.baseRulesScopingKmId.isEmpty()) {
			String lErrStr = "An error occurred: knowledge module not properly initialized: base rules Scoping Km Id not set; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

		if (schedule == null || schedule.isScheduleInitialized() == false) {
			String lErrStr = "Schedule has not been fully initialized; something went wrong; cannot process request";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);			
		}
		
		cmds.add(CommandFactory.newSetGlobal("schedule", schedule));
		
		/*
		 * Add globals provided by plugin; don't allow any global that have the same name as our globals.
		 */
		for (Map.Entry<String, Object> global : globals.entrySet()) {
			if (ALL_GLOBALS.contains(global.getKey())) {
				logger.error("Global from Plugin is not allowed to overwrite expected global; choose a different name for the global: name= " + global.getKey());
			} else {
				logger.info("Adding global from plugin: name= " + global.getKey());
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
		if (knowledgeModule.getPrimaryProcess() != null && !knowledgeModule.getPrimaryProcess().isEmpty()) {
			cmds.add(CommandFactory.newStartProcess(knowledgeModule.getPrimaryProcess()));
			if (logger.isDebugEnabled()) {
				logger.debug("II: " + interactionId + " KMId: " + requestedKmId + " knowledgeBase Primary Process: " + knowledgeModule.getPrimaryProcess());
			}
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
			if (logger.isDebugEnabled()) {
				logger.debug("Borrowing package from pool...");
			}
			knowledgeBase = knowledgeRepository.getKnowledgePackageService().borrowKnowledgePackage(knowledgeModule);
			if (logger.isDebugEnabled()) {
				logger.debug("Package borrowed.");
			}
			StatelessKnowledgeSession statelessKnowledgeSession = knowledgeBase.newStatelessKnowledgeSession();
			if (logger.isDebugEnabled()) {
				logger.debug("KM (Drools) execution...");
			}
			long d0 = 0L;
			if (logger.isInfoEnabled()) {
				d0 = System.nanoTime();
			}
			results = statelessKnowledgeSession.execute(CommandFactory.newBatchExecution((cmds)));
			if (logger.isInfoEnabled()) {
				logger.info(_METHODNAME + "Drools Execution Duration: " + (System.nanoTime() - d0) / 1e6 + " ms");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("KM (Drools) execution done.");
			}
		} catch (Exception e) {
			String err = "OpenCDS call to Drools.execute failed with error: " + e.getMessage();
			logger.error(err, e);
			throw new DSSRuntimeExceptionFault(err);
		} finally {
			if (knowledgeBase != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Returning package to pool...");
				}
				knowledgeRepository.getKnowledgePackageService().returnKnowledgePackage(knowledgeModule, knowledgeBase);
				if (logger.isDebugEnabled()) {
					logger.debug("Package returned.");
				}
			}
		}

		/**
		 ********************************************************************************
		 * END Drools
		 * 
		 */
		// grab session logging of whichever type was started...
		//         log.debug(memorylog.getEvents());
		//        filelog.writeToDisk();

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
		 * 
		 * For now (ICE): not executing post-process plugins
        List<PluginId> allPostProcessPluginIds = knowledgeModule.getPostProcessPluginIds();
        if (allPostProcessPluginIds != null) {
        	for (PluginId pluginId : plugins) {
        		// and supporting data loadedBy plugin
        		if (allPostProcessPluginIds.contains(pluginId)) {
        			logger.debug("applying plugin: " + pluginId.toString());
        			OpencdsPlugin<PostProcessPluginContext> opencdsPlugin = knowledgeRepository
        					.getPluginPackageService().load(pluginId);
        			PostProcessPluginContext postContext = PluginContext.createPostProcessPluginContext(allFactLists,
        					namedObjects, assertions, resultFactLists, supportingData, knowledgeRepository.getPluginDataCacheService().getPluginDataCache(pluginId));
        			opencdsPlugin.execute(postContext);
        		}
        	}
        }
		 */

		if (logger.isDebugEnabled()) {
			logger.debug("II: " + interactionId + " KMId: " + requestedKmId + " completed Drools inferencing engine");
		}

		if (logger.isInfoEnabled()) {
			logger.info(_METHODNAME + "ICE Request Duration: " + (System.nanoTime() - t0) / 1e6 + " ms");
		}
		return resultFactLists;
	}

	
	public KnowledgeBase loadKnowledgePackage(KnowledgePackageService knowledgePackageService, KnowledgeModule knowledgeModule) {

		String _METHODNAME = "loadKnowledgePackage(): ";
		if (knowledgePackageService == null || knowledgeModule == null) {
			String lErrStr = "KnowledgePackageService or KnowledgeModule not supplied";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}

		KMId lKMId = knowledgeModule.getKMId();
		if (lKMId == null) {
			String lErrStr = "KMId not populated";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}

		if (lKMId.getScopingEntityId() == null || lKMId.getBusinessId() == null || lKMId.getVersion() == null) {
			String errStr = "ScopingID and/or BusinessID and/or Version not specified";
			logger.error(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}

		String lRequestedKmId = lKMId.getScopingEntityId() + "^" + lKMId.getBusinessId() + "^" + lKMId.getVersion();
		logger.info("Initializing ICE3 Drools 5.5 KnowledgeBase");
		KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		config.setProperty("drools.accumulate.function.maxDate", "org.cdsframework.ice.service.MaximumDateAccumulateFunction");
		config.setProperty("drools.accumulate.function.minDate", "org.cdsframework.ice.service.MinimumDateAccumulateFunction");
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config);

		File pkgFile = null;
		File drlFile = null;
		File drlFileDuplicateShotSameDay = null;
		File dslFile = null;
		File bpmnFile = null;

		Properties lProps = load("ice.properties");
		
		// Get the ICE knowledge repository directory location
		String baseConfigurationLocation = lProps.getProperty("ice_knowledge_repository_location");
		if (baseConfigurationLocation == null) {
			String lErrStr = "ICE knowledge repository data location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isInfoEnabled()) {
				String lErrStr = "ICE knowledge repository data location specified in properties file: " + baseConfigurationLocation;
				logger.info(lErrStr);
			}
		}
		
		// Get the ICE knowledge modules subdirectory location
		String knowledgeModulesSubDirectory = lProps.getProperty("ice_knowledge_modules_subdirectory");
		if (knowledgeModulesSubDirectory == null) {
			String lErrStr = "ICE knowledge modules subdirectory location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isDebugEnabled()) {
				String lInfoStr = "ICE knowledge modules data location specified in properties file: " + knowledgeModulesSubDirectory;
				logger.info(lInfoStr);
			}
		}

		File lKnowledgeModulesDirectory = new File(baseConfigurationLocation, knowledgeModulesSubDirectory);
		if (! new File(lKnowledgeModulesDirectory, lRequestedKmId).exists()) {
			String lErrStr = "ICE knowledge repository data location specified in properties file does not exist";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		
		// Get the default scoping ID for the base ICE rules
		String baseRulesScopingEntityId = lProps.getProperty("ice_base_rules_scoping_entity_id");
		if (baseRulesScopingEntityId == null) {
			String lErrStr = "ICE base rules scoping entity ID not specified in the properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isInfoEnabled()) {
				String lErrStr = "ICE base rules scoping entity ID specified in properties file: " + baseRulesScopingEntityId;
				logger.info(lErrStr);
			}
		}
		String lBaseRulesScopingKmId = baseRulesScopingEntityId + "^" + lKMId.getBusinessId() + "^" + lKMId.getVersion();
		
		// Load knowledge from pkg file?
		String loadRulesFromPkgFile = lProps.getProperty("load_knowledge_from_pkg_file");
		boolean loadRulesFromPkgFileBool = false;
		if (loadRulesFromPkgFile != null && loadRulesFromPkgFile.equals("Y")) {
			pkgFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".pkg");
			String lInfoStr = "ICE rules will be loaded from pkg file (if available), as per properties file setting. Pkg file: " + pkgFile.getAbsolutePath();
			loadRulesFromPkgFileBool = true;
			logger.info(_METHODNAME + lInfoStr);
		}
		else {
			String lInfoStr = "ICE rules will be _not_ be loaded from pkg file, as per properties file setting";
			logger.info(_METHODNAME + lInfoStr);
		}
		
		KnowledgeBase lKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

		if (loadRulesFromPkgFileBool == true && pkgFile != null && pkgFile.exists()) {
			logger.info(_METHODNAME + "loading knowledge from pkg file");
			try {
				logger.info(_METHODNAME + "loading knowledge from pkg file: " + pkgFile.getAbsolutePath());
				
				ObjectInputStream in = new ObjectInputStream( new FileInputStream(pkgFile.getAbsolutePath()) );
				// The input stream might contain an individual package or a collection.
				@SuppressWarnings( "unchecked" )
				Collection<KnowledgePackage> kpkgs = (Collection<KnowledgePackage>) in.readObject();
				in.close();
				lKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
				lKnowledgeBase.addKnowledgePackages( kpkgs );
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to load Drools package file" + pkgFile.getAbsolutePath(), e);			
			}
		}
		else {
			loadRulesFromPkgFileBool = false;
			logger.info(_METHODNAME + "loading knowledge from source files");
			//////////////////////////////////////////////////////////////////////
			// Base rules and DSL
			// DSL and Base rules
			dslFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".dsl");
			drlFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".drl");
			drlFileDuplicateShotSameDay = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
			bpmnFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".bpmn");

			if (! dslFile.exists() ||  ! drlFile.exists() || ! drlFileDuplicateShotSameDay.exists() || ! bpmnFile.exists()) {
				// Try in the knowledge module directory
				dslFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".dsl");
				drlFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".drl");
				drlFileDuplicateShotSameDay = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + "^DuplicateShotSameDay.drl");
				bpmnFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".bpmn");
				if (! dslFile.exists() ||  ! drlFile.exists() || ! drlFileDuplicateShotSameDay.exists() || ! bpmnFile.exists()) {
					String lErrStr = "Some or all ICE base rules not found; base repository location: " + baseConfigurationLocation + "; " +
							"base rules scoping entity id: " + lBaseRulesScopingKmId + "; knowledge module location: " + lRequestedKmId;
					logger.error(_METHODNAME + lErrStr);
					throw new RuntimeException(lErrStr);
				}
			}

			logger.info(_METHODNAME + "Loading knowledge base BPMN, DSL, DRL and DSLR rules");

			// BPMN file
			if (bpmnFile != null) { 
				knowledgeBuilder.add(ResourceFactory.newFileResource(bpmnFile), ResourceType.BPMN2 );
				logger.info(_METHODNAME + "Loaded BPMN file " + bpmnFile.getPath());
			}
			// DSL file
			if (dslFile != null) {
				logger.info(_METHODNAME + "Loaded DSL file " + dslFile.getPath());
				knowledgeBuilder.add(ResourceFactory.newFileResource(dslFile), ResourceType.DSL);
			}
			if (drlFile  != null) {
				knowledgeBuilder.add( ResourceFactory.newFileResource(drlFile), ResourceType.DRL );
				logger.info(_METHODNAME + "Loaded DRL file " + drlFile.getPath());
			}
			if (drlFileDuplicateShotSameDay  != null) {
				knowledgeBuilder.add( ResourceFactory.newFileResource(drlFileDuplicateShotSameDay), ResourceType.DRL );
				logger.info(_METHODNAME + "Loaded DRL file " + drlFileDuplicateShotSameDay.getPath());
			}
			//////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////////////
			// Now load the Knowledge Module specific rules - Do so by reading all of the files that fit the filter for the knowledge module directory
			File dslrFileDirectory = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId);
			if (dslrFileDirectory == null || dslrFileDirectory.exists() == false || dslrFileDirectory.isDirectory() == false) {
				String lErrStr = "Knowledge module specific directory does not exist; cannot continue";
				logger.error(_METHODNAME + lErrStr);
				throw new RuntimeException(lErrStr);
			}

			// Obtain the files in this directory that adheres to the base and extension, ordered.
			String[] lValidFileExtensionsForCustomRules = { "drl", "dslr" };
			String[] lResultFiles = dslrFileDirectory.list(new FileNameWithExtensionFilterImpl(lRequestedKmId, lValidFileExtensionsForCustomRules));
			if (lResultFiles != null && lResultFiles.length > 0) {
				Arrays.sort(lResultFiles);
			}
			if (logger.isDebugEnabled()) {
				String lDebugStr = "Custom rule files to be loaded into this knowledge module:\n";
				for (int i=0; i < lResultFiles.length; i++) {
					if (i == lResultFiles.length-1) {
						lDebugStr += lResultFiles[i];
					}
					else {
						lDebugStr += lResultFiles[i] + "\n";
					}
				}
				logger.debug(lDebugStr);
			}

			logger.info(_METHODNAME + "Loading knowledge base with custom DRL and DSLR files: do DRL first, then DSLR");
			File customRuleFile = null;
			if (lResultFiles != null) {
				for (int i=0; i < lResultFiles.length; i++) {
					String lResultFile = lResultFiles[i];
					customRuleFile = new File(dslrFileDirectory, lResultFile);
					if (customRuleFile.equals(drlFile) || customRuleFile.equals(drlFileDuplicateShotSameDay)) {
						continue;
					}
					if (customRuleFile != null && customRuleFile.exists()) {
						if (lResultFile.endsWith(".drl") || lResultFile.endsWith(".DRL")) {
							knowledgeBuilder.add(ResourceFactory.newFileResource(customRuleFile), ResourceType.DRL);
							logger.info(_METHODNAME + "Loaded DRL file " + customRuleFile.getPath());
						}
					}
				}
				for (int i=0; i < lResultFiles.length; i++) {
					String lResultFile = lResultFiles[i];
					customRuleFile = new File(dslrFileDirectory, lResultFile);
					if (customRuleFile.equals(drlFile) || customRuleFile.equals(drlFileDuplicateShotSameDay)) {
						continue;
					}
					if (customRuleFile != null && customRuleFile.exists()) {
						if (lResultFile.endsWith(".dslr") || lResultFile.endsWith(".DSLR")) {
							knowledgeBuilder.add(ResourceFactory.newFileResource(customRuleFile), ResourceType.DSLR);
							logger.info(_METHODNAME + "Loaded DSLR file " + customRuleFile.getPath());
						}
					}
				}
			}
			//////////////////////////////////////////////////////////////////////

			if ( knowledgeBuilder.hasErrors() ) {
				throw new RuntimeException("KnowledgeBuilder had errors on build of: '" + lRequestedKmId + "', "+ knowledgeBuilder.getErrors().toString() );
			}	
			if (knowledgeBuilder.getKnowledgePackages().size() == 0) {
				throw new RuntimeException("KnowledgeBuilder did not find any VALID '.drl', 'dsl', '.bpmn' or '.pkg' files for: '" + lRequestedKmId + "', "+ knowledgeBuilder.getErrors().toString());
			}

			lKnowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		}

		if (loadRulesFromPkgFileBool == false && pkgFile != null && pkgFile.exists() == false) {
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "since pkg file did not exist and knowledge package was loaded via source, persisting dynamically loaded knowledge base to a pkg file for future use");
			}

			//get the generated package (change this if you have more than one package)
			// KnowledgePackage kpkg = (KnowledgePackage) knowledgeBuilder.getKnowledgePackages().iterator().next();
			Collection<KnowledgePackage> kpkgs = knowledgeBuilder.getKnowledgePackages();

			//	write out the package to a file
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pkgFile.getAbsolutePath()) );
				out.writeObject( kpkgs );
				out.close();
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to write serialized pkg file", e);
			}
		}
		
		knowledgePackageService.putPackage(knowledgeModule, lKnowledgeBase);

		this.baseRulesScopingKmId = lBaseRulesScopingKmId;
		logger.info("Date/Time " + lRequestedKmId + "; Base Rules Scoping Km Id: " + this.baseRulesScopingKmId + "; Initialized: " + new Date());

		// Initialize schedule 
		logger.info("Initializing Schedule");
		List<String> cdsVersions = new ArrayList<String>();
		cdsVersions.add(this.baseRulesScopingKmId);
		cdsVersions.add(lRequestedKmId);
		try {
			this.schedule = new Schedule("requestedKmId", cdsVersions, lKnowledgeModulesDirectory);
		}
		catch (ImproperUsageException | InconsistentConfigurationException ii) {
			String lErrStr = "Failed to initialize immunization schedule";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		logger.info("Schedule Initialization complete");
		return lKnowledgeBase;
	}


	private Properties load(String filename) {

		String _METHODNAME = "load(): ";

		if(filename == null) { 
			String lErrStr = "No FileName given to load. Defaulting to mci.properties";
			logger.error(_METHODNAME + "No FileName given to load. Defaulting to mci.properties");
			throw new RuntimeException(lErrStr);
		}

		Properties lProps = new Properties();
		try {
			// lProps.load( new FileInputStream(filename) );
			lProps.load(this.getClass().getClassLoader().getResourceAsStream(filename));
		} 
		catch(IOException e) {
			String lErrStr = "ICE properties file not found or could not be loaded: " + filename;
			logger.error(_METHODNAME + "Properties file not found: " + filename); 
			throw new RuntimeException(lErrStr); 
		}

		return lProps;
	}
}

