package org.cdsframework.ice.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.ice.config.IceProperties;
import org.omg.dss.EntityIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
public class KnowledgeModuleIdResolver
{
    private final IceProperties iceProperties;

    public KnowledgeModuleIdResolver(final IceProperties iceProperties)
    {
        this.iceProperties = iceProperties;
    }

    public String deriveKmIdFromModuleCanonical(final String moduleCanonical)
    {
        if (!StringUtils.hasText(moduleCanonical))
            throw new IllegalArgumentException("Module canonical must be provided");

        if (moduleCanonical.contains("^"))
            throw new IllegalArgumentException("Module must be canonical in '<url>|<version>' format");

        final String[] parts = moduleCanonical.split("\\|", -1);
        if (parts.length != 2 || !StringUtils.hasText(parts[1]))
            throw new IllegalArgumentException("Module canonical must be in '<canonical>|<version>' format");

        final String canonical = parts[0];
        final String version = parts[1];

        final URI uri;
        try
        {
            uri = URI.create(canonical);
        }
        catch (final IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Module canonical is not a valid URI: " + canonical, e);
        }

        final String host = Optional.ofNullable(uri.getHost())
                .filter(StringUtils::hasText)
                .orElseThrow(() -> new IllegalArgumentException("Module canonical must contain a host"));
        final String path = Optional.ofNullable(uri.getPath()).orElse("");

        final String[] pathSegments = Stream.of(path.split("/")).filter(StringUtils::hasText).toArray(String[]::new);
        final int planDefinitionIndex = findPlanDefinitionSegmentIndex(pathSegments);
        if (planDefinitionIndex < 0 || planDefinitionIndex == pathSegments.length - 1)
            throw new IllegalArgumentException("Module canonical must reference a PlanDefinition resource id");

        final String scopingEntityId = deriveScopingEntityId(host, pathSegments, planDefinitionIndex);
        final String businessId = deriveBusinessId(pathSegments[planDefinitionIndex + 1]);
        final String derivedKmId = "%s^%s^%s".formatted(scopingEntityId, businessId, version);

        return resolveConfiguredKmId(derivedKmId, moduleCanonical);
    }

    public EntityIdentifier parseKmEntityIdentifier(final String kmId)
    {
        final String[] parts = kmId.split("\\^", -1);
        if (parts.length != 3 || Stream.of(parts).anyMatch(ObjectUtils::isEmpty))
            throw new IllegalArgumentException("kmId must be in '<scopingEntityId>^<businessId>^<version>' format");

        final EntityIdentifier entityIdentifier = new EntityIdentifier();
        entityIdentifier.setScopingEntityId(parts[0]);
        entityIdentifier.setBusinessId(parts[1]);
        entityIdentifier.setVersion(parts[2]);
        return entityIdentifier;
    }

    private int findPlanDefinitionSegmentIndex(final String[] pathSegments)
    {
        for (int i = 0; i < pathSegments.length; i++)
        {
            if ("PlanDefinition".equals(pathSegments[i]))
                return i;
        }
        return -1;
    }

    private String deriveScopingEntityId(final String host, final String[] pathSegments, final int planDefinitionIndex)
    {
        final List<String> scopeSegments = new ArrayList<>();
        final String[] hostSegments = host.split("\\.");
        for (int i = hostSegments.length - 1; i >= 0; i--)
        {
            if (!hostSegments[i].isBlank())
                scopeSegments.add(hostSegments[i].toLowerCase(Locale.ROOT));
        }
        if (!scopeSegments.isEmpty() && "gov".equals(scopeSegments.getFirst()))
            scopeSegments.set(0, "org");

        for (int i = 0; i < planDefinitionIndex; i++)
            scopeSegments.add(pathSegments[i].toLowerCase(Locale.ROOT));

        final String scopingEntityId = String.join(".", scopeSegments);
        if (scopingEntityId.isBlank())
            throw new IllegalArgumentException("Unable to derive scopingEntityId from module canonical");
        return scopingEntityId;
    }

    private String deriveBusinessId(final String artifactId)
    {
        final String businessToken = Stream.of(artifactId.split("[_-]"))
                .filter(token -> !token.isBlank())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to derive businessId from PlanDefinition id"));
        return businessToken.toUpperCase(Locale.ROOT);
    }

    private String resolveConfiguredKmId(final String derivedKmId, final String moduleCanonical)
    {
        final Map<String, IceProperties.KnowledgeModuleProperties> configuredKnowledgeModules =
                Optional.ofNullable(iceProperties).map(IceProperties::getKnowledgeModules).orElse(Map.of());
        if (configuredKnowledgeModules.containsKey(derivedKmId))
            return derivedKmId;

        final List<String> caseInsensitiveMatches =
                configuredKnowledgeModules.keySet().stream().filter(kmId -> kmId.equalsIgnoreCase(derivedKmId)).toList();
        if (caseInsensitiveMatches.size() == 1)
            return caseInsensitiveMatches.getFirst();

        throw new IllegalArgumentException(
                "No configured knowledge module matches derived kmId '%s' from module '%s'".formatted(derivedKmId,
                        moduleCanonical));
    }
}
