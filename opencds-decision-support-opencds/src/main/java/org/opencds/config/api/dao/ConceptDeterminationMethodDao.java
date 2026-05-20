package org.opencds.config.api.dao;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencds.config.api.dao.util.PathUtil;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.mapper.util.RestConfigUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConceptDeterminationMethodDao
{
    private final Map<CDMId, ConceptDeterminationMethod> cache = new HashMap<>();

    public ConceptDeterminationMethodDao(final Path path)
    {
        final RestConfigUtil restConfigUtil = new RestConfigUtil();

        log.debug("Finding CDM resources in path: {}", path);
        for (final Path input : PathUtil.findFiles(path, true))
        {
            log.debug("Loading resource: {}", input);
            final List<ConceptDeterminationMethod> cdms = restConfigUtil.unmarshalCdms(PathUtil.getResourceAsStream(input));
            if (cdms != null)
            {
                for (final ConceptDeterminationMethod cdm : cdms)
                {
                    log.debug("Caching ConceptDeterminationMethod with CDMID: {}", cdm.cdmId());
                    cache.put(cdm.cdmId(), cdm);
                }
            }
            else
            {
                log.debug(
                        "Loading resource as ConceptDeterminationMethod (resources was not a ConceptDeterminationMethods instance)");
                final ConceptDeterminationMethod cdm = restConfigUtil.unmarshalCdm(PathUtil.getResourceAsStream(input));
                cache.put(cdm.cdmId(), cdm);
            }
        }
    }

    public ConceptDeterminationMethod find(final CDMId cdmId)
    {
        return cache.get(cdmId);
    }

    public List<ConceptDeterminationMethod> getAll()
    {
        return new ArrayList<>(cache.values());
    }
}
