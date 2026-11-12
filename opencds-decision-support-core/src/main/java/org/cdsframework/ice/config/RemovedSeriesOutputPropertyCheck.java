package org.cdsframework.ice.config;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Fails startup when the removed {@code output-series-information} setting is still configured. Ignoring it would silently stop
 * series output for deployments that have not migrated to the evaluation and recommendation settings that replaced it.
 */
@Component
class RemovedSeriesOutputPropertyCheck
{
    RemovedSeriesOutputPropertyCheck(final Environment environment, final IceProperties properties)
    {
        final Binder binder = Binder.get(environment);
        final List<String> removedProperties = Stream.concat(Stream.of("ice"), properties.getKnowledgeBases()
                        .keySet()
                        .stream()
                        .flatMap(key -> Stream.of("ice.knowledge-bases.%s".formatted(key), "ice.knowledge-bases.[%s]".formatted(key))))
                .map("%s.output-series-information"::formatted)
                .filter(ConfigurationPropertyName::isValid)
                .filter(name -> binder.bind(name, String.class).isBound())
                .toList();
        if (removedProperties.isEmpty())
            return;

        throw new IllegalStateException("""
                                        Removed setting %s is still configured. Replace it with output-series-information-for-evaluations and \
                                        output-series-information-for-recommendations at the same level.""".formatted(
                String.join(", ", removedProperties)));
    }
}
