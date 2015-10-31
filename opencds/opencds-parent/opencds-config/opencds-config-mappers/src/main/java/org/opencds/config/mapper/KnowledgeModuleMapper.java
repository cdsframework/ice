package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.model.TraitId;
import org.opencds.config.api.model.impl.KnowledgeModuleImpl;
import org.opencds.config.api.model.impl.SecondaryCDMImpl;
import org.opencds.config.schema.KnowledgeModule.ConceptDeterminationMethods;
import org.opencds.config.schema.KnowledgeModule.PostProcessPlugins;
import org.opencds.config.schema.KnowledgeModule.PreProcessPlugins;
import org.opencds.config.schema.KnowledgeModules;

public abstract class KnowledgeModuleMapper {

    public static KnowledgeModule internal(org.opencds.config.schema.KnowledgeModule external) {
        if (external == null) {
            return null;
        }
        CDMId primaryCDMId = null;
        List<SecondaryCDM> secondaryCDMs = null;
        if (external.getConceptDeterminationMethods() != null) {
            if (external.getConceptDeterminationMethods().getPrimaryCDM() != null) {
                primaryCDMId = CDMIdMapper.internal(external.getConceptDeterminationMethods().getPrimaryCDM());
            }
            secondaryCDMs = new ArrayList<>();
            if (external.getConceptDeterminationMethods().getSecondaryCDM() != null) {
                for (org.opencds.config.schema.KnowledgeModule.ConceptDeterminationMethods.SecondaryCDM scdm : external
                        .getConceptDeterminationMethods().getSecondaryCDM()) {
                    CDMId cdmId = CDMIdMapper.internal(scdm);
                    secondaryCDMs.add(SecondaryCDMImpl.create(cdmId, SupportMethod.valueOf(scdm.getMethod().value())));
                }
            }
        }

        List<PluginId> preProcPlugins = null;
        if (external.getPreProcessPlugins() != null && external.getPreProcessPlugins().getPreProcessPlugin() != null) {
            preProcPlugins = PluginIdMapper.internal(external.getPreProcessPlugins().getPreProcessPlugin());
        }
        List<PluginId> postProcPlugins = null;
        if (external.getPostProcessPlugins() != null && external.getPostProcessPlugins().getPostProcessPlugin() != null) {
            postProcPlugins = PluginIdMapper.internal(external.getPostProcessPlugins().getPostProcessPlugin());
        }

        KMId kmid = KMIdMapper.internal(external.getIdentifier());
        SSId ssid = SSIdMapper.internal(external.getSemanticSignifierId());

        Boolean preload = Boolean.FALSE;
        if (external.getPackage() != null) {
            preload = Boolean.valueOf(external.getPackage().isPreload());
        }
        org.opencds.config.schema.KMStatus status = external.getStatus();
        if (status == null) {
            throw new OpenCDSRuntimeException("KM requires a valid status.");
        }
        return KnowledgeModuleImpl.create(kmid, KMStatus.valueOf(status.value()), external
                .getExecutionEngine(), ssid, primaryCDMId, secondaryCDMs, external.getPackage().getPackageType(),
                external.getPackage().getPackageId(), preload, external.getPrimaryProcess(), TraitIdMapper
                        .internal(external.getTraitId()), preProcPlugins, postProcPlugins, external.getTimestamp()
                        .toGregorianCalendar().getTime(), external.getUserId());
    }

    public static List<KnowledgeModule> internal(KnowledgeModules knowledgeModules) {
        if (knowledgeModules == null) {
            return null;
        }
        List<KnowledgeModule> internalKMs = new ArrayList<>();
        for (org.opencds.config.schema.KnowledgeModule externalKM : knowledgeModules.getKnowledgeModule()) {
            internalKMs.add(internal(externalKM));
        }
        return internalKMs;
    }

    public static org.opencds.config.schema.KnowledgeModule external(KnowledgeModule internal) {
        if (internal == null) {
            return null;
        }
        org.opencds.config.schema.KnowledgeModule external = new org.opencds.config.schema.KnowledgeModule();
        external.setIdentifier(KMIdMapper.external(internal.getKMId()));
        org.opencds.config.schema.KMStatus intKMStatus = org.opencds.config.schema.KMStatus.fromValue(internal
                .getStatus().name());
        external.setStatus(intKMStatus);
        external.setExecutionEngine(internal.getExecutionEngine());
        external.setSemanticSignifierId(SSIdMapper.external(internal.getSSId()));
        org.opencds.config.schema.KnowledgeModule.ConceptDeterminationMethods cdms = new org.opencds.config.schema.KnowledgeModule.ConceptDeterminationMethods();
        cdms.setPrimaryCDM(CDMIdMapper.external(internal.getPrimaryCDM()));
        if (internal.getSecondaryCDMs() != null) {
            for (SecondaryCDM internalSCDM : internal.getSecondaryCDMs()) {
                ConceptDeterminationMethods.SecondaryCDM extSCDM = new ConceptDeterminationMethods.SecondaryCDM();
                extSCDM.setCode(internalSCDM.getCDMId().getCode());
                extSCDM.setCodeSystem(internalSCDM.getCDMId().getCodeSystem());
                // extSCDM.setDisplayName(value); // FIXME
                // FIXME: not sure where displayname went in the internal
                // primarycdm
                extSCDM.setVersion(internalSCDM.getCDMId().getVersion());
                extSCDM.setMethod(org.opencds.config.schema.SupportMethod.fromValue(internalSCDM.getSupportMethod()
                        .name()));
                cdms.getSecondaryCDM().add(extSCDM);
            }
        }
        external.setConceptDeterminationMethods(cdms);
        org.opencds.config.schema.Package pkg = new org.opencds.config.schema.Package();
        pkg.setPackageId(internal.getPackageId());
        pkg.setPackageType(internal.getPackageType());
        pkg.setPreload(internal.isPreload());
        external.setPackage(pkg);
        external.setPrimaryProcess(internal.getPrimaryProcess());
        if (internal.getTraitIds() != null) {
            for (TraitId trait : internal.getTraitIds()) {
                external.getTraitId().add(TraitIdMapper.external(trait));
            }
        }
        if (internal.getPreProcessPluginIds() != null) {
            PreProcessPlugins ppp = new PreProcessPlugins();
            for (PluginId pid : internal.getPreProcessPluginIds()) {
                ppp.getPreProcessPlugin().add(PluginIdMapper.external(pid));
            }
            external.setPreProcessPlugins(ppp);
        }
        if (internal.getPostProcessPluginIds() != null) {
            PostProcessPlugins ppp = new PostProcessPlugins();
            for (PluginId pid : internal.getPostProcessPluginIds()) {
                ppp.getPostProcessPlugin().add(PluginIdMapper.external(pid));
            }
            external.setPostProcessPlugins(ppp);
        }
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());
        return external;
    }

    public static KnowledgeModules external(List<KnowledgeModule> internalKMs) {
        if (internalKMs == null) {
            return null;
        }
        KnowledgeModules externalKMs = new KnowledgeModules();
        for (KnowledgeModule internalKM : internalKMs) {
            externalKMs.getKnowledgeModule().add(external(internalKM));
        }
        return externalKMs;
    }

}
