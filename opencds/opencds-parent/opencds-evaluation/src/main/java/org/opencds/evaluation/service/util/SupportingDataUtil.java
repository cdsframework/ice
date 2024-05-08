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

package org.opencds.evaluation.service.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.evaluation.service.SupportingDataPackageSupplierImpl;

public class SupportingDataUtil {

	/**
	 * Retrieves all relevant SupportingData, i.e., SDs that are associated with
	 * the KnowledgeModule or SDs that have no associated KM.
	 *
	 * See documentation referenced by OP-41.
	 *
	 * @param knowledgeRepository
	 * @param knowledgeModule
	 * @return
	 */
	public static Map<String, org.opencds.plugin.SupportingData> getSupportingData(
			KnowledgeRepository knowledgeRepository, KnowledgeModule knowledgeModule) {
		List<SupportingData> supportingDataList = filterByKM(knowledgeModule.getKMId(),
				knowledgeRepository.getSupportingDataService().getAll());

		Map<String, org.opencds.plugin.SupportingData> supportingData = new LinkedHashMap<>();
		for (SupportingData sd : supportingDataList) {
			supportingData.put(sd.getIdentifier(), org.opencds.plugin.SupportingData.create(sd.getIdentifier(),
					EntityIdentifierUtil.makeEIString(sd.getKMId()),
					EntityIdentifierUtil.makeEIString(sd.getLoadedBy()), sd.getPackageId(), sd.getPackageType(),
					sd.getTimestamp(),
					new SupportingDataPackageSupplierImpl(knowledgeRepository.getSupportingDataPackageService(), sd)));
		}
		return supportingData;
	}

	/**
	 * Inclusion filter by SupportingData by KMId, or SDs that have no
	 * associated KMId.
	 *
	 * @param kmId
	 * @param sds
	 * @return
	 */
	private static List<SupportingData> filterByKM(KMId kmId, List<SupportingData> sds) {
		List<SupportingData> sdList = new ArrayList<>();
		for (SupportingData sd : sds) {
			if (sd.getKMId() == null || sd.getKMId().equals(kmId)) {
				sdList.add(sd);
			}
		}
		return sdList;
	}

}
