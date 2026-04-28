package org.cdsframework.ice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
        for (final AdministrativeGender gender : values())
            if (gender.value.equalsIgnoreCase(value))
                return gender;
        throw new IllegalArgumentException("Unknown AdministrativeGender: " + value);
    }

    private final String value;

    AdministrativeGender(final String value)
    {
        this.value = value;
    }

    @JsonValue
    public String getValue()
    {
        return value;
    }
}
