package org.opencds.config.api.strategy;

import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractConfigStrategy implements ConfigStrategy
{
    private final Set<ConfigCapability> capabilities;
    private final String supportedConfigType;

    @Override
    public boolean supports(final String configType)
    {
        return supportedConfigType.equalsIgnoreCase(configType);
    }

    @Override
    public boolean isReloadable()
    {
        return capabilities.contains(ConfigCapability.RELOAD);
    }
}
