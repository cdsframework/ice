package org.cdsframework.ice.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Extension;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.supportingdata.Dose;
import org.cdsframework.ice.supportingdata.DoseInterval;
import org.cdsframework.ice.supportingdata.DoseVaccine;
import org.cdsframework.ice.supportingdata.Season;
import org.cdsframework.ice.supportingdata.Series;
import org.cdsframework.ice.supportingdata.SeriesData;
import org.cdsframework.ice.supportingdata.Vaccine;
import org.cdsframework.ice.supportingdata.VaccineGroup;

public class PlanDefinitionSeriesDataConsumer
{
    private record DoseParseResult(Map<String, Dose> doses,
                                   Map<String, Integer> doseNumberByActionId)
    {
    }

    public static final String SERIES_PLAN_DEFINITION_PROFILE_URL =
            "http://cdsframework.org/fhir/StructureDefinition/ice-series-plan-definition";
    public static final String SERIES_METADATA_EXTENSION_URL =
            "http://cdsframework.org/fhir/StructureDefinition/ice-series-metadata";
    private static final String DOSE_NUMBER_EXTENSION_URL = "http://cdsframework.org/fhir/StructureDefinition/ice-dose-number";
    private static final String DOSE_AGE_CONSTRAINT_EXTENSION_URL =
            "http://cdsframework.org/fhir/StructureDefinition/ice-dose-age-constraint";
    private static final String DOSE_INTERVAL_CONSTRAINT_EXTENSION_URL =
            "http://cdsframework.org/fhir/StructureDefinition/ice-dose-interval-constraint";
    private static final String DOSE_VACCINE_EXTENSION_URL = "http://cdsframework.org/fhir/StructureDefinition/ice-dose-vaccine";
    private static final String SERIES_CODE_EXTENSION_URL = "series";
    private static final String NUMBER_OF_DOSES_IN_SERIES_EXTENSION_URL = "numberOfDosesInSeries";
    private static final String RECURRING_DOSES_AFTER_COMPLETE_EXTENSION_URL = "recurringDosesAfterSeriesComplete";
    private static final String SERIES_GROUP_EXTENSION_URL = "seriesGroup";
    private static final String PATIENT_START_AGE_EXTENSION_URL = "patientStartAge";
    private static final String PATIENT_END_AGE_EXTENSION_URL = "patientEndAge";
    private static final String DISEASE_TARGETING_DOSE_NUMBER_CALCULATION_EXTENSION_URL =
            "doseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered";
    private static final String VACCINE_GROUP_EXTENSION_URL = "vaccineGroup";
    private static final String SEASON_EXTENSION_URL = "season";
    private static final String ABSOLUTE_MINIMUM_AGE_EXTENSION_URL = "absoluteMinimumAge";
    private static final String MINIMUM_AGE_EXTENSION_URL = "minimumAge";
    private static final String EARLIEST_RECOMMENDED_AGE_EXTENSION_URL = "earliestRecommendedAge";
    private static final String LATEST_RECOMMENDED_AGE_EXTENSION_URL = "latestRecommendedAge";
    private static final String ABSOLUTE_MAXIMUM_AGE_EXTENSION_URL = "absoluteMaximumAge";
    private static final String VACCINE_EXTENSION_URL = "vaccine";
    private static final String PREFERRED_EXTENSION_URL = "preferred";
    private static final String ALLOWABLE_MINIMUM_AGE_OF_USE_EXTENSION_URL = "allowableMinimumAgeOfUse";
    private static final String ALLOWABLE_MAXIMUM_AGE_OF_USE_EXTENSION_URL = "allowableMaximumAgeOfUse";
    private static final String ABSOLUTE_MINIMUM_INTERVAL_EXTENSION_URL = "absoluteMinimumInterval";
    private static final String MINIMUM_INTERVAL_EXTENSION_URL = "minimumInterval";
    private static final String EARLIEST_RECOMMENDED_INTERVAL_EXTENSION_URL = "earliestRecommendedInterval";
    private static final String LATEST_RECOMMENDED_INTERVAL_EXTENSION_URL = "latestRecommendedInterval";
    private static final Pattern TRAILING_INTEGER_PATTERN = Pattern.compile(".*?(\\d+)$");
    private static final Map<String, String> CODE_SYSTEM_NAMES_BY_SYSTEM =
            Map.of("2.16.840.1.113883.3.795.12.100.10", "ICE Vaccine Series", "2.16.840.1.113883.3.795.12.100.1",
                    "ICE Vaccine Group", "2.16.840.1.113883.12.292", "CVX", "http://hl7.org/fhir/sid/cvx", "CVX");

