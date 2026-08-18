package org.cdsframework.ice.service.conversion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.GuidanceResponse;
import org.cdsframework.fhir.ImmunizationEvaluation;
import org.cdsframework.fhir.ImmunizationRecommendation;
import org.cdsframework.fhir.Narrative;
import org.cdsframework.fhir.OperationOutcome;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.fhir.Reference;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FhirParametersResponseAdapter
{
    private static final String DURATION_MS_PARAM = "durationMs";
    private static final String ENGINE_VERSION_PARAM = "engineVersion";
    private static final String EVALUATION_PARAM = "evaluation";
    private static final String RECOMMENDATION_PARAM = "recommendation";
    private static final String GUIDANCE_RESPONSE_PARAM = "guidanceResponse";
    private static final String OPERATION_OUTCOME_PARAM = "operationOutcome";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_GENERATED = "generated";
    private static final String OUTCOME_CODE_INFORMATIONAL = "informational";
    private static final String OUTCOME_CODE_WARNING = "warning";
    private static final String OUTCOME_SEVERITY_INFORMATION = "information";
    private static final String OUTCOME_SEVERITY_WARNING = "warning";
    private static final String OUTCOME_SEVERITY_ERROR = "error";

    public static Parameters createForecastParametersResponse(final String knowledgeBase, final Reference patientReference,
            final LocalDateTime requestDateTime, final String engineVersion, final List<OperationOutcome.Issue> validationIssues,
            final List<ImmunizationEvaluation> immunizationEvaluations,
            final List<ImmunizationRecommendation> immunizationRecommendations)
    {
        final int durationMs = Math.max(0, Math.toIntExact(ChronoUnit.MILLIS.between(requestDateTime, LocalDateTime.now())));
        final List<OperationOutcome.Issue> issues = new ArrayList<>();
        Optional.ofNullable(validationIssues).stream().flatMap(Collection::stream).forEach(issues::add);
        if (immunizationEvaluations.isEmpty() && immunizationRecommendations.isEmpty())
            issues.add(createOperationOutcomeIssue(OUTCOME_CODE_WARNING,
                    "No evaluation or recommendation resources were produced. Ensure the request includes a patient birthDate and relevant immunization/observation input data.",
                    OUTCOME_SEVERITY_WARNING));

        final String outcomeSummarySuffix = buildOutcomeSummarySuffix(issues);
        issues.addFirst(createOperationOutcomeIssue(OUTCOME_CODE_INFORMATIONAL,
                "Forecast completed successfully using ICE %s%s.".formatted(engineVersion, outcomeSummarySuffix),
                OUTCOME_SEVERITY_INFORMATION));

        final GuidanceResponse guidanceResponse = GuidanceResponse.builder()
                .text(createNarrative(
                        "Immunization forecast guidance response generated successfully%s.".formatted(outcomeSummarySuffix)))
                .status(STATUS_SUCCESS)
                .subject(patientReference)
                .occurrenceDateTime(requestDateTime.atZone(ZoneId.systemDefault()).toInstant().toString())
                .moduleCanonical(knowledgeBase)
                .build();

        final Parameters.ParametersBuilder parametersBuilder = Parameters.builder()
                .parameter(ParametersParameter.builder().name(DURATION_MS_PARAM).valueInteger(durationMs).build())
                .parameter(ParametersParameter.builder().name(ENGINE_VERSION_PARAM).valueString(engineVersion).build())
                .parameter(ParametersParameter.builder().name(GUIDANCE_RESPONSE_PARAM).resource(guidanceResponse).build())
                .parameter(ParametersParameter.builder()
                        .name(OPERATION_OUTCOME_PARAM)
                        .resource(createOperationOutcome(issues))
                        .build());

        immunizationEvaluations.forEach(immunizationEvaluation -> parametersBuilder.parameter(
                ParametersParameter.builder().name(EVALUATION_PARAM).resource(immunizationEvaluation).build()));
        immunizationRecommendations.forEach(immunizationRecommendation -> parametersBuilder.parameter(
                ParametersParameter.builder().name(RECOMMENDATION_PARAM).resource(immunizationRecommendation).build()));

        return parametersBuilder.build();
    }

    public static Parameters createErrorParametersResponse(final String outcomeCode, final String detailText,
            final LocalDateTime requestDateTime, final String engineVersion)
    {
        final int durationMs = Math.max(0, Math.toIntExact(ChronoUnit.MILLIS.between(requestDateTime, LocalDateTime.now())));
        final OperationOutcome operationOutcome =
                createOperationOutcome(List.of(createOperationOutcomeIssue(outcomeCode, detailText, OUTCOME_SEVERITY_ERROR)));
        return Parameters.builder()
                .parameter(ParametersParameter.builder().name(DURATION_MS_PARAM).valueInteger(durationMs).build())
                .parameter(ParametersParameter.builder().name(ENGINE_VERSION_PARAM).valueString(engineVersion).build())
                .parameter(ParametersParameter.builder().name(OPERATION_OUTCOME_PARAM).resource(operationOutcome).build())
                .build();
    }

    private static String buildOutcomeSummarySuffix(final List<OperationOutcome.Issue> issues)
    {
        final boolean hasErrors = Optional.ofNullable(issues)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(issue -> OUTCOME_SEVERITY_ERROR.equalsIgnoreCase(issue.severity()));
        final boolean hasWarnings = Optional.ofNullable(issues)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(issue -> OUTCOME_SEVERITY_WARNING.equalsIgnoreCase(issue.severity())
                        || OUTCOME_CODE_WARNING.equalsIgnoreCase(issue.code()));

        if (hasErrors && hasWarnings)
            return " with errors and warnings";

        if (hasErrors)
            return " with errors";

        if (hasWarnings)
            return " with warnings";

        return "";
    }

    private static Narrative createNarrative(final String text)
    {
        if (!StringUtils.hasText(text))
            return null;

        return Narrative.builder()
                .status(STATUS_GENERATED)
                .div("<div xmlns=\"http://www.w3.org/1999/xhtml\">%s</div>".formatted(HtmlUtils.htmlEscape(text)))
                .build();
    }

    private static OperationOutcome.Issue createOperationOutcomeIssue(final String code, final String detailText,
            final String severity)
    {
        return OperationOutcome.Issue.builder()
                .severity(severity)
                .code(code)
                .details(CodeableConcept.builder().text(detailText).build())
                .build();
    }

    private static OperationOutcome createOperationOutcome(final List<OperationOutcome.Issue> issues)
    {
        return OperationOutcome.builder()
                .text(createNarrative("Operational messages for the immunization forecast request."))
                .issue(issues)
                .build();
    }
}
