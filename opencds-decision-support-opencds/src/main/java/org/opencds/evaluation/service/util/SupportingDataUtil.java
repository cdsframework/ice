package org.opencds.evaluation.service.util;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.plugin.api.SupportingDataPackage;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SupportingDataUtil
{
    public static Map<String, org.opencds.plugin.api.SupportingData> getSupportingData(
            final KnowledgeRepository knowledgeRepository, final KnowledgeModule knowledgeModule)
    {
        return filterByKM(knowledgeModule.kmId(), knowledgeRepository.supportingDataService().getAll()).stream()
                .map(sd -> Map.entry(sd.identifier(),
                        new org.opencds.plugin.api.SupportingData(sd.identifier(), sd.kmId().toEIString(),
                                sd.loadedBy().toEIString(), sd.packageId(), sd.packageType(), sd.timestamp(),
                                supportingDataPackageSupplier(knowledgeRepository.supportingDataPackageService(), sd))))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Supplier<SupportingDataPackage> supportingDataPackageSupplier(
            final SupportingDataPackageService supportingDataPackageService, final SupportingData sd)
    {
        return () -> new SupportingDataPackage(() -> supportingDataPackageService.getFile(sd),
                () -> supportingDataPackageService.getPackageBytes(sd));
    }

    private static List<SupportingData> filterByKM(final KMId kmId, final List<SupportingData> sds)
    {
        return sds.stream().filter(sd -> sd.kmId() == null || sd.kmId().equals(kmId)).toList();
    }
}
