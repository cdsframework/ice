/*
 * Copyright 2015-2020 OpenCDS.org
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

package org.opencds.config.store.je.evolve;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.dao.util.FileUtil;
import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KMStatus;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.model.TraitId;
import org.opencds.config.store.model.je.CDMIdJe;
import org.opencds.config.store.model.je.ConceptDeterminationMethodJe;
import org.opencds.config.store.model.je.ExecutionEngineJe;
import org.opencds.config.store.model.je.KMIdJe;
import org.opencds.config.store.model.je.KnowledgeModuleJe;
import org.opencds.config.store.model.je.PPIdJe;
import org.opencds.config.store.model.je.PluginIdJe;
import org.opencds.config.store.model.je.PluginPackageJe;
import org.opencds.config.store.model.je.PrePostProcessPluginIdJe;
import org.opencds.config.store.model.je.SSIdJe;
import org.opencds.config.store.model.je.SecondaryCDMJe;
import org.opencds.config.store.model.je.SemanticSignifierJe;
import org.opencds.config.store.model.je.SupportingDataJe;
import org.opencds.config.store.model.je.TraitIdJe;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.EntityModel;
import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;

public class StoreConversion {
	private static final Log log = LogFactory.getLog(StoreConversion.class);

	public EntityStore convert(File storeLocation, String storeName, boolean readOnly) {
		FileUtil fileUtil = new FileUtil();
		// create a listing of db files
		List<String> files = fileUtil.findFiles(storeLocation, false);

		// create temporary staging dir for conversion
		File rawStoreLoc = new File(storeLocation, "OLD_STORE");
		rawStoreLoc.mkdirs();

		// move db files to temporary staging dir
		for (String fileName : files) {
			File file = Paths.get(fileName).toFile();
			file.renameTo(Paths.get(rawStoreLoc.getAbsolutePath(), file.getName()).toFile());
		}

		// load staging dir as raw store
		EnvironmentConfig rawEnvCfg = new EnvironmentConfig();
		StoreConfig rawStoreCfg = new StoreConfig();
		rawEnvCfg.setAllowCreate(false);
		rawStoreCfg.setAllowCreate(false);
		Environment rawEnv = new Environment(rawStoreLoc, rawEnvCfg);
		RawStore rawStore = new RawStore(rawEnv, storeName, rawStoreCfg);

		// reopen the existing store
		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		envConfig.setAllowCreate(!readOnly);
		storeConfig.setAllowCreate(!readOnly);
		Environment env = new Environment(storeLocation, envConfig);
		EntityStore store = new EntityStore(env, storeName, storeConfig);

		// migrate the data from the old store to the new store
		convert(rawStore, "org.opencds.config.store.model.je.ConceptDeterminationMethodJe", store, CDMIdJe.class, ConceptDeterminationMethodJe.class);
		convert(rawStore, "org.opencds.config.store.model.je.ExecutionEngineJe", store, String.class, ExecutionEngineJe.class);
		convertKnowledgeModules(rawStore, "org.opencds.config.store.model.je.KnowledgeModuleJe", store, KMIdJe.class, KnowledgeModuleJe.class);
		convert(rawStore, "org.opencds.config.store.model.je.PluginPackageJe", store, PPIdJe.class, PluginPackageJe.class);
		convert(rawStore, "org.opencds.config.store.model.je.SemanticSignifierJe", store, SSIdJe.class, SemanticSignifierJe.class);
		convertSupportingData(rawStore, "org.opencds.config.store.model.je.SupportingDataJe", store, String.class, SupportingDataJe.class);

		// close the old store
		rawStore.close();
		rawEnv.close();

		// delete the old store and its files
		fileUtil.delete(rawStoreLoc);
		return store;
	}

	@SuppressWarnings("unchecked")
	private <K, E> void convert(RawStore rawStore, String rawClassName, EntityStore store, Class<K> keyClass, Class<E> entityClass) {
		EntityModel entityModel = store.getModel();
		PrimaryIndex<Object, RawObject> raws = rawStore
				.getPrimaryIndex(rawClassName);
		long rawCount = raws.count();
		PrimaryIndex<K, E> pk = store.getPrimaryIndex(keyClass, entityClass);
		EntityCursor<RawObject> cursor = raws.entities();
		for (RawObject rawObject : cursor) {
			pk.put((E) entityModel.convertRawObject(rawObject));
		}
		long count = pk.count();
		log.info("Converted " + rawCount + " instances of " + rawClassName + " to " + count + " instances of " + entityClass.getCanonicalName());
		assert count == rawCount;
		cursor.close();
	}

	@SuppressWarnings("unchecked")
	private <K, E> void convertSupportingData(RawStore rawStore, String rawClassName, EntityStore store, Class<String> keyClass, Class<SupportingDataJe> entityClass) {
		PrimaryIndex<Object, RawObject> raws = rawStore
				.getPrimaryIndex(rawClassName);
		long rawCount = raws.count();
		PrimaryIndex<String, SupportingDataJe> pk = store.getPrimaryIndex(keyClass, entityClass);
		EntityCursor<RawObject> cursor = raws.entities();
		for (RawObject rawObject : cursor) {
			Map<String, Object> fields = rawObject.getValues();
			RawObject pluginIdRO = (RawObject) fields.get("loadedBy");
			Map<String, Object> pluginId = pluginIdRO.getValues();
			RawObject sdIdRO = (RawObject) fields.get("sdId");
			Map<String, Object> sdId = sdIdRO.getValues();
			SupportingDataJe sd = SupportingDataJe.create(
					(String) sdId.get("identifier"), 
					(KMId) KMIdJe.create(
							(String) sdId.get("scopingEntityId"), 
							(String) sdId.get("businessId"), 
							(String) sdId.get("version")), 
					(String) fields.get("packageType"), 
					(String) fields.get("packageId"),
					(PluginId) PluginIdJe.create(
							(String) pluginId.get("scopingEntityId"), 
							(String) pluginId.get("businessId"), 
							(String) pluginId.get("version")), 
					(Date) fields.get("timestamp"), 
					(String) fields.get("userId"));
			pk.put(sd);
		}
		long count = pk.count();
		log.info("Converted " + rawCount + " instances of " + rawClassName + " to " + count + " instances of " + entityClass.getCanonicalName());
		assert count == rawCount;
		cursor.close();
	}
	
	private <K, E> void convertKnowledgeModules(RawStore rawStore, String rawClassName, EntityStore store, Class<KMIdJe> keyClass, Class<KnowledgeModuleJe> entityClass) {
		PrimaryIndex<Object, RawObject> raws = rawStore
				.getPrimaryIndex(rawClassName);
		long rawCount = raws.count();
		PrimaryIndex<KMIdJe, KnowledgeModuleJe> pk = store.getPrimaryIndex(keyClass, entityClass);
		EntityCursor<RawObject> cursor = raws.entities();
		for (RawObject rawObject : cursor) {
			Map<String, Object> fields = rawObject.getValues();
			
			RawObject kmIdR0 = (RawObject) fields.get("kmId");
			Map<String, Object> kmId = kmIdR0.getValues();
			RawObject ssidR0 = (RawObject) fields.get("semanticSignifierId");
			SSIdJe ssIdJe = null;
			if (ssidR0 != null) {
				Map<String, Object> ssId = ssidR0.getValues();
				ssIdJe = SSIdJe.create(
					(String) ssId.get("scopingEntityId"), 
					(String) ssId.get("businessId"), 
					(String) ssId.get("version"));
			}
			
			RawObject primaryCDMR0 = (RawObject) fields.get("primaryCDM");
			CDMIdJe primaryCDM = null;
			if (primaryCDMR0 != null) {
				Map<String, Object> primaryCDMMap = primaryCDMR0.getValues();
				primaryCDM = CDMIdJe.create(
						(String) primaryCDMMap.get("codeSystem"), 
						(String) primaryCDMMap.get("code"), 
						(String) primaryCDMMap.get("version"));
			}
			
			RawObject secondaryCDMsR0 = (RawObject) fields.get("secondaryCDM");
			List<SecondaryCDM> secondaryCDMs = null;
			if (secondaryCDMsR0 != null) {
				secondaryCDMs = new ArrayList<>();
				for (Object o : secondaryCDMsR0.getElements()) {
					RawObject ro = (RawObject) o;
					Map<String, Object> sv = ro.getValues();
					secondaryCDMs.add(SecondaryCDMJe.create(CDMIdJe.create(
							(String) sv.get("codeSystem"), 
							(String) sv.get("code"), 
							(String) sv.get("version")), 
							(SupportMethod) sv.get("supportMethod")));
				}
			}
			
			RawObject traitIdsR0 = (RawObject) fields.get("traidId");
			List<TraitId> traidIds = null;
			if (traitIdsR0 != null) {
				traidIds = new ArrayList<>();
				for (Object o : traitIdsR0.getElements()) {
					RawObject ro = (RawObject) o;
					Map<String, Object> sv = ro.getValues();
					traidIds.add(TraitIdJe.create(
							(String) sv.get("codeSystem"), 
							(String) sv.get("code"), 
							(String) sv.get("version")));
				}
			}
			
			RawObject preProcessPluginIdsR0 = (RawObject) fields.get("preProcessPluginIds");
			List<PrePostProcessPluginId> preProcPluginIds = null;
			if (preProcessPluginIdsR0 != null && preProcessPluginIdsR0.getElements() != null) {
				preProcPluginIds = new ArrayList<>();
				for (Object o : preProcessPluginIdsR0.getElements()) {
					RawObject ro = (RawObject) o;
					Map<String, Object> sv = ro.getValues();
					preProcPluginIds.add(PrePostProcessPluginIdJe.create(
							(String) sv.get("codeSystem"), 
							(String) sv.get("code"), 
							(String) sv.get("version"),
							null));
				}
			}

			RawObject postProcessPluginIdsR0 = (RawObject) fields.get("preProcessPluginIds");
			List<PrePostProcessPluginId> postProcPluginIds = null;
			if (postProcessPluginIdsR0 != null && postProcessPluginIdsR0.getElements() != null) {
				postProcPluginIds = new ArrayList<>();
				for (Object o : postProcessPluginIdsR0.getElements()) {
					RawObject ro = (RawObject) o;
					Map<String, Object> sv = ro.getValues();
					postProcPluginIds.add(PrePostProcessPluginIdJe.create(
							(String) sv.get("codeSystem"), 
							(String) sv.get("code"), 
							(String) sv.get("version"),
							null));
				}
			}
			
			KnowledgeModuleJe km = KnowledgeModuleJe.create(
					KMIdJe.create(
							(String) kmId.get("scopingEntityId"), 
							(String) kmId.get("businessId"), 
							(String) kmId.get("version")), 
					KMStatus.valueOf(((RawObject)fields.get("status")).getEnum()), 
					null, // cdsHook is not in previous versions
					(String) fields.get("executionEngine"), 
					ssIdJe, 
					primaryCDM, 
					secondaryCDMs, 
					(String) fields.get("packageType"),
					(String) fields.get("packageId"), 
					(boolean) fields.get("preload"), 
					(String) fields.get("primaryProcess"), 
					traidIds, 
					preProcPluginIds, 
					postProcPluginIds, 
					(Date) fields.get("timestamp"), 
					(String) fields.get("userId"));
			pk.put(km);
		}
		long count = pk.count();
		log.info("Converted " + rawCount + " instances of " + rawClassName + " to " + count + " instances of " + entityClass.getCanonicalName());
		assert count == rawCount;
		cursor.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
