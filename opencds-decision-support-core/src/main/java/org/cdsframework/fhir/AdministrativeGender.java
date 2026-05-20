package org.cdsframework.fhir;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdministrativeGender
{
    MALE("male"),
    FEMALE("female"),
    OTHER("other"),
    UNKNOWN("unknown");

    @JsonCreator
    public static AdministrativeGender fromValue(final String value)
    {
        if (value == null)
            return null;

        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown AdministrativeGender: " + value));
    }

    @JsonValue
    private final String value;
}
