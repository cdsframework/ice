package org.cdsframework.ice.config;

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

    public record KnowledgeModuleProperties(@NotNull
                                            Boolean outputEarliestAndOverdueDates,
                                            @NotNull
                                            Boolean enableDoseOverrideFeature,
                                            @NotNull
                                            Boolean outputSupplementalText,
                                            @NotNull
                                            Boolean outputNumberOfDosesRemaining,
                                            @NotNull
                                            Boolean outputSeriesInformation,
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
        public KnowledgeModuleProperties
        {
            vaccineGroupExclusions = Objects.requireNonNullElseGet(vaccineGroupExclusions, List::of);
            vaccineGroupInclusions = Objects.requireNonNullElseGet(vaccineGroupInclusions, List::of);
        }
    }

    @NotBlank
    private String iceBaseModuleCanonical;

    @NotEmpty
    private Map<@NotBlank String, @NotNull @Valid KnowledgeModuleProperties> knowledgeModules;

    private List<@NotBlank String> vaccineGroupExclusions;

    private List<@NotBlank String> vaccineGroupInclusions;

    private Optional<Boolean> outputEarliestAndOverdueDates = Optional.empty();

    private Optional<Boolean> enableDoseOverrideFeature = Optional.empty();

    private Optional<Boolean> outputSupplementalText = Optional.empty();

    private Optional<Boolean> outputNumberOfDosesRemaining = Optional.empty();

    private Optional<Boolean> outputSeriesInformation = Optional.empty();

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
}
