package org.cdsframework.rules.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.drools.core.common.DroolsObjectOutputStream;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.opencds.config.api.model.KMId;

/**
 * The Packager class is responsible for packaging Drools knowledge modules into serialized .pkg files
 * for the ICE (Immunization Calculation Engine) rules engine. Use of the .pkg file is obsolete and
 * disabled by default. Thus, the primary usage is performing error checking and validation of knowledge modules.
 * <p>
 * This utility handles:
 * - Loading and parsing knowledge module configuration from properties files
 * - Building KieBase objects from DSL, DRL, DSLR, and BPMN rule files
 * - Combining base common rules with module-specific custom rules
 * - Serializing the compiled knowledge base into .pkg format for runtime usage
 * <p>
 * The packager supports both base common rules (shared across modules) and
 * module-specific rules, ensuring proper loading order and validation.
 * <p>
 * Usage: java Packager [--output-file] [properties-file] [knowledge-module-id] [base-rules-id]
 *
 * @author sdn
 */
public class Packager
{

    /**
     * FilenameFilter implementation that filters files based on a prefix and allowed extensions.
     * This filter is used to identify rule files (DRL/DSLR) that belong to a specific knowledge module.
     */
    public class FileNameWithExtensionFilterImpl implements FilenameFilter
    {
        private final String startsWith;
        private final String[] validExtensions;

        /**
         * Creates a new filename filter.
         *
         * @param pStartsWith          The prefix that filenames must start with (knowledge module ID)
         * @param pValidFileExtensions Array of valid file extensions (e.g., "drl", "dslr")
         */
        public FileNameWithExtensionFilterImpl(final String pStartsWith, final String[] pValidFileExtensions)
        {
            this.startsWith = pStartsWith;
            this.validExtensions = pValidFileExtensions != null ? pValidFileExtensions.clone() : new String[0];
        }

        /**
         * Tests whether or not the specified file should be included in a file list.
         *
         * @param dir  The directory in which the file was found
         * @param name The name of the file
         * @return true if the file matches the criteria, false otherwise
         */
        @Override
        public boolean accept(final File dir, final String name)
        {
            if (startsWith != null && !name.startsWith(startsWith))
                return false;

            if (validExtensions.length == 0)
                return true;

            // Check if file has one of the valid extensions
            for (String extension : validExtensions)
                if (name.toLowerCase().endsWith("." + extension.toLowerCase()))
                    return true;

            return false;
        }
    }
    private static final Logger logger = LogManager.getLogger();

    /**
     * Main entry point for the Packager utility.
     *
     * @param args Command line arguments:
     *             args[0]: --output-file flag (optional) - if present, generates .pkg file
     *             args[1]: path to properties file (default: .gcp.config/ice.properties)
     *             args[2]: knowledge module ID (default: gov.nyc.cir^ICE^1.0.0)
     *             args[3]: base rules scoping module ID (default: org.cdsframework^ICE^1.0.0)
     * @throws Exception if packaging fails
     */
    public static void main(String[] args) throws Exception
    {
        final Packager packager = new Packager();
        packager.run(args);
    }

