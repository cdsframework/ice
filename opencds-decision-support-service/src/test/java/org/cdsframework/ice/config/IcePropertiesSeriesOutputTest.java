package org.cdsframework.ice.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

class IcePropertiesSeriesOutputTest
{
    private record BooleanOverride(String propertyName,
                                   BiPredicate<IceProperties, IceProperties.KnowledgeBaseProperties> resolver)
    {
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(IceProperties.class)
    static class PropertiesConfiguration
    {
    }

    private static final String KNOWLEDGE_BASE = "test-kb";

    private static Stream<BooleanOverride> booleanOverrides()
    {
        return Stream.of(
                new BooleanOverride("output-earliest-and-overdue-dates", IceProperties::resolveOutputEarliestAndOverdueDates),
                new BooleanOverride("enable-dose-override-feature", IceProperties::resolveEnableDoseOverrideFeature),
                new BooleanOverride("output-supplemental-text", IceProperties::resolveOutputSupplementalText),
                new BooleanOverride("output-number-of-doses-remaining", IceProperties::resolveOutputNumberOfDosesRemaining),
                new BooleanOverride("output-series-information-for-evaluations",
                        IceProperties::resolveOutputSeriesInformationForEvaluations),
                new BooleanOverride("output-series-information-for-recommendations",
                        IceProperties::resolveOutputSeriesInformationForRecommendations),
                new BooleanOverride("output-schedule-authorities", IceProperties::resolveOutputScheduleAuthorities),
                new BooleanOverride("output-vaccine-group-rules-artifact", IceProperties::resolveOutputVaccineGroupRulesArtifact),
                new BooleanOverride("enable-unsupported-vaccines-group", IceProperties::resolveEnableUnsupportedVaccinesGroup),
                new BooleanOverride("disable-covid19-sep2023-dose-number-reset",
                        IceProperties::resolveDisableCovid19Sep2023DoseNumberReset));
    }

    private static ApplicationContextRunner contextRunner()
    {
        return baseContextRunner().withPropertyValues("ice.knowledge-bases.test-kb.output-series-information-for-evaluations=false",
                "ice.knowledge-bases.test-kb.output-series-information-for-recommendations=false");
    }

    private static ApplicationContextRunner baseContextRunner()
    {
        return new ApplicationContextRunner().withUserConfiguration(PropertiesConfiguration.class)
                .withPropertyValues("ice.ice-base-knowledge-base=test-kb", "ice.ice-version=test", "ice.fire-limit=100",
                        "ice.enable-drools-event-logging=false", "ice.config-path=classpath:config")
                .withPropertyValues(knowledgeBaseDefaults(KNOWLEDGE_BASE));
    }

    private static String[] knowledgeBaseDefaults(final String knowledgeBase)
    {
        return List.of("output-earliest-and-overdue-dates=true", "enable-dose-override-feature=false",
                        "output-supplemental-text=true", "output-number-of-doses-remaining=false", "output-schedule-authorities=false",
                        "output-vaccine-group-rules-artifact=false", "enable-unsupported-vaccines-group=false",
                        "disable-covid19-sep2023-dose-number-reset=false", "supplemental-text-mode=LEGACY", "drools-path=classpath:drools")
                .stream()
                .map(setting -> "ice.knowledge-bases.%s.%s".formatted(knowledgeBase, setting))
                .toArray(String[]::new);
    }

    @ParameterizedTest
    @MethodSource("booleanOverrides")
    void resolvesBooleanOverridesForEachKnowledgeBase(final BooleanOverride setting)
    {
        for (final boolean localValue : new boolean[] { false, true })
            for (final String globalValue : List.of("", "false", "true"))
            {
                ApplicationContextRunner runner = contextRunner().withPropertyValues(knowledgeBaseDefaults("other-kb"))
                        .withPropertyValues("ice.knowledge-bases.test-kb.output-series-information-for-recommendations=true",
                                "ice.knowledge-bases.other-kb.output-series-information-for-evaluations=false",
                                "ice.knowledge-bases.other-kb.output-series-information-for-recommendations=true",
                                "ice.knowledge-bases.test-kb.%s=%s".formatted(setting.propertyName(), localValue),
                                "ice.knowledge-bases.other-kb.%s=%s".formatted(setting.propertyName(), !localValue));
                if (!globalValue.isEmpty())
                    runner = runner.withPropertyValues("ice.%s=%s".formatted(setting.propertyName(), globalValue));

                runner.run(context ->
                {
                    assertThat(context).hasNotFailed();
                    final IceProperties properties = context.getBean(IceProperties.class);
                    assertThat(setting.resolver().test(properties, properties.getKnowledgeBases().get(KNOWLEDGE_BASE))).isEqualTo(
                            globalValue.isEmpty() ? localValue : Boolean.parseBoolean(globalValue));
                    assertThat(setting.resolver().test(properties, properties.getKnowledgeBases().get("other-kb"))).isEqualTo(
                            globalValue.isEmpty() ? !localValue : Boolean.parseBoolean(globalValue));
                });
            }
    }

    @ParameterizedTest
    @CsvSource(
            { "false,false,false", "true,false,false", "false,false,true", "true,false,true", "false,true,false", "true,true,false",
                    "false,true,true", "true,true,true" })
    void validatesAllOutputFlagCombinationsAtStartup(final boolean evaluations, final boolean recommendations, final boolean doses)
    {
        contextRunner().withPropertyValues(
                "ice.knowledge-bases.test-kb.output-series-information-for-evaluations=%s".formatted(evaluations),
                "ice.knowledge-bases.test-kb.output-series-information-for-recommendations=%s".formatted(recommendations),
                "ice.knowledge-bases.test-kb.output-number-of-doses-remaining=%s".formatted(doses)).run(context ->
        {
            if (doses && !recommendations)
            {
                assertThat(context).hasFailed();
                assertThat(context.getStartupFailure()).hasStackTraceContaining(KNOWLEDGE_BASE)
                        .hasStackTraceContaining("output-number-of-doses-remaining")
                        .hasStackTraceContaining("output-series-information-for-recommendations");
            }
            else
            {
                assertThat(context).hasNotFailed();
                final IceProperties properties = context.getBean(IceProperties.class);
                final IceProperties.KnowledgeBaseProperties knowledgeBase = properties.getKnowledgeBases().get(KNOWLEDGE_BASE);
                assertThat(properties.resolveOutputSeriesInformationForEvaluations(knowledgeBase)).isEqualTo(evaluations);
                assertThat(properties.resolveOutputSeriesInformationForRecommendations(knowledgeBase)).isEqualTo(recommendations);
                assertThat(properties.resolveOutputNumberOfDosesRemaining(knowledgeBase)).isEqualTo(doses);
            }
        });
    }

    @ParameterizedTest
    @CsvSource({ "ice.output-series-information,true", "ice.output-series-information,false",
            "ice.knowledge-bases.test-kb.output-series-information,true",
            "ice.knowledge-bases.[test-kb].output-series-information,false" })
    void rejectsRemovedSeriesInformationSetting(final String propertyName, final boolean value)
    {
        contextRunner().withUserConfiguration(RemovedSeriesOutputPropertyCheck.class)
                .withPropertyValues("%s=%s".formatted(propertyName, value))
                .run(context ->
                {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasStackTraceContaining(propertyName)
                            .hasStackTraceContaining("output-series-information-for-evaluations")
                            .hasStackTraceContaining("output-series-information-for-recommendations");
                });
    }

    @Test
    void startsWithoutRemovedSeriesInformationSetting()
    {
        contextRunner().withUserConfiguration(RemovedSeriesOutputPropertyCheck.class).run(context ->
        {
            assertThat(context).hasNotFailed();
            final IceProperties properties = context.getBean(IceProperties.class);
            final IceProperties.KnowledgeBaseProperties knowledgeBase = properties.getKnowledgeBases().get(KNOWLEDGE_BASE);
            assertThat(properties.getOutputSeriesInformationForEvaluations()).isEmpty();
            assertThat(properties.getOutputSeriesInformationForRecommendations()).isEmpty();
            assertThat(knowledgeBase.outputSeriesInformationForEvaluations()).isFalse();
            assertThat(knowledgeBase.outputSeriesInformationForRecommendations()).isFalse();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "false", "true" })
    void checksRemovedSeriesInformationSettingForCanonicalKnowledgeBase(final String removedValue)
    {
        final String knowledgeBase = "[https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0]";
        final String removedProperty = "ice.knowledge-bases.%s.output-series-information".formatted(knowledgeBase);
        final Map<String, String> values = new HashMap<>();
        for (final String setting : knowledgeBaseDefaults(knowledgeBase))
        {
            final int separator = setting.indexOf('=');
            values.put(setting.substring(0, separator), setting.substring(separator + 1));
        }
        values.put("ice.knowledge-bases.%s.output-series-information-for-evaluations".formatted(knowledgeBase), "false");
        values.put("ice.knowledge-bases.%s.output-series-information-for-recommendations".formatted(knowledgeBase), "false");
        if (!removedValue.isEmpty())
            values.put(removedProperty, removedValue);

        contextRunner().withUserConfiguration(RemovedSeriesOutputPropertyCheck.class)
                .withInitializer(context -> TestPropertyValues.of(values).applyTo(context))
                .run(context ->
                {
                    if (removedValue.isEmpty())
                        assertThat(context).hasNotFailed();
                    else
                    {
                        assertThat(context).hasFailed();
                        assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalStateException.class)
                                .hasStackTraceContaining(removedProperty)
                                .hasStackTraceContaining("output-series-information-for-evaluations")
                                .hasStackTraceContaining("output-series-information-for-recommendations");
                    }
                });
    }

    @ParameterizedTest
    @CsvSource({ "true,false", "false,true", "false,false" })
    void requiresBothKnowledgeBaseSettingsEvenWhenGlobalOverridesArePresent(final boolean evaluationsPresent,
            final boolean recommendationsPresent)
    {
        ApplicationContextRunner runner =
                baseContextRunner().withPropertyValues("ice.output-series-information-for-evaluations=false",
                        "ice.output-series-information-for-recommendations=false");
        if (evaluationsPresent)
            runner = runner.withPropertyValues("ice.knowledge-bases.test-kb.output-series-information-for-evaluations=false");
        if (recommendationsPresent)
            runner = runner.withPropertyValues("ice.knowledge-bases.test-kb.output-series-information-for-recommendations=false");

        runner.run(context ->
        {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining("must not be null");
            if (!evaluationsPresent)
                assertThat(context.getStartupFailure()).hasStackTraceContaining("outputSeriesInformationForEvaluations");
            if (!recommendationsPresent)
                assertThat(context.getStartupFailure()).hasStackTraceContaining("outputSeriesInformationForRecommendations");
        });
    }

    @ParameterizedTest
    @CsvSource({ "true,false,false,true", "false,true,true,false" })
    void appliesGlobalSeriesOverridesIndependently(final boolean localEvaluations, final boolean localRecommendations,
            final boolean globalEvaluations, final boolean globalRecommendations)
    {
        contextRunner().withPropertyValues(
                "ice.knowledge-bases.test-kb.output-series-information-for-evaluations=%s".formatted(localEvaluations),
                "ice.knowledge-bases.test-kb.output-series-information-for-recommendations=%s".formatted(localRecommendations),
                "ice.output-series-information-for-evaluations=%s".formatted(globalEvaluations),
                "ice.output-series-information-for-recommendations=%s".formatted(globalRecommendations)).run(context ->
        {
            assertThat(context).hasNotFailed();
            final IceProperties properties = context.getBean(IceProperties.class);
            final IceProperties.KnowledgeBaseProperties knowledgeBase = properties.getKnowledgeBases().get(KNOWLEDGE_BASE);
            assertThat(properties.resolveOutputSeriesInformationForEvaluations(knowledgeBase)).isEqualTo(globalEvaluations);
            assertThat(properties.resolveOutputSeriesInformationForRecommendations(knowledgeBase)).isEqualTo(globalRecommendations);
        });
    }

    @ParameterizedTest
    @CsvSource({ "false,true,true,true,true", "true,false,false,true,false", "false,false,true,false,true",
            "false,true,false,false,true", "true,false,true,true,true" })
    void validatesEffectiveValuesAfterGlobalOverrides(final boolean localRecommendations, final boolean localDoses,
            final boolean globalRecommendations, final boolean globalDoses, final boolean valid)
    {
        contextRunner().withPropertyValues(
                "ice.knowledge-bases.test-kb.output-series-information-for-recommendations=%s".formatted(localRecommendations),
                "ice.knowledge-bases.test-kb.output-number-of-doses-remaining=%s".formatted(localDoses),
                "ice.output-series-information-for-recommendations=%s".formatted(globalRecommendations),
                "ice.output-number-of-doses-remaining=%s".formatted(globalDoses)).run(context ->
        {
            if (valid)
                assertThat(context).hasNotFailed();
            else
                assertThat(context).hasFailed();
        });
    }

    @Test
    void failsStartupAndReportsEveryInvalidKnowledgeBase()
    {
        contextRunner().withPropertyValues(knowledgeBaseDefaults("other-kb"))
                .withPropertyValues(knowledgeBaseDefaults("valid-kb"))
                .withPropertyValues("ice.output-number-of-doses-remaining=true",
                        "ice.knowledge-bases.other-kb.output-number-of-doses-remaining=false",
                        "ice.knowledge-bases.other-kb.output-series-information-for-evaluations=false",
                        "ice.knowledge-bases.other-kb.output-series-information-for-recommendations=false",
                        "ice.knowledge-bases.valid-kb.output-number-of-doses-remaining=false",
                        "ice.knowledge-bases.valid-kb.output-series-information-for-evaluations=false",
                        "ice.knowledge-bases.valid-kb.output-series-information-for-recommendations=true")
                .run(context ->
                {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasStackTraceContaining("Knowledge base 'test-kb'")
                            .hasStackTraceContaining("Knowledge base 'other-kb'")
                            .hasStackTraceContaining(
                                    "output-number-of-doses-remaining=true requires output-series-information-for-recommendations=true")
                            .hasStackTraceContaining("(global override applied: ice.output-number-of-doses-remaining)");
                });
    }

    @Test
    void namesGlobalRecommendationsOverrideInViolation()
    {
        contextRunner().withPropertyValues("ice.knowledge-bases.test-kb.output-number-of-doses-remaining=true",
                "ice.knowledge-bases.test-kb.output-series-information-for-recommendations=true",
                "ice.output-series-information-for-recommendations=false").run(context ->
        {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining("Knowledge base 'test-kb'")
                    .hasStackTraceContaining("(global override applied: ice.output-series-information-for-recommendations)");
        });
    }

    @ParameterizedTest
    @CsvSource({ "ICE_OUTPUTSERIESINFORMATION,ice.output-series-information",
            "ICE_KNOWLEDGEBASES_TESTKB_OUTPUTSERIESINFORMATION,ice.knowledge-bases.test-kb.output-series-information" })
    void rejectsRemovedSeriesInformationEnvironmentVariable(final String variable, final String propertyName)
    {
        contextRunner().withUserConfiguration(RemovedSeriesOutputPropertyCheck.class)
                .withInitializer(context -> context.getEnvironment()
                        .getPropertySources()
                        .replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                                new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                                        Map.of(variable, "true"))))
                .run(context ->
                {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(IllegalStateException.class)
                            .hasStackTraceContaining(propertyName);
                });
    }
}
