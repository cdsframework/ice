package org.opencds.config.api.model;

import java.util.List;

import org.springframework.util.StringUtils;

public record CDSHook(String hook,
                      String id,
                      String title,
                      String description,
                      List<String> clientIds,
                      Prefetch prefetch,
                      FhirVersion fhirVersion)
{
    public CDSHook
    {
        assert StringUtils.hasText(hook);
        assert StringUtils.hasText(id);
        assert StringUtils.hasText(description);
        assert fhirVersion != null;
        clientIds = clientIds == null ? List.of() : List.copyOf(clientIds);
        prefetch = prefetch == null ? Prefetch.empty() : prefetch;
    }
}
