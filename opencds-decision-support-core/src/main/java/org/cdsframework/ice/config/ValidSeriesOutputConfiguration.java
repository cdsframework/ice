package org.cdsframework.ice.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SeriesOutputConfigurationValidator.class)
public @interface ValidSeriesOutputConfiguration
{
    String message() default "output-number-of-doses-remaining requires output-series-information-for-recommendations";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
