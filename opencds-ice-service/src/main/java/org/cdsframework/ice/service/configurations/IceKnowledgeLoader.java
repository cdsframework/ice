/**
 * Copyright (C) 2024 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.ICEPropertiesDataConfiguration;
import org.cdsframework.ice.util.FileNameWithExtensionFilterImpl;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KMIdImpl;

public class IceKnowledgeLoader implements KnowledgeLoader<InputStream, IceKnowledgePackage> {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public IceKnowledgePackage loadKnowledgePackage(final KnowledgeModule knowledgeModule,
            final Function<KnowledgeModule, InputStream> knowledgeModuleInputSreamFunction) {
        String _METHODNAME = "loadKnowledgePackage(): ";
        if (knowledgeModule == null) {
            String lErrStr = "KnowledgeModule not supplied";
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

        String lRequestedKmId =
                KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion());
        if (lRequestedKmId != null && lRequestedKmId.equals("org.nyc.cir^ICE^1.0.0")) {
            lRequestedKmId = "gov.nyc.cir^ICE^1.0.0";
            lKMId = KMIdImpl.create("gov.nyc.cir", "ICE", "1.0.0");
        }
        logger.info("Initializing ICE3 Drools 7 KnowledgeBase - Knowledge Module " + lRequestedKmId);

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

        ////////////////////////////////////////////////////////////////////////////////////
        // Determine Knowledge Modules Directory Location
        ////////////////////////////////////////////////////////////////////////////////////
        File lKnowledgeModulesDirectory = new File(new File(baseConfigurationLocation, knowledgeModulesSubDirectory),
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMId.getScopingEntityId(), lKMId.getBusinessId(), lKMId.getVersion()));
        if (!lKnowledgeModulesDirectory.exists()) {
            String lErrStr = "Requested ICE knowledge module does not exist: " + lKnowledgeModulesDirectory.getAbsolutePath() + " for knowledge module " + lRequestedKmId;
            logger.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }
        else
            if (logger.isDebugEnabled()) {
                logger.debug(_METHODNAME + "Requested ICE knowledge module directory: " + lKnowledgeModulesDirectory.getAbsolutePath() + "for knowledge module "
                        + lRequestedKmId);
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
        final String lBaseRulesScopingKmId =
                KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(iceconfig.getBaseRulesScopingEntityId(), lKMId.getBusinessId(),
                        iceconfig.getBaseRulesVersion());
        File lKnowledgeCommonDirectory = new File(new File(baseConfigurationLocation, knowledgeCommonSubDirectory),
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(iceconfig.getBaseRulesScopingEntityId(), lKMId.getBusinessId(),
                        iceconfig.getBaseRulesVersion()));
        if (!lKnowledgeCommonDirectory.exists()) {
            String lErrStr = "Base ICE knowledge module does not exist" + lKnowledgeCommonDirectory.getAbsolutePath() + "for common logic: " + lBaseRulesScopingKmId;
            logger.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }
        else
            if (logger.isDebugEnabled()) {
                logger.debug(
                        _METHODNAME + "Base knowledge modules directory: " + lKnowledgeCommonDirectory.getAbsolutePath() + "for common logic: " + lBaseRulesScopingKmId);
            }
        ////////////////////////////////////////////////////////////////////////////////////
        // END - Get the ICE Common rules subdirectory location
        ////////////////////////////////////////////////////////////////////////////////////

        ///////
        // Load knowledge from pkg file?
        String loadRulesFromPkgFile = lProps.getProperty("load_knowledge_from_pkg_file");
        boolean loadRulesFromPkgFileBool = false;
        if (loadRulesFromPkgFile != null && loadRulesFromPkgFile.equals("Y")) {
            pkgFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".pkg");
            String lInfoStr = "ICE rules will be loaded from pkg file (if available), as per properties file setting. Pkg file: " + pkgFile.getAbsolutePath();
            loadRulesFromPkgFileBool = true;
            logger.info(_METHODNAME + lInfoStr);
        }
        else {
            String lInfoStr = "ICE rules will be _not_ be loaded from pkg file, as per properties file setting";
            logger.info(_METHODNAME + lInfoStr);
        }

        /////// Set up knowledge base
        KieServices kieServices = KieServices.Factory.get();
        KieBase kieBase = null;
        if (loadRulesFromPkgFileBool && pkgFile != null && pkgFile.exists()) {
            logger.info(_METHODNAME + "loading knowledge from pkg file: " + pkgFile.getAbsolutePath());
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pkgFile.getAbsolutePath()))) {
                kieBase = (KieBase) ois.readObject();
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
            dslFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".dsl");
            drlFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".drl");
            drlFileDuplicateShotSameDay = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
            bpmnFile = new File(lKnowledgeCommonDirectory, lBaseRulesScopingKmId + ".bpmn");

            if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists()) {
                // Try in the knowledge module directory
                dslFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".dsl");
                drlFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".drl");
                drlFileDuplicateShotSameDay = new File(lKnowledgeModulesDirectory, lRequestedKmId + "^DuplicateShotSameDay.drl");
                bpmnFile = new File(lKnowledgeModulesDirectory, lRequestedKmId + ".bpmn");
                if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists()) {
                    String lErrStr =
                            "Some or all ICE base rules not found; base repository location: " + baseConfigurationLocation + "; " + "base rules scoping entity id: "
                                    + lBaseRulesScopingKmId + "; knowledge module location: " + lRequestedKmId;
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

            final ClassLoader classLoader = getClass().getClassLoader();
            //////////////////////////////////////////////////////////////////////
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs, classLoader).buildAll();
            if (kieBuilder.getResults().getMessages(Message.Level.ERROR).size() != 0) {
                String lErrStr = "KieBuilder had errors on build of: " + lRequestedKmId + ", as follows:";
                int i = 1;
                for (Message lMessage : kieBuilder.getResults().getMessages()) {
                    lErrStr += "\n(" + i + "): " + lMessage.getLevel().toString() + " " + lMessage.getText();
                }
                throw new RuntimeException(lErrStr);
            }
            //////////////////////////////////////////////////////////////////////
            KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId(), classLoader);
            kieBase = kieContainer.getKieBase();
        }

        if (loadRulesFromPkgFileBool == false && pkgFile != null && pkgFile.exists() == false) {
            if (logger.isDebugEnabled()) {
                logger.debug(_METHODNAME
                        + "since pkg file did not exist and knowledge package was loaded via source, persisting dynamically loaded knowledge base to a pkg file for future use");
            }
            //	write out the package to a file
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pkgFile.getAbsolutePath()));
                out.writeObject(kieBase);
                out.close();
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to write serialized pkg file", e);
            }
        }

        /////// knowledgePackageService.putPackage(knowledgeModule, kieBase);

        logger.info("Date/Time " + lRequestedKmId + "; Base Rules Scoping Km Id: " + lBaseRulesScopingKmId + "; Initialized: " + new Date());

        return new IceKnowledgePackage(lKMId, kieBase);
    }

    private static List<File> retrieveCollectionOfDSLRsToAddToKnowledgeBase(String pRequestedKmId, File pDSLRFileDirectory, List<File> pFilesToExcludeFromKB) {

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
            for (int i = 0; i < lResultFiles.length; i++) {
                if (i == lResultFiles.length - 1) {
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
}
