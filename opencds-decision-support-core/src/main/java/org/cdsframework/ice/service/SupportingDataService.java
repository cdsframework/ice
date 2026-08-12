package org.cdsframework.ice.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.supportingdata.Series;
import org.cdsframework.ice.supportingdata.SeriesData;
import org.omg.dss.EntityIdentifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SupportingDataService
{
    private static final String SUPPORTED_SERIES_CODE_SYSTEM_NAME = "SUPPORTED_SERIES";
    private static final String SUPPORTED_SCHEDULE_FLAGS_CODE_SYSTEM_NAME = "SUPPORTED_SCHEDULE_FLAGS";
    private static final String SERIES_OPENCDS_PREFIX = "VACCINE_SERIES_";
    private static final String SERIES_CONCEPT_TYPE_PREFIX = ICEConceptType.SERIES.getIceConceptTypeValue() + ".";
    private static final String OID_URN_PREFIX = "urn:oid:";
    private static final String IDENTIFIER_SYSTEM_URN_RFC_3986 = "urn:ietf:rfc:3986";
    private static final String IDENTIFIER_SYSTEM_KNOWLEDGE_MODULES = "http://cdsframework.org/identifiers/knowledge-modules";
    private static final String DISEASE_IMMUNITY_SOURCE_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.8";
    private static final String RECOMMENDATION_REASON_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.6";
    private static final String EVALUATION_REASON_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.3";
    private static final String DISEASE_IMMUNITY_REASON_CODE_SYSTEM = "2.16.840.1.113883.3.795.12.100.9";
    private static final Pattern OID_PATTERN = Pattern.compile("^\\d+(?:\\.\\d+)+$");

    private static String normalizeCodeSystemToOid(final String codeSystem)
    {
        if (codeSystem == null)
            return null;

        final String normalized = codeSystem.trim();
        if (normalized.isEmpty())
            return normalized;

        return normalized.toLowerCase(Locale.ROOT).startsWith(OID_URN_PREFIX)
               ? normalized.substring(OID_URN_PREFIX.length())
               : normalized;
    }

    private static String normalizeAndValidateOidValue(final String codeSystemName, final String value)
    {
        final String normalizedOid = normalizeCodeSystemToOid(value);
        if (normalizedOid != null && OID_PATTERN.matcher(normalizedOid).matches())
            return normalizedOid;

        throw new IllegalArgumentException(
                "CodeSystem '%s' must declare identifier.system '%s' with a valid identifier.value 'urn:oid:<oid>'".formatted(
                        Optional.ofNullable(codeSystemName).orElse("<unknown>"), IDENTIFIER_SYSTEM_URN_RFC_3986));
    }

    private static String normalizeCodeSystemLookupKey(final String value)
    {
        if (value == null)
            return null;

        final String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private final CdsEngineProperties cdsEngineProperties;
    private final IceProperties iceProperties;
    private final Map<String, String> canonicalToKmId;
    private final String baseKnowledgeModuleId;
    private final Map<String, CdsEngineProperties.ModuleCanonicalDefinition> supportingKnowledgeModulesByKmId;
    private final PlanDefinitionSeriesDataConsumer planDefinitionSeriesDataConsumer;
    @Getter
    private final Map<String, IceProperties.KnowledgeModuleProperties> knowledgeModulePropertiesByKmId;
    private final Map<String, Map<String, String>> outboundCodeSystemMapByKm;
    private final Map<String, Map<String, String>> inboundCodeSystemOidMapByKm;
    private final Map<String, Map<String, Map<String, String>>> conceptDisplayLookupByKm;

    public SupportingDataService(final CdsEngineProperties cdsEngineProperties, final IceProperties iceProperties)
    {
        this.cdsEngineProperties = cdsEngineProperties;
        this.iceProperties = iceProperties;
        this.planDefinitionSeriesDataConsumer = new PlanDefinitionSeriesDataConsumer();

        this.canonicalToKmId = Optional.ofNullable(cdsEngineProperties)
                .map(CdsEngineProperties::getModuleCanonicalDefinitionMap)
                .map(Map::entrySet)
                .stream()
                .flatMap(Collection::stream)
                .filter(entry -> StringUtils.hasText(entry.getKey()))
                .map(entry -> Map.entry(entry.getKey().trim(),
                        extractKnowledgeModuleIdentifier(entry.getValue().modulePlanDefinition())))
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, replacement) -> replacement,
                        LinkedHashMap::new));

        this.baseKnowledgeModuleId = Optional.ofNullable(iceProperties)
                .map(IceProperties::getIceBaseModuleCanonical)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(baseModuleCanonical -> Optional.ofNullable(canonicalToKmId.get(baseModuleCanonical))
                        .filter(StringUtils::hasText)
                        .orElseThrow(() -> new IllegalStateException(
                                "No plan-definition mapping found for configured ice-base-module-canonical '%s'".formatted(
                                        baseModuleCanonical))))
                .orElseThrow(() -> new IllegalStateException("ice-base-module-canonical must be configured"));

        this.supportingKnowledgeModulesByKmId = buildSupportingKnowledgeModulesByKmId();

        this.knowledgeModulePropertiesByKmId = buildKnowledgeModulePropertiesByKmId();

        this.outboundCodeSystemMapByKm =
                supportingKnowledgeModulesByKmId.keySet().stream().collect(Collectors.toMap(Function.identity(), kmId ->
                {
                    final Map<String, String> merged = new HashMap<>();

                    Optional.ofNullable(supportingKnowledgeModulesByKmId.get(baseKnowledgeModuleId))
                            .map(CdsEngineProperties.ModuleCanonicalDefinition::outboundCodeSystemMap)
                            .ifPresent(merged::putAll);

                    Optional.ofNullable(supportingKnowledgeModulesByKmId.get(kmId))
                            .map(CdsEngineProperties.ModuleCanonicalDefinition::outboundCodeSystemMap)
                            .ifPresent(merged::putAll);

                    streamCandidateKnowledgeModuleIds(kmId).map(supportingKnowledgeModulesByKmId::get)
                            .filter(Objects::nonNull)
                            .map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                            .filter(Objects::nonNull)
                            .map(Map::values)
                            .flatMap(Collection::stream)
                            .map(codeSystem -> Map.entry(extractCodeSystemOid(codeSystem),
                                    Optional.ofNullable(codeSystem.url()).map(String::trim).filter(Predicate.not(String::isEmpty))))
                            .filter(entry -> entry.getValue().isPresent())
                            .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
                            .forEach(entry ->
                            {
                                merged.putIfAbsent(entry.getKey(), entry.getValue());
                                merged.putIfAbsent(OID_URN_PREFIX + entry.getKey(), entry.getValue());
                            });

                    return Map.copyOf(merged);
                }));

        this.inboundCodeSystemOidMapByKm =
                outboundCodeSystemMapByKm.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e ->
                {
                    final Map<String, String> inboundMap = new HashMap<>();
                    e.getValue().forEach((source, target) ->
                    {
                        final String sourceOid = normalizeCodeSystemToOid(source);
                        if (sourceOid != null && OID_PATTERN.matcher(sourceOid).matches())
                        {
                            final String targetKey = normalizeCodeSystemLookupKey(target);
                            if (targetKey != null)
                                inboundMap.putIfAbsent(targetKey, sourceOid);
                        }

                        final String targetOid = normalizeCodeSystemToOid(target);
                        if (targetOid != null && OID_PATTERN.matcher(targetOid).matches())
                        {
                            final String sourceKey = normalizeCodeSystemLookupKey(source);
                            if (sourceKey != null)
                                inboundMap.putIfAbsent(sourceKey, targetOid);
                        }
                    });
                    return Map.copyOf(inboundMap);
                }));

        this.conceptDisplayLookupByKm =
                supportingKnowledgeModulesByKmId.keySet().stream().collect(Collectors.toMap(Function.identity(), kmId ->
                {
                    final Map<String, Map<String, String>> displayByCodeSystem = new HashMap<>();
                    streamCandidateKnowledgeModuleIds(kmId).map(supportingKnowledgeModulesByKmId::get)
                            .filter(Objects::nonNull)
                            .map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                            .filter(Objects::nonNull)
                            .map(Map::values)
                            .flatMap(Collection::stream)
                            .forEach(codeSystem ->
                            {
                                final String codeSystemOid = extractCodeSystemOid(codeSystem);
                                if (!StringUtils.hasText(codeSystemOid))
                                    return;

                                final Map<String, String> displayByCode =
                                        displayByCodeSystem.computeIfAbsent(codeSystemOid, _ -> new HashMap<>());
                                Optional.ofNullable(codeSystem.concept()).orElse(List.of()).forEach(concept ->
                                {
                                    if (concept == null)
                                        return;

                                    final String display = Optional.ofNullable(concept.display())
                                            .filter(StringUtils::hasText)
                                            .orElse(concept.code());
                                    if (!StringUtils.hasText(display))
                                        return;

                                    if (StringUtils.hasText(concept.code()))
                                        displayByCode.putIfAbsent(concept.code().trim(), display);

                                    Optional.ofNullable(concept.property())
                                            .orElse(List.of())
                                            .stream()
                                            .filter(Objects::nonNull)
                                            .filter(property -> "conceptMapping".equals(property.code()) || "outboundCode".equals(
                                                    property.code()))
                                            .map(CodeSystemConceptProperty::valueCoding)
                                            .filter(Objects::nonNull)
                                            .map(Coding::code)
                                            .filter(StringUtils::hasText)
                                            .map(String::trim)
                                            .forEach(mappedCode -> displayByCode.putIfAbsent(mappedCode, display));
                                });
                            });

                    return Map.copyOf(displayByCodeSystem);
                }));

        validateConfiguredScheduleFlags();
    }

    public String getKmIdFromModuleCanonicalUrl(final String moduleCanonical)
    {
        return Optional.ofNullable(moduleCanonical)
                .map(String::trim)
                .map(canonicalToKmId::get)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Knowledge module mapping not found for canonical module: %s".formatted(moduleCanonical)));
    }

    private Optional<String> extractKnowledgeModuleIdentifier(final PlanDefinition planDefinition)
    {
        return Optional.ofNullable(planDefinition)
                .map(PlanDefinition::identifier)
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(identifier -> IDENTIFIER_SYSTEM_KNOWLEDGE_MODULES.equals(identifier.system()))
                .map(Identifier::value)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .findFirst();
    }

    public CdsEngineProperties.ModuleCanonicalDefinition getKnowledgeModuleFromCanonicalUrlVersion(final String moduleCanonical,
            final String moduleVersion)
    {
        return getKnowledgeModuleFromCanonicalUrl("%s|%s".formatted(moduleCanonical, moduleVersion));
    }

    public CdsEngineProperties.ModuleCanonicalDefinition getKnowledgeModuleFromCanonicalUrl(final String moduleCanonical)
    {
        return getSupportingKnowledgeModuleByCanonical(moduleCanonical);
    }

    public CodeSystem lookupCodeSystemFromCanonicalUrlVersion(final String moduleCanonical, final String moduleVersion,
            final String codeSystemName)
    {
        return lookupCodeSystemFromCanonicalUrl("%s|%s".formatted(moduleCanonical, moduleVersion), codeSystemName);
    }

    public CodeSystem lookupCodeSystemFromCanonicalUrl(final String moduleCanonical, final String codeSystemName)
    {
        return Optional.ofNullable(getKnowledgeModuleFromCanonicalUrl(moduleCanonical).codeSystems().get(codeSystemName))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Code system not found for canonical module: %s and code system name: %s".formatted(moduleCanonical,
                                codeSystemName)));
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

    public CdsEngineProperties.ModuleCanonicalDefinition getSupportingKnowledgeModuleByCanonical(final String moduleCanonical)
    {
        if (!StringUtils.hasText(moduleCanonical))
            throw new IllegalArgumentException("moduleCanonical must be provided");

        return getSupportingKnowledgeModuleByKmId(getKmIdFromModuleCanonicalUrl(moduleCanonical.trim()));
    }

    public CdsEngineProperties.ModuleCanonicalDefinition getSupportingKnowledgeModuleByKmId(final String kmId)
    {
        if (!StringUtils.hasText(kmId))
            throw new IllegalArgumentException("kmId must be provided");

        return Optional.ofNullable(supportingKnowledgeModulesByKmId.get(kmId.trim()))
                .orElseThrow(() -> new IllegalStateException("Supporting knowledge module not found for kmId: %s".formatted(kmId)));
    }

    public IceProperties.KnowledgeModuleProperties getKnowledgeModulePropertiesForKmId(final String kmId)
    {
        if (!StringUtils.hasText(kmId))
            throw new IllegalArgumentException("kmId must be provided");

        return Optional.ofNullable(knowledgeModulePropertiesByKmId.get(kmId.trim()))
                .orElseThrow(() -> new IllegalStateException("KnowledgeModuleProperties not found for kmId: %s".formatted(kmId)));
    }

    public Map<String, IceProperties.SeriesOverride> getSeriesOverrides()
    {
        return Optional.ofNullable(iceProperties).map(IceProperties::getSeriesOverrides).orElse(Map.of());
    }

    public Map<String, IceProperties.SeasonOverride> getSeasonOverrides()
    {
        return Optional.ofNullable(iceProperties).map(IceProperties::getSeasonOverrides).orElse(Map.of());
    }

    private Map<String, CdsEngineProperties.ModuleCanonicalDefinition> getRawSupportingKnowledgeModules()
    {
        return Optional.ofNullable(cdsEngineProperties).map(CdsEngineProperties::getModuleCanonicalDefinitionMap).orElse(Map.of());
    }

    private Map<String, IceProperties.KnowledgeModuleProperties> getRawKnowledgeModuleProperties()
    {
        return Optional.ofNullable(iceProperties).map(IceProperties::getKnowledgeModules).orElse(Map.of());
    }

    public List<String> getConfiguredScheduleFlags()
    {
        return Optional.ofNullable(iceProperties)
                .map(IceProperties::getScheduleFlags)
                .stream()
                .flatMap(Collection::stream)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    private Map<String, CdsEngineProperties.ModuleCanonicalDefinition> buildSupportingKnowledgeModulesByKmId()
    {
        final Map<String, CdsEngineProperties.ModuleCanonicalDefinition> rawKnowledgeModules = getRawSupportingKnowledgeModules();
        if (rawKnowledgeModules.isEmpty())
            return Map.of();

        final Map<String, CdsEngineProperties.ModuleCanonicalDefinition> normalized = new LinkedHashMap<>();
        rawKnowledgeModules.forEach((moduleKey, moduleData) ->
        {
            final String kmId = resolveConfiguredModuleKeyToKmId(moduleKey);
            if (!StringUtils.hasText(kmId))
                return;
            normalized.merge(kmId, routePlanDefinitionsToSeriesData(moduleData), this::mergeKnowledgeModules);
        });
        return Map.copyOf(normalized);
    }

    private Map<String, IceProperties.KnowledgeModuleProperties> buildKnowledgeModulePropertiesByKmId()
    {
        final Map<String, IceProperties.KnowledgeModuleProperties> rawKnowledgeModules = getRawKnowledgeModuleProperties();
        if (rawKnowledgeModules.isEmpty())
            return Map.of();

        final Map<String, IceProperties.KnowledgeModuleProperties> normalized = new LinkedHashMap<>();
        rawKnowledgeModules.forEach((moduleKey, moduleProperties) ->
        {
            final String kmId = resolveConfiguredModuleKeyToKmId(moduleKey);
            if (!StringUtils.hasText(kmId))
                return;
            normalized.put(kmId, moduleProperties);
        });
        return Map.copyOf(normalized);
    }

    private CdsEngineProperties.ModuleCanonicalDefinition mergeKnowledgeModules(
            final CdsEngineProperties.ModuleCanonicalDefinition left, final CdsEngineProperties.ModuleCanonicalDefinition right)
    {
        if (left == null)
            return right;
        if (right == null)
            return left;

        final Map<String, PlanDefinition> planDefinitions = new LinkedHashMap<>();
        planDefinitions.putAll(Optional.ofNullable(left.planDefinitions()).orElse(Map.of()));
        planDefinitions.putAll(Optional.ofNullable(right.planDefinitions()).orElse(Map.of()));
        final Map<String, CodeSystem> codeSystems = new LinkedHashMap<>();
        codeSystems.putAll(Optional.ofNullable(left.codeSystems()).orElse(Map.of()));
        codeSystems.putAll(Optional.ofNullable(right.codeSystems()).orElse(Map.of()));

        final Map<String, String> outboundCodeSystemMap = new LinkedHashMap<>();
        outboundCodeSystemMap.putAll(Optional.ofNullable(left.outboundCodeSystemMap()).orElse(Map.of()));
        outboundCodeSystemMap.putAll(Optional.ofNullable(right.outboundCodeSystemMap()).orElse(Map.of()));

        final PlanDefinition planDefinition = Optional.ofNullable(right.modulePlanDefinition()).orElse(left.modulePlanDefinition());
        return routePlanDefinitionsToSeriesData(
                new CdsEngineProperties.ModuleCanonicalDefinition(planDefinition, Map.copyOf(planDefinitions), Map.of(),
                        Map.copyOf(codeSystems), Map.copyOf(outboundCodeSystemMap)));
    }

    private CdsEngineProperties.ModuleCanonicalDefinition routePlanDefinitionsToSeriesData(
            final CdsEngineProperties.ModuleCanonicalDefinition definition)
    {
        if (definition == null)
            return null;

        final Map<String, PlanDefinition> planDefinitions =
                new LinkedHashMap<>(Optional.ofNullable(definition.planDefinitions()).orElse(Map.of()));

        return new CdsEngineProperties.ModuleCanonicalDefinition(definition.modulePlanDefinition(), Map.copyOf(planDefinitions),
                new LinkedHashMap<>(planDefinitionSeriesDataConsumer.toSeriesDataMap(filterSeriesPlanDefinitions(planDefinitions))),
                Map.copyOf(Optional.ofNullable(definition.codeSystems()).orElse(Map.of())),
                Map.copyOf(Optional.ofNullable(definition.outboundCodeSystemMap()).orElse(Map.of())));
    }

    private Map<String, PlanDefinition> filterSeriesPlanDefinitions(final Map<String, PlanDefinition> planDefinitions)
    {
        if (planDefinitions == null || planDefinitions.isEmpty())
            return Map.of();

        final Map<String, PlanDefinition> seriesPlanDefinitions = new LinkedHashMap<>();
        planDefinitions.forEach((planDefinitionId, planDefinition) ->
        {
            if (!StringUtils.hasText(planDefinitionId) || planDefinition == null)
                return;
            if (!planDefinitionSeriesDataConsumer.isSeriesPlanDefinition(planDefinition))
                return;
            seriesPlanDefinitions.put(planDefinitionId, planDefinition);
        });
        return Map.copyOf(seriesPlanDefinitions);
    }

    private String resolveConfiguredModuleKeyToKmId(final String moduleKey)
    {
        if (!StringUtils.hasText(moduleKey))
            throw new IllegalArgumentException("knowledge-module configuration key must not be blank");

        final String key = moduleKey.trim();
        if (isKmId(key))
            return key;

        final String kmId = canonicalToKmId.get(key);
        if (!StringUtils.hasText(kmId))
            throw new IllegalStateException(
                    "No plan-definition mapping found for configured knowledge-module key: %s".formatted(key));

        return kmId;
    }

    private boolean isKmId(final String value)
    {
        return StringUtils.hasText(value) && value.contains("^");
    }

    private Stream<String> streamCandidateKnowledgeModuleIds(final String kmId)
    {
        return Stream.of(kmId, baseKnowledgeModuleId).filter(StringUtils::hasText).map(String::trim).distinct();
    }

    private CdsEngineProperties.ModuleCanonicalDefinition getKnowledgeModule(final String kmId)
    {
        return getSupportingKnowledgeModuleByKmId(kmId);
    }

    private Stream<CdsEngineProperties.ModuleCanonicalDefinition> streamCandidateKnowledgeModules(final String kmId)
    {
        return streamCandidateKnowledgeModuleIds(kmId).map(this::getKnowledgeModule);
    }

    private void validateConfiguredScheduleFlags()
    {
        final List<String> configuredScheduleFlags = getConfiguredScheduleFlags();
        if (configuredScheduleFlags.isEmpty())
            return;

        for (final String kmId : new java.util.TreeSet<>(!knowledgeModulePropertiesByKmId.isEmpty()
                                                         ? knowledgeModulePropertiesByKmId.keySet()
                                                         : supportingKnowledgeModulesByKmId.keySet()))
        {
            try
            {
                validateScheduleFlagsForKmId(kmId, configuredScheduleFlags);
            }
            catch (final IllegalArgumentException e)
            {
                throw new IllegalStateException(
                        "Configured ice.schedule-flags are invalid for knowledge module '%s': %s".formatted(kmId, e.getMessage()), e);
            }
        }
    }

    public List<String> validateScheduleFlagsForKmId(final String kmId, final Collection<String> scheduleFlags)
    {
        if (!StringUtils.hasText(kmId))
            throw new IllegalArgumentException("kmId must be provided");

        final List<String> normalizedScheduleFlags = Optional.ofNullable(scheduleFlags)
                .stream()
                .flatMap(Collection::stream)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
        if (normalizedScheduleFlags.isEmpty())
            return List.of();

        final Set<String> supportedScheduleFlags =
                streamCandidateKnowledgeModules(kmId).map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                        .filter(Objects::nonNull)
                        .map(codeSystems -> codeSystems.get(SUPPORTED_SCHEDULE_FLAGS_CODE_SYSTEM_NAME))
                        .filter(Objects::nonNull)
                        .map(CodeSystem::concept)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .filter(Objects::nonNull)
                        .map(CodeSystemConcept::code)
                        .filter(StringUtils::hasText)
                        .map(String::trim)
                        .collect(Collectors.toCollection(java.util.TreeSet::new));
        if (supportedScheduleFlags.isEmpty())
            throw new IllegalArgumentException("no %s code system was loaded".formatted(SUPPORTED_SCHEDULE_FLAGS_CODE_SYSTEM_NAME));

        final List<String> unsupportedScheduleFlags =
                normalizedScheduleFlags.stream().filter(flag -> !supportedScheduleFlags.contains(flag)).toList();
        if (!unsupportedScheduleFlags.isEmpty())
            throw new IllegalArgumentException(
                    "unsupported code(s): %s; supported %s codes: %s".formatted(String.join(", ", unsupportedScheduleFlags),
                            SUPPORTED_SCHEDULE_FLAGS_CODE_SYSTEM_NAME, String.join(", ", supportedScheduleFlags)));

        return normalizedScheduleFlags;
    }

    @Cacheable("diseaseGroupMapping")
    public List<CodeableConcept> getDiseasesForVaccineGroup(@NotBlank final String kmId, @NotBlank final String vaccineGroupCode)
    {
        if (vaccineGroupCode == null || vaccineGroupCode.isBlank())
            return List.of();

        final String normalizedVaccineGroupCode = vaccineGroupCode.trim();

        final var knowledgeModule = getKnowledgeModule(kmId);
        if (knowledgeModule.codeSystems() == null)
            return List.of();

        final List<CodeSystemConcept> supportedDiseaseConcepts =
                Optional.ofNullable(knowledgeModule.codeSystems().get("SUPPORTED_DISEASE_CONCEPT"))
                        .map(CodeSystem::concept)
                        .orElse(List.of());

        final Optional<CodeSystemConcept> vaccineGroupConcept =
                Optional.ofNullable(knowledgeModule.codeSystems().get("VACCINE_GROUP_CONCEPT"))
                        .map(CodeSystem::concept)
                        .orElse(List.of())
                        .stream()
                        .filter(concept -> normalizedVaccineGroupCode.equals(concept.code()) || Optional.ofNullable(
                                        concept.property())
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(prop -> "outboundCode".equals(prop.code()))
                                .map(CodeSystemConceptProperty::valueCoding)
                                .filter(Objects::nonNull)
                                .map(Coding::code)
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .anyMatch(normalizedVaccineGroupCode::equals))
                        .findFirst();
        if (vaccineGroupConcept.isEmpty())
        {
            log.warn("No VACCINE_GROUP_CONCEPT match for kmId='{}' and vaccineGroupCode='{}'", kmId, normalizedVaccineGroupCode);
            return List.of();
        }

        return Optional.ofNullable(vaccineGroupConcept.get().property())
                .stream()
                .flatMap(Collection::stream)
                .filter(property -> "diseaseImmunity".equals(property.code()))
                .map(CodeSystemConceptProperty::valueCoding)
                .filter(Objects::nonNull)
                .map(diseaseImmunity -> supportedDiseaseConcepts.stream()
                        .filter(concept -> concept.code().equals(diseaseImmunity.code()))
                        .findFirst()
                        .map(supportedDiseaseConcept -> CodeableConcept.builder()
                                .text(supportedDiseaseConcept.display())
                                .coding(Optional.ofNullable(supportedDiseaseConcept.property())
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .filter(prop -> prop.code().equals("outboundCode"))
                                        .map(CodeSystemConceptProperty::valueCoding)
                                        .filter(Objects::nonNull)
                                        .map(valueCoding -> Coding.builder()
                                                .code(valueCoding.code())
                                                .system(toFhirCodeSystemUrl(kmId, valueCoding.system()))
                                                .display(valueCoding.display())
                                                .build())
                                        .sorted(Comparator.comparing(coding -> Optional.ofNullable(coding.display()).orElse(""),
                                                Comparator.nullsLast(String::compareTo)))
                                        .toList())
                                .build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public CodeableConcept getCodeableConcept(final String kmId, final String code, final String displayName,
            final String codeSystem, final String originalText)
    {
        final String resolvedDisplayName = resolveConceptDisplayName(kmId, codeSystem, code).orElse(displayName);
        final String text = Optional.ofNullable(originalText)
                .filter(Predicate.not(String::isBlank))
                .or(() -> Optional.ofNullable(resolvedDisplayName).filter(Predicate.not(String::isBlank)))
                .orElse(code);
        return CodeableConcept.builder()
                .text(text)
                .coding(Coding.builder()
                        .code(code)
                        .system(toFhirCodeSystemUrl(kmId, codeSystem))
                        .display(resolvedDisplayName)
                        .build())
                .build();
    }

    private String normalizeSeriesCodeForLookup(final String value)
    {
        if (!StringUtils.hasText(value))
            return "";

        String normalized = value.trim();
        if (normalized.startsWith(SERIES_CONCEPT_TYPE_PREFIX))
            normalized = normalized.substring(SERIES_CONCEPT_TYPE_PREFIX.length());
        if (normalized.startsWith(SERIES_OPENCDS_PREFIX))
            normalized = normalized.substring(SERIES_OPENCDS_PREFIX.length());
        return normalized.trim();
    }

    public String getSupportedSeriesDisplayName(final String kmId, final String seriesCode)
    {
        if (!StringUtils.hasText(seriesCode))
            return null;

        final String normalizedSeriesCode = normalizeSeriesCodeForLookup(seriesCode);
        if (!StringUtils.hasText(normalizedSeriesCode))
            return null;

        final var knowledgeModule = getKnowledgeModule(kmId);
        final String displayFromSeriesData = Optional.ofNullable(knowledgeModule.series())
                .orElse(Map.of())
                .entrySet()
                .stream()
                .filter(entry -> normalizedSeriesCode.equals(normalizeSeriesCodeForLookup(entry.getKey()))
                        || normalizedSeriesCode.equals(Optional.ofNullable(entry.getValue())
                        .map(SeriesData::series)
                        .map(Series::code)
                        .map(this::normalizeSeriesCodeForLookup)
                        .orElse(null)))
                .map(Map.Entry::getValue)
                .map(SeriesData::series)
                .filter(Objects::nonNull)
                .map(series -> Optional.ofNullable(series.displayName()).filter(StringUtils::hasText).orElse(series.code()))
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
        if (displayFromSeriesData != null)
            return displayFromSeriesData;

        final String displayFromSeriesPlanDefinition = Optional.ofNullable(knowledgeModule.planDefinitions())
                .orElse(Map.of())
                .entrySet()
                .stream()
                .filter(entry -> planDefinitionSeriesDataConsumer.isSeriesPlanDefinition(entry.getValue()))
                .filter(entry -> normalizedSeriesCode.equals(normalizeSeriesCodeForLookup(entry.getKey()))
                        || normalizedSeriesCode.equals(Optional.ofNullable(entry.getValue())
                        .map(PlanDefinition::name)
                        .map(this::normalizeSeriesCodeForLookup)
                        .orElse(null)))
                .map(Map.Entry::getValue)
                .map(planDefinition -> Optional.ofNullable(planDefinition.title())
                        .filter(StringUtils::hasText)
                        .orElse(planDefinition.name()))
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
        if (displayFromSeriesPlanDefinition != null)
            return displayFromSeriesPlanDefinition;

        return Optional.ofNullable(knowledgeModule.codeSystems())
                .map(codeSystems -> codeSystems.get(SUPPORTED_SERIES_CODE_SYSTEM_NAME))
                .map(CodeSystem::concept)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .filter(concept -> normalizedSeriesCode.equals(normalizeSeriesCodeForLookup(concept.code())))
                .map(concept -> Optional.ofNullable(concept.display()).orElse(concept.code()))
                .findFirst()
                .orElse(null);
    }

    @Cacheable("seriesSelectionTypeDisplayName")
    public String getSeriesSelectionTypeDisplayName(@NotBlank final String kmId, @NotBlank final String selectionTypeCode)
    {
        if (!StringUtils.hasText(selectionTypeCode))
            return null;

        return streamCandidateKnowledgeModules(kmId).map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                .filter(Objects::nonNull)
                .map(codeSystems -> codeSystems.get("SERIES_DISPLAY_SELECTION_TYPE"))
                .filter(Objects::nonNull)
                .map(CodeSystem::concept)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(concept -> selectionTypeCode.equals(concept.code()))
                .map(concept -> Optional.ofNullable(concept.display()).orElse(concept.code()))
                .findFirst()
                .orElse(null);
    }

    public boolean isCodeInCodeSystem(final String kmId, final String codeSystemName, final String code)
    {
        if (!StringUtils.hasText(codeSystemName) || !StringUtils.hasText(code))
            return false;

        return streamCandidateKnowledgeModules(kmId).map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                .filter(Objects::nonNull)
                .map(codeSystems -> codeSystems.get(codeSystemName))
                .filter(Objects::nonNull)
                .map(CodeSystem::concept)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(CodeSystemConcept::code)
                .filter(Objects::nonNull)
                .anyMatch(code::equals);
    }

    public boolean isCodeSupportedInCodeSystem(final String kmId, final String codeSystemName, final String code)
    {
        if (!StringUtils.hasText(codeSystemName) || !StringUtils.hasText(code))
            return false;

        return streamCandidateKnowledgeModules(kmId).map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                .filter(Objects::nonNull)
                .map(codeSystems -> codeSystems.get(codeSystemName))
                .filter(Objects::nonNull)
                .map(CodeSystem::concept)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(concept -> code.equals(concept.code()))
                .findFirst()
                .map(this::isConceptSupported)
                .orElse(false);
    }

    private boolean isConceptSupported(final CodeSystemConcept concept)
    {
        if (concept == null)
            return false;

        return Optional.ofNullable(concept.property())
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(property -> "supported".equals(property.code()))
                .map(CodeSystemConceptProperty::valueBoolean)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(true);
    }

    public String normalizeObservationValueCodeSystemOid(final String kmId, final String codeSystemOid, final String code)
    {
        if (!StringUtils.hasText(kmId) || !StringUtils.hasText(codeSystemOid) || !StringUtils.hasText(code))
            return codeSystemOid;

        if (isCodeInCodeSystem(kmId, ICEConceptType.DISEASE_IMMUNITY_SOURCE.getIceConceptTypeValue(), code) && (
                RECOMMENDATION_REASON_CODE_SYSTEM.equals(codeSystemOid) || EVALUATION_REASON_CODE_SYSTEM.equals(codeSystemOid)))
            return DISEASE_IMMUNITY_SOURCE_CODE_SYSTEM;

        return codeSystemOid;
    }

    public String normalizeObservationInterpretationCodeSystemOid(final String kmId, final String codeSystemOid, final String code)
    {
        if (!StringUtils.hasText(kmId) || !StringUtils.hasText(codeSystemOid) || !StringUtils.hasText(code))
            return codeSystemOid;

        if (isCodeInCodeSystem(kmId, ICEConceptType.DISEASE_IMMUNITY_REASON.getIceConceptTypeValue(), code))
            return DISEASE_IMMUNITY_REASON_CODE_SYSTEM;

        return codeSystemOid;
    }

    public String toFhirCodeSystemUrl(final String kmId, final String codeSystem)
    {
        if (codeSystem == null)
            return null;

        final String normalized = codeSystem.trim();
        if (normalized.isEmpty())
            return normalized;

        final String oid = normalizeCodeSystemToOid(normalized);
        final Map<String, String> outboundCodeSystemMap = getOutboundCodeSystemMap(kmId);

        final String mappedSystem = Optional.ofNullable(outboundCodeSystemMap.get(oid))
                .or(() -> Optional.ofNullable(outboundCodeSystemMap.get(normalized)))
                .or(() -> Optional.ofNullable(outboundCodeSystemMap.get(OID_URN_PREFIX + oid)))
                .orElse(null);
        if (mappedSystem != null)
            return mappedSystem;

        if (OID_PATTERN.matcher(oid).matches())
            return oid;

        return normalized;
    }

    private String toInternalCodeSystemOid(final String kmId, final String codeSystem)
    {
        if (codeSystem == null)
            return null;

        final String normalized = codeSystem.trim();
        if (normalized.isEmpty())
            return normalized;

        final String oid = normalizeCodeSystemToOid(normalized);
        if (OID_PATTERN.matcher(oid).matches())
            return oid;

        final String mappedOid = getInboundCodeSystemOidMap(kmId).get(normalizeCodeSystemLookupKey(normalized));
        if (mappedOid == null)
            return normalized;

        return mappedOid;
    }

    public String toRequiredInternalCodeSystemOid(final String kmId, final String codeSystem)
    {
        if (codeSystem == null)
            return null;

        final String canonicalCodeSystem = codeSystem.trim();
        final String oidOrMapped = toInternalCodeSystemOid(kmId, canonicalCodeSystem);
        if (!ObjectUtils.isEmpty(oidOrMapped) && !canonicalCodeSystem.equals(oidOrMapped))
            return oidOrMapped;

        throw new IllegalArgumentException(
                "Unsupported code system '%s' for knowledge module '%s'; no outbound-code-system-map mapping found".formatted(
                        codeSystem, kmId));
    }

    private Map<String, String> getOutboundCodeSystemMap(final String kmId)
    {
        if (!StringUtils.hasText(kmId))
            return Map.of();

        return Optional.ofNullable(outboundCodeSystemMapByKm.get(kmId)).orElseGet(Map::of);
    }

    private Map<String, String> getInboundCodeSystemOidMap(final String kmId)
    {
        if (!StringUtils.hasText(kmId))
            return Map.of();

        return Optional.ofNullable(inboundCodeSystemOidMapByKm.get(kmId)).orElseGet(Map::of);
    }

    public String getBaseKnowledgeModuleId()
    {
        if (!StringUtils.hasText(baseKnowledgeModuleId))
            throw new IllegalStateException(
                    "Base knowledge module ID is not available; verify ice-base-module-canonical and plan-definition mappings");

        return baseKnowledgeModuleId;
    }

    private Optional<String> resolveConceptDisplayName(final String kmId, final String codeSystem, final String code)
    {
        if (ObjectUtils.isEmpty(codeSystem) || ObjectUtils.isEmpty(code))
            return Optional.empty();

        final String normalizedCode = code.trim();
        if (normalizedCode.isEmpty())
            return Optional.empty();

        final String normalizedCodeSystem = toInternalCodeSystemOid(kmId, codeSystem);
        if (ObjectUtils.isEmpty(normalizedCodeSystem))
            return Optional.empty();

        return Optional.ofNullable(conceptDisplayLookupByKm.get(kmId))
                .map(byCodeSystem -> byCodeSystem.get(normalizedCodeSystem))
                .map(byCode -> byCode.get(normalizedCode))
                .filter(Predicate.not(String::isBlank));
    }

    public String extractCodeSystemOid(final CodeSystem codeSystem)
    {
        return Optional.ofNullable(codeSystem.identifier())
                .orElseGet(List::of)
                .stream()
                .filter(Objects::nonNull)
                .filter(identifier -> IDENTIFIER_SYSTEM_URN_RFC_3986.equalsIgnoreCase(
                        Optional.ofNullable(identifier.system()).orElse("")))
                .map(Identifier::value)
                .map(value -> Optional.ofNullable(value).map(String::trim).orElse(""))
                .findFirst()
                .map(value -> normalizeAndValidateOidValue(codeSystem.name(), value))
                .orElseThrow(() -> new IllegalArgumentException(
                        "CodeSystem '%s' must declare identifier.system '%s' with a valid identifier.value 'urn:oid:<oid>'".formatted(
                                Optional.ofNullable(codeSystem.name()).orElse("<unknown>"), IDENTIFIER_SYSTEM_URN_RFC_3986)));
    }
}
