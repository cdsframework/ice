package org.cdsframework.ice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("open-api")
public final class OpenApiProperties
{
    public record OpenApiContactProperties(@NotNull
                                           String name,
                                           @NotNull
                                           String email,
                                           @NotNull
                                           String url)
    {
    }

    public record OpenApiLicenseProperties(@NotNull
                                           String name,
                                           @NotNull
                                           String url)
    {
    }

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String version;

    @NotNull
    @Valid
    private OpenApiContactProperties contact;

    @NotNull
    @Valid
    private OpenApiLicenseProperties license;
}