    public Map<String, SeriesData> toSeriesDataMap(final Map<String, PlanDefinition> planDefinitions)
    {
        if (planDefinitions == null || planDefinitions.isEmpty())
            return Map.of();

        final Map<String, SeriesData> seriesDataByCode = new LinkedHashMap<>();
        planDefinitions.forEach((seriesCode, planDefinition) ->
        {
            if (seriesCode == null || seriesCode.isBlank() || planDefinition == null)
                return;
            if (!isSeriesPlanDefinition(planDefinition))
                return;
            seriesDataByCode.put(seriesCode, toSeriesData(planDefinition));
        });
        return Map.copyOf(seriesDataByCode);
    }

    public boolean isSeriesPlanDefinition(final PlanDefinition planDefinition)
    {
        return Optional.ofNullable(planDefinition)
                .map(PlanDefinition::meta)
                .map(meta -> meta.profile()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .anyMatch(SERIES_PLAN_DEFINITION_PROFILE_URL::equals))
                .orElse(false);
    }

    private SeriesData toSeriesData(final PlanDefinition planDefinition)
    {
        final Extension seriesMetadata = getRequiredExtension(planDefinition.extension(), SERIES_METADATA_EXTENSION_URL);
        final Series series = parseSeries(getRequiredCodeableConcept(seriesMetadata, SERIES_CODE_EXTENSION_URL));

        final Integer numberOfDosesInSeries =
                Optional.ofNullable(getOptionalInteger(seriesMetadata.extension(), NUMBER_OF_DOSES_IN_SERIES_EXTENSION_URL))
                        .orElseGet(() -> Optional.ofNullable(planDefinition.action()).orElse(List.of()).size());
        if (numberOfDosesInSeries <= 0)
            throw new IllegalArgumentException("PlanDefinition series metadata must include numberOfDosesInSeries");

        final Boolean recurringDosesAfterSeriesComplete =
                getOptionalBoolean(seriesMetadata.extension(), RECURRING_DOSES_AFTER_COMPLETE_EXTENSION_URL);
        final Integer seriesGroup = getOptionalInteger(seriesMetadata.extension(), SERIES_GROUP_EXTENSION_URL);
        final String patientStartAge = getOptionalString(seriesMetadata.extension(), PATIENT_START_AGE_EXTENSION_URL);
        final String patientEndAge = getOptionalString(seriesMetadata.extension(), PATIENT_END_AGE_EXTENSION_URL);
        final Boolean doseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered =
                getOptionalBoolean(seriesMetadata.extension(), DISEASE_TARGETING_DOSE_NUMBER_CALCULATION_EXTENSION_URL);

        final DoseParseResult doseParseResult = parseDoses(planDefinition.action());

        return new SeriesData(series, numberOfDosesInSeries, recurringDosesAfterSeriesComplete, parseSeasonMap(seriesMetadata),
                parseVaccineGroupMap(seriesMetadata), seriesGroup, patientStartAge, patientEndAge,
                doseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered, doseParseResult.doses(),
                parseDoseIntervalMap(planDefinition.action(), doseParseResult.doseNumberByActionId()));
    }

    private Map<String, VaccineGroup> parseVaccineGroupMap(final Extension seriesMetadata)
    {
        final Map<String, VaccineGroup> vaccineGroups = new LinkedHashMap<>();

        int key = 1;
        for (final Extension vaccineGroupExtension : findextension(seriesMetadata.extension(), VACCINE_GROUP_EXTENSION_URL))
        {
            final CodeableConcept concept = requireCodeableConcept(vaccineGroupExtension, VACCINE_GROUP_EXTENSION_URL);
            vaccineGroups.put(Integer.toString(key++), parseVaccineGroup(concept));
        }

        if (vaccineGroups.isEmpty())
            throw new IllegalArgumentException("PlanDefinition series metadata must include at least one vaccineGroup");
        return Map.copyOf(vaccineGroups);
    }

    private Map<String, Season> parseSeasonMap(final Extension seriesMetadata)
    {
        final Map<String, Season> seasons = new LinkedHashMap<>();

        int key = 1;
        for (final Extension seasonExtension : findextension(seriesMetadata.extension(), SEASON_EXTENSION_URL))
        {
            final CodeableConcept concept = requireCodeableConcept(seasonExtension, "season");
            seasons.put(Integer.toString(key++), parseSeason(concept));
        }

        return seasons.isEmpty() ? null : Map.copyOf(seasons);
    }

