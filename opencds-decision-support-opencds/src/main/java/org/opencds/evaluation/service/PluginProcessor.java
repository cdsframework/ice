package org.opencds.evaluation.service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.config.api.util.PluginIdComparator;
import org.opencds.config.api.util.PluginIdTuple;
import org.opencds.plugin.api.SupportingData;
import org.opencds.plugin.support.PostProcessPluginContextImpl;
import org.opencds.plugin.support.PreProcessPluginContextImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginProcessor
{
    private static final String KM_CONFIGURED = "kmConfigured";
    private static final String LOADED_BY = "loadedBy";

    public static <D> void preProcess(final KnowledgeRepository knowledgeRepository, final KnowledgeModule knowledgeModule,
            final Map<String, SupportingData> supportingData, final EvaluationContext context)
    {
        log.debug("Plugin pre-processing...");
        final List<PluginId> plugins = knowledgeRepository.pluginPackageService().getAllPluginIds();
        final List<PrePostProcessPluginId> allPreProcessPluginIds = knowledgeModule.getPreProcessPluginIds();
        if (allPreProcessPluginIds != null)
        {
            PluginIdComparator.intersect(allPreProcessPluginIds, plugins)
                    .forEach(tuple -> applyPreProcessPlugin(knowledgeRepository, context, supportingData, tuple));
        }
        log.debug("Plugin pre-processing done.");
    }

    public static void postProcess(final KnowledgeRepository knowledgeRepository, final KnowledgeModule knowledgeModule,
            final Map<String, SupportingData> supportingData, final EvaluationContext context)
    {
        log.debug("Plugin post-processing...");
        final List<PluginId> plugins = knowledgeRepository.pluginPackageService().getAllPluginIds();
        final List<PrePostProcessPluginId> allPostProcessPluginIds = knowledgeModule.getPostProcessPluginIds();
        if (allPostProcessPluginIds != null)
        {
            PluginIdComparator.intersect(allPostProcessPluginIds, plugins)
                    .forEach(tuple -> applyPostProcessPlugin(knowledgeRepository, context, supportingData, tuple));
        }
        log.debug("Plugin post-processing done.");
    }

    private static void applyPreProcessPlugin(final KnowledgeRepository knowledgeRepository, final EvaluationContext context,
            final Map<String, SupportingData> supportingData, final PluginIdTuple tuple)
    {
        log.debug("applying plugin: {}", tuple.getLeft().toString());
        final var opencdsPlugin = knowledgeRepository.pluginPackageService().load(tuple.getLeft());
        opencdsPlugin.execute(PreProcessPluginContextImpl.createPreProcessPluginContext(filterSupportingData(tuple, supportingData),
                knowledgeRepository.pluginDataCache(tuple.getLeft()), context.globals()));
    }

    private static void applyPostProcessPlugin(final KnowledgeRepository knowledgeRepository, final EvaluationContext context,
            final Map<String, SupportingData> supportingData, final PluginIdTuple tuple)
    {
        log.debug("applying plugin: {}", tuple.getLeft().toString());
        final var opencdsPlugin = knowledgeRepository.pluginPackageService().load(tuple.getLeft());
        opencdsPlugin.execute(
                PostProcessPluginContextImpl.createPostProcessPluginContext(filterSupportingData(tuple, supportingData),
                        knowledgeRepository.pluginDataCache(tuple.getLeft())));
    }

    public static Map<String, SupportingData> filterSupportingData(final PluginIdTuple tuple,
            final Map<String, SupportingData> supportingData)
    {
        final Map<String, List<Entry<String, Entry<String, SupportingData>>>> allSupportingData =
                supportingData.entrySet().stream().map((final Entry<String, SupportingData> sdEntry) ->
                {
                    if (kmConfigured(sdEntry, tuple.getRight()))
                        return newEntry(KM_CONFIGURED, sdEntry);
                    else
                        if (loadedBy(sdEntry, tuple.getLeft()))
                            return newEntry(LOADED_BY, sdEntry);
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.groupingBy(Entry::getKey));
        if (allSupportingData.containsKey(KM_CONFIGURED))
            return filter(KM_CONFIGURED, allSupportingData);
        else
            if (allSupportingData.containsKey(LOADED_BY))
                return filter(LOADED_BY, allSupportingData);
        return Collections.emptyMap();
    }

    private static Map<String, SupportingData> filter(final String key,
            final Map<String, List<Entry<String, Entry<String, SupportingData>>>> allSupportingData)
    {
        return allSupportingData.get(key).stream().map(Entry::getValue).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static Entry<String, Entry<String, SupportingData>> newEntry(final String key,
            final Entry<String, SupportingData> value)
    {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private static boolean loadedBy(final Entry<String, SupportingData> sdEntry, final PluginId pluginId)
    {
        final String pluginEID = EntityIdentifierUtil.makeEIString(pluginId);
        if (sdEntry.getValue().getLoadedByPluginId() == null || pluginEID == null)
            return false;
        return sdEntry.getValue().getLoadedByPluginId().equals(EntityIdentifierUtil.makeEIString(pluginId));
    }

    private static boolean kmConfigured(final Entry<String, SupportingData> sdEntry, final PrePostProcessPluginId right)
    {
        return right.getSupportingDataIdentifiers().contains(sdEntry.getKey());
    }
}
