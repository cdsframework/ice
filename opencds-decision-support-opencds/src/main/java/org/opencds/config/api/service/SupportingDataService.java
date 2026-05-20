package org.opencds.config.api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportingDataService
{
    private final Map<String, SupportingData> supportingDataMap;

    public SupportingDataService(final SupportingDataDao dao)
    {
        supportingDataMap = dao.getAll()
                .stream()
                .map(sd -> Map.entry(sd.identifier(), sd))
                .peek(entry -> log.debug("CACHEABLE SD: {} -> {}", entry.getKey(), entry.getValue()))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SupportingData find(final String supportingDataId)
    {
        return supportingDataMap.get(supportingDataId);
    }

    public SupportingData find(final KMId kmId, final String supportingDataId)
    {
        final var sd = supportingDataMap.get(supportingDataId);
        if (kmId.equals(sd.kmId()))
            return sd;

        return null;
    }

    public List<SupportingData> find(final KMId kmid)
    {
        return supportingDataMap.values().stream().filter(sd -> kmid.equals(sd.kmId())).toList();
    }

    public List<SupportingData> getAll()
    {
        return List.copyOf(supportingDataMap.values());
    }
}
