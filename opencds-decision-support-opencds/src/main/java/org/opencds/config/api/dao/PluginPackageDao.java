package org.opencds.config.api.dao;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.util.PathUtil;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.mapper.util.RestConfigUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginPackageDao
{
    private final Map<PPId, PluginPackage> cache;

    public PluginPackageDao(final Path path)
    {
        final RestConfigUtil restConfigUtil = new RestConfigUtil();

        log.debug("Finding plugin resources in path: {}", path);

        cache = PathUtil.findFiles(path, false)
                .stream()
                .peek(resource ->
                {
                    log.debug("Loading resource: {}", resource);
                    log.debug("Loading resource as PluginPackages (resource was not a PluginPackage instance)");
                })
                .map(PathUtil::getResourceAsStream)
                .map(restConfigUtil::unmarshalPluginPackages)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(PluginPackage::identifier, Function.identity()));
    }

    public PluginPackage find(final PPId ppId)
    {
        return cache.get(ppId);
    }

    public List<PluginPackage> getAll()
    {
        return List.copyOf(cache.values());
    }
}
