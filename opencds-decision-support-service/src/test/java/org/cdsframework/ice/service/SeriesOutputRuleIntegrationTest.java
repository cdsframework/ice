package org.cdsframework.ice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Extension;
import org.cdsframework.fhir.ImmunizationEvaluation;
import org.cdsframework.fhir.ImmunizationRecommendation;
import org.cdsframework.fhir.ImmunizationRecommendationRecommendation;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.conversion.FhirToVmrInputAdapter;
import org.cdsframework.ice.service.conversion.OpenCdsResponseAdapter;
import org.cdsframework.ice.service.conversion.ScheduleAuthorityExtensionBuilder;
import org.cdsframework.ice.service.conversion.SelectionContextExtensionBuilder;
import org.cdsframework.ice.service.conversion.VaccineGroupRulesArtifactExtensionBuilder;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationResponse;
import org.opencds.dss.evaluate.Evaluation;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(
        properties = { "ice.vaccine-group-inclusions=ZOSTER,INFLUENZA,PNEUMOCOCCAL,HEP_A,HEP_B", "ice.vaccine-group-exclusions=" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SeriesOutputRuleIntegrationTest
{
    private record OutputFlags(boolean evaluations,
                               boolean recommendations,
                               boolean doses)
    {
    }

    private static final String INFLUENZA_CVX = "140";
    private static final String INFLUENZA_VACCINE_GROUP = "800";
    private static final String SELECTION_CONTEXT_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context";

    @DynamicPropertySource
    static void supportingDataFixture(final DynamicPropertyRegistry registry) throws IOException
    {
        final Path fixture = SeriesOutputTestFixture.configurationPath();
        registry.add("ice.config-path", () -> fixture.toUri().toString());
    }

    private static Stream<ObservationResult> observations(final Collection<RelatedClinicalStatement> statements)
    {
        return statements.stream().flatMap(statement ->
        {
            if (statement.getObservationResult() != null)
                return Stream.concat(Stream.of(statement.getObservationResult()),
                        observations(statement.getObservationResult().getRelatedClinicalStatement()));
            if (statement.getSubstanceAdministrationProposal() != null)
                return observations(statement.getSubstanceAdministrationProposal().getRelatedClinicalStatement());
            if (statement.getSubstanceAdministrationEvent() != null)
                return observations(statement.getSubstanceAdministrationEvent().getRelatedClinicalStatement());
            return Stream.empty();
        });
    }

    private static boolean hasObservation(final List<ObservationResult> observations, final String focusCode)
    {
        return observations.stream().anyMatch(observation -> focusCode.equals(observation.getObservationFocus().getCode()));
    }

    private static boolean isInfluenza(final CodeableConcept targetDisease)
    {
        return targetDisease != null && targetDisease.text() != null && targetDisease.text().contains("Influenza");
    }

    private static boolean hasInfluenza(final List<CodeableConcept> targetDiseases)
    {
        return targetDiseases != null && targetDiseases.stream().anyMatch(SeriesOutputRuleIntegrationTest::isInfluenza);
    }

    private static boolean hasSelectionContext(final List<Extension> extensions)
    {
        return extensions != null && extensions.stream().anyMatch(extension -> SELECTION_CONTEXT_URL.equals(extension.url()));
    }

    private static List<String> clinicalResults(final Parameters response)
    {
        return response.parameter().stream().map(ParametersParameter::resource).flatMap(resource ->
        {
            if (resource instanceof final ImmunizationEvaluation evaluation)
                return Stream.of("%s|%s|%s|%s|%s".formatted(evaluation.targetDisease(), evaluation.date(), evaluation.doseStatus(),
                        evaluation.doseStatusReason(), evaluation.doseNumber()));
            if (resource instanceof final ImmunizationRecommendation recommendation)
                return recommendation.recommendation()
                        .stream()
                        .map(entry -> "%s|%s|%s|%s|%s".formatted(entry.targetDisease(), entry.vaccineCode(), entry.forecastStatus(),
                                entry.forecastReason(), entry.dateCriterion()));
            return Stream.empty();
        }).sorted().toList();
    }

    @Autowired
    private IceProperties properties;

    @Autowired
    private SupportingDataService supportingData;

    @Autowired
    private Evaluation evaluationService;

    private Optional<Boolean> originalEvaluations;

    private Optional<Boolean> originalRecommendations;

    private Optional<Boolean> originalDoses;

    @BeforeEach
    void captureGlobalOverrides()
    {
        originalEvaluations = properties.getOutputSeriesInformationForEvaluations();
        originalRecommendations = properties.getOutputSeriesInformationForRecommendations();
        originalDoses = properties.getOutputNumberOfDosesRemaining();
    }

    @AfterEach
    void restoreGlobalOverrides()
    {
        properties.setOutputSeriesInformationForEvaluations(originalEvaluations);
        properties.setOutputSeriesInformationForRecommendations(originalRecommendations);
        properties.setOutputNumberOfDosesRemaining(originalDoses);
    }

    @Test
    void controlsFinalVmrAndFhirOutputWithoutChangingClinicalResults() throws Exception
    {
        final List<OutputFlags> configurations = List.of(new OutputFlags(false, false, false), new OutputFlags(true, false, false),
                new OutputFlags(false, true, false), new OutputFlags(false, true, true), new OutputFlags(true, true, false),
                new OutputFlags(true, true, true));
        List<String> baseline = null;
        final Parameters request = SeriesOutputTestFixture.request();
        for (final OutputFlags flags : configurations)
        {
            properties.setOutputSeriesInformationForEvaluations(Optional.of(flags.evaluations()));
            properties.setOutputSeriesInformationForRecommendations(Optional.of(flags.recommendations()));
            properties.setOutputNumberOfDosesRemaining(Optional.of(flags.doses()));
            final VmrConversionComponent converter =
                    new VmrConversionComponent(supportingData, properties, new FhirToVmrInputAdapter(supportingData),
                            new SelectionContextExtensionBuilder(supportingData), new VaccineGroupRulesArtifactExtensionBuilder(),
                            new ScheduleAuthorityExtensionBuilder());
            final EvaluateAtSpecifiedTime input = converter.convertToEvaluateAtSpecifiedTime(request);
            final EvaluationResponse evaluationResponse =
                    evaluationService.evaluateAtSpecifiedTime(input.getInteractionId(), input.getSpecifiedTime(),
                            input.getEvaluationRequest());
            final List<CDSOutput> outputs = OpenCdsResponseAdapter.extractCdsOutputs(evaluationResponse);
            assertThat(outputs).isNotEmpty();
            final EvaluatedPerson.ClinicalStatements statements =
                    outputs.getFirst().getVmrOutput().getPatient().getClinicalStatements();
            final List<ObservationResult> evaluationObservations = statements.getSubstanceAdministrationEvents()
                    .getSubstanceAdministrationEvent()
                    .stream()
                    .flatMap(event -> observations(event.getRelatedClinicalStatement()))
                    .toList();
            final List<ObservationResult> recommendationObservations = statements.getSubstanceAdministrationProposals()
                    .getSubstanceAdministrationProposal()
                    .stream()
                    .flatMap(proposal -> observations(proposal.getRelatedClinicalStatement()))
                    .toList();
            final List<ObservationResult> influenzaEvaluationObservations = statements.getSubstanceAdministrationEvents()
                    .getSubstanceAdministrationEvent()
                    .stream()
                    .filter(event -> INFLUENZA_CVX.equals(event.getSubstance().getSubstanceCode().getCode()))
                    .flatMap(event -> observations(event.getRelatedClinicalStatement()))
                    .toList();
            final List<ObservationResult> influenzaRecommendationObservations = statements.getSubstanceAdministrationProposals()
                    .getSubstanceAdministrationProposal()
                    .stream()
                    .filter(proposal -> INFLUENZA_VACCINE_GROUP.equals(proposal.getSubstance().getSubstanceCode().getCode()))
                    .flatMap(proposal -> observations(proposal.getRelatedClinicalStatement()))
                    .toList();

            assertThat(hasObservation(evaluationObservations, "SERIES_DISPLAY_OPTIONS")).isEqualTo(flags.evaluations());
            assertThat(hasObservation(recommendationObservations, "SERIES_DISPLAY_OPTIONS")).isEqualTo(flags.recommendations());
            assertThat(hasObservation(recommendationObservations, "NUMBER_OF_DOSES_REMAINING")).isEqualTo(flags.doses());
            assertThat(hasObservation(evaluationObservations, "NUMBER_OF_DOSES_REMAINING")).isFalse();
            assertThat(influenzaEvaluationObservations).isNotEmpty();
            assertThat(influenzaRecommendationObservations).isNotEmpty();
            assertThat(hasObservation(influenzaEvaluationObservations, "SEASON")).isEqualTo(flags.evaluations());
            assertThat(hasObservation(influenzaRecommendationObservations, "SEASON")).isEqualTo(flags.recommendations());

            final Parameters response = converter.convertToParametersResponse(evaluationResponse, request, LocalDateTime.now());
            final List<String> clinicalResults = clinicalResults(response);
            assertThat(clinicalResults).isNotEmpty();
            if (baseline == null)
                baseline = clinicalResults;
            else
                assertThat(clinicalResults).isEqualTo(baseline);

            final List<ImmunizationEvaluation> evaluations = response.parameter()
                    .stream()
                    .map(ParametersParameter::resource)
                    .filter(ImmunizationEvaluation.class::isInstance)
                    .map(ImmunizationEvaluation.class::cast)
                    .toList();
            final List<ImmunizationRecommendationRecommendation> recommendations = response.parameter()
                    .stream()
                    .map(ParametersParameter::resource)
                    .filter(ImmunizationRecommendation.class::isInstance)
                    .map(ImmunizationRecommendation.class::cast)
                    .flatMap(resource -> resource.recommendation().stream())
                    .toList();
            assertThat(evaluations.stream().anyMatch(evaluation -> evaluation.series() != null)).isEqualTo(flags.evaluations());
            assertThat(recommendations.stream().anyMatch(recommendation -> recommendation.series() != null)).isEqualTo(
                    flags.recommendations());
            assertThat(recommendations.stream().anyMatch(recommendation -> recommendation.seriesDosesRemaining() != null)).isEqualTo(
                    flags.doses());
            if (!flags.evaluations())
                assertThat(evaluations).allMatch(evaluation -> evaluation.seriesDoses() == null);
            if (!flags.recommendations())
                assertThat(recommendations).allMatch(recommendation -> recommendation.seriesDoses() == null);

            final List<ImmunizationEvaluation> influenzaEvaluations =
                    evaluations.stream().filter(evaluation -> hasInfluenza(evaluation.targetDisease())).toList();
            final List<ImmunizationRecommendationRecommendation> influenzaRecommendations = recommendations.stream()
                    .filter(recommendation -> hasInfluenza(recommendation.targetDisease()))
                    .toList();
            assertThat(influenzaEvaluations).isNotEmpty();
            assertThat(influenzaRecommendations).isNotEmpty();
            assertThat(influenzaEvaluations).allMatch(evaluation -> (evaluation.series() != null) == flags.evaluations()
                    && (evaluation.season() != null) == flags.evaluations()
                    && hasSelectionContext(evaluation.extension()) == flags.evaluations());
            assertThat(influenzaRecommendations).allMatch(
                    recommendation -> (recommendation.series() != null) == flags.recommendations()
                            && (recommendation.season() != null) == flags.recommendations()
                            && hasSelectionContext(recommendation.extension()) == flags.recommendations());
        }
    }
}