    /**
     * Executes the complete packaging process for a knowledge module.
     * This method handles configuration loading, knowledge base building, and serialization.
     *
     * @param args Command line arguments containing configuration parameters
     * @throws Exception if any step of the packaging process fails
     */
    public void run(String[] args) throws Exception
    {
        // Parse command line arguments
        final boolean writeOutputFile = args.length > 0 && args[0].equals("--output-file");

        // Load configuration properties
        final String propsPath = args.length > 1 ? args[1] : ".gcp.config/ice.properties";
        final Properties lProps = new Properties();
        lProps.load(new FileInputStream(propsPath));

        // Determine knowledge module IDs with defaults
        final String lRequestedKmId =
                args.length > 2 && !args[2].equals("org.nyc.cir^ICE^1.0.0") ? args[2] : "gov.nyc.cir^ICE^1.0.0";
        final String lBaseRulesScopingKmId = args.length > 3 ? args[3] : "org.cdsframework^ICE^1.0.0";

        logger.info("loading knowledge from source files");
        logger.info("Initializing ICE3 Drools 7 KnowledgeBase");

        // Convert string IDs to KMId objects for validation
        final KMId lKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lRequestedKmId);
        final KMId lKMIdBase = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lBaseRulesScopingKmId);

        // Validate knowledge module IDs are properly formatted
        if (lKMId == null || lKMIdBase == null)
        {
            String lErrStr =
                    "One or both incorrectly formatted knowledge module passed in; cannot continue. KMId: %s; KMIdBase: %s".formatted(
                            lRequestedKmId, lBaseRulesScopingKmId);
            logger.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }

        // Resolve base knowledge repository directory from properties
        final String baseConfigurationLocation = Optional.of(lProps.getProperty("ice_knowledge_drools_location"))
                .map(path -> path.replace("/usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/", ""))
                .orElseThrow(() ->
                {
                    final String lErrStr = "ICE knowledge repository data location not specified in properties file";
                    logger.error(lErrStr);
                    throw new RuntimeException(lErrStr);
                });

        if (logger.isInfoEnabled())
            logger.info("ICE knowledge repository data location specified in properties file: {}", baseConfigurationLocation);

        ////////////////////////////////////////////////////////////////////////////////////
        // START - Get the ICE knowledge modules subdirectory location
        /// /////////////////////////////////////////////////////////////////////////////////
        final String knowledgeModulesSubDirectory = Optional.ofNullable(lProps.getProperty("ice_knowledge_modules_subdirectory"))
                .map(path -> path.replace("/usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/", ""))
                .orElseThrow(() ->
                {
                    final String lErrStr = "ICE knowledge modules subdirectory location not specified in properties file";
                    logger.error(lErrStr);
                    throw new RuntimeException(lErrStr);
                });

        if (logger.isDebugEnabled())
            logger.info("ICE knowledge modules data location specified in properties file: {}", knowledgeModulesSubDirectory);

        ////////////////////////////////////////////////////////////////////////////////////
        // Determine Knowledge Modules Directory Location
        /// /////////////////////////////////////////////////////////////////////////////////
        final File lKnowledgeModulesDirectory = new File(new File(baseConfigurationLocation, knowledgeModulesSubDirectory),
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMId.getScopingEntityId(), lKMId.getBusinessId(),
                        lKMId.getVersion()));

        if (!lKnowledgeModulesDirectory.exists())
        {
            final String lErrStr = "Requested ICE knowledge module does not exist: " + lKnowledgeModulesDirectory.getAbsolutePath()
                    + " for knowledge module " + lRequestedKmId;
            logger.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }
        else
            if (logger.isDebugEnabled())
                logger.debug("Requested ICE knowledge module directory: {} for knowledge module {}",
                        lKnowledgeModulesDirectory.getAbsolutePath(), lRequestedKmId);

        ////////////////////////////////////////////////////////////////////////////////////
        // END - Get the ICE knowledge modules subdirectory location
        ////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////
        // START - Get the ICE Common rules subdirectory location
        /// /////////////////////////////////////////////////////////////////////////////////
        final String knowledgeCommonSubDirectory = lProps.getProperty("ice_knowledge_common_subdirectory");
        if (knowledgeCommonSubDirectory == null)
        {
            final String lErrStr = "ICE common knowledge subdirectory location not specified in properties file";
            logger.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }
        else
            if (logger.isDebugEnabled())
                logger.info("ICE common knowledge data location specified in properties file: {}", knowledgeCommonSubDirectory);

        final File lKnowledgeCommonDirectory = new File(new File(baseConfigurationLocation, knowledgeCommonSubDirectory),
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMIdBase.getScopingEntityId(), lKMIdBase.getBusinessId(),
                        lKMIdBase.getVersion()));
        if (!lKnowledgeCommonDirectory.exists())
        {
            final String lErrStr =
                    "Base ICE knowledge module does not exist" + lKnowledgeCommonDirectory.getAbsolutePath() + "for common logic: "
                            + lBaseRulesScopingKmId;
            logger.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }
        else
            if (logger.isDebugEnabled())
                logger.debug("Base knowledge modules directory: {} for common logic: {}",
                        lKnowledgeCommonDirectory.getAbsolutePath(), lBaseRulesScopingKmId);

        ////////////////////////////////////////////////////////////////////////////////////
        // END - Get the ICE Common rules subdirectory location
        ////////////////////////////////////////////////////////////////////////////////////

        // Initialize Drools KIE services for knowledge base creation
        final KieServices kieServices = KieServices.Factory.get();
        KieBase kieBase = null;
        logger.info("loading knowledge from source files");

        // Locate base rule files (DSL, DRL, BPMN) in common directory
        File dslFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".dsl");
        File drlFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".drl");
        File drlFileDuplicateShotSameDay = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
        File bpmnFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".bpmn");

        if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists())
        {
            // Try in the knowledge module directory
            dslFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".dsl");
            drlFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".drl");
            drlFileDuplicateShotSameDay = new File(lKnowledgeModulesDirectory, lRequestedKmId + "^DuplicateShotSameDay.drl");
            bpmnFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".bpmn");
            if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists())
            {
                final String lErrStr =
                        "Some or all ICE base rules not found; base repository location: %s; base rules scoping entity id: %s; knowledge module location: %s".formatted(
                                baseConfigurationLocation, lBaseRulesScopingKmId, lRequestedKmId);
                logger.error(lErrStr);
                throw new RuntimeException(lErrStr);
            }
        }

        logger.info("Loading knowledge base BPMN, DSL, DRL and DSLR rules");
        final KieFileSystem kfs = kieServices.newKieFileSystem();
        // BPMN file
        if (bpmnFile != null)
        {
            final Resource bpmnResource = kieServices.getResources().newFileSystemResource(bpmnFile);
            bpmnResource.setResourceType(ResourceType.BPMN2);
            kfs.write(bpmnResource);
            logger.info("Loaded BPMN file {}", bpmnFile.getPath());
        }

        // DSL file
        if (dslFile != null)
        {
            final Resource dslResource = kieServices.getResources().newFileSystemResource(dslFile);
            dslResource.setResourceType(ResourceType.DSL);
            kfs.write(dslResource);
            logger.info("Loaded DSL file {}", dslFile.getPath());
        }

        //////////////////////////////////////////////////////////////////////
        // Now load the Knowledge Module specific rules - Do so by reading all of the files that fit the filter for the knowledge module directory
        /// ///////////////////////////////////////////////////////////////////
        final List<File> lFilesToExcludeFromKB = new ArrayList<File>();

        // Add base rules to knowledge base
        final List<File> lBaseFilesToLoad =
                retrieveCollectionOfDSLRsToAddToKnowledgeBase(lBaseRulesScopingKmId, lKnowledgeCommonDirectory,
                        lFilesToExcludeFromKB);

        if (lBaseFilesToLoad.isEmpty())
        {
            final String lErrStr = "No base ICE rules found; cannot continue";
            logger.error(lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Load base rule files using helper method
        loadRuleFiles(kieServices, kfs, lBaseFilesToLoad, "Base");

        // Add custom rules to knowledge base - both DRL and DSLR files permitted, DRL files loaded first.
        final List<File> lFilesToLoad =
                retrieveCollectionOfDSLRsToAddToKnowledgeBase(lRequestedKmId, lKnowledgeModulesDirectory, lFilesToExcludeFromKB);

        // Load custom rule files using helper method
        loadRuleFiles(kieServices, kfs, lFilesToLoad, "Custom");

        /// ///////////////////////////////////////////////////////////////////
        logger.info("Running KieBuilder build...");
        final KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        if (!kieBuilder.getResults().getMessages(Message.Level.ERROR).isEmpty())
        {
            String lErrStr = "KieBuilder had errors on build of: " + lRequestedKmId + ", as follows:";
            int i = 1;
            for (Message lMessage : kieBuilder.getResults().getMessages())
                lErrStr += "\n(%d) %s %s, line %d: %s".formatted(i++, lMessage.getLevel().toString(),
                        lMessage.getPath().replaceAll("^.*%s/".formatted(baseConfigurationLocation), ""), lMessage.getLine(),
                        lMessage.getText());

            logger.error(lErrStr);
            throw new RuntimeException("Completed with build errors");
        }

        if (!writeOutputFile)
        {
            logger.info("Completed without generating the pkg file (due to --output-file option not specified)");
            System.exit(0);
        }

        /// ///////////////////////////////////////////////////////////////////
        /// //// ReleaseId kieContainerRelease = kieServices.newReleaseId(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion());
        /// //// KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        final KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        kieBase = kieContainer.getKieBase();

        final File pkgFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".pkg");
        try (final OutputStream fos = new FileOutputStream(pkgFile.getAbsolutePath());
                final ObjectOutputStream out = new DroolsObjectOutputStream(fos))
        {
            out.writeObject(kieBase);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to write serialized pkg file", e);
        }

        logger.info("Completed generation of pkg file {}", pkgFile.getAbsolutePath());
    }

    /**
     * Loads DRL and DSLR files into the KieFileSystem in the correct order.
     * DRL files are loaded first, followed by DSLR files.
     *
     * @param kieServices The KieServices instance for resource creation
     * @param kfs         The KieFileSystem to write resources to
     * @param filesToLoad List of files to be loaded
     * @param logPrefix   Prefix for log messages (e.g., "Base" for base files)
     */
    private void loadRuleFiles(final KieServices kieServices, final KieFileSystem kfs, final List<File> filesToLoad,
            final String logPrefix)
    {
        // Load DRL files first
        for (File fileToLoad : filesToLoad)
        {
            if (fileToLoad != null && (fileToLoad.getName().toLowerCase().endsWith(".drl")))
            {
                final Resource drlFile = kieServices.getResources().newFileSystemResource(fileToLoad);
                drlFile.setResourceType(ResourceType.DRL);
                kfs.write(drlFile);
                logger.info("Loaded {} DRL file {}", logPrefix, fileToLoad.getPath());
            }
        }

        // Load DSLR files second
        for (File fileToLoad : filesToLoad)
        {
            if (fileToLoad != null && (fileToLoad.getName().toLowerCase().endsWith(".dslr")))
            {
                final Resource dslrFile = kieServices.getResources().newFileSystemResource(fileToLoad);
                dslrFile.setResourceType(ResourceType.DSLR);
                kfs.write(dslrFile);
                logger.info("Loaded {} DSLR file {}", logPrefix, fileToLoad.getPath());
            }
        }
    }

    /**
     * Retrieves a collection of DRL and DSLR files from the specified directory that match
     * the knowledge module ID and should be included in the knowledge base.
     *
     * @param pRequestedKmId        The knowledge module identifier used for file filtering
     * @param pDSLRFileDirectory    The directory containing rule files to scan
     * @param pFilesToExcludeFromKB List of files to exclude from the knowledge base
     * @return List of File objects representing valid rule files to include
     */
    private List<File> retrieveCollectionOfDSLRsToAddToKnowledgeBase(final String pRequestedKmId, final File pDSLRFileDirectory,
            final List<File> pFilesToExcludeFromKB)
    {
        final String _METHODNAME = "retrieveCollectionOfDSLRsToAddToKnowledgeBase(): ";

        if (pDSLRFileDirectory == null || pDSLRFileDirectory.exists() == false || pDSLRFileDirectory.isDirectory() == false)
        {
            final String lErrStr = "Knowledge module specific directory does not exist; cannot continue. Directory: "
                    + pDSLRFileDirectory.getAbsolutePath();
            logger.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        // Obtain the files in this directory that adheres to the base and extension, ordered.
        final String[] lValidFileExtensionsForCustomRules = { "drl", "dslr" };
        final String[] lResultFiles =
                pDSLRFileDirectory.list(new FileNameWithExtensionFilterImpl(pRequestedKmId, lValidFileExtensionsForCustomRules));
        if (lResultFiles != null && lResultFiles.length > 0)
            Arrays.sort(lResultFiles);

        if (logger.isDebugEnabled())
        {
            String lDebugStr = "Custom rule files to be loaded into this knowledge module:\n";
            for (int i = 0; i < lResultFiles.length; i++)
                lDebugStr += i == lResultFiles.length - 1 ? lResultFiles[i] : lResultFiles[i] + "\n";

            logger.debug(lDebugStr);
        }

        logger.info(_METHODNAME + "Determining knowledge base with custom DRL and DSLR files");
        final List<File> drlFilesToAddToKB = new ArrayList<>();
        File customRuleFile = null;
        if (lResultFiles != null)
            // Add DRL files first to KB
            for (int i = 0; i < lResultFiles.length; i++)
            {
                boolean exclusionFound = false;
                final String lResultFile = lResultFiles[i];
                customRuleFile = new File(pDSLRFileDirectory, lResultFile);
                for (File lExclusion : pFilesToExcludeFromKB)
                    if (customRuleFile.equals(lExclusion))
                    {
                        exclusionFound = true;
                        break;
                    }

                if (exclusionFound)
                    continue;

                if (customRuleFile != null && customRuleFile.exists())
                    if (lResultFile.endsWith(".drl") || lResultFile.endsWith(".DRL") || lResultFile.endsWith(".dslr")
                            || lResultFile.endsWith(".DSLR"))
                        drlFilesToAddToKB.add(customRuleFile);
            }

        return drlFilesToAddToKB;
    }

}
