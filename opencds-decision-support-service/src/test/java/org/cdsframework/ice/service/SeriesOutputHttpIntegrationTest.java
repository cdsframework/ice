package org.cdsframework.ice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.cdsframework.ice.IceApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

class SeriesOutputHttpIntegrationTest
{
    private record Scenario(String name,
                            Boolean localEvaluations,
                            Boolean localRecommendations,
                            Boolean localDoses,
                            Boolean globalEvaluations,
                            Boolean globalRecommendations,
                            Boolean globalDoses,
                            boolean evaluations,
                            boolean recommendations,
                            boolean doses)
    {
    }

    private static final String KNOWLEDGE_BASE = "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String SELECTION_CONTEXT_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context";

    private static ServletWebServerApplicationContext startApplication(final Path fixture, final Scenario scenario,
            final String... extraArguments)
    {
        final List<String> arguments = new ArrayList<>(
                List.of("--server.port=0", "--server.address=127.0.0.1", "--cds-engine.experimental-features.enable-fhir-r6=true",
                        "--ice.config-path=%s".formatted(fixture.toUri()),
                        "--ice.vaccine-group-inclusions=ZOSTER,INFLUENZA,PNEUMOCOCCAL,HEP_A,HEP_B",
                        "--ice.vaccine-group-exclusions=", "--spring.main.banner-mode=off"));
        final String localPrefix = "--ice.knowledge-bases.[%s].".formatted(KNOWLEDGE_BASE);
        addSetting(arguments, localPrefix, "output-series-information-for-evaluations", scenario.localEvaluations());
        addSetting(arguments, localPrefix, "output-series-information-for-recommendations", scenario.localRecommendations());
        addSetting(arguments, localPrefix, "output-number-of-doses-remaining", scenario.localDoses());
        addSetting(arguments, "--ice.", "output-series-information-for-evaluations", scenario.globalEvaluations());
        addSetting(arguments, "--ice.", "output-series-information-for-recommendations", scenario.globalRecommendations());
        addSetting(arguments, "--ice.", "output-number-of-doses-remaining", scenario.globalDoses());
        arguments.addAll(List.of(extraArguments));
        return (ServletWebServerApplicationContext) new SpringApplicationBuilder(IceApplication.class).run(
                arguments.toArray(String[]::new));
    }

    private static void addSetting(final List<String> arguments, final String prefix, final String name, final Boolean value)
    {
        if (value != null)
            arguments.add("%s%s=%s".formatted(prefix, name, value));
    }

    private static List<JsonNode> resources(final JsonNode response, final String type)
    {
        final List<JsonNode> resources = new ArrayList<>();
        for (final JsonNode parameter : response.path("parameter"))
        {
            final JsonNode resource = parameter.path("resource");
            if (type.equals(resource.path("resourceType").asText()))
                resources.add(resource);
        }
        return resources;
    }

    private static List<JsonNode> recommendations(final JsonNode response)
    {
        final List<JsonNode> recommendations = new ArrayList<>();
        for (final JsonNode resource : resources(response, "ImmunizationRecommendation"))
            resource.path("recommendation").forEach(recommendations::add);
        return recommendations;
    }

    private static boolean hasSelectionContext(final JsonNode resource)
    {
        for (final JsonNode extension : resource.path("extension"))
            if (SELECTION_CONTEXT_URL.equals(extension.path("url").asText()))
                return true;
        return false;
    }

    private static void assertSeriesOutput(final List<JsonNode> resources, final boolean enabled)
    {
        assertThat(resources).isNotEmpty();
        if (enabled)
        {
            assertThat(resources).anyMatch(resource -> resource.hasNonNull("series"));
            assertThat(resources).anyMatch(SeriesOutputHttpIntegrationTest::hasSelectionContext);
        }
        else
        {
            assertThat(resources).allMatch(resource -> !resource.hasNonNull("series"));
            assertThat(resources).noneMatch(SeriesOutputHttpIntegrationTest::hasSelectionContext);
            assertThat(resources).allMatch(resource -> !resource.path("description").asText().contains("Series:"));
        }
    }

