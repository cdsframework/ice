package org.opencds.evaluation.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.util.EntityIdentifierUtil;

public class SupportingDataUtil {
    
    public static Map<String, org.opencds.plugin.SupportingData> getSupportingData(KnowledgeRepository knowledgeRepository,
            KnowledgeModule knowledgeModule) {
        List<SupportingData> supportingDataList = knowledgeRepository.getSupportingDataService().find(
                knowledgeModule.getKMId());
        Map<String, org.opencds.plugin.SupportingData> supportingData = new LinkedHashMap<>();
        for (SupportingData sd : supportingDataList) {
            byte[] data = knowledgeRepository.getSupportingDataPackageService().getPackageBytes(sd);
            supportingData.put(sd.getIdentifier(), org.opencds.plugin.SupportingData.create(sd.getIdentifier(),
                    EntityIdentifierUtil.makeEIString(sd.getKMId()), EntityIdentifierUtil.makeEIString(sd.getLoadedBy()), sd.getPackageId(), sd.getPackageType(), data));
        }
        return supportingData;
    }


}