    private DoseParseResult parseDoses(final List<PlanDefinition.Action> actions)
    {
        final List<PlanDefinition.Action> actionList = Optional.ofNullable(actions).orElse(List.of());
        if (actionList.isEmpty())
            throw new IllegalArgumentException("PlanDefinition series data must include at least one action");

        final Map<String, Dose> doses = new LinkedHashMap<>();
        final Map<String, Integer> doseNumberByActionId = new LinkedHashMap<>();

        for (int i = 0; i < actionList.size(); i++)
        {
            final PlanDefinition.Action action = actionList.get(i);
            if (action == null)
                continue;

            final Integer doseNumber = resolveDoseNumber(action, i);
            final String doseKey = Integer.toString(doseNumber);
            if (doses.containsKey(doseKey))
                throw new IllegalArgumentException("Duplicate dose number '%s' in PlanDefinition actions".formatted(doseKey));

            final Extension ageConstraint = getOptionalExtension(action.extension(), DOSE_AGE_CONSTRAINT_EXTENSION_URL);
            final List<Extension> ageExtensions = ageConstraint == null ? List.of() : ageConstraint.extension();

            doses.put(doseKey, new Dose(null, getOptionalString(ageExtensions, ABSOLUTE_MINIMUM_AGE_EXTENSION_URL),
                    getOptionalString(ageExtensions, MINIMUM_AGE_EXTENSION_URL),
                    getOptionalString(ageExtensions, EARLIEST_RECOMMENDED_AGE_EXTENSION_URL),
                    getOptionalString(ageExtensions, LATEST_RECOMMENDED_AGE_EXTENSION_URL),
                    getOptionalString(ageExtensions, ABSOLUTE_MAXIMUM_AGE_EXTENSION_URL), parseDoseVaccineMap(action)));

            if (action.id() != null && !action.id().isBlank())
                doseNumberByActionId.put(action.id(), doseNumber);
        }

        if (doses.isEmpty())
            throw new IllegalArgumentException("PlanDefinition series data must include at least one dose action");

        return new DoseParseResult(Map.copyOf(doses), Map.copyOf(doseNumberByActionId));
    }

    private Map<String, DoseVaccine> parseDoseVaccineMap(final PlanDefinition.Action action)
    {
        final Map<String, DoseVaccine> doseVaccines = new LinkedHashMap<>();
        final List<Extension> doseVaccineExtensions = findextension(action.extension(), DOSE_VACCINE_EXTENSION_URL);

        for (int i = 0; i < doseVaccineExtensions.size(); i++)
        {
            final Extension doseVaccineExtension = doseVaccineExtensions.get(i);
            final Extension vaccineExtension = getRequiredExtension(doseVaccineExtension.extension(), VACCINE_EXTENSION_URL);
            final CodeableConcept vaccineConcept = requireCodeableConcept(vaccineExtension, VACCINE_EXTENSION_URL);

            doseVaccines.put(Integer.toString(i + 1),
                    new DoseVaccine(getOptionalBoolean(doseVaccineExtension.extension(), PREFERRED_EXTENSION_URL),
                            parseVaccine(vaccineConcept),
                            getOptionalString(doseVaccineExtension.extension(), ALLOWABLE_MINIMUM_AGE_OF_USE_EXTENSION_URL),
                            getOptionalString(doseVaccineExtension.extension(), ALLOWABLE_MAXIMUM_AGE_OF_USE_EXTENSION_URL)));
        }

        if (doseVaccines.isEmpty())
            throw new IllegalArgumentException("PlanDefinition actions must include at least one dose vaccine");

        return Map.copyOf(doseVaccines);
    }

