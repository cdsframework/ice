/*
 * Copyright 2016-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.evaluation.service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.config.api.util.PluginIdComparator;
import org.opencds.config.api.util.PluginIdTuple;
import org.opencds.plugin.OpencdsPlugin;
import org.opencds.plugin.PluginContext;
import org.opencds.plugin.PluginContext.PostProcessPluginContext;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.plugin.SupportingData;

public class PluginProcessor {
	private static final Log log = LogFactory.getLog(PluginProcessor.class);
	private static final String KM_CONFIGURED = "kmConfigured";
	private static final String LOADED_BY = "loadedBy";

	public static <D> void preProcess(KnowledgeRepository knowledgeRepository, KnowledgeModule knowledgeModule,
			Map<String, org.opencds.plugin.SupportingData> supportingData, EvaluationContext context) {
		log.debug("Plugin pre-processing...");
		List<PluginId> plugins = knowledgeRepository.getPluginPackageService().getAllPluginIds();
		List<PrePostProcessPluginId> allPreProcessPluginIds = knowledgeModule.getPreProcessPluginIds();
		if (allPreProcessPluginIds != null) {
			PluginIdComparator.intersect(allPreProcessPluginIds, plugins).stream()
					.forEach(tuple -> applyPreProcessPlugin(knowledgeRepository, context, supportingData, tuple));
		}
		log.debug("Plugin pre-processing done.");
	}

	public static void postProcess(KnowledgeRepository knowledgeRepository, KnowledgeModule knowledgeModule,
			Map<String, org.opencds.plugin.SupportingData> supportingData, EvaluationContext context,
			Map<String, List<?>> resultFactLists) {
		log.debug("Plugin post-processing...");
		List<PluginId> plugins = knowledgeRepository.getPluginPackageService().getAllPluginIds();
		List<PrePostProcessPluginId> allPostProcessPluginIds = knowledgeModule.getPostProcessPluginIds();
		if (allPostProcessPluginIds != null) {
			PluginIdComparator.intersect(allPostProcessPluginIds, plugins).stream()
                    .forEach(tuple -> applyPostProcessPlugin(knowledgeRepository, context, resultFactLists,
                            supportingData, tuple));
		}
		log.debug("Plugin post-processing done.");
	}

	private static void applyPreProcessPlugin(KnowledgeRepository knowledgeRepository, EvaluationContext context,
			Map<String, SupportingData> supportingData, PluginIdTuple tuple) {
		log.debug("applying plugin: " + tuple.getLeft().toString());
		OpencdsPlugin<PreProcessPluginContext> opencdsPlugin = knowledgeRepository.getPluginPackageService()
				.load(tuple.getLeft());
		opencdsPlugin.execute(PluginContext.createPreProcessPluginContext(context.getAllFactLists(),
				context.getNamedObjects(), context.getGlobals(), filterSupportingData(tuple, supportingData),
				knowledgeRepository.getPluginDataCache(tuple.getLeft())));
	}
	
	private static void applyPostProcessPlugin(KnowledgeRepository knowledgeRepository, EvaluationContext context,
	        Map<String, List<?>> resultFactLists, Map<String, SupportingData> supportingData, PluginIdTuple tuple) {
	    log.debug("applying plugin: " + tuple.getLeft().toString());
	    OpencdsPlugin<PostProcessPluginContext> opencdsPlugin = knowledgeRepository.getPluginPackageService()
	            .load(tuple.getLeft());
	    opencdsPlugin.execute(PluginContext.createPostProcessPluginContext(context.getAllFactLists(),
	            context.getNamedObjects(), context.getAssertions(), resultFactLists, filterSupportingData(tuple, supportingData),
	            knowledgeRepository.getPluginDataCache(tuple.getLeft())));
	}

	public static Map<String, SupportingData> filterSupportingData(PluginIdTuple tuple,
			Map<String, SupportingData> supportingData) {
		Map<String, List<Entry<String, Entry<String, SupportingData>>>> allSupportingData = supportingData.entrySet().stream()
				.map((Map.Entry<String,SupportingData> sdEntry) -> {
					if (kmConfigured(sdEntry, tuple.getRight())) {
						return newEntry(KM_CONFIGURED, sdEntry);
					} else if (loadedBy(sdEntry, tuple.getLeft())) {
						return newEntry(LOADED_BY, sdEntry);
					}
					return null;
				})
				.filter(e -> e != null)
				.collect(Collectors.groupingBy(e -> e.getKey()));
		if (allSupportingData.containsKey(KM_CONFIGURED)) {
			return filter(KM_CONFIGURED, allSupportingData);
		} else if (allSupportingData.containsKey(LOADED_BY)) {
			return filter(LOADED_BY, allSupportingData);
		}
		return Collections.emptyMap();
	}
	
	private static Map<String, SupportingData> filter(String key,
			Map<String, List<Entry<String, Entry<String, SupportingData>>>> allSupportingData) {
		return allSupportingData.get(key).stream().map(e -> e.getValue())
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	private static Map.Entry<String,Map.Entry<String, SupportingData>> newEntry(String key, Entry<String, SupportingData> value) {
		return new AbstractMap.SimpleEntry<String,Map.Entry<String,SupportingData>>(key, value);
	}

	private static boolean loadedBy(Entry<String, SupportingData> sdEntry, PluginId pluginId) {
		String pluginEID = EntityIdentifierUtil.makeEIString(pluginId);
		if (sdEntry.getValue().getLoadedByPluginId() == null || pluginEID == null) {
			return false;
		}
		return sdEntry.getValue().getLoadedByPluginId().equals(EntityIdentifierUtil.makeEIString(pluginId));
	}

	private static boolean kmConfigured(Entry<String, SupportingData> sdEntry, PrePostProcessPluginId right) {
		return right.getSupportingDataIdentifiers().contains(sdEntry.getKey());
	}

}
