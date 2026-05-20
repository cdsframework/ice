package org.opencds.config.mapper;

import java.util.List;
import java.util.Optional;

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

        final CDMId primaryCDMId;
        final List<SecondaryCDM> secondaryCDMs;
        if (external.getConceptDeterminationMethods() != null)
        {
            primaryCDMId = Optional.ofNullable(external.getConceptDeterminationMethods().getPrimaryCDM())
                    .map(CDMIdMapper::internal)
                    .orElse(null);

            secondaryCDMs = Optional.ofNullable(external.getConceptDeterminationMethods().getSecondaryCDM())
                    .map(secondaryCDMS -> secondaryCDMS.stream()
                            .map(scdm -> new SecondaryCDM(CDMIdMapper.internal(scdm),
                                    SupportMethod.valueOf(scdm.getMethod().value())))
                            .toList())
                    .orElseGet(List::of);
        }
        else
        {
            primaryCDMId = null;
            secondaryCDMs = null;
        }

        final List<PrePostProcessPluginId> preProcPlugins = Optional.ofNullable(external.getPreProcessPlugins())
                .map(PreProcessPlugins::getPreProcessPlugin)
                .map(PrePostProcessPluginIdMapper::internal)
                .orElse(null);

        final List<PrePostProcessPluginId> postProcPlugins = Optional.ofNullable(external.getPostProcessPlugins())
                .map(PostProcessPlugins::getPostProcessPlugin)
                .map(PrePostProcessPluginIdMapper::internal)
                .orElse(null);

        final KMId kmid = KMIdMapper.internal(external.getIdentifier());
        final SSId ssid = SSIdMapper.internal(external.getSemanticSignifierId());

        final Boolean preload =
                Optional.ofNullable(external.getPackage()).map(org.opencds.config.schema.Package::isPreload).orElse(Boolean.FALSE);

        final org.opencds.config.schema.KMStatus status = external.getStatus();
        if (status == null)
            throw new OpenCDSRuntimeException("KM requires a valid status.");

        return new KnowledgeModule(kmid, KMStatus.valueOf(status.value()), CDSHookMapper.internal(external.getCdsHook()),
                external.getExecutionEngine(), ssid, primaryCDMId, secondaryCDMs, external.getPackage().getPackageType(),
                external.getPackage().getPackageId(), preload, external.getPrimaryProcess(),
                TraitIdMapper.internal(external.getTraitId()), preProcPlugins, postProcPlugins,
                external.getTimestamp().toGregorianCalendar().toZonedDateTime().toLocalDate(), external.getUserId());
    }

    public static List<KnowledgeModule> internal(final KnowledgeModules knowledgeModules)
    {
        if (knowledgeModules == null)
            return null;

        return knowledgeModules.getKnowledgeModule().stream().map(KnowledgeModuleMapper::internal).toList();
    }

    public static org.opencds.config.schema.KnowledgeModule external(final KnowledgeModule internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.KnowledgeModule external = new org.opencds.config.schema.KnowledgeModule();
        external.setIdentifier(KMIdMapper.external(internal.kmId()));
        external.setStatus(org.opencds.config.schema.KMStatus.fromValue(internal.status().name()));
        external.setCdsHook(CDSHookMapper.external(internal.cdsHook()));
        external.setExecutionEngine(internal.executionEngine());
        external.setSemanticSignifierId(SSIdMapper.external(internal.ssId()));

        final ConceptDeterminationMethods cdms = new ConceptDeterminationMethods();
        cdms.setPrimaryCDM(CDMIdMapper.external(internal.primaryCDM()));
        if (internal.secondaryCDMs() != null)
            internal.secondaryCDMs().stream().map(internalSCDM ->
            {
                final ConceptDeterminationMethods.SecondaryCDM extSCDM = new ConceptDeterminationMethods.SecondaryCDM();
                extSCDM.setCode(internalSCDM.cdmId().code());
                extSCDM.setCodeSystem(internalSCDM.cdmId().codeSystem());
                extSCDM.setVersion(internalSCDM.cdmId().version());
                extSCDM.setMethod(org.opencds.config.schema.SupportMethod.fromValue(internalSCDM.supportMethod().name()));
                return extSCDM;
            }).forEach(cdms.getSecondaryCDM()::add);
        external.setConceptDeterminationMethods(cdms);

        final org.opencds.config.schema.Package pkg = new org.opencds.config.schema.Package();
        pkg.setPackageId(internal.packageId());
        pkg.setPackageType(internal.packageType());
        pkg.setPreload(internal.preload());
        external.setPackage(pkg);

        external.setPrimaryProcess(internal.primaryProcess());
        if (internal.traitIds() != null)
            internal.traitIds().stream().map(TraitIdMapper::external).forEach(external.getTraitId()::add);

        if (internal.preProcessPluginIds() != null)
        {
            final PreProcessPlugins ppp = new PreProcessPlugins();
            internal.preProcessPluginIds()
                    .stream()
                    .map(PrePostProcessPluginIdMapper::external)
                    .forEach(ppp.getPreProcessPlugin()::add);
            external.setPreProcessPlugins(ppp);
        }

        if (internal.postProcessPluginIds() != null)
        {
            final PostProcessPlugins ppp = new PostProcessPlugins();
            internal.postProcessPluginIds()
                    .stream()
                    .map(PrePostProcessPluginIdMapper::external)
                    .forEach(ppp.getPostProcessPlugin()::add);
            external.setPostProcessPlugins(ppp);
        }

        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.timestamp()));
        external.setUserId(internal.userId());
        return external;
    }

    public static KnowledgeModules external(final List<KnowledgeModule> internalKMs)
    {
        if (internalKMs == null)
            return null;

        final KnowledgeModules externalKMs = new KnowledgeModules();

        internalKMs.stream().map(KnowledgeModuleMapper::external).forEach(externalKMs.getKnowledgeModule()::add);

        return externalKMs;
    }
}