    private Map<String, DoseInterval> parseDoseIntervalMap(final List<PlanDefinition.Action> actions,
            final Map<String, Integer> doseNumberByActionId)
    {
        final Map<String, DoseInterval> doseIntervals = new LinkedHashMap<>();
        int intervalKey = 1;

        final List<PlanDefinition.Action> actionList = Optional.ofNullable(actions).orElse(List.of());
        for (int i = 0; i < actionList.size(); i++)
        {
            final PlanDefinition.Action action = actionList.get(i);
            if (action == null)
                continue;

            final List<PlanDefinition.RelatedAction> relatedActions = Optional.ofNullable(action.relatedAction()).orElse(List.of());

            for (final PlanDefinition.RelatedAction relatedAction : relatedActions)
            {
                if (relatedAction == null)
                    continue;

                final List<Extension> intervalConstraints =
                        findextension(relatedAction.extension(), DOSE_INTERVAL_CONSTRAINT_EXTENSION_URL);
                if (intervalConstraints.isEmpty())
                    continue;

                final Integer toDoseNumber = resolveToDoseNumber(action, relatedAction, i, doseNumberByActionId);
                Integer fromDoseNumber = resolveFromDoseNumber(relatedAction.actionId(), doseNumberByActionId);
                if (fromDoseNumber == null && toDoseNumber > 1)
                    fromDoseNumber = toDoseNumber - 1;

                if (fromDoseNumber == null)
                    throw new IllegalArgumentException(
                            "Unable to resolve from-dose number for interval constraint on action '%s'".formatted(
                                    Optional.ofNullable(action.id()).orElse("<unknown>")));

                for (final Extension intervalConstraint : intervalConstraints)
                {
                    doseIntervals.put(Integer.toString(intervalKey++), new DoseInterval(fromDoseNumber, toDoseNumber,
                            getOptionalString(intervalConstraint.extension(), ABSOLUTE_MINIMUM_INTERVAL_EXTENSION_URL),
                            getOptionalString(intervalConstraint.extension(), MINIMUM_INTERVAL_EXTENSION_URL),
                            getOptionalString(intervalConstraint.extension(), EARLIEST_RECOMMENDED_INTERVAL_EXTENSION_URL),
                            getOptionalString(intervalConstraint.extension(), LATEST_RECOMMENDED_INTERVAL_EXTENSION_URL)));
                }
            }
        }

        return doseIntervals.isEmpty() ? null : Map.copyOf(doseIntervals);
    }

    private Integer resolveToDoseNumber(final PlanDefinition.Action action, final PlanDefinition.RelatedAction relatedAction,
            final int actionIndex, final Map<String, Integer> doseNumberByActionId)
    {
        final Integer toDose = resolveDoseNumberReference(relatedAction.targetId(), doseNumberByActionId);
        if (toDose != null)
            return toDose;
        return resolveDoseNumber(action, actionIndex);
    }

    private Integer resolveFromDoseNumber(final String actionId, final Map<String, Integer> doseNumberByActionId)
    {
        return resolveDoseNumberReference(actionId, doseNumberByActionId);
    }

    private Integer resolveDoseNumberReference(final String actionReference, final Map<String, Integer> doseNumberByActionId)
    {
        if (actionReference == null || actionReference.isBlank())
            return null;

        final Integer fromDose = doseNumberByActionId.get(actionReference);
        return fromDose != null ? fromDose : trailingInteger(actionReference);
    }

    private Integer resolveDoseNumber(final PlanDefinition.Action action, final int actionIndex)
    {
        return Optional.ofNullable(getOptionalInteger(action.extension(), DOSE_NUMBER_EXTENSION_URL)).orElse(actionIndex + 1);
    }

    private Series parseSeries(final CodeableConcept concept)
    {
        final Coding coding = getRequiredCoding(concept, "series");
        final String codeSystem = requireNonBlank(normalizeCodeSystem(coding.system()), "series codeSystem");
        return new Series(requireNonBlank(coding.code(), "series code"), codeSystem,
                requireNonBlank(resolveCodeSystemName(codeSystem, coding.version()), "series codeSystemName"),
                firstNonBlank(coding.display(), concept.text(), coding.code()));
    }

    private VaccineGroup parseVaccineGroup(final CodeableConcept concept)
    {
        final Coding coding = getRequiredCoding(concept, "vaccineGroup");
        final String codeSystem = normalizeCodeSystem(coding.system());
        return new VaccineGroup(requireNonBlank(coding.code(), "vaccineGroup code"), blankToNull(codeSystem),
                blankToNull(resolveCodeSystemName(codeSystem, coding.version())),
                firstNonBlank(coding.display(), concept.text(), coding.code()));
    }

    private Vaccine parseVaccine(final CodeableConcept concept)
    {
        final Coding coding = getRequiredCoding(concept, "vaccine");
        final String codeSystem = requireNonBlank(normalizeCodeSystem(coding.system()), "vaccine codeSystem");
        return new Vaccine(requireNonBlank(coding.code(), "vaccine code"), codeSystem,
                requireNonBlank(resolveCodeSystemName(codeSystem, coding.version()), "vaccine codeSystemName"),
                firstNonBlank(coding.display(), concept.text(), coding.code()));
    }

    private Season parseSeason(final CodeableConcept concept)
    {
        final Coding coding = getRequiredCoding(concept, "season");
        final String codeSystem = requireNonBlank(normalizeCodeSystem(coding.system()), "season codeSystem");
        return new Season(requireNonBlank(coding.code(), "season code"), codeSystem,
                requireNonBlank(resolveCodeSystemName(codeSystem, coding.version()), "season codeSystemName"),
                firstNonBlank(coding.display(), concept.text(), coding.code()));
    }

