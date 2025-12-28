package org.cdsframework.ice.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

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
        NEW,
        BOTH
    }

    @NotBlank
    private String iceBaseRulesScopingEntityId;

    @NotBlank
    private String iceBaseRulesVersion;

    @NotNull
    private Boolean outputEarliestAndOverdueDates;

    @NotNull
    private Boolean enableDoseOverrideFeature;

    @NotNull
    private Boolean outputSupplementalText;

    @NotNull
    private Boolean enableUnsupportedVaccinesGroup;

    @NotEmpty
    private List<@NotBlank String> vaccineGroupExclusions;

    @NotNull
    private Boolean disableCovid19Sep2023DoseNumberReset;

    @NotBlank
    private String iceVersion;

    @Positive
    private int fireLimit;

    @NotNull
    private Boolean enableDroolsEventLogging;

    @NotNull
    private SupplementalTextMode supplementalTextMode;
}
