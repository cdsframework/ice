package org.cdsframework.ice.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SeriesOutputConfigurationValidator implements ConstraintValidator<ValidSeriesOutputConfiguration, IceProperties>
{
    @Override
    public boolean isValid(final IceProperties properties, final ConstraintValidatorContext context)
    {
        if (properties == null || properties.getKnowledgeBases() == null)
            return true;

        final List<String> globalOverrides =
                Stream.of(properties.getOutputNumberOfDosesRemaining().map(_ -> "ice.output-number-of-doses-remaining"),
                        properties.getOutputSeriesInformationForRecommendations()
                                .map(_ -> "ice.output-series-information-for-recommendations")).flatMap(Optional::stream).toList();
        final String overrideNote =
                globalOverrides.isEmpty() ? "" : " (global override applied: %s)".formatted(String.join(", ", globalOverrides));

        boolean valid = true;
        for (final Map.Entry<String, IceProperties.KnowledgeBaseProperties> entry : properties.getKnowledgeBases().entrySet())
        {
            final IceProperties.KnowledgeBaseProperties knowledgeBase = entry.getValue();
            if (knowledgeBase == null || knowledgeBase.outputNumberOfDosesRemaining() == null
                    || knowledgeBase.outputSeriesInformationForRecommendations() == null)
                continue;

            if (properties.resolveOutputNumberOfDosesRemaining(knowledgeBase)
                    && !properties.resolveOutputSeriesInformationForRecommendations(knowledgeBase))
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Knowledge base '%s': output-number-of-doses-remaining=true requires output-series-information-for-recommendations=true%s".formatted(
                                entry.getKey(), overrideNote)).addPropertyNode("knowledgeBases").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}