    private static List<String> clinicalResults(final JsonNode response)
    {
        final List<String> results = new ArrayList<>();
        for (final JsonNode evaluation : resources(response, "ImmunizationEvaluation"))
            results.add(evaluation.properties()
                    .stream()
                    .filter(entry -> List.of("targetDisease", "date", "doseStatus", "doseStatusReason", "doseNumber")
                            .contains(entry.getKey()))
                    .map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue()))
                    .sorted()
                    .toList()
                    .toString());
        for (final JsonNode recommendation : recommendations(response))
            results.add(recommendation.properties()
                    .stream()
                    .filter(entry -> List.of("targetDisease", "vaccineCode", "forecastStatus", "forecastReason", "dateCriterion")
                            .contains(entry.getKey()))
                    .map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue()))
                    .sorted()
                    .toList()
                    .toString());
        return results.stream().sorted().toList();
    }

    @Test
    void includesEarliestAndOverdueDatesWhenEnabled() throws Exception
    {
        verifyDateOutput(true);
    }

    @Test
    void omitsEarliestAndOverdueDatesWhenDisabled() throws Exception
    {
        verifyDateOutput(false);
    }

    private void verifyDateOutput(final boolean enabled) throws Exception
    {
        final Path fixture = SeriesOutputTestFixture.configurationPath();
        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final String requestBody = mapper.writeValueAsString(SeriesOutputTestFixture.request());
        final Scenario scenario = new Scenario("dates", null, null, null, null, null, null, false, false, false);
        final String localSetting = "--ice.knowledge-bases.[%s].output-earliest-and-overdue-dates=".formatted(KNOWLEDGE_BASE);
        try (final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build())
        {
            try (final ServletWebServerApplicationContext context = startApplication(fixture, scenario,
                    localSetting + !enabled, "--ice.output-earliest-and-overdue-dates=" + enabled))
            {
                final URI endpoint = URI.create(
                        "http://127.0.0.1:%d/opencds-decision-support-service/cds/$immds-forecast".formatted(
                                context.getWebServer().getPort()));
                final HttpRequest request = HttpRequest.newBuilder(endpoint)
                        .timeout(Duration.ofSeconds(60))
                        .header("Content-Type", "application/fhir+json")
                        .header("Accept", "application/fhir+json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                final HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertThat(result.statusCode()).isEqualTo(200);
                final List<JsonNode> recommendations = recommendations(mapper.readTree(result.body()));
                assertThat(recommendations).isNotEmpty();
                final List<String> dateCodes = new ArrayList<>();
                for (final JsonNode recommendation : recommendations)
                    for (final JsonNode criterion : recommendation.path("dateCriterion"))
                        for (final JsonNode coding : criterion.path("code").path("coding"))
                            dateCodes.add(coding.path("code").asText());
                assertThat(dateCodes).contains("30980-7");
                if (enabled)
                    assertThat(dateCodes).contains("30981-5", "59778-1");
                else
                    assertThat(dateCodes).doesNotContain("30981-5", "59778-1");
            }
        }
    }

    @Test
    void propagatesBoundConfigurationThroughHttpAndFhirSerialization() throws Exception
    {
        final Path fixture = SeriesOutputTestFixture.configurationPath();
        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final String requestBody = mapper.writeValueAsString(SeriesOutputTestFixture.request());
        // Representative bindings only; SeriesOutputRuleIntegrationTest covers every output combination through the rules.
        final List<Scenario> scenarios =
                List.of(new Scenario("bundled defaults", null, null, null, null, null, null, false, false, false),
                        new Scenario("recommendations and doses", false, true, true, null, null, null, false, true, true),
                        new Scenario("independent global overrides", false, true, false, true, false, null, true, false, false));
        List<String> baseline = null;
        try (final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build())
        {
            for (final Scenario scenario : scenarios)
            {
                try (final ServletWebServerApplicationContext context = startApplication(fixture, scenario))
                {
                    final URI endpoint = URI.create(
                            "http://127.0.0.1:%d/opencds-decision-support-service/cds/$immds-forecast".formatted(
                                    context.getWebServer().getPort()));
                    final HttpRequest request = HttpRequest.newBuilder(endpoint)
                            .timeout(Duration.ofSeconds(60))
                            .header("Content-Type", "application/fhir+json")
                            .header("Accept", "application/fhir+json")
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                            .build();
                    final HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertThat(httpResponse.statusCode()).as(scenario.name()).isEqualTo(200);
                    assertThat(httpResponse.headers().firstValue("Content-Type").orElseThrow()).startsWith("application/fhir+json");
                    final JsonNode response = mapper.readTree(httpResponse.body());
                    assertThat(response.path("resourceType").asText()).isEqualTo("Parameters");
                    final List<JsonNode> evaluations = resources(response, "ImmunizationEvaluation");
                    final List<JsonNode> recommendations = recommendations(response);
                    assertSeriesOutput(evaluations, scenario.evaluations());
                    assertSeriesOutput(recommendations, scenario.recommendations());
                    if (!scenario.evaluations())
                        assertThat(evaluations).allMatch(evaluation -> !evaluation.hasNonNull("seriesDoses"));
                    if (!scenario.recommendations())
                        assertThat(recommendations).allMatch(recommendation -> !recommendation.hasNonNull("seriesDoses"));
                    if (scenario.doses())
                        assertThat(recommendations).anyMatch(recommendation -> recommendation.hasNonNull("seriesDosesRemaining"));
                    else
                        assertThat(recommendations).allMatch(recommendation -> !recommendation.hasNonNull("seriesDosesRemaining"));
                    final List<String> clinicalResults = clinicalResults(response);
                    assertThat(clinicalResults).isNotEmpty();
                    if (baseline == null)
                        baseline = clinicalResults;
                    else
                        assertThat(clinicalResults).as(scenario.name()).isEqualTo(baseline);
                }
            }
        }
    }

    @ParameterizedTest
    @CsvSource({ "false,false", "false,true" })
    void rejectsInvalidEffectiveConfigurationAtApplicationStartup(final boolean evaluations, final boolean globalOverride)
            throws IOException
    {
        final Path fixture = SeriesOutputTestFixture.configurationPath();
        final Scenario scenario = new Scenario("invalid doses dependency", evaluations, globalOverride, true, null,
                globalOverride ? Boolean.FALSE : null, null, evaluations, false, true);
        assertThatThrownBy(() -> startApplication(fixture, scenario).close()).hasStackTraceContaining(KNOWLEDGE_BASE)
                .hasStackTraceContaining("output-number-of-doses-remaining")
                .hasStackTraceContaining("output-series-information-for-recommendations");
    }

    @ParameterizedTest
    @CsvSource({ "ice.output-series-information", "ice.knowledge-bases.[" + KNOWLEDGE_BASE + "].output-series-information" })
    void rejectsRemovedSeriesInformationSettingAtApplicationStartup(final String removedProperty) throws IOException
    {
        final Path fixture = SeriesOutputTestFixture.configurationPath();
        final Scenario scenario = new Scenario("removed setting", null, null, null, null, null, null, false, false, false);
        assertThatThrownBy(
                () -> startApplication(fixture, scenario, "--%s=true".formatted(removedProperty)).close()).hasRootCauseInstanceOf(
                        IllegalStateException.class)
                .hasStackTraceContaining(removedProperty)
                .hasStackTraceContaining("output-series-information-for-evaluations")
                .hasStackTraceContaining("output-series-information-for-recommendations");
    }
}
