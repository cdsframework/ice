package org.cdsframework.ice.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.CodeableConcept;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.dto.Identifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
public class SupportingDataService
{
    private static final String OID_URN_PREFIX = "urn:oid:";
    private static final String IDENTIFIER_SYSTEM_URN_RFC_3986 = "urn:ietf:rfc:3986";
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

    private final IceSupportingDataProperties iceSupportingDataProperties;
    private final IceProperties iceProperties;
    private final Map<String, Map<String, String>> outboundCodeSystemMapByKm;
    private final Map<String, Map<String, String>> inboundCodeSystemOidMapByKm;
    private final Map<String, Map<String, Map<String, String>>> conceptDisplayLookupByKm;

    public SupportingDataService(final IceSupportingDataProperties iceSupportingDataProperties, final IceProperties iceProperties)
    {
        this.iceSupportingDataProperties = iceSupportingDataProperties;
        this.iceProperties = iceProperties;
        this.outboundCodeSystemMapByKm = buildOutboundCodeSystemMapByKm();
        this.inboundCodeSystemOidMapByKm = buildInboundCodeSystemOidMapByKm(this.outboundCodeSystemMapByKm);
        this.conceptDisplayLookupByKm = buildConceptDisplayLookupByKm();
    }

    private Map<String, IceSupportingDataProperties.KnowledgeModule> getKnowledgeModules()
    {
        return Optional.ofNullable(iceSupportingDataProperties)
                .map(IceSupportingDataProperties::getKnowledgeModules)
                .orElse(Map.of());
    }

    private Optional<IceSupportingDataProperties.KnowledgeModule> getKnowledgeModule(final String kmId)
    {
        if (ObjectUtils.isEmpty(kmId))
            return Optional.empty();
        return Optional.ofNullable(getKnowledgeModules().get(kmId));
    }

    private Stream<IceSupportingDataProperties.KnowledgeModule> streamCandidateKnowledgeModules(final String kmId)
    {
        return Stream.of(kmId, deriveCommonKnowledgeModuleId(kmId))
                .filter(Objects::nonNull)
                .distinct()
                .map(this::getKnowledgeModule)
                .flatMap(Optional::stream);
    }

    private String normalizeAndValidateOidValue(final String codeSystemName, final String value)
    {
        final String normalizedOid = normalizeCodeSystemToOid(value);
        if (normalizedOid != null && OID_PATTERN.matcher(normalizedOid).matches())
            return normalizedOid;

        throw new IllegalArgumentException(
                "CodeSystem '%s' must declare identifier.system '%s' with a valid identifier.value 'urn:oid:<oid>'".formatted(
                        Optional.ofNullable(codeSystemName).orElse("<unknown>"), IDENTIFIER_SYSTEM_URN_RFC_3986));
    }

