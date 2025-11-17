package org.cdsframework.rules.packager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class Packager
{
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
    public static void main(final String[] args) throws Exception
    {
        new Packager().run(args);
    }

    /**
     * Executes the complete packaging process for a knowledge module.
     * This method handles configuration loading, knowledge base building, and serialization.
     *
     * @param args Command line arguments containing configuration parameters
     * @throws Exception if any step of the packaging process fails
     */
    public void run(final String[] args) throws Exception
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

        log.info("loading knowledge from source files");
        log.info("Initializing ICE3 Drools 7 KnowledgeBase");

        // Convert string IDs to KMId objects for validation
        final KMId lKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lRequestedKmId);
        final KMId lKMIdBase = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lBaseRulesScopingKmId);

        // Validate knowledge module IDs are properly formatted
        if (lKMId == null || lKMIdBase == null)
        {
            final String lErrStr =
                    "One or both incorrectly formatted knowledge module passed in; cannot continue. KMId: %s; KMIdBase: %s".formatted(
                            lRequestedKmId, lBaseRulesScopingKmId);
            log.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }

        // Resolve base knowledge repository directory from properties
        final String baseConfigurationLocation = Optional.of(lProps.getProperty("ice_knowledge_drools_location"))
                .map(path -> path.replace("/usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/", ""))
                .orElseThrow(() ->
                {
                    final String lErrStr = "ICE knowledge repository data location not specified in properties file";
                    log.error(lErrStr);
                    return new RuntimeException(lErrStr);
                });

        if (log.isInfoEnabled())
            log.info("ICE knowledge repository data location specified in properties file: {}", baseConfigurationLocation);

        ////////////////////////////////////////////////////////////////////////////////////
        // START - Get the ICE knowledge modules subdirectory location
        /// /////////////////////////////////////////////////////////////////////////////////
        final String knowledgeModulesSubDirectory = Optional.ofNullable(lProps.getProperty("ice_knowledge_modules_subdirectory"))
                .map(path -> path.replace("/usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/", ""))
                .orElseThrow(() ->
                {
                    final String lErrStr = "ICE knowledge modules subdirectory location not specified in properties file";
                    log.error(lErrStr);
                    return new RuntimeException(lErrStr);
                });

        if (log.isDebugEnabled())
            log.info("ICE knowledge modules data location specified in properties file: {}", knowledgeModulesSubDirectory);

        ////////////////////////////////////////////////////////////////////////////////////
        // Determine Knowledge Modules Directory Location
        /// /////////////////////////////////////////////////////////////////////////////////
        final Path lKnowledgeModulesDirectory = Path.of(baseConfigurationLocation, knowledgeModulesSubDirectory,
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMId.getScopingEntityId(), lKMId.getBusinessId(),
                        lKMId.getVersion()));

        if (!Files.exists(lKnowledgeModulesDirectory))
        {
            final String lErrStr = "Requested ICE knowledge module does not exist: %s for knowledge module %s".formatted(
                    lKnowledgeModulesDirectory, lRequestedKmId);
            log.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isDebugEnabled())
            log.debug("Requested ICE knowledge module directory: {} for knowledge module {}", lKnowledgeModulesDirectory,
                    lRequestedKmId);

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
            log.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isDebugEnabled())
            log.info("ICE common knowledge data location specified in properties file: {}", knowledgeCommonSubDirectory);

        final Path lKnowledgeCommonDirectory = Path.of(baseConfigurationLocation, knowledgeCommonSubDirectory,
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMIdBase.getScopingEntityId(), lKMIdBase.getBusinessId(),
                        lKMIdBase.getVersion()));
        if (!Files.exists(lKnowledgeCommonDirectory))
        {
            final String lErrStr =
                    "Base ICE knowledge module does not exist%sfor common logic: %s".formatted(lKnowledgeCommonDirectory,
                            lBaseRulesScopingKmId);
            log.error(lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isDebugEnabled())
            log.debug("Base knowledge modules directory: {} for common logic: {}", lKnowledgeCommonDirectory,
                    lBaseRulesScopingKmId);

        ////////////////////////////////////////////////////////////////////////////////////
        // END - Get the ICE Common rules subdirectory location
        ////////////////////////////////////////////////////////////////////////////////////

        // Initialize Drools KIE services for knowledge base creation
        final KieServices kieServices = KieServices.Factory.get();
        KieBase kieBase = null;
        log.info("loading knowledge from source files");

        // Locate base rule files (DSL, DRL, BPMN) in common directory
        Path dslFile = lKnowledgeCommonDirectory.resolve(lBaseRulesScopingKmId + ".dsl");
        Path drlFile = lKnowledgeCommonDirectory.resolve(lBaseRulesScopingKmId + ".drl");
        Path drlFileDuplicateShotSameDay = lKnowledgeCommonDirectory.resolve(lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
        Path bpmnFile = lKnowledgeCommonDirectory.resolve(lBaseRulesScopingKmId + ".bpmn");

        if (!Files.exists(dslFile) || !Files.exists(drlFile) || !Files.exists(drlFileDuplicateShotSameDay) || !Files.exists(
                bpmnFile))
        {
            // Try in the knowledge module directory
            dslFile = lKnowledgeModulesDirectory.resolve(lRequestedKmId + ".dsl");
            drlFile = lKnowledgeModulesDirectory.resolve(lRequestedKmId + ".drl");
            drlFileDuplicateShotSameDay = lKnowledgeModulesDirectory.resolve(lRequestedKmId + "^DuplicateShotSameDay.drl");
            bpmnFile = lKnowledgeModulesDirectory.resolve(lRequestedKmId + ".bpmn");
            if (!Files.exists(dslFile) || !Files.exists(drlFile) || !Files.exists(drlFileDuplicateShotSameDay) || !Files.exists(
                    bpmnFile))
            {
                final String lErrStr =
                        "Some or all ICE base rules not found; base repository location: %s; base rules scoping entity id: %s; knowledge module location: %s".formatted(
                                baseConfigurationLocation, lBaseRulesScopingKmId, lRequestedKmId);
                log.error(lErrStr);
                throw new RuntimeException(lErrStr);
            }
        }

        log.info("Loading knowledge base BPMN, DSL, DRL and DSLR rules");
        final KieFileSystem kfs = kieServices.newKieFileSystem();
        // BPMN file
        final Resource bpmnResource = kieServices.getResources().newFileSystemResource(bpmnFile.toFile());
        bpmnResource.setResourceType(ResourceType.BPMN2);
        kfs.write(bpmnResource);
        log.info("Loaded BPMN file {}", bpmnFile);

        // DSL file
        final Resource dslResource = kieServices.getResources().newFileSystemResource(dslFile.toFile());
        dslResource.setResourceType(ResourceType.DSL);
        kfs.write(dslResource);
        log.info("Loaded DSL file {}", dslFile);

        //////////////////////////////////////////////////////////////////////
        // Now load the Knowledge Module specific rules - Do so by reading all of the files that fit the filter for the knowledge module directory
        /// ///////////////////////////////////////////////////////////////////
        final Set<Path> lFilesToExcludeFromKB = new HashSet<>();

        // Add base rules to knowledge base
        final List<Path> lBaseFilesToLoad =
                retrieveCollectionOfDSLRsToAddToKnowledgeBase(lBaseRulesScopingKmId, lKnowledgeCommonDirectory,
                        lFilesToExcludeFromKB);

        if (lBaseFilesToLoad.isEmpty())
        {
            final String lErrStr = "No base ICE rules found; cannot continue";
            log.error(lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Load base rule files using helper method
        loadRuleFiles(kieServices, kfs, lBaseFilesToLoad, "Base");

        // Add custom rules to knowledge base - both DRL and DSLR files permitted, DRL files loaded first.
        final List<Path> lFilesToLoad =
                retrieveCollectionOfDSLRsToAddToKnowledgeBase(lRequestedKmId, lKnowledgeModulesDirectory, lFilesToExcludeFromKB);

        // Load custom rule files using helper method
        loadRuleFiles(kieServices, kfs, lFilesToLoad, "Custom");

        /// ///////////////////////////////////////////////////////////////////
        log.info("Running KieBuilder build...");
        final KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        if (!kieBuilder.getResults().getMessages(Message.Level.ERROR).isEmpty())
        {
            final StringBuilder lErrStr = new StringBuilder();
            lErrStr.append("KieBuilder had errors on build of: ").append(lRequestedKmId).append(", as follows:");
            int i = 1;
            for (final Message lMessage : kieBuilder.getResults().getMessages())
                lErrStr.append("\n(%d) %s %s, line %d: %s".formatted(i++, lMessage.getLevel().toString(),
                        lMessage.getPath().replaceAll("^.*%s/".formatted(baseConfigurationLocation), ""), lMessage.getLine(),
                        lMessage.getText()));

            log.error(lErrStr.toString());
            throw new RuntimeException("Completed with build errors");
        }

        if (!writeOutputFile)
        {
            log.info("Completed without generating the pkg file (due to --output-file option not specified)");
            System.exit(0);
        }

        /// ///////////////////////////////////////////////////////////////////
        /// //// ReleaseId kieContainerRelease = kieServices.newReleaseId(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion());
        /// //// KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        final KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        kieBase = kieContainer.getKieBase();

        final Path pkgFile = lKnowledgeModulesDirectory.resolve(lRequestedKmId + ".pkg");
        try (final ObjectOutputStream out = new DroolsObjectOutputStream(Files.newOutputStream(pkgFile)))
        {
            out.writeObject(kieBase);
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Failed to write serialized pkg file", e);
        }

        log.info("Completed generation of pkg file {}", pkgFile);
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
    private void loadRuleFiles(final KieServices kieServices, final KieFileSystem kfs, final List<Path> filesToLoad,
            final String logPrefix)
    {
        // Load DRL files first
        for (final Path fileToLoad : filesToLoad)
        {
            if (fileToLoad != null && (fileToLoad.getFileName().toString().toLowerCase().endsWith(".drl")))
            {
                final Resource drlFile = kieServices.getResources().newFileSystemResource(fileToLoad.toFile());
                drlFile.setResourceType(ResourceType.DRL);
                kfs.write(drlFile);
                log.info("Loaded {} DRL file {}", logPrefix, fileToLoad);
            }
        }

        // Load DSLR files second
        for (final Path fileToLoad : filesToLoad)
        {
            if (fileToLoad != null && (fileToLoad.getFileName().toString().toLowerCase().endsWith(".dslr")))
            {
                final Resource dslrFile = kieServices.getResources().newFileSystemResource(fileToLoad.toFile());
                dslrFile.setResourceType(ResourceType.DSLR);
                kfs.write(dslrFile);
                log.info("Loaded {} DSLR file {}", logPrefix, fileToLoad);
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
    private List<Path> retrieveCollectionOfDSLRsToAddToKnowledgeBase(final String pRequestedKmId, final Path pDSLRFileDirectory,
            final Set<Path> pFilesToExcludeFromKB)
    {
        final String _METHODNAME = "retrieveCollectionOfDSLRsToAddToKnowledgeBase(): ";

        if (pDSLRFileDirectory == null || !Files.exists(pDSLRFileDirectory) || !Files.exists(pDSLRFileDirectory))
        {
            final String lErrStr =
                    "Knowledge module specific directory does not exist; cannot continue. Directory: " + pDSLRFileDirectory;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new RuntimeException(lErrStr);
        }

        log.info(_METHODNAME + "Determining knowledge base with custom DRL and DSLR files");

        // Obtain the files in this directory that adheres to the base and extension, ordered.
        final List<Path> lResultFiles;
        try (final Stream<Path> stream = Files.list(pDSLRFileDirectory))
        {
            final List<String> lValidFileExtensionsForCustomRules = List.of(".drl", ".dslr");

            lResultFiles = stream.filter(p -> p.getFileName().toString().startsWith(pRequestedKmId))
                    .filter(p -> lValidFileExtensionsForCustomRules.stream()
                            .anyMatch(extension -> p.getFileName().toString().toLowerCase().endsWith(extension)))
                    .filter(Predicate.not(pFilesToExcludeFromKB::contains))
                    .sorted()
                    .toList();
        }
        catch (final IOException ignored)
        {
            final String lErrStr = "Failed to retrieve list of files in directory: " + pDSLRFileDirectory;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (ObjectUtils.isEmpty(lResultFiles))
            return List.of();

        if (log.isDebugEnabled())
        {
            log.debug("Custom rule files to be loaded into this knowledge module:\n{}",
                    lResultFiles.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining("\n")));
        }

        return lResultFiles;
    }
}
