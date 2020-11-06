package org.cdsframework.rules.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.opencds.config.api.model.KMId;

/**
 *
 * @author sdn
 */
public class Packager {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * @param args the command line arguments
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception {
		Packager packager = new Packager();
		packager.run(args);
	}

	public void run(String[] args) throws Exception {
		final String _METHODNAME = "run ";

		String propsPath = args[0];
		Properties lProps = new Properties();
		lProps.load(new FileInputStream(propsPath));

		String lRequestedKmId = args[1];
		String lBaseRulesScopingKmId = args[2];

		logger.info(_METHODNAME + "loading knowledge from source files");
		if (lRequestedKmId != null && lRequestedKmId.equals("org.nyc.cir^ICE^1.0.0")) {
			lRequestedKmId = "gov.nyc.cir^ICE^1.0.0";
		}
		logger.info("Initializing ICE3 Drools 7 KnowledgeBase");

		KMId lKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lRequestedKmId);
		KMId lKMIdBase = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lBaseRulesScopingKmId);
		if (lKMId == null || lKMIdBase == null) {
			String lErrStr = "One or both incorrectly formatted knowledge module passed in; cannot continue. KMId: " + lRequestedKmId + "; KMIdBase: " + lBaseRulesScopingKmId;
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

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

		////////////////////////////////////////////////////////////////////////////////////
		// Determine Knowledge Modules Directory Location
		////////////////////////////////////////////////////////////////////////////////////
		File lKnowledgeModulesDirectory = new File(new File(baseConfigurationLocation, knowledgeModulesSubDirectory), 
				KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion()));
		if (! lKnowledgeModulesDirectory.exists()) {
			String lErrStr = "Requested ICE knowledge module does not exist: " + lKnowledgeModulesDirectory.getAbsolutePath() + " for knowledge module " + lRequestedKmId;
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Requested ICE knowledge module directory: " + lKnowledgeModulesDirectory.getAbsolutePath() + "for knowledge module " + lRequestedKmId);
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
		File lKnowledgeCommonDirectory = new File(new File(baseConfigurationLocation, knowledgeCommonSubDirectory), 
				KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMIdBase.getScopingEntityId(), lKMIdBase.getBusinessId(), lKMIdBase.getVersion()));
		if (! lKnowledgeCommonDirectory.exists()) {
			String lErrStr = "Base ICE knowledge module does not exist" + lKnowledgeCommonDirectory.getAbsolutePath() + "for common logic: " + lBaseRulesScopingKmId;;
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Base knowledge modules directory: " + lKnowledgeCommonDirectory.getAbsolutePath()+ "for common logic: " + lBaseRulesScopingKmId);
		}
		////////////////////////////////////////////////////////////////////////////////////
		// END - Get the ICE Common rules subdirectory location
		////////////////////////////////////////////////////////////////////////////////////		

		/////// Set up knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieBase kieBase = null;
		logger.info(_METHODNAME + "loading knowledge from source files");

		//////////////////////////////////////////////////////////////////////
		// Base rules and DSL
		// DSL and Base rules
		File dslFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".dsl");
		File drlFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".drl");
		File drlFileDuplicateShotSameDay = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
		File bpmnFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".bpmn");

		if (! dslFile.exists() ||  ! drlFile.exists() || ! drlFileDuplicateShotSameDay.exists() || ! bpmnFile.exists()) {
			// Try in the knowledge module directory
			dslFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".dsl");
			drlFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".drl");
			drlFileDuplicateShotSameDay = new File(lKnowledgeModulesDirectory, lRequestedKmId + "^DuplicateShotSameDay.drl");
			bpmnFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".bpmn");
			if (! dslFile.exists() ||  ! drlFile.exists() || ! drlFileDuplicateShotSameDay.exists() || ! bpmnFile.exists()) {
				String lErrStr = "Some or all ICE base rules not found; base repository location: " + baseConfigurationLocation + "; " +
						"base rules scoping entity id: " + lBaseRulesScopingKmId + "; knowledge module location: " + lRequestedKmId;
				logger.error(_METHODNAME + lErrStr);
				throw new RuntimeException(lErrStr);
			}
		}

		logger.info(_METHODNAME + "Loading knowledge base BPMN, DSL, DRL and DSLR rules");
		KieFileSystem kfs = kieServices.newKieFileSystem();			
		// BPMN file
		if (bpmnFile != null) {
			Resource bpmnResource = kieServices.getResources().newFileSystemResource(bpmnFile);
			bpmnResource.setResourceType(ResourceType.BPMN2);
			kfs.write(bpmnResource);
			logger.info(_METHODNAME + "Loaded BPMN file " + bpmnFile.getPath());
		}
		// DSL file
		if (dslFile != null) {
			Resource dslResource = kieServices.getResources().newFileSystemResource(dslFile);
			dslResource.setResourceType(ResourceType.DSL);
			kfs.write(dslResource);
			logger.info(_METHODNAME + "Loaded DSL file " + dslFile.getPath());
		}

		//////////////////////////////////////////////////////////////////////
		// Now load the Knowledge Module specific rules - Do so by reading all of the files that fit the filter for the knowledge module directory
		//////////////////////////////////////////////////////////////////////
		List<File> lFilesToExcludeFromKB = new ArrayList<File>();
		/////// lFilesToExcludeFromKB.add(drlFile);
		/////// lFilesToExcludeFromKB.add(drlFileDuplicateShotSameDay);

		// Add base rules to knowledge base
		List<File> lBaseFilesToLoad = retrieveCollectionOfDSLRsToAddToKnowledgeBase(lBaseRulesScopingKmId, lKnowledgeCommonDirectory, lFilesToExcludeFromKB);
		if (lBaseFilesToLoad.isEmpty()) {
			String lErrStr = "No base ICE rules found; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		// Load the files - DRL and DSLR files permitted for the base cdsframework rules
		for (File lFileToLoad : lBaseFilesToLoad) {
			if (lFileToLoad != null) {
				if (lFileToLoad.getName().endsWith(".drl") || lFileToLoad.getName().endsWith(".DRL")) {
					Resource lDrlFile = kieServices.getResources().newFileSystemResource(lFileToLoad);
					lDrlFile.setResourceType(ResourceType.DRL);
					kfs.write(lDrlFile);
					logger.info(_METHODNAME + "Loaded Base DRL file " + lFileToLoad.getPath());
				}
			}
		}
		for (File lFileToLoad : lBaseFilesToLoad) {
			if (lFileToLoad != null) {
				if (lFileToLoad.getName().endsWith(".dslr") || lFileToLoad.getName().endsWith(".DSLR")) {
					Resource lDslrFile = kieServices.getResources().newFileSystemResource(lFileToLoad);
					lDslrFile.setResourceType(ResourceType.DSLR);
					kfs.write(lDslrFile);
					logger.info(_METHODNAME + "Loaded Base DSLR file " + lFileToLoad.getPath());
				}
			}
		}

		// Add custom rules to knowledge base - both DRL and DSLR files permitted, DRL files loaded first.
		List<File> lFilesToLoad = retrieveCollectionOfDSLRsToAddToKnowledgeBase(lRequestedKmId, lKnowledgeModulesDirectory, lFilesToExcludeFromKB);
		// First do the DRL files, then the DSLR files
		for (File lFileToLoad : lFilesToLoad) {
			if (lFileToLoad != null) {
				if (lFileToLoad.getName().endsWith(".drl") || lFileToLoad.getName().endsWith(".DRL")) {
					Resource lDrlFile = kieServices.getResources().newFileSystemResource(lFileToLoad);
					lDrlFile.setResourceType(ResourceType.DRL);
					kfs.write(lDrlFile);
					logger.info(_METHODNAME + "Loaded DRL file " + lFileToLoad.getPath());
				}
			}
		}
		for (File lFileToLoad : lFilesToLoad) {
			if (lFileToLoad != null) {
				if (lFileToLoad.getName().endsWith(".dslr") || lFileToLoad.getName().endsWith(".DSLR")) {
					Resource lDslrFile = kieServices.getResources().newFileSystemResource(lFileToLoad);
					lDslrFile.setResourceType(ResourceType.DSLR);
					kfs.write(lDslrFile);
					logger.info(_METHODNAME + "Loaded DSLR file " + lFileToLoad.getPath());
				}
			}
		}

		//////////////////////////////////////////////////////////////////////
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
		if (kieBuilder.getResults().getMessages(Message.Level.ERROR).size() != 0) {
			String lErrStr = "KieBuilder had errors on build of: " + lRequestedKmId + ", as follows:";
			int i=1;
			for (Message lMessage : kieBuilder.getResults().getMessages()) {
				lErrStr += "\n(" + i + "): " + lMessage.getLevel().toString() + " " + lMessage.getText();
			}
			throw new RuntimeException(lErrStr);
		}	
		//////////////////////////////////////////////////////////////////////
		/////// ReleaseId kieContainerRelease = kieServices.newReleaseId(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion());
		/////// KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
		KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
		kieBase = kieContainer.getKieBase();

		File pkgFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".pkg");;
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pkgFile.getAbsolutePath()) );
			out.writeObject( kieBase );
			out.close();
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to write serialized pkg file", e);
		}
		
		logger.info(_METHODNAME + "Completed generation of pkg file");
	}
	

	private List<File> retrieveCollectionOfDSLRsToAddToKnowledgeBase(String pRequestedKmId, File pDSLRFileDirectory, List<File> pFilesToExcludeFromKB) {

		String _METHODNAME = "retrieveCollectionOfDSLRsToAddToKnowledgeBase(): ";

		if (pDSLRFileDirectory == null || pDSLRFileDirectory.exists() == false || pDSLRFileDirectory.isDirectory() == false) {
			String lErrStr = "Knowledge module specific directory does not exist; cannot continue. Directory: " + pDSLRFileDirectory.getAbsolutePath();
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

		// Obtain the files in this directory that adheres to the base and extension, ordered.
		String[] lValidFileExtensionsForCustomRules = {"drl", "dslr"};
		String[] lResultFiles = pDSLRFileDirectory.list(new FileNameWithExtensionFilterImpl(pRequestedKmId,	lValidFileExtensionsForCustomRules));
		if (lResultFiles != null && lResultFiles.length > 0) {
			Arrays.sort(lResultFiles);
		}
		if (logger.isDebugEnabled()) {
			String lDebugStr = "Custom rule files to be loaded into this knowledge module:\n";
			for (int i = 0; i < lResultFiles.length; i++) {
				if (i == lResultFiles.length - 1) {
					lDebugStr += lResultFiles[i];
				} else {
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
			for (int i = 0; i < lResultFiles.length; i++) {
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

	public class FileNameWithExtensionFilterImpl implements FilenameFilter {

		public FileNameWithExtensionFilterImpl(String pStartsWith, String[] pValidFileExtensions) {

		}

		@Override
		public boolean accept(File dir, String name) {
			return true;
		}

	}

}