    private Map<String, Map<String, String>> buildOutboundCodeSystemMapByKm()
    {
        final Map<String, IceSupportingDataProperties.KnowledgeModule> knowledgeModules = getKnowledgeModules();
        if (knowledgeModules.isEmpty())
            return Map.of();

        final Map<String, Map<String, String>> mergedByKm = new HashMap<>();
        for (final String kmId : knowledgeModules.keySet())
        {
            final String commonKmId = deriveCommonKnowledgeModuleId(kmId);
            final Map<String, String> merged = new HashMap<>();
            merged.putAll(Optional.ofNullable(knowledgeModules.get(commonKmId))
                    .map(IceSupportingDataProperties.KnowledgeModule::outboundCodeSystemMap)
                    .orElse(Map.of()));
            merged.putAll(Optional.ofNullable(knowledgeModules.get(kmId))
                    .map(IceSupportingDataProperties.KnowledgeModule::outboundCodeSystemMap)
                    .orElse(Map.of()));

            Stream.of(kmId, commonKmId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(knowledgeModules::get)
                    .filter(Objects::nonNull)
                    .map(IceSupportingDataProperties.KnowledgeModule::codeSystems)
                    .filter(Objects::nonNull)
                    .forEach(codeSystems -> codeSystems.values().forEach(codeSystem ->
                    {
                        final String url = Optional.ofNullable(codeSystem.url()).map(String::trim).orElse(null);
                        if (url == null || url.isEmpty())
                            return;

                        final String oid = extractCodeSystemOid(codeSystem);
                        merged.putIfAbsent(oid, url);
                        merged.putIfAbsent(OID_URN_PREFIX + oid, url);
                    }));

            mergedByKm.put(kmId, Map.copyOf(merged));
        }
        return Map.copyOf(mergedByKm);
    }

    private Map<String, Map<String, String>> buildInboundCodeSystemOidMapByKm(
            final Map<String, Map<String, String>> outboundMapsByKm)
    {
        if (outboundMapsByKm.isEmpty())
            return Map.of();

        final Map<String, Map<String, String>> reverseByKm = new HashMap<>();
        outboundMapsByKm.forEach((kmId, outboundMap) ->
        {
            final Map<String, String> inboundMap = new HashMap<>();
            outboundMap.forEach((source, target) ->
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
            reverseByKm.put(kmId, Map.copyOf(inboundMap));
        });
        return Map.copyOf(reverseByKm);
    }

    private String normalizeCodeSystemLookupKey(final String value)
    {
        if (value == null)
            return null;

        final String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private Map<String, Map<String, Map<String, String>>> buildConceptDisplayLookupByKm()
    {
        final Map<String, IceSupportingDataProperties.KnowledgeModule> knowledgeModules = getKnowledgeModules();
        if (knowledgeModules.isEmpty())
            return Map.of();

        final Map<String, Map<String, Map<String, String>>> displayByKm = new HashMap<>();
        for (final String kmId : knowledgeModules.keySet())
        {
            final String commonKmId = deriveCommonKnowledgeModuleId(kmId);
            final Map<String, Map<String, String>> displayByCodeSystem = new HashMap<>();
            Stream.of(kmId, commonKmId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(knowledgeModules::get)
                    .filter(Objects::nonNull)
                    .map(IceSupportingDataProperties.KnowledgeModule::codeSystems)
                    .filter(Objects::nonNull)
                    .forEach(codeSystems -> codeSystems.values().forEach(codeSystem ->
                    {
                        final String codeSystemOid = extractCodeSystemOid(codeSystem);
                        if (!StringUtils.hasText(codeSystemOid))
                            return;

                        final Map<String, String> displayByCode =
                                displayByCodeSystem.computeIfAbsent(codeSystemOid, ignored -> new HashMap<>());
                        Optional.ofNullable(codeSystem.concept()).orElse(List.of()).forEach(concept ->
                        {
                            if (concept == null)
                                return;
                            final String display =
                                    Optional.ofNullable(concept.display()).filter(StringUtils::hasText).orElse(concept.code());
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
                    }));

            final Map<String, Map<String, String>> immutableDisplayByCodeSystem = new HashMap<>();
            displayByCodeSystem.forEach(
                    (codeSystemOid, displayByCode) -> immutableDisplayByCodeSystem.put(codeSystemOid, Map.copyOf(displayByCode)));
            displayByKm.put(kmId, Map.copyOf(immutableDisplayByCodeSystem));
        }
        return Map.copyOf(displayByKm);
    }

    @Cacheable("diseaseGroupMapping")
    public List<CodeableConcept> getDiseasesForVaccineGroup(@NotBlank final String kmId, @NotBlank final String vaccineGroupCode)
    {
        if (vaccineGroupCode == null || vaccineGroupCode.isBlank())
            return List.of();

        final String normalizedVaccineGroupCode = vaccineGroupCode.trim();

        final var knowledgeModule = getKnowledgeModule(kmId).orElse(null);
        if (knowledgeModule == null || knowledgeModule.codeSystems() == null)
            return List.of();

        final List<org.cdsframework.ice.dto.CodeSystemConcept> supportedDiseaseConcepts =
                Optional.ofNullable(knowledgeModule.codeSystems().get("SUPPORTED_DISEASE_CONCEPT"))
                        .map(org.cdsframework.ice.dto.CodeSystem::concept)
                        .orElse(List.of());

        final Optional<org.cdsframework.ice.dto.CodeSystemConcept> vaccineGroupConcept =
                Optional.ofNullable(knowledgeModule.codeSystems().get("VACCINE_GROUP_CONCEPT"))
                        .map(org.cdsframework.ice.dto.CodeSystem::concept)
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

    @Cacheable("codeableConceptCache")
    public CodeableConcept getCodeableConcept(@NotBlank final String kmId, @NotBlank final String code, final String displayName,
            @NotBlank final String codeSystem, final String originalText)
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

    @Cacheable("supportedSeriesDisplayName")
    public String getSupportedSeriesDisplayName(@NotBlank final String kmId, @NotBlank final String seriesCode)
    {
        if (seriesCode == null || seriesCode.isBlank())
            return null;

        final var knowledgeModule = getKnowledgeModule(kmId).orElse(null);
        if (knowledgeModule == null || knowledgeModule.codeSystems() == null)
            return null;

        final var supportedSeriesCodeSystem = knowledgeModule.codeSystems().get("SUPPORTED_SERIES");
        if (supportedSeriesCodeSystem == null || supportedSeriesCodeSystem.concept() == null)
            return null;

        return supportedSeriesCodeSystem.concept()
                .stream()
                .filter(concept -> seriesCode.equals(concept.code()) || Optional.ofNullable(concept.property())
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(property -> "conceptMapping".equals(property.code()))
                        .map(CodeSystemConceptProperty::valueCoding)
                        .filter(Objects::nonNull)
                        .anyMatch(valueCoding -> seriesCode.equals(valueCoding.code())))
                .map(concept -> Optional.ofNullable(concept.display()).orElse(concept.code()))
                .findFirst()
                .orElse(null);
    }

    @Cacheable("seriesSelectionTypeDisplayName")
    public String getSeriesSelectionTypeDisplayName(@NotBlank final String kmId, @NotBlank final String selectionTypeCode)
    {
        if (selectionTypeCode == null || selectionTypeCode.isBlank())
            return null;

        return streamCandidateKnowledgeModules(kmId).map(IceSupportingDataProperties.KnowledgeModule::codeSystems)
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

    @Cacheable("codeInCodeSystem")
    public boolean isCodeInCodeSystem(@NotBlank final String kmId, @NotBlank final String codeSystemName,
            @NotBlank final String code)
    {
        if (codeSystemName == null || codeSystemName.isBlank() || code == null || code.isBlank())
            return false;

        return streamCandidateKnowledgeModules(kmId).map(IceSupportingDataProperties.KnowledgeModule::codeSystems)
                .filter(Objects::nonNull)
                .map(codeSystems -> codeSystems.get(codeSystemName))
                .filter(Objects::nonNull)
                .map(CodeSystem::concept)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(org.cdsframework.ice.dto.CodeSystemConcept::code)
                .filter(Objects::nonNull)
                .anyMatch(code::equals);
    }

    @Cacheable("fhirCodeSystemUrl")
    public String toFhirCodeSystemUrl(@NotBlank final String kmId, @NotBlank final String codeSystem)
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

    @Cacheable("internalCodeSystemOid")
    public String toRequiredInternalCodeSystemOid(@NotBlank final String kmId, @NotBlank final String codeSystem)
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
        if (kmId == null || kmId.isBlank())
            return Map.of();

        return Optional.ofNullable(outboundCodeSystemMapByKm.get(kmId)).orElse(Map.of());
    }

    private Map<String, String> getInboundCodeSystemOidMap(final String kmId)
    {
        if (kmId == null || kmId.isBlank())
            return Map.of();

        return Optional.ofNullable(inboundCodeSystemOidMapByKm.get(kmId)).orElse(Map.of());
    }

    private String deriveCommonKnowledgeModuleId(final String kmId)
    {
        if (kmId == null || iceProperties == null)
            return null;

        final String[] parts = kmId.split("\\^");
        if (parts.length < 3)
            return null;

        return "%s^%s^%s".formatted(iceProperties.getIceBaseRulesScopingEntityId(), parts[1],
                iceProperties.getIceBaseRulesVersion());
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

    public String extractCodeSystemOid(@NotNull final CodeSystem codeSystem)
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
