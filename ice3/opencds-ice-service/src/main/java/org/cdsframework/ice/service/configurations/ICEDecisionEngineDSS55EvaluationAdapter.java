/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.supportingdata.ICEPropertiesDataConfiguration;
import org.cdsframework.ice.util.FileNameWithExtensionFilterImpl;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
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
 */

public class ICEDecisionEngineDSS55EvaluationAdapter implements Evaluater {

	private String baseRulesScopingKmId = null;
	private Boolean outputEarliestOverdueDates = null;
	private Boolean doseOverrideFeatureEnabled = null;
	private Boolean outputSupplementalText = null;
	
	private static Log logger = LogFactory.getLog(ICEDecisionEngineDSS55EvaluationAdapter.class);

	private static final String EVAL_TIME = "evalTime";
	private static final String CLIENT_LANG = "clientLanguage";
	private static final String CLIENT_TZ_OFFSET = "clientTimeZoneOffset";
	private static final String FOCAL_PERSON_ID = "focalPersonId";
	private static final String ASSERTIONS = "assertions";
	private static final String NAMED_OBJECTS = "namedObjects";
	private static final Set<String> ALL_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID, ASSERTIONS, NAMED_OBJECTS));
	private static final Set<String> FILTERED_GLOBALS = new HashSet<>(Arrays.asList(EVAL_TIME, CLIENT_LANG, CLIENT_TZ_OFFSET, FOCAL_PERSON_ID));
	/////// private static final String ALL_FACT_LISTS = "allFactLists";

	
	/*
	 * Orig:
    private static Map<String, org.opencds.plugin.SupportingData> getSupportingData(KnowledgeRepository knowledgeRepository, KnowledgeModule knowledgeModule) {
        List<SupportingData> supportingDataList = filterByKM(knowledgeModule.getKMId(), knowledgeRepository.getSupportingDataService().getAll());

        Map<String, org.opencds.plugin.SupportingData> supportingData = new LinkedHashMap<>();
        for (SupportingData sd : supportingDataList) {
            byte[] data = knowledgeRepository.getSupportingDataPackageService().getPackageBytes(sd);
            supportingData.put(sd.getIdentifier(), org.opencds.plugin.SupportingData.create(sd.getIdentifier(),
                    EntityIdentifierUtil.makeEIString(sd.getKMId()),
                    EntityIdentifierUtil.makeEIString(sd.getLoadedBy()), sd.getPackageId(), sd.getPackageType(), data));
        }
        return supportingData;
    }
    */
    
    private static Map<String, org.opencds.plugin.SupportingData> getSupportingData(KnowledgeRepository knowledgeRepository, KnowledgeModule knowledgeModule, 
    		boolean getRawData) {
    	
    	String _METHODNAME = "getSupportingData(): ";
    	
        List<SupportingData> supportingDataList = filterByKM(knowledgeModule.getKMId(), knowledgeRepository.getSupportingDataService().getAll());
        Map<String, org.opencds.plugin.SupportingData> supportingDataListWithoutRawData = new LinkedHashMap<>();
        for (SupportingData sd : supportingDataList) {
            byte[] data = new byte[0];
            if (getRawData) {
            	if (logger.isDebugEnabled()) {
            		logger.debug(_METHODNAME + "raw data to be loaded in with supporting data metadata");
            	}
            	data = knowledgeRepository.getSupportingDataPackageService().getPackageBytes(sd);
            }
            else {
            	
            }
            org.opencds.plugin.SupportingData lSD = org.opencds.plugin.SupportingData.create(sd.getIdentifier(), EntityIdentifierUtil.makeEIString(sd.getKMId()),
                    EntityIdentifierUtil.makeEIString(sd.getLoadedBy()), sd.getPackageId(), sd.getPackageType(), data);
            supportingDataListWithoutRawData.put(sd.getIdentifier(), lSD);
        }
        
        return supportingDataListWithoutRawData;
    }
    
    
    /**
     * Inclusion filter by SupportingData by KMId, or SDs that have no associated KMId.
     */
    private static List<SupportingData> filterByKM(KMId kmId, List<SupportingData> sds) {
        List<SupportingData> sdList = new ArrayList<>();
        for (SupportingData sd : sds) {
            if (sd.getKMId() == null || sd.getKMId().equals(kmId)) {
                sdList.add(sd);
            }
        }
        return sdList;
    }
    

	/**
	 * big picture pseudo code for following method:
	 * 
	 * for this requestedKmId { getResponse: create Drools session load KM into session load globals into session load data from allFactLists into
	 * session KBase.execute (calls Drools) unload result from KM to outputString }
	 * 
	 * This means that we are considering the OMG-CDSS concept of KnowledgeModule equivalent to the Drools concept of KnowledgeBase.
	 */
	@Override
	public Map<String, List<?>> getOneResponse(KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem evaluationRequestKMItem)
		throws InvalidDriDataFormatExceptionFault, RequiredDataNotProvidedExceptionFault, EvaluationExceptionFault, InvalidTimeZoneOffsetExceptionFault,
			UnrecognizedScopedEntityExceptionFault, UnrecognizedLanguageExceptionFault, UnsupportedLanguageExceptionFault, DSSRuntimeExceptionFault {

		String _METHODNAME = "getOneResponse(): ";
		
		long t0 = 0L;
		if (logger.isInfoEnabled()) {
			t0 = System.nanoTime();
		}
		
		String requestedKmId = evaluationRequestKMItem.getRequestedKmId();
        KnowledgeModule knowledgeModule = knowledgeRepository.getKnowledgeModuleService().find(requestedKmId);
        Map<String, org.opencds.plugin.SupportingData> supportingData = getSupportingData(knowledgeRepository, knowledgeModule, false);

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
		 * Load fact map from specific externalFactModels, as specified in externalFactModel SSId...
		 * 
		 * Every separately identified SSId, by definition, specifies separate input and output mappings. Input mappings are used here, and then
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
		 * Get the KMs and Load them into a stateless session. Currently, assumption is made that each requested knowledge module will be run separately 
		 * (i.e., as part of a separate distinct knowledge base)
		 */

		/**
		 * to create a new Drools Working Memory log for in depth Drools debugging - Use either the InMemorylog to record logs on all
		 * input, or use the Filelog for debugging of one input at a time in Drools and JBPM
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
			logger.debug("PreProcessPlugin processing...");
		}
		Schedule lSchedule = null;
		List<PluginId> plugins = knowledgeRepository.getPluginPackageService().getAllPluginIds();
		List<PluginId> allPreProcessPluginIds = knowledgeModule.getPreProcessPluginIds();
		if (allPreProcessPluginIds != null) {
			for (PluginId pluginId : plugins) {
				if (allPreProcessPluginIds.contains(pluginId)) {
					if (logger.isDebugEnabled()) {
						logger.debug("applying preprocess plugin: " + pluginId.toString());
					}
					OpencdsPlugin<PreProcessPluginContext> opencdsPlugin = knowledgeRepository.getPluginPackageService().load(pluginId);
					PreProcessPluginContext preContext = PluginContext.createPreProcessPluginContext(allFactLists, namedObjects, globals, supportingData, 
							knowledgeRepository.getPluginDataCacheService().getPluginDataCache(pluginId));

					/*
					// Refactor in upgrade to OpenCDS 2.1 or above... (Cheating here by calling ICEPlugin implementation directly :( ) 
					// If the supporting data content has not been loaded into the cache, read the raw data from the file so that it can be loaded
					if (! ICESupportingDataLoaderPlugin.supportingDataAlreadyLoadedInContext(preContext)) {
						if (logger.isDebugEnabled()) {
							logger.debug(_METHODNAME + "preprocess plugin: supporting data content not loaded into context: content to be loaded now");
						}
						supportingData = getSupportingData(knowledgeRepository, knowledgeModule, true);
						preContext = PluginContext.createPreProcessPluginContext(allFactLists, namedObjects, globals, supportingData, 
							knowledgeRepository.getPluginDataCacheService().getPluginDataCache(pluginId));
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug(_METHODNAME + "preprocess plugin: supporting data content (already) loaded into context");
						}
					}
					*/
					// Execute the preprocess plugin to get the supporting data - fix in OpenCDS upgrade
					if (! (opencdsPlugin instanceof ICESupportingDataLoaderPlugin)) {
						logger.error(_METHODNAME + "unknown supporting data plugin");
						throw new RuntimeException("initialization error: unknown supporting data plugin");
					}
					((ICESupportingDataLoaderPlugin) opencdsPlugin).execute(preContext, requestedKmId);
					lSchedule = preContext.getCache().get(requestedKmId);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Plugin processing done.");
		}
		
		if (lSchedule == null) {
			// Immunization schedule not loaded 
			String lErrStr = "Immunization schedule not loaded; something went wrong. Incorrect configuration or requested knowledge module ID likely: " + requestedKmId;
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}

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

		if (lSchedule == null || lSchedule.isScheduleInitialized() == false) {
			String lErrStr = "Schedule has not been fully initialized; something went wrong; cannot process request";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);			
		}
		cmds.add(CommandFactory.newSetGlobal("schedule", lSchedule));
		
		if (outputEarliestOverdueDates == null) {
			String lErrStr = "An error occurred: knowledge module not properly initialized: output earliest/overdue flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		cmds.add(CommandFactory.newSetGlobal("outputEarliestOverdueDates", outputEarliestOverdueDates));
		
		if (doseOverrideFeatureEnabled == null) {
			String lErrStr = "An error occurred: knowledge module not properly initialized: dose override flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		cmds.add(CommandFactory.newSetGlobal("doseOverrideFeatureEnabled", doseOverrideFeatureEnabled));
		
		if (outputSupplementalText == null) {
			String lErrStr = "An error occurred: knowledge module not properly initialized: dose override flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);			
		}
		cmds.add(CommandFactory.newSetGlobal("outputSupplementalText", outputSupplementalText));
		
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

		///////String lRequestedKmId = lKMId.getScopingEntityId() + "^" + lKMId.getBusinessId() + "^" + lKMId.getVersion();
		String lRequestedKmId = KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion());
		if (lRequestedKmId != null && lRequestedKmId.equals("org.nyc.cir^ICE^1.0.0")) {
			lRequestedKmId = "gov.nyc.cir^ICE^1.0.0";
		}
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

		ICEPropertiesDataConfiguration iceconfig = new ICEPropertiesDataConfiguration();
		Properties lProps = iceconfig.getProperties();
		
		/////// Get the ICE knowledge repository directory location
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
		
		////////////////////////////////////////////////////////////////////////////////////
		// START - Get the ICE knowledge modules subdirectory location
		////////////////////////////////////////////////////////////////////////////////////		
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
		/////// 
		// Determine Knowledge Modules Directory Location
		///////
		File lKnowledgeModulesDirectory = new File(baseConfigurationLocation, knowledgeModulesSubDirectory);
		if (! new File(lKnowledgeModulesDirectory, lRequestedKmId).exists()) {
			String lErrStr = "Requested ICE knowledge module does not exist: " + lKnowledgeModulesDirectory.getAbsolutePath() + "; knowledge module " + lRequestedKmId;
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Requested ICE knowledge module directory: " + lKnowledgeModulesDirectory.getAbsolutePath() + "; knowledge module " + lRequestedKmId);
		}
		////////////////////////////////////////////////////////////////////////////////////
		// END - Get the ICE knowledge modules subdirectory location
		////////////////////////////////////////////////////////////////////////////////////		

		////////////////////////////////////////////////////////////////////////////////////
		// START - Get the ICE Common rules subdirectory location
		////////////////////////////////////////////////////////////////////////////////////		
		String knowledgeCommonSubDirectory = lProps.getProperty("ice_knowledge_common_subdirectory");
		if (knowledgeCommonSubDirectory == null) {
			String lErrStr = "ICE common knowledge subdirectory location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isDebugEnabled()) {
				String lInfoStr = "ICE common knowledge data location specified in properties file: " + knowledgeCommonSubDirectory;
				logger.info(lInfoStr);
			}
		}

		String lBaseRulesScopingKmId = KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(iceconfig.getBaseRulesScopingEntityId(), lKMId.getBusinessId(), iceconfig.getBaseRulesVersion());		
		File lKnowledgeCommonDirectory = new File(baseConfigurationLocation, knowledgeCommonSubDirectory);
		if (! new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId).exists()) {
			String lErrStr = "Base ICE knowledge module does not exist" + lKnowledgeCommonDirectory.getAbsolutePath() + "; knowledge module " + lBaseRulesScopingKmId;;
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Base knowledge modules directory: " + lKnowledgeCommonDirectory.getAbsolutePath()+ "; knowledge module " + lBaseRulesScopingKmId);
		}
		////////////////////////////////////////////////////////////////////////////////////
		// END - Get the ICE Common rules subdirectory location
		////////////////////////////////////////////////////////////////////////////////////		
		
		///////
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
				
		// Output earliest and overdue dates for each series recommendation?
		String lOutputEarliestOverdueDates = lProps.getProperty("output_earliest_and_overdue_dates");
		if (lOutputEarliestOverdueDates != null && lOutputEarliestOverdueDates.equals("Y")) {
			outputEarliestOverdueDates = new Boolean(true);
		}
		else {
			outputEarliestOverdueDates = new Boolean(false);
		}

		// Permit Dose Override by client?
		String lEnableDoseOverrideFeature = lProps.getProperty("enable_dose_override_feature");
		if (lEnableDoseOverrideFeature != null && lEnableDoseOverrideFeature.equals("Y")) {
			doseOverrideFeatureEnabled = new Boolean(true);
		}
		else {
			doseOverrideFeatureEnabled = new Boolean(false);
		}
		
		// Output Supplemental Text, when available?
		String lOutputSupplementalText = lProps.getProperty("output_supplemental_text");
		if (lOutputSupplementalText != null && lOutputSupplementalText.equals("Y")) {
			outputSupplementalText = new Boolean(true);
		}
		else {
			outputSupplementalText = new Boolean(false);
		}
		
		/////// Set up knowledge base
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
			dslFile = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".dsl");
			drlFile = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".drl");
			drlFileDuplicateShotSameDay = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
			bpmnFile = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".bpmn");

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
			
			//////////////////////////////////////////////////////////////////////
			// Now load the Knowledge Module specific rules - Do so by reading all of the files that fit the filter for the knowledge module directory
			//////////////////////////////////////////////////////////////////////
			List<File> lFilesToExcludeFromKB = new ArrayList<File>();
			// lFilesToExcludeFromKB.add(drlFile);
			// lFilesToExcludeFromKB.add(drlFileDuplicateShotSameDay);

			// Add base rules to knowledge base
			File dslrBaseFileDirectory = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId);
			List<File> lBaseFilesToLoad = retrieveCollectionOfDSLRsToAddToKnowledgeBase(lBaseRulesScopingKmId, dslrBaseFileDirectory, lFilesToExcludeFromKB);
			if (lBaseFilesToLoad.isEmpty()) {
				String lErrStr = "No base ICE rules found; cannot continue";
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			// Load the files - DRL and DSLR files permitted for the base cdsframework rules
			for (File lFileToLoad : lBaseFilesToLoad) {
				if (lFileToLoad != null) {
					if (lFileToLoad.getName().endsWith(".drl") || lFileToLoad.getName().endsWith(".DRL")) {
						knowledgeBuilder.add(ResourceFactory.newFileResource(lFileToLoad), ResourceType.DRL);
						logger.info(_METHODNAME + "Loaded Base DRL file " + lFileToLoad.getPath());
					}
				}
			}
			for (File lFileToLoad : lBaseFilesToLoad) {
				if (lFileToLoad != null) {
					if (lFileToLoad.getName().endsWith(".dslr") || lFileToLoad.getName().endsWith(".DSLR")) {
						knowledgeBuilder.add(ResourceFactory.newFileResource(lFileToLoad), ResourceType.DSLR);
						logger.info(_METHODNAME + "Loaded Base DSLR file " + lFileToLoad.getPath());
					}
				}
			}

			// Add custom rules to knowledge base - both DRL and DSLR files permitted, DRL files loaded first.
			File dslrFileDirectory = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId);
			List<File> lFilesToLoad = retrieveCollectionOfDSLRsToAddToKnowledgeBase(lRequestedKmId, dslrFileDirectory, lFilesToExcludeFromKB);
			// First do the DRL files, then the DSLR files
			for (File lFileToLoad : lFilesToLoad) {
				if (lFileToLoad != null) {
					if (lFileToLoad.getName().endsWith(".drl") || lFileToLoad.getName().endsWith(".DRL")) {
						knowledgeBuilder.add(ResourceFactory.newFileResource(lFileToLoad), ResourceType.DRL);
						logger.info(_METHODNAME + "Loaded DRL file " + lFileToLoad.getPath());
					}
				}
			}
			for (File lFileToLoad : lFilesToLoad) {
				if (lFileToLoad != null) {
					if (lFileToLoad.getName().endsWith(".dslr") || lFileToLoad.getName().endsWith(".DSLR")) {
						knowledgeBuilder.add(ResourceFactory.newFileResource(lFileToLoad), ResourceType.DSLR);
						logger.info(_METHODNAME + "Loaded DSLR file " + lFileToLoad.getPath());
					}
				}
			}

			//////////////////////////////////////////////////////////////////////

			if (knowledgeBuilder.hasErrors() ) {
				throw new RuntimeException("KnowledgeBuilder had errors on build of: '" + lRequestedKmId + "', "+ knowledgeBuilder.getErrors().toString() );
			}	
			if (knowledgeBuilder.getKnowledgePackages().size() == 0) {
				throw new RuntimeException("KnowledgeBuilder did not find any VALID '.drl', 'dsl', '.bpmn' or '.pkg' files for: '" + lRequestedKmId + "', "+ knowledgeBuilder.getErrors().toString());
			}
			//////////////////////////////////////////////////////////////////////

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

		return lKnowledgeBase;
	}
	
	
	private List<File> retrieveCollectionOfDSLRsToAddToKnowledgeBase(String pRequestedKmId, File pDSLRFileDirectory, List<File> pFilesToExcludeFromKB) {
		
		String _METHODNAME = "retrieveCollectionOfDSLRsToAddToKnowledgeBase(): ";
		
		if (pDSLRFileDirectory == null || pDSLRFileDirectory.exists() == false || pDSLRFileDirectory.isDirectory() == false) {
			String lErrStr = "Knowledge module specific directory does not exist; cannot continue. Directory: " + pDSLRFileDirectory.getAbsolutePath();
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

		// Obtain the files in this directory that adheres to the base and extension, ordered.
		String[] lValidFileExtensionsForCustomRules = { "drl", "dslr", "DRL", "DSLR" };
		String[] lResultFiles = pDSLRFileDirectory.list(new FileNameWithExtensionFilterImpl(pRequestedKmId, lValidFileExtensionsForCustomRules));
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

		logger.info(_METHODNAME + "Determining knowledge base with custom DRL and DSLR files");
		List<File> drlFilesToAddToKB = new ArrayList<>();
		File customRuleFile = null;
		if (lResultFiles != null) {
			// Add DRL files first to KB
			for (int i=0; i < lResultFiles.length; i++) {
				boolean exclusionFound = false;
				String lResultFile = lResultFiles[i];
				customRuleFile = new File(pDSLRFileDirectory, lResultFile);
				for (File lExclusion : pFilesToExcludeFromKB) {
					if (customRuleFile.equals(lExclusion)) {
						exclusionFound = true;
						break;
					}
				}
				if (exclusionFound) {
					continue;
				}
				if (customRuleFile != null && customRuleFile.exists()) {
					if (lResultFile.endsWith(".drl") || lResultFile.endsWith(".DRL") || lResultFile.endsWith(".dslr") || lResultFile.endsWith(".DSLR")) {
						drlFilesToAddToKB.add(customRuleFile);
					}
				}
			}
			
		}
		
		return drlFilesToAddToKB;
	}
	
}