    private Coding getRequiredCoding(final CodeableConcept concept, final String fieldName)
    {
        return Optional.ofNullable(concept)
                .map(CodeableConcept::coding)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Required coding for '%s' is missing".formatted(fieldName)));
    }

    private CodeableConcept getRequiredCodeableConcept(final Extension parent,
            @SuppressWarnings("SameParameterValue") final String childUrl)
    {
        final Extension child = getRequiredExtension(parent.extension(), childUrl);
        return requireCodeableConcept(child, childUrl);
    }

    private CodeableConcept requireCodeableConcept(final Extension extension, final String fieldName)
    {
        final CodeableConcept concept = codeableConceptValue(extension);
        if (concept == null)
            throw new IllegalArgumentException("Required CodeableConcept extension '%s' is missing".formatted(fieldName));
        return concept;
    }

    private CodeableConcept codeableConceptValue(final Extension extension)
    {
        if (extension.valueCodeableConcept() != null)
            return extension.valueCodeableConcept();
        if (extension.valueCoding() != null)
            return CodeableConcept.builder().coding(extension.valueCoding()).build();
        return null;
    }

    private List<Extension> findextension(final List<Extension> extensions, final String url)
    {
        return Optional.ofNullable(extensions)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .filter(extension -> url.equals(extension.url()))
                .toList();
    }

    private Extension getRequiredExtension(final List<Extension> extensions, final String url)
    {
        return Optional.ofNullable(getOptionalExtension(extensions, url))
                .orElseThrow(() -> new IllegalArgumentException("Required extension '%s' is missing".formatted(url)));
    }

    private Extension getOptionalExtension(final List<Extension> extensions, final String url)
    {
        return findextension(extensions, url).stream().findFirst().orElse(null);
    }

    private String getOptionalString(final List<Extension> extensions, final String url)
    {
        return findextension(extensions, url).stream()
                .findFirst()
                .map(this::stringValue)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .orElse(null);
    }

    private Integer getOptionalInteger(final List<Extension> extensions, final String url)
    {
        return findextension(extensions, url).stream().findFirst().map(this::integerValue).orElse(null);
    }

    private Boolean getOptionalBoolean(final List<Extension> extensions, final String url)
    {
        return findextension(extensions, url).stream().findFirst().map(this::booleanValue).orElse(null);
    }

    private String stringValue(final Extension extension)
    {
        if (extension.valueString() != null)
            return extension.valueString();
        if (extension.valueCode() != null)
            return extension.valueCode();
        if (extension.valueInteger() != null)
            return Integer.toString(extension.valueInteger());
        if (extension.valueBoolean() != null)
            return Boolean.toString(extension.valueBoolean());
        return "";
    }

    private Integer integerValue(final Extension extension)
    {
        if (extension.valueInteger() != null)
            return extension.valueInteger();

        final String value = stringValue(extension);
        return value.isBlank() ? null : Integer.valueOf(value);
    }

    private Boolean booleanValue(final Extension extension)
    {
        if (extension.valueBoolean() != null)
            return extension.valueBoolean();

        final String value = stringValue(extension);
        return value.isBlank() ? null : Boolean.valueOf(value);
    }

    private String normalizeCodeSystem(final String codeSystem)
    {
        final String value = blankToNull(codeSystem);
        if (value == null)
            return "";
        return value.startsWith("urn:oid:") ? value.substring("urn:oid:".length()) : value;
    }

    private String resolveCodeSystemName(final String codeSystem, final String explicitCodeSystemName)
    {
        final String explicit = blankToNull(explicitCodeSystemName);
        if (explicit != null)
            return explicit;

        final String normalizedCodeSystem = normalizeCodeSystem(codeSystem);
        final String resolved = CODE_SYSTEM_NAMES_BY_SYSTEM.get(normalizedCodeSystem);
        return resolved != null ? resolved : normalizedCodeSystem;
    }

    private Integer trailingInteger(final String value)
    {
        if (value == null)
            return null;

        final Matcher matcher = TRAILING_INTEGER_PATTERN.matcher(value);
        return matcher.matches() ? Integer.valueOf(matcher.group(1)) : null;
    }

    private String requireNonBlank(final String value, final String fieldName)
    {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Required value for '%s' is missing".formatted(fieldName));
        return value;
    }

    private String firstNonBlank(final String... values)
    {
        if (values == null)
            return null;
        for (final String value : values)
            if (value != null && !value.isBlank())
                return value;
        return null;
    }

    private String blankToNull(final String value)
    {
        return value == null || value.isBlank() ? null : value;
    }
}
