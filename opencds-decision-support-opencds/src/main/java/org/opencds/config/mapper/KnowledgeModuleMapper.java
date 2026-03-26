package org.opencds.config.mapper;

import java.util.ArrayList;
import java.util.List;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PrePostProcessPluginId;
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

public abstract class KnowledgeModuleMapper
{
    public static KnowledgeModule internal(final org.opencds.config.schema.KnowledgeModule external)
    {
        if (external == null)
            return null;
        CDMId primaryCDMId = null;
        List<SecondaryCDM> secondaryCDMs = null;
        if (external.getConceptDeterminationMethods() != null)
        {
            if (external.getConceptDeterminationMethods().getPrimaryCDM() != null)
                primaryCDMId = CDMIdMapper.internal(external.getConceptDeterminationMethods().getPrimaryCDM());
            secondaryCDMs = new ArrayList<>();
            if (external.getConceptDeterminationMethods().getSecondaryCDM() != null)
            {
                for (final ConceptDeterminationMethods.SecondaryCDM scdm : external.getConceptDeterminationMethods()
                        .getSecondaryCDM())
                {
                    final CDMId cdmId = CDMIdMapper.internal(scdm);
                    secondaryCDMs.add(SecondaryCDMImpl.create(cdmId, SupportMethod.valueOf(scdm.getMethod().value())));
                }
            }
        }

        List<PrePostProcessPluginId> preProcPlugins = null;
        if (external.getPreProcessPlugins() != null && external.getPreProcessPlugins().getPreProcessPlugin() != null)
            preProcPlugins = PrePostProcessPluginIdMapper.internal(external.getPreProcessPlugins().getPreProcessPlugin());
        List<PrePostProcessPluginId> postProcPlugins = null;
        if (external.getPostProcessPlugins() != null && external.getPostProcessPlugins().getPostProcessPlugin() != null)
            postProcPlugins = PrePostProcessPluginIdMapper.internal(external.getPostProcessPlugins().getPostProcessPlugin());

        final KMId kmid = KMIdMapper.internal(external.getIdentifier());
        final SSId ssid = SSIdMapper.internal(external.getSemanticSignifierId());

        Boolean preload = Boolean.FALSE;
        if (external.getPackage() != null && external.getPackage().isPreload() != null)
            preload = external.getPackage().isPreload();
        final org.opencds.config.schema.KMStatus status = external.getStatus();
        if (status == null)
            throw new OpenCDSRuntimeException("KM requires a valid status.");

        return KnowledgeModuleImpl.create(kmid, KMStatus.valueOf(status.value()), CDSHookMapper.internal(external.getCdsHook()),
                external.getExecutionEngine(), ssid, primaryCDMId, secondaryCDMs, external.getPackage().getPackageType(),
                external.getPackage().getPackageId(), preload, external.getPrimaryProcess(),
                TraitIdMapper.internal(external.getTraitId()), preProcPlugins, postProcPlugins,
                external.getTimestamp().toGregorianCalendar().getTime(), external.getUserId());
    }

    public static List<KnowledgeModule> internal(final KnowledgeModules knowledgeModules)
    {
        if (knowledgeModules == null)
            return null;
        final List<KnowledgeModule> internalKMs = new ArrayList<>();
        for (final org.opencds.config.schema.KnowledgeModule externalKM : knowledgeModules.getKnowledgeModule())
            internalKMs.add(internal(externalKM));
        return internalKMs;
    }

    public static org.opencds.config.schema.KnowledgeModule external(final KnowledgeModule internal)
    {
        if (internal == null)
            return null;
        final org.opencds.config.schema.KnowledgeModule external = new org.opencds.config.schema.KnowledgeModule();
        external.setIdentifier(KMIdMapper.external(internal.getKMId()));
        final org.opencds.config.schema.KMStatus intKMStatus =
                org.opencds.config.schema.KMStatus.fromValue(internal.getStatus().name());
        external.setStatus(intKMStatus);
        external.setCdsHook(CDSHookMapper.external(internal.getCDSHook()));
        external.setExecutionEngine(internal.getExecutionEngine());
        external.setSemanticSignifierId(SSIdMapper.external(internal.getSSId()));
        final ConceptDeterminationMethods cdms = new ConceptDeterminationMethods();
        cdms.setPrimaryCDM(CDMIdMapper.external(internal.getPrimaryCDM()));
        if (internal.getSecondaryCDMs() != null)
        {
            for (final SecondaryCDM internalSCDM : internal.getSecondaryCDMs())
            {
                final ConceptDeterminationMethods.SecondaryCDM extSCDM = new ConceptDeterminationMethods.SecondaryCDM();
                extSCDM.setCode(internalSCDM.getCDMId().getCode());
                extSCDM.setCodeSystem(internalSCDM.getCDMId().getCodeSystem());
                extSCDM.setVersion(internalSCDM.getCDMId().getVersion());
                extSCDM.setMethod(org.opencds.config.schema.SupportMethod.fromValue(internalSCDM.getSupportMethod().name()));
                cdms.getSecondaryCDM().add(extSCDM);
            }
        }
        external.setConceptDeterminationMethods(cdms);
        final org.opencds.config.schema.Package pkg = new org.opencds.config.schema.Package();
        pkg.setPackageId(internal.getPackageId());
        pkg.setPackageType(internal.getPackageType());
        pkg.setPreload(internal.isPreload());
        external.setPackage(pkg);
        external.setPrimaryProcess(internal.getPrimaryProcess());
        if (internal.getTraitIds() != null)
        {
            for (final TraitId trait : internal.getTraitIds())
                external.getTraitId().add(TraitIdMapper.external(trait));
        }
        if (internal.getPreProcessPluginIds() != null)
        {
            final PreProcessPlugins ppp = new PreProcessPlugins();
            for (final PrePostProcessPluginId pid : internal.getPreProcessPluginIds())
                ppp.getPreProcessPlugin().add(PrePostProcessPluginIdMapper.external(pid));
            external.setPreProcessPlugins(ppp);
        }
        if (internal.getPostProcessPluginIds() != null)
        {
            final PostProcessPlugins ppp = new PostProcessPlugins();
            for (final PrePostProcessPluginId pid : internal.getPostProcessPluginIds())
                ppp.getPostProcessPlugin().add(PrePostProcessPluginIdMapper.external(pid));
            external.setPostProcessPlugins(ppp);
        }
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.getTimestamp()));
        external.setUserId(internal.getUserId());
        return external;
    }

    public static KnowledgeModules external(final List<KnowledgeModule> internalKMs)
    {
        if (internalKMs == null)
            return null;
        final KnowledgeModules externalKMs = new KnowledgeModules();
        for (final KnowledgeModule internalKM : internalKMs)
            externalKMs.getKnowledgeModule().add(external(internalKM));
        return externalKMs;
    }
}
