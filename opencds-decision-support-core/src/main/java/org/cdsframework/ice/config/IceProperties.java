package org.cdsframework.ice.config;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Validated
@ValidSeriesOutputConfiguration
@Component
@ConfigurationProperties("ice")
public class IceProperties
{
    public enum SupplementalTextMode
    {
        LEGACY,
        CODED,
        BOTH
    }

    public record OpenApiContactProperties(String name,
                                           String email,
                                           String url)
    {
    }

    public record OpenApiLicenseProperties(String name,
                                           String url)
    {
    }

    public record OpenApiProperties(String title,
                                    String description,
                                    String version,
                                    OpenApiContactProperties contact,
                                    OpenApiLicenseProperties license)
    {
    }

    public record KnowledgeBaseProperties(@NotNull
                                          Boolean outputEarliestAndOverdueDates,
                                          @NotNull
                                          Boolean enableDoseOverrideFeature,
                                          @NotNull
                                          Boolean outputSupplementalText,
                                          @NotNull
                                          Boolean outputNumberOfDosesRemaining,
                                          @NotNull
                                          Boolean outputSeriesInformationForEvaluations,
                                          @NotNull
                                          Boolean outputSeriesInformationForRecommendations,
                                          @NotNull
                                          Boolean outputScheduleAuthorities,
                                          @NotNull
                                          Boolean outputVaccineGroupRulesArtifact,
                                          @NotNull
                                          Boolean enableUnsupportedVaccinesGroup,
                                          @NotNull
                                          List<@NotBlank String> vaccineGroupExclusions,
                                          @NotNull
                                          List<@NotBlank String> vaccineGroupInclusions,
                                          @NotNull
                                          Boolean disableCovid19Sep2023DoseNumberReset,
                                          @NotNull
                                          SupplementalTextMode supplementalTextMode,
                                          @NotNull
                                          Resource droolsPath)
    {
        public KnowledgeBaseProperties
        {
            vaccineGroupExclusions = Objects.requireNonNullElseGet(vaccineGroupExclusions, List::of);
            vaccineGroupInclusions = Objects.requireNonNullElseGet(vaccineGroupInclusions, List::of);
        }
    }

    public record SeasonOverride(LocalDate startDate,
                                 LocalDate endDate,
                                 String defaultStartMonthAndDay,
                                 String defaultStopMonthAndDay)
    {
    }

    public record SeriesOverride(Map<@Positive Integer, @NotNull @Valid SeriesDoseOverride> doseOverrides,
                                 Map<@NotNull @Positive Integer, Map<@NotNull @Positive Integer, @NotNull @Valid SeriesDoseIntervalOverride>> doseIntervalOverrides,
                                 Integer numberOfDosesInSeries,
                                 Boolean recurringDosesAfterSeriesComplete,
                                 List<String> seasons)
    {
    }

    public record SeriesDoseOverride(String absoluteMinimumAge,
                                     String minimumAge,
                                     String earliestRecommendedAge,
                                     LocalDate earliestRecommendedDate,
                                     String absoluteMaximumAge,
                                     Map<@NotBlank String, SeriesDoseVaccineOverride> seriesVaccineOverrides)
    {
    }

    public record SeriesDoseVaccineOverride(Boolean preferred,
                                            String allowableMinimumAgeOfUse)
    {
    }

    public record SeriesDoseIntervalOverride(String absoluteMinimumInterval,
                                             String minimumInterval,
                                             String earliestRecommendedInterval,
                                             String latestRecommendedInterval)
    {
    }

    @NotBlank
    private String iceBaseKnowledgeBase;

    @NotEmpty
    private Map<@NotBlank String, @NotNull @Valid KnowledgeBaseProperties> knowledgeBases;

    private List<@NotBlank String> vaccineGroupExclusions;

    private List<@NotBlank String> vaccineGroupInclusions;

    private List<@NotBlank String> scheduleFlags;

    private Map<@NotBlank String, @NotNull @Valid SeriesOverride> seriesOverrides;

    private Map<@NotBlank String, @NotNull @Valid SeasonOverride> seasonOverrides;

    private Optional<Boolean> outputEarliestAndOverdueDates = Optional.empty();

    private Optional<Boolean> enableDoseOverrideFeature = Optional.empty();

    private Optional<Boolean> outputSupplementalText = Optional.empty();

    private Optional<Boolean> outputNumberOfDosesRemaining = Optional.empty();

    private Optional<Boolean> outputSeriesInformationForEvaluations = Optional.empty();

    private Optional<Boolean> outputSeriesInformationForRecommendations = Optional.empty();

    private Optional<Boolean> outputScheduleAuthorities = Optional.empty();

    private Optional<Boolean> outputVaccineGroupRulesArtifact = Optional.empty();

    private Optional<Boolean> enableUnsupportedVaccinesGroup = Optional.empty();

    private Optional<Boolean> disableCovid19Sep2023DoseNumberReset = Optional.empty();

    private Optional<SupplementalTextMode> supplementalTextMode = Optional.empty();

    @NotBlank
    private String iceVersion;

    @Positive
    private int fireLimit;

    @NotNull
    private Boolean enableDroolsEventLogging;

    @NotNull
    private Resource configPath;

    @Valid
    private OpenApiProperties openApi;

    public boolean resolveOutputEarliestAndOverdueDates(final KnowledgeBaseProperties properties)
    {
        return outputEarliestAndOverdueDates.orElseGet(properties::outputEarliestAndOverdueDates);
    }

    public boolean resolveEnableDoseOverrideFeature(final KnowledgeBaseProperties properties)
    {
        return enableDoseOverrideFeature.orElseGet(properties::enableDoseOverrideFeature);
    }

    public boolean resolveOutputSupplementalText(final KnowledgeBaseProperties properties)
    {
        return outputSupplementalText.orElseGet(properties::outputSupplementalText);
    }

    public boolean resolveOutputNumberOfDosesRemaining(final KnowledgeBaseProperties properties)
    {
        return outputNumberOfDosesRemaining.orElseGet(properties::outputNumberOfDosesRemaining);
    }

    public boolean resolveOutputSeriesInformationForEvaluations(final KnowledgeBaseProperties properties)
    {
        return outputSeriesInformationForEvaluations.orElseGet(properties::outputSeriesInformationForEvaluations);
    }

    public boolean resolveOutputSeriesInformationForRecommendations(final KnowledgeBaseProperties properties)
    {
        return outputSeriesInformationForRecommendations.orElseGet(properties::outputSeriesInformationForRecommendations);
    }

    public boolean resolveOutputScheduleAuthorities(final KnowledgeBaseProperties properties)
    {
        return outputScheduleAuthorities.orElseGet(properties::outputScheduleAuthorities);
    }

    public boolean resolveOutputVaccineGroupRulesArtifact(final KnowledgeBaseProperties properties)
    {
        return outputVaccineGroupRulesArtifact.orElseGet(properties::outputVaccineGroupRulesArtifact);
    }

    public boolean resolveEnableUnsupportedVaccinesGroup(final KnowledgeBaseProperties properties)
    {
        return enableUnsupportedVaccinesGroup.orElseGet(properties::enableUnsupportedVaccinesGroup);
    }

    public boolean resolveDisableCovid19Sep2023DoseNumberReset(final KnowledgeBaseProperties properties)
    {
        return disableCovid19Sep2023DoseNumberReset.orElseGet(properties::disableCovid19Sep2023DoseNumberReset);
    }
}
