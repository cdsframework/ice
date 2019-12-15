package org.cdsframework.rules.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;

/**
 *
 * @author sdn
 */
public class Packager {

    private static final Log logger = LogFactory.getLog(Packager.class);

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

        /////// Get the ICE knowledge repository directory location
        String baseConfigurationLocation = lProps.getProperty("ice_knowledge_repository_location");
        if (baseConfigurationLocation == null) {
            String lErrStr = "ICE knowledge repository data location not specified in properties file";
            logger.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        } else {
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
        } else {
            if (logger.isDebugEnabled()) {
                String lInfoStr = "ICE knowledge modules data location specified in properties file: " + knowledgeModulesSubDirectory;
                logger.info(lInfoStr);
            }
        }
        /////// 
        // Determine Knowledge Modules Directory Location
        ///////
        File lKnowledgeModulesDirectory = new File(baseConfigurationLocation, knowledgeModulesSubDirectory);
        if (!new File(lKnowledgeModulesDirectory, lRequestedKmId).exists()) {
            String lErrStr = "Requested ICE knowledge module does not exist: " + lKnowledgeModulesDirectory.getAbsolutePath() + "; knowledge module " + lRequestedKmId;
            logger.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        } else if (logger.isDebugEnabled()) {
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
        } else {
            if (logger.isDebugEnabled()) {
                String lInfoStr = "ICE common knowledge data location specified in properties file: " + knowledgeCommonSubDirectory;
                logger.info(lInfoStr);
            }
        }

        File lKnowledgeCommonDirectory = new File(baseConfigurationLocation, knowledgeCommonSubDirectory);
        if (!new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId).exists()) {
            String lErrStr = "Base ICE knowledge module does not exist" + lKnowledgeCommonDirectory.getAbsolutePath() + "; knowledge module " + lBaseRulesScopingKmId;;
            logger.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        } else if (logger.isDebugEnabled()) {
            logger.debug(_METHODNAME + "Base knowledge modules directory: " + lKnowledgeCommonDirectory.getAbsolutePath() + "; knowledge module " + lBaseRulesScopingKmId);
        }
        ////////////////////////////////////////////////////////////////////////////////////
        // END - Get the ICE Common rules subdirectory location
        ////////////////////////////////////////////////////////////////////////////////////		

        pkgFile = new File(lKnowledgeModulesDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".pkg");

        KnowledgeBase lKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        //////////////////////////////////////////////////////////////////////
        // Base rules and DSL
        // DSL and Base rules
        dslFile = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".dsl");
        drlFile = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".drl");
        drlFileDuplicateShotSameDay = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
        bpmnFile = new File(baseConfigurationLocation, knowledgeCommonSubDirectory + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".bpmn");

        if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists()) {
            // Try in the knowledge module directory
            dslFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".dsl");
            drlFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".drl");
            drlFileDuplicateShotSameDay = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + "^DuplicateShotSameDay.drl");
            bpmnFile = new File(baseConfigurationLocation, knowledgeModulesSubDirectory + "/" + lRequestedKmId + "/" + lRequestedKmId + ".bpmn");
            if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists()) {
                String lErrStr = "Some or all ICE base rules not found; base repository location: " + baseConfigurationLocation + "; "
                        + "base rules scoping entity id: " + lBaseRulesScopingKmId + "; knowledge module location: " + lRequestedKmId;
                logger.error(_METHODNAME + lErrStr);
                throw new RuntimeException(lErrStr);
            }
        }

        logger.info(_METHODNAME + "Loading knowledge base BPMN, DSL, DRL and DSLR rules");

        // BPMN file
        if (bpmnFile != null) {
            knowledgeBuilder.add(ResourceFactory.newFileResource(bpmnFile), ResourceType.BPMN2);
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
        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException("KnowledgeBuilder had errors on build of: '" + lRequestedKmId + "', " + knowledgeBuilder.getErrors().toString());
        }
        if (knowledgeBuilder.getKnowledgePackages().size() == 0) {
            throw new RuntimeException("KnowledgeBuilder did not find any VALID '.drl', 'dsl', '.bpmn' or '.pkg' files for: '" + lRequestedKmId + "', " + knowledgeBuilder.getErrors().toString());
        }
        //////////////////////////////////////////////////////////////////////

        lKnowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

        if (logger.isDebugEnabled()) {
            logger.debug(_METHODNAME + "since pkg file did not exist and knowledge package was loaded via source, persisting dynamically loaded knowledge base to a pkg file for future use");
        }

        //get the generated package (change this if you have more than one package)
        // KnowledgePackage kpkg = (KnowledgePackage) knowledgeBuilder.getKnowledgePackages().iterator().next();
        Collection<KnowledgePackage> kpkgs = knowledgeBuilder.getKnowledgePackages();

        //	write out the package to a file
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pkgFile.getAbsolutePath()));
            out.writeObject(kpkgs);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to write serialized pkg file", e);
        }

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
        String[] lResultFiles = pDSLRFileDirectory.list(
                new FileNameWithExtensionFilterImpl(
                        pRequestedKmId,
                        lValidFileExtensionsForCustomRules)
        );
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
