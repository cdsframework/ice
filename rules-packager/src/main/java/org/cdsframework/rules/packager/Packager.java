package org.cdsframework.rules.packager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
        final String _METHODNAME = "main ";
        Packager packager = new Packager();
        packager.run(args);
    }

    public void run(String[] args) throws Exception {
        final String _METHODNAME = "run ";

        String lBaseRulesScopingKmId = args[0];
        String lRequestedKmId = args[1];
        String path = args[2];

        logger.info(_METHODNAME + "loading knowledge from source files");

        KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        config.setProperty("drools.accumulate.function.maxDate", "org.cdsframework.ice.service.MaximumDateAccumulateFunction");
        config.setProperty("drools.accumulate.function.minDate", "org.cdsframework.ice.service.MinimumDateAccumulateFunction");
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config);

        File pkgFile = new File(path + "/" + lRequestedKmId + "/" + lRequestedKmId + ".pkg");
        KnowledgeBase lKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        File drlFile = null;
        File drlFileDuplicateShotSameDay = null;
        File dslFile = null;
        File bpmnFile = null;

        //////////////////////////////////////////////////////////////////////
        // Base rules and DSL
        // DSL and Base rules
        dslFile = new File(path + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".dsl");
        drlFile = new File(path + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".drl");
        drlFileDuplicateShotSameDay = new File(path + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + "^DuplicateShotSameDay.drl");
        bpmnFile = new File(path + "/" + lBaseRulesScopingKmId + "/" + lBaseRulesScopingKmId + ".bpmn");

        if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists()) {
            // Try in the knowledge module directory
            dslFile = new File(path + "/" + lRequestedKmId + "/" + lRequestedKmId + ".dsl");
            drlFile = new File(path + "/" + lRequestedKmId + "/" + lRequestedKmId + ".drl");
            drlFileDuplicateShotSameDay = new File(path + "/" + lRequestedKmId + "/" + lRequestedKmId + "^DuplicateShotSameDay.drl");
            bpmnFile = new File(path + "/" + lRequestedKmId + "/" + lRequestedKmId + ".bpmn");
            if (!dslFile.exists() || !drlFile.exists() || !drlFileDuplicateShotSameDay.exists() || !bpmnFile.exists()) {
                String lErrStr = "Some or all ICE base rules not found; base repository location: " + path + "; "
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
        File dslrBaseFileDirectory = new File(path + "/" + lBaseRulesScopingKmId);
        List<File> lBaseFilesToLoad = retrieveCollectionOfDSLRsToAddToKnowledgeBase(lBaseRulesScopingKmId, dslrBaseFileDirectory, lFilesToExcludeFromKB);
        if (lBaseFilesToLoad.isEmpty()) {
            String lErrStr = "No base ICE rules found; cannot continue";
            logger.error(_METHODNAME + lErrStr);
            throw new IllegalStateException(lErrStr);
        }
        // Load the files - only DRL files permitted for the base cdsframework rules
        for (File lFileToLoad : lBaseFilesToLoad) {
            if (lFileToLoad != null) {
                if (lFileToLoad.getName().endsWith(".drl") || lFileToLoad.getName().endsWith(".DRL")) {
                    knowledgeBuilder.add(ResourceFactory.newFileResource(lFileToLoad), ResourceType.DRL);
                    logger.info(_METHODNAME + "Loaded DRL file " + lFileToLoad.getPath());
                }
            }
        }

        // Add custom rules to knowledge base - both DRL and DSLR files permitted, DRL files loaded first.
        File dslrFileDirectory = new File(path + "/" + lRequestedKmId);
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
