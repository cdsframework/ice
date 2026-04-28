package org.cdsframework.ice.config;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentalFeaturesLogger
{
    private final IceProperties iceProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void logEnabledExperimentalFeatures()
    {
        final Map<String, Boolean> experimentalFeatures =
                Optional.ofNullable(iceProperties.getExperimentalFeatures()).orElse(Map.of());

        final String enabled = experimentalFeatures.entrySet()
                .stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.joining(", "));

        if (!enabled.isEmpty())
            log.info("Experimental features enabled: {}", enabled);
    }
}
